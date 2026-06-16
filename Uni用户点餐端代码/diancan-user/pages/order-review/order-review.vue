<template>
<view class="review-view">
	<view class="review-content">
		<view class="section">
			<view class="section-title">评分</view>
			
			<view class="score-item">
				<text class="score-label">火锅锅底口味</text>
				<view class="stars">
					<view v-for="i in 5" :key="i" class="star" :class="i <= potTasteScore ? 'active' : ''" @click="setScore('potTaste', i)">
						★
					</view>
				</view>
			</view>
			
			<view class="score-item">
				<text class="score-label">菜品新鲜度</text>
				<view class="stars">
					<view v-for="i in 5" :key="i" class="star" :class="i <= dishFreshnessScore ? 'active' : ''" @click="setScore('dishFreshness', i)">
						★
					</view>
				</view>
			</view>
			
			<view class="score-item">
				<text class="score-label">用餐体验</text>
				<view class="stars">
					<view v-for="i in 5" :key="i" class="star" :class="i <= diningExperienceScore ? 'active' : ''" @click="setScore('diningExperience', i)">
						★
					</view>
				</view>
			</view>
		</view>
		
		<view class="section">
			<view class="section-title">文字评价</view>
			<textarea class="textarea" v-model="content" placeholder="请输入您的评价..." maxlength="500"></textarea>
		</view>
		
		<view class="section">
			<view class="section-title">上传图片</view>
			<view class="image-list">
				<view class="image-item" v-for="(img, index) in images" :key="index">
					<image :src="img" mode="aspectFill" @click="previewImage(index)"></image>
					<view class="delete-btn" @click="deleteImage(index)">×</view>
				</view>
				<view class="upload-btn" v-if="images.length < 9" @click="chooseImage">
					<text class="upload-icon">+</text>
					<text>上传图片</text>
				</view>
			</view>
		</view>
	</view>
	
	<view class="submit-btn" @click="submitReview">提交评价</view>
</view>
</template>

<script>
import {getBaseUrl, requestUtil} from "../../utils/requestUtil.js"
export default{
	data() {
		return {
			baseUrl: '',
			orderId: null,
			tableId: null,
			potTasteScore: 5,
			dishFreshnessScore: 5,
			diningExperienceScore: 5,
			content: '',
			images: []
		}
	},
	methods: {
		setScore(type, score) {
			if (type === 'potTaste') {
				this.potTasteScore = score
			} else if (type === 'dishFreshness') {
				this.dishFreshnessScore = score
			} else if (type === 'diningExperience') {
				this.diningExperienceScore = score
			}
		},
		chooseImage() {
			const that = this
			wx.chooseImage({
				count: 9 - this.images.length,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success(res) {
					that.images = that.images.concat(res.tempFilePaths)
				}
			})
		},
		previewImage(index) {
			wx.previewImage({
				current: this.images[index],
				urls: this.images
			})
		},
		deleteImage(index) {
			this.images.splice(index, 1)
		},
		uploadImage(tempFilePath) {
			return new Promise((resolve, reject) => {
				wx.uploadFile({
					url: this.baseUrl + '/order-review/uploadImage',
					filePath: tempFilePath,
					name: 'file',
					header: {
						'Authorization': wx.getStorageSync('token')
					},
					success: (res) => {
						const data = JSON.parse(res.data)
						if (data.code === 0) {
							resolve(data.data.src)
						} else {
							reject(new Error(data.msg || '上传失败'))
						}
					},
					fail: (err) => {
						reject(err)
					}
				})
			})
		},
		async submitReview() {
			if (this.potTasteScore === 0 || this.dishFreshnessScore === 0 || this.diningExperienceScore === 0) {
				wx.showToast({
					title: '请完成评分',
					icon: 'none'
				})
				return
			}
			
			wx.showLoading({title: '提交中...'})
			
			try {
				const uploadedImages = []
				
				for (let i = 0; i < this.images.length; i++) {
					const imgUrl = await this.uploadImage(this.images[i])
					uploadedImages.push(imgUrl)
				}
				
				const reviewData = {
					orderId: this.orderId,
					tableId: this.tableId,
					potTasteScore: this.potTasteScore,
					dishFreshnessScore: this.dishFreshnessScore,
					diningExperienceScore: this.diningExperienceScore,
					content: this.content,
					images: JSON.stringify(uploadedImages)
				}
				
				await requestUtil({
					url: "/order-review/create",
					data: reviewData,
					method: "post"
				})
				
				wx.hideLoading()
				wx.showToast({
					title: '评价成功',
					icon: 'success'
				})
				
				setTimeout(() => {
					wx.navigateBack()
				}, 1500)
			} catch (e) {
				wx.hideLoading()
				console.error('提交评价失败:', e)
				wx.showToast({
					title: '评价失败',
					icon: 'none'
				})
			}
		}
	},
	onLoad(options) {
		this.baseUrl = getBaseUrl()
		this.orderId = options.orderId
		this.tableId = options.tableId
	}
}
</script>

<style>
page {background-color: #f4f4f4;}
.review-view {min-height: 100vh; padding-bottom: 120rpx;}
.review-content {padding: 20rpx;}
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
.score-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}
.score-item:last-child {margin-bottom: 0;}
.score-label {font-size: 28rpx; color: #666;}
.stars {display: flex;}
.star {
	font-size: 40rpx;
	color: #ddd;
	margin-left: 10rpx;
}
.star.active {color: #f7d45f;}
.textarea {
	width: 100%;
	min-height: 200rpx;
	font-size: 28rpx;
	line-height: 1.6;
	color: #333;
}
.image-list {
	display: flex;
	flex-wrap: wrap;
}
.image-item {
	position: relative;
	width: 200rpx;
	height: 200rpx;
	margin-right: 20rpx;
	margin-bottom: 20rpx;
}
.image-item image {
	width: 100%;
	height: 100%;
	border-radius: 10rpx;
}
.delete-btn {
	position: absolute;
	top: -10rpx;
	right: -10rpx;
	width: 40rpx;
	height: 40rpx;
	background-color: #ff4d4f;
	color: white;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 30rpx;
	line-height: 1;
}
.upload-btn {
	width: 200rpx;
	height: 200rpx;
	border: 2rpx dashed #ddd;
	border-radius: 10rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	color: #999;
	font-size: 24rpx;
}
.upload-icon {
	font-size: 60rpx;
	line-height: 1;
	margin-bottom: 10rpx;
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
