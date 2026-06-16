<template>
<view class="service-request-view">
	<view class="content">
		<view class="section">
			<view class="section-title">选择服务类型</view>
			<view class="type-list">
				<view 
					class="type-item" 
					:class="{ active: selectedType === 'add_soup' }"
					@click="selectType('add_soup')">
					<view class="type-icon">🍲</view>
					<view class="type-text">加汤</view>
				</view>
				<view 
					class="type-item" 
					:class="{ active: selectedType === 'urge' }"
					@click="selectType('urge')">
					<view class="type-icon">⏰</view>
					<view class="type-text">催菜</view>
				</view>
				<view 
					class="type-item" 
					:class="{ active: selectedType === 'change_plate' }"
					@click="selectType('change_plate')">
					<view class="type-icon">🍽️</view>
					<view class="type-text">换碟</view>
				</view>
				<view 
					class="type-item" 
					:class="{ active: selectedType === 'other' }"
					@click="selectType('other')">
					<view class="type-icon">❓</view>
					<view class="type-text">其他</view>
				</view>
			</view>
		</view>
		
		<view class="section">
			<view class="section-title">补充说明（选填）</view>
			<textarea 
				class="textarea" 
				v-model="content" 
				placeholder="请输入详细说明..." 
				maxlength="200">
			</textarea>
		</view>
		
		<view class="section">
			<view class="section-title">我的服务请求</view>
			<view v-if="requestList.length > 0">
				<view class="request-item" v-for="(item, index) in requestList" :key="index">
					<view class="request-header">
						<view class="request-type">{{ getTypeText(item.type) }}</view>
						<view class="request-status" :class="'status-' + item.state">
							{{ getStateText(item.state) }}
						</view>
					</view>
					<view v-if="item.content" class="request-content">{{ item.content }}</view>
					<view class="request-time">{{ formatTime(item.createTime) }}</view>
					<view v-if="item.state === 2 && item.handleRemark" class="request-remark">
						服务员备注：{{ item.handleRemark }}
					</view>
				</view>
			</view>
			<view v-else class="empty-tip">暂无服务请求</view>
		</view>
	</view>
	
	<view class="submit-btn" @click="submitRequest">提交请求</view>
</view>
</template>

<script>
import {getBaseUrl, requestUtil} from "../../utils/requestUtil.js"
export default{
	data() {
		return {
			baseUrl: '',
			selectedType: '',
			content: '',
			requestList: []
		}
	},
	methods: {
		selectType(type) {
			this.selectedType = type
		},
		getTypeText(type) {
			const typeMap = {
				'add_soup': '加汤',
				'urge': '催菜',
				'change_plate': '换碟',
				'other': '其他'
			}
			return typeMap[type] || type
		},
		getStateText(state) {
			const textMap = {
				0: '待处理',
				1: '处理中',
				2: '已完成'
			}
			return textMap[state] || '未知'
		},
		formatTime(time) {
			if (!time) return ''
			
			let date = new Date(time)
			
			if (isNaN(date.getTime())) {
				return ''
			}
			
			let m = String(date.getMonth() + 1).padStart(2, '0')
			let d = String(date.getDate()).padStart(2, '0')
			let h = String(date.getHours()).padStart(2, '0')
			let min = String(date.getMinutes()).padStart(2, '0')
			return m + '-' + d + ' ' + h + ':' + min
		},
		async loadRequestList() {
			try {
				let table_id = wx.getStorageSync('table_num')
				table_id = parseInt(table_id)
				
				let res = await requestUtil({
					url: "/order/service-request/list",
					data: {tableId: table_id},
					method: "get"
				})
				
				if (res && res.data) {
					this.requestList = res.data || []
				}
			} catch(e) {
				console.error('加载服务请求列表失败:', e)
			}
		},
		async submitRequest() {
			if (!this.selectedType) {
				wx.showToast({
					title: '请选择服务类型',
					icon: 'none'
				})
				return
			}
			
			wx.showLoading({title: '提交中...'})
			
			try {
				let table_id = wx.getStorageSync('table_num')
				let order_id = wx.getStorageSync('orderId')
				table_id = parseInt(table_id)
				
				const requestData = {
					tableId: table_id,
					orderId: order_id ? parseInt(order_id) : null,
					type: this.selectedType,
					content: this.content
				}
				
				await requestUtil({
					url: "/order/service-request/create",
					data: requestData,
					method: "post"
				})
				
				wx.hideLoading()
				wx.showToast({
					title: '提交成功',
					icon: 'success'
				})
				
				this.selectedType = ''
				this.content = ''
				
				setTimeout(() => {
					this.loadRequestList()
				}, 1500)
				
			} catch (e) {
				wx.hideLoading()
				console.error('提交服务请求失败:', e)
				wx.showToast({
					title: '提交失败',
					icon: 'none'
				})
			}
		}
	},
	onLoad() {
		this.baseUrl = getBaseUrl()
	},
	onShow() {
		this.loadRequestList()
	}
}
</script>

<style>
page {background-color: #f4f4f4;}
.service-request-view {min-height: 100vh; padding-bottom: 140rpx;}
.content {padding: 20rpx;}
.section {
	background-color: #fefefe;
	border-radius: 10rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
}
.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 30rpx;
}
.type-list {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
}
.type-item {
	flex: 1;
	min-width: 150rpx;
	background-color: #f8f8f8;
	border-radius: 10rpx;
	padding: 30rpx 20rpx;
	text-align: center;
	transition: all 0.3s;
	border: 2rpx solid transparent;
}
.type-item.active {
	background-color: #fff7e6;
	border-color: #f7d45f;
}
.type-icon {
	font-size: 50rpx;
	margin-bottom: 10rpx;
}
.type-text {
	font-size: 28rpx;
	color: #666;
}
.type-item.active .type-text {
	color: #333;
	font-weight: bold;
}
.textarea {
	width: 100%;
	min-height: 150rpx;
	font-size: 28rpx;
	line-height: 1.6;
	color: #333;
	background-color: #f8f8f8;
	border-radius: 8rpx;
	padding: 20rpx;
}
.request-item {
	background-color: #f8f8f8;
	border-radius: 10rpx;
	padding: 25rpx;
	margin-bottom: 15rpx;
}
.request-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15rpx;
}
.request-type {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
}
.request-status {
	font-size: 24rpx;
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
}
.status-0 {
	background-color: #fff1f0;
	color: #ff4d4f;
}
.status-1 {
	background-color: #fff7e6;
	color: #fa8c16;
}
.status-2 {
	background-color: #f6ffed;
	color: #52c41a;
}
.request-content {
	font-size: 28rpx;
	color: #666;
	margin-bottom: 10rpx;
	line-height: 1.5;
}
.request-time {
	font-size: 24rpx;
	color: #999;
}
.request-remark {
	font-size: 26rpx;
	color: #1890ff;
	margin-top: 15rpx;
	padding-top: 15rpx;
	border-top: 1rpx solid #e8e8e8;
}
.empty-tip {
	text-align: center;
	color: #999;
	font-size: 28rpx;
	padding: 60rpx 0;
}
.submit-btn {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	height: 120rpx;
	background: linear-gradient(to right, #f8da81, #f7d362);
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 32rpx;
	font-weight: bold;
	z-index: 99;
}
</style>
