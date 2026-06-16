<template>
<view class="container">
	<view class="header"></view>
	
	<view v-if="orderList.length > 0" class="order-list">
		<view v-for="order in orderList" :key="order.id" class="order-card" @click="goToDetail(order.id)">
			<view class="order-header">
				<view class="order-id">订单 #{{order.id}}</view>
				<view class="order-status" :class="order.pay_state == 1 ? 'status-paid' : 'status-unpaid'">
					{{order.pay_state == 1 ? '已结账' : '待结账'}}
				</view>
			</view>
			
			<view class="order-divider"></view>
			
			<view class="order-content">
				<view class="order-info">
					<view class="info-item">
						<text class="info-label">总价</text>
						<text class="info-value price">¥{{order.total_price}}</text>
					</view>
					<view class="info-item" v-if="order.table_number">
						<text class="info-label">桌号</text>
						<text class="info-value">{{order.table_number}}</text>
					</view>
					<view class="info-item" v-if="order.order_time">
						<text class="info-label">下单时间</text>
						<text class="info-value time">{{formatTime(order.order_time)}}</text>
					</view>
				</view>
			</view>
			
			<view class="order-footer" v-if="order.pay_state == 1">
				<view class="review-btn" @click.stop="goToReview(order.id, order.table_id)">
					<text class="review-icon">✨</text>
					<text>去评价</text>
				</view>
				<view class="detail-btn" @click.stop="goToDetail(order.id)">
					<text>查看详情</text>
					<text class="arrow">→</text>
				</view>
			</view>
			
			<view class="order-footer" v-else>
				<view class="detail-btn full" @click.stop="goToDetail(order.id)">
					<text>查看详情</text>
					<text class="arrow">→</text>
				</view>
			</view>
		</view>
	</view>
	
	<view v-else class="empty-state">
		<view class="empty-icon">📋</view>
		<view class="empty-text">暂无订单</view>
		<view class="empty-subtext">快去点餐吧~</view>
	</view>
</view>
</template>

<script>
import {requestUtil} from "../../utils/requestUtil.js"
export default {
	data() {
		return {
			orderList: []
		}
	},
	methods: {
		formatTime(timeStr) {
			if (!timeStr) return ''
			const date = new Date(timeStr)
			const month = (date.getMonth() + 1).toString().padStart(2, '0')
			const day = date.getDate().toString().padStart(2, '0')
			const hours = date.getHours().toString().padStart(2, '0')
			const minutes = date.getMinutes().toString().padStart(2, '0')
			return month + '-' + day + ' ' + hours + ':' + minutes
		},
		async loadOrders() {
			try {
				let tableId = wx.getStorageSync('table_num')
				let params = {}
				if (tableId) {
					params.table_id = parseInt(tableId)
				}
				let res = await requestUtil({url: '/order/history', method: 'get', data: params})
				if (res && res.data) {
					this.orderList = res.data
				}
			} catch(e) {
				console.error(e)
			}
		},
		goToDetail(orderId) {
			wx.navigateTo({
				url: '/pages/order-details/details?orderId=' + orderId
			})
		},
		goToReview(orderId, tableId) {
			wx.navigateTo({
				url: '/pages/order-review/order-review?orderId=' + orderId + '&tableId=' + (tableId || 1)
			})
		}
	},
	onLoad() {
		this.loadOrders()
	},
	onShow() {
		this.loadOrders()
	}
}
</script>

<style>
page {
	background: #fafafa;
	min-height: 100vh;
}

.container {
	padding-bottom: 40rpx;
}

.header {
	background: linear-gradient(to bottom, #f7d45f, #f7d562, #f8d561, #f9db76, #f9de80);
	height: 40rpx;
	border-radius: 0 0 40rpx 40rpx;
	box-shadow: 0 8rpx 30rpx rgba(247, 212, 95, 0.25);
}

.order-list {
	padding: 30rpx 20rpx;
}

.order-card {
	background: #ffffff;
	border-radius: 24rpx;
	padding: 30rpx;
	margin-bottom: 24rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
	transition: all 0.3s ease;
	position: relative;
	overflow: hidden;
}

.order-card::before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	height: 6rpx;
	background: linear-gradient(to right, #f8da81, #f8d771, #f7d362, #f6cb4a);
}

.order-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.order-id {
	font-size: 32rpx;
	font-weight: 600;
	color: #333333;
	letter-spacing: 1rpx;
}

.order-status {
	font-size: 24rpx;
	font-weight: 500;
	padding: 8rpx 24rpx;
	border-radius: 30rpx;
}

.status-paid {
	background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
	color: #065f46;
}

.status-unpaid {
	background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
	color: #92400e;
}

.order-divider {
	height: 2rpx;
	background: linear-gradient(90deg, transparent 0%, #f0f0f0 50%, transparent 100%);
	margin: 20rpx 0;
}

.order-content {
	margin-bottom: 24rpx;
}

.order-info {
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.info-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.info-label {
	font-size: 26rpx;
	color: #797979;
	font-weight: 400;
}

.info-value {
	font-size: 28rpx;
	color: #333333;
	font-weight: 500;
}

.info-value.price {
	font-size: 36rpx;
	font-weight: 700;
	color: #eb5941;
	letter-spacing: 1rpx;
}

.info-value.time {
	font-size: 24rpx;
	color: #a4a4a4;
}

.order-footer {
	display: flex;
	gap: 16rpx;
	justify-content: flex-end;
}

.review-btn {
	background: linear-gradient(to right, #f8da81, #f8d771, #f7d362, #f6cb4a);
	color: #333333;
	padding: 16rpx 32rpx;
	border-radius: 50rpx;
	font-size: 26rpx;
	font-weight: 500;
	display: flex;
	align-items: center;
	gap: 8rpx;
	box-shadow: 0 4rpx 12rpx rgba(246, 203, 74, 0.3);
	transition: all 0.3s ease;
}

.review-btn:active {
	transform: scale(0.95);
	box-shadow: 0 2rpx 8rpx rgba(246, 203, 74, 0.2);
}

.review-icon {
	font-size: 24rpx;
}

.detail-btn {
	background: linear-gradient(to right, #f8da81, #f8d771, #f7d362, #f6cb4a);
	color: #333333;
	padding: 16rpx 32rpx;
	border-radius: 50rpx;
	font-size: 26rpx;
	font-weight: 500;
	display: flex;
	align-items: center;
	gap: 8rpx;
	box-shadow: 0 4rpx 12rpx rgba(246, 203, 74, 0.3);
	transition: all 0.3s ease;
}

.detail-btn:active {
	transform: scale(0.95);
	box-shadow: 0 2rpx 8rpx rgba(246, 203, 74, 0.2);
}

.detail-btn.full {
	flex: 1;
	justify-content: center;
}

.arrow {
	font-size: 20rpx;
	opacity: 0.8;
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 160rpx 40rpx;
}

.empty-icon {
	font-size: 120rpx;
	margin-bottom: 30rpx;
	opacity: 0.6;
	animation: float 3s ease-in-out infinite;
}

@keyframes float {
	0%, 100% {
		transform: translateY(0);
	}
	50% {
		transform: translateY(-10rpx);
	}
}

.empty-text {
	font-size: 32rpx;
	font-weight: 600;
	color: #797979;
	margin-bottom: 12rpx;
}

.empty-subtext {
	font-size: 26rpx;
	color: #a4a4a4;
}
</style>
