<template>
<view class="feedback-view">
	<view class="header"></view>
	
	<view class="feedback-content">
		<view class="section">
			<view class="section-title">反馈类型</view>
			<view class="type-list">
				<view 
					class="type-item" 
					:class="{active: feedbackType === 'complaint'}" 
					@click="setFeedbackType('complaint')">
					<text class="type-icon">😠</text>
					<text class="type-text">投诉</text>
				</view>
				<view 
					class="type-item" 
					:class="{active: feedbackType === 'suggestion'}" 
					@click="setFeedbackType('suggestion')">
					<text class="type-icon">💡</text>
					<text class="type-text">建议</text>
				</view>
			</view>
		</view>
		
		<view class="section">
			<view class="section-title">反馈内容</view>
			<textarea 
				class="textarea" 
				v-model="content" 
				placeholder="请详细描述您的问题或建议..." 
				maxlength="500"
				:show-confirm-bar="false">
			</textarea>
			<view class="char-count">{{content.length}}/500</view>
		</view>
	</view>
	
	<view class="submit-btn" @click="submitFeedback" :class="{disabled: !canSubmit}">
		<text>提交反馈</text>
	</view>
</view>
</template>

<script>
import {requestUtil} from "../../utils/requestUtil.js"
export default{
	data() {
		return {
			feedbackType: 'complaint',
			content: ''
		}
	},
	computed: {
		canSubmit() {
			return this.content.trim().length > 0
		}
	},
	methods: {
		setFeedbackType(type) {
			this.feedbackType = type
		},
		async submitFeedback() {
			if (!this.canSubmit) {
				uni.showToast({
					title: '请填写反馈内容',
					icon: 'none'
				})
				return
			}
			
			let tableId = uni.getStorageSync('table_num')
			if (!tableId) {
				tableId = 1
			}
			
			let orderId = uni.getStorageSync('orderId')
			
			uni.showLoading({title: '提交中...'})
			
			try {
				let feedbackData = {
					tableId: parseInt(tableId),
					orderId: orderId ? parseInt(orderId) : null,
					type: this.feedbackType,
					content: this.content.trim()
				}
				
				await requestUtil({
					url: "/order/feedback/create",
					data: feedbackData,
					method: "post"
				})
				
				uni.hideLoading()
				uni.showToast({
					title: '反馈提交成功',
					icon: 'success'
				})
				
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			} catch (e) {
				uni.hideLoading()
				console.error('提交反馈失败:', e)
				uni.showToast({
					title: '提交失败，请重试',
					icon: 'none'
				})
			}
		}
	}
}
</script>

<style>
page {
	background-color: #f4f4f4;
}

.feedback-view {
	min-height: 100vh;
	padding-bottom: 160rpx;
}

.header {
	background: linear-gradient(to bottom, #f7d45f, #f7d562, #f8d561, #f9db76, #f9de80);
	height: 40rpx;
	border-radius: 0 0 40rpx 40rpx;
	box-shadow: 0 8rpx 30rpx rgba(247, 212, 95, 0.25);
}

.feedback-content {
	padding: 30rpx 20rpx;
}

.section {
	background-color: #fefefe;
	border-radius: 20rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 30rpx;
}

.type-list {
	display: flex;
	gap: 20rpx;
}

.type-item {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 40rpx 20rpx;
	background: #f8fafc;
	border: 2rpx solid #e2e8f0;
	border-radius: 16rpx;
	transition: all 0.3s;
}

.type-item.active {
	background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
	border-color: #f7d45f;
	box-shadow: 0 4rpx 12rpx rgba(247, 212, 95, 0.3);
}

.type-icon {
	font-size: 60rpx;
	margin-bottom: 10rpx;
}

.type-text {
	font-size: 28rpx;
	color: #666;
}

.type-item.active .type-text {
	color: #92400e;
	font-weight: 500;
}

.textarea {
	width: 100%;
	min-height: 300rpx;
	font-size: 28rpx;
	line-height: 1.8;
	color: #333;
	background: #f8fafc;
	padding: 20rpx;
	border-radius: 12rpx;
	box-sizing: border-box;
}

.char-count {
	text-align: right;
	font-size: 24rpx;
	color: #999;
	margin-top: 10rpx;
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
	color: #333;
	z-index: 99;
	transition: all 0.3s;
}

.submit-btn.disabled {
	background: #e5e5e5;
	color: #999;
}

.submit-btn text {
	letter-spacing: 2rpx;
}
</style>
