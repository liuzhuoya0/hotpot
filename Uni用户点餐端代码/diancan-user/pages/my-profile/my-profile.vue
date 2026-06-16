<template>
	<view class="profile-container">
		<view class="profile-header">
			<button class="avatar-btn" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
				<view class="avatar">
					<image :src="userInfo.avatarUrl || '/static/tab/我的-1.png'" mode="aspectFill"></image>
				</view>
			</button>
			<view class="user-info">
				<input class="user-name-input" type="nickname" placeholder="点击设置昵称" v-model="userInfo.nickName" @blur="saveUserInfo" />
				<view class="user-phone">欢迎使用扫码点餐</view>
			</view>
		</view>
		
		<view class="menu-list">
			<view class="menu-item" @click="goToMyOrder">
				<view class="menu-icon">
					<text>📋</text>
				</view>
				<view class="menu-text">我的订单</view>
				<view class="menu-arrow">></view>
			</view>
			
			<view class="menu-item" @click="goToServiceRequest">
				<view class="menu-icon">
					<text>🛎️</text>
				</view>
				<view class="menu-text">服务请求</view>
				<view class="menu-arrow">></view>
			</view>
			
			<view class="menu-item" @click="goToOrderReview">
				<view class="menu-icon">
					<text>⭐</text>
				</view>
				<view class="menu-text">我的评价</view>
				<view class="menu-arrow">></view>
			</view>
			
			<view class="menu-item" @click="goToFeedback">
				<view class="menu-icon">
					<text>💬</text>
				</view>
				<view class="menu-text">意见反馈</view>
				<view class="menu-arrow">></view>
			</view>
		</view>
		
		<view class="version-info">
			<text>版本 1.0.0</text>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			userInfo: {
				avatarUrl: '',
				nickName: ''
			}
		}
	},
	onLoad() {
		this.loadUserInfo()
	},
	methods: {
		loadUserInfo() {
			const savedUserInfo = uni.getStorageSync('userInfo')
			if (savedUserInfo) {
				this.userInfo = savedUserInfo
			}
		},
		onChooseAvatar(e) {
			this.userInfo.avatarUrl = e.detail.avatarUrl
			this.saveUserInfo()
		},
		saveUserInfo() {
			uni.setStorageSync('userInfo', this.userInfo)
		},
		goToMyOrder() {
			uni.switchTab({
				url: '/pages/my-order/my-order'
			})
		},
		goToServiceRequest() {
			uni.navigateTo({
				url: '/pages/service-request/service-request'
			})
		},
		goToOrderReview() {
			uni.navigateTo({
				url: '/pages/review-list/review-list'
			})
		},
		goToFeedback() {
			uni.navigateTo({
				url: '/pages/feedback-submit/feedback-submit'
			})
		}
	}
}
</script>

<style scoped>
.profile-container {
	min-height: 100vh;
	background: linear-gradient(180deg, #f7d45f 0%, #f7d45f 200px, #f5f7fa 200px, #f5f7fa 100%);
	padding-bottom: 120rpx;
}

.profile-header {
	display: flex;
	align-items: center;
	padding: 60rpx 40rpx 40rpx;
}

.avatar-btn {
	padding: 0;
	margin: 0;
	background: transparent;
	border: none;
	line-height: 1;
}

.avatar-btn::after {
	border: none;
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 60rpx;
	background: #ffffff;
	overflow: hidden;
	margin-right: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.avatar image {
	width: 100%;
	height: 100%;
}

.user-info {
	flex: 1;
}

.user-name-input {
	font-size: 36rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 10rpx;
	background: transparent;
	border: none;
	padding: 0;
}

.user-phone {
	font-size: 26rpx;
	color: #666666;
}

.menu-list {
	margin: 20rpx 30rpx;
	background: #ffffff;
	border-radius: 20rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
	overflow: hidden;
}

.menu-item {
	display: flex;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
	transition: background 0.3s;
}

.menu-item:last-child {
	border-bottom: none;
}

.menu-item:active {
	background: #f8f8f8;
}

.menu-icon {
	width: 60rpx;
	height: 60rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 24rpx;
	font-size: 40rpx;
}

.menu-text {
	flex: 1;
	font-size: 30rpx;
	color: #333333;
}

.menu-arrow {
	font-size: 30rpx;
	color: #cccccc;
}

.version-info {
	text-align: center;
	padding: 40rpx;
	font-size: 24rpx;
	color: #999999;
}
</style>
