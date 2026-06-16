<template>
<view class="review-list-view">
	<view class="header"></view>
	
	<view v-if="reviewList.length > 0" class="review-list">
		<view v-for="review in reviewList" :key="review.id" class="review-card">
			<view class="review-header">
				<view class="review-id">评价 #{{review.id}}</view>
				<view class="review-time">{{formatTime(review.createTime)}}</view>
			</view>
			
			<view class="review-divider"></view>
			
			<view class="review-content">
				<view class="score-section">
					<view class="score-item">
						<text class="score-label">火锅锅底口味</text>
						<view class="stars">
							<view v-for="i in 5" :key="i" class="star" :class="i <= review.potTasteScore ? 'active' : ''">
								★
							</view>
						</view>
					</view>
					
					<view class="score-item">
						<text class="score-label">菜品新鲜度</text>
						<view class="stars">
							<view v-for="i in 5" :key="i" class="star" :class="i <= review.dishFreshnessScore ? 'active' : ''">
								★
							</view>
						</view>
					</view>
					
					<view class="score-item">
						<text class="score-label">用餐体验</text>
						<view class="stars">
							<view v-for="i in 5" :key="i" class="star" :class="i <= review.diningExperienceScore ? 'active' : ''">
								★
							</view>
						</view>
					</view>
				</view>
				
				<view class="content-section" v-if="review.content">
					<view class="content-label">评价内容</view>
					<view class="content-text">{{review.content}}</view>
				</view>
				
				<view class="image-section" v-if="review.images && review.images.length > 0">
					<view class="image-label">评价图片</view>
					<view class="image-list">
						<view v-for="(img, index) in getImageArray(review.images)" :key="index" class="image-item">
							<image :src="baseUrl + img" mode="aspectFill" @click="previewImage(getImageArray(review.images), index)"></image>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
	
	<view v-else class="empty-state">
		<view class="empty-icon">⭐</view>
		<view class="empty-text">暂无评价</view>
		<view class="empty-subtext">快去发表评价吧~</view>
	</view>
</view>
</template>

<script>
import {getBaseUrl, requestUtil} from "../../utils/requestUtil.js"
export default{
	data() {
		return {
			baseUrl: '',
			reviewList: []
		}
	},
	methods: {
		formatTime(timeStr) {
			if (!timeStr) return ''
			const date = new Date(timeStr)
			const year = date.getFullYear()
			const month = (date.getMonth() + 1).toString().padStart(2, '0')
			const day = date.getDate().toString().padStart(2, '0')
			const hours = date.getHours().toString().padStart(2, '0')
			const minutes = date.getMinutes().toString().padStart(2, '0')
			return year + '-' + month + '-' + day + ' ' + hours + ':' + minutes
		},
		getImageArray(imagesStr) {
			if (!imagesStr) return []
			try {
				return JSON.parse(imagesStr)
			} catch (e) {
				return []
			}
		},
		previewImage(images, index) {
			const fullUrls = images.map(img => this.baseUrl + img)
			wx.previewImage({
				current: fullUrls[index],
				urls: fullUrls
			})
		},
		async loadReviews() {
			try {
				let tableId = wx.getStorageSync('table_num')
				if (!tableId) {
					tableId = 1
				}
				let res = await requestUtil({
					url: '/order-review/list-by-table-id',
					method: 'get',
					data: {tableId: parseInt(tableId)}
				})
				if (res && res.data) {
					this.reviewList = res.data
				}
			} catch(e) {
				console.error(e)
			}
		}
	},
	onLoad() {
		this.baseUrl = getBaseUrl()
		this.loadReviews()
	},
	onShow() {
		this.loadReviews()
	}
}
</script>

<style>
page {
	background: #fafafa;
	min-height: 100vh;
}

.review-list-view {
	padding-bottom: 40rpx;
}

.header {
	background: linear-gradient(to bottom, #f7d45f, #f7d562, #f8d561, #f9db76, #f9de80);
	height: 40rpx;
	border-radius: 0 0 40rpx 40rpx;
	box-shadow: 0 8rpx 30rpx rgba(247, 212, 95, 0.25);
}

.review-list {
	padding: 30rpx 20rpx;
}

.review-card {
	background: #ffffff;
	border-radius: 24rpx;
	padding: 30rpx;
	margin-bottom: 24rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
	position: relative;
	overflow: hidden;
}

.review-card::before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	height: 6rpx;
	background: linear-gradient(to right, #f8da81, #f8d771, #f7d362, #f6cb4a);
}

.review-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.review-id {
	font-size: 32rpx;
	font-weight: 600;
	color: #333333;
	letter-spacing: 1rpx;
}

.review-time {
	font-size: 24rpx;
	color: #a4a4a4;
}

.review-divider {
	height: 2rpx;
	background: linear-gradient(90deg, transparent 0%, #f0f0f0 50%, transparent 100%);
	margin: 20rpx 0;
}

.review-content {
	margin-bottom: 10rpx;
}

.score-section {
	margin-bottom: 30rpx;
}

.score-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.score-item:last-child {
	margin-bottom: 0;
}

.score-label {
	font-size: 28rpx;
	color: #666;
}

.stars {
	display: flex;
}

.star {
	font-size: 36rpx;
	color: #ddd;
	margin-left: 6rpx;
}

.star.active {
	color: #f7d45f;
}

.content-section {
	margin-bottom: 30rpx;
}

.content-label {
	font-size: 28rpx;
	color: #666;
	font-weight: 500;
	margin-bottom: 12rpx;
}

.content-text {
	font-size: 28rpx;
	color: #333;
	line-height: 1.8;
	background: #f8fafc;
	padding: 20rpx;
	border-radius: 12rpx;
}

.image-section {
	margin-bottom: 10rpx;
}

.image-label {
	font-size: 28rpx;
	color: #666;
	font-weight: 500;
	margin-bottom: 16rpx;
}

.image-list {
	display: flex;
	flex-wrap: wrap;
}

.image-item {
	width: 160rpx;
	height: 160rpx;
	margin-right: 16rpx;
	margin-bottom: 16rpx;
	border-radius: 12rpx;
	overflow: hidden;
}

.image-item image {
	width: 100%;
	height: 100%;
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
