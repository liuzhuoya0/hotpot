<template>
<view class="details-view">
	<view class="order-top" v-if="!isChecked">
		<view class="order-remind">
			<view>下单成功，坐等开吃</view>
			<view>菜品已在制作中</view>
		</view>
	</view>
	<view class="order-top checked" v-else>
		<view class="order-remind">
			<view>已结账</view>
			<view>感谢您的光临！</view>
		</view>
	</view>
	<view class="food-list">
		<view class="foot-back">
			<view class="foot-til">
				<text>我的订单</text>
			</view>
			<view v-for="(item,index) in goods_data" :key="index">
				<view class="foot-deta">
					<view>
						<image :src="baseUrl + '/image/dish/' + item.image" mode="aspectFill"></image>
					</view>
					<view class="foot-name">
						<text>{{item.name}}</text>
						<text>{{item.quantity}}{{item.unit}}</text>
					</view>
					<view class="foot-total">¥{{item.total_price}}</view>
				</view>
			</view>
			<view class="total-view">
				<view>共 {{overall}} 份</view>
				<view class="total-price">
					<text>总计</text>
					<text>¥{{totalPrice}}</text>
				</view>
			</view>
		</view>
		<view class="foot-back order-number">
			<text>订单编号：{{other_data.order_no}}</text>
			<text>下单时间：{{other_data.order_time}}</text>
			<text>桌台名称：{{other_data.table_number}}</text>
		</view>
		<view style="height: 300rpx;"></view>
	</view>
	<view class="add-a-dish" v-if="!isChecked">
		<view @click="checkout">结账</view>
	</view>
	<view class="add-a-dish" v-else>
		<view class="go-home-btn" @click="goHome">返回首页</view>
		<view class="review-btn" @click="goToReview">去评价</view>
	</view>
</view>
</template>

<script>
import {getBaseUrl, requestUtil} from "../../utils/requestUtil.js"
const app = getApp()
export default {
	data() {
		return {
			baseUrl: '',
			overall: 2,
			isChecked: false,
			currentOrderId: null,
			other_data: {
				order_no: '202401010001',
				order_time: '',
				table_number: '1',
				sett_amount: 66
			},
			goods_data: []
		}
	},
	computed: {
		totalPrice() {
			return this.goods_data.reduce((sum, item) => sum + item.total_price, 0)
		}
	},
	methods: {
		goToReview() {
			if (this.currentOrderId) {
				wx.navigateTo({
					url: '/pages/order-review/order-review?orderId=' + this.currentOrderId + '&tableId=' + this.other_data.table_number
				})
			}
		},
		async get_menu(orderIdParam) {
			if (orderIdParam) {
				wx.setStorageSync('orderId', orderIdParam)
				this.currentOrderId = orderIdParam
			}
			
			try {
				let table_id = wx.getStorageSync('table_num')
				let orderId = orderIdParam || wx.getStorageSync('orderId')
				table_id = parseInt(table_id) || 1
				
				let res
				if (orderId) {
					res = await requestUtil({url: "/order/getById", data: {orderId: orderId}, method: "get"})
				} else {
					res = await requestUtil({url: "/order/get", data: {table_id: table_id, pay_state: 0}, method: "get"})
				}
				
				if (res && res.goods_list) {
					let res_data = res.goods_list
					
					res_data = res_data.map((item, index) => ({
						...item,
						name: item.name || ('菜品' + (index + 1)),
						unit: item.unit || '份',
						image: item.image || 'default.jpg',
						total_price: item.price || 0
					}))

					this.overall = res_data.length
					const menu = res.menu || {}
					
					this.other_data = {
						order_no: menu.order_no || menu.id || '未知',
						order_time: this.formatTime(menu.order_time),
						table_number: menu.table_number || menu.table_id || table_id,
						sett_amount: menu.sett_amount || menu.total_price || 0
					}
					
					this.goods_data = res_data
					
					if (menu && (menu.pay_state === 1 || menu.pay_state === '1')) {
						this.isChecked = true
					}
					
					if (menu && menu.id) {
						wx.setStorageSync('orderId', menu.id)
						if (!this.currentOrderId) {
							this.currentOrderId = menu.id
						}
					}
				}
				
			} catch(e) {
				console.error('从服务器获取订单详情失败:', e)
			}
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
		async checkout() {
			let orderId = wx.getStorageSync('orderId')
			if (!orderId) {
				wx.showToast({
					title: '订单ID不存在',
					icon: 'none'
				})
				return
			}
			
			wx.showModal({
				title: '确认结账',
				content: '确定要结账吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							await requestUtil({url: "/order/checkout", data: {orderId: orderId}, method: "get"})
							wx.showToast({
								title: '结账成功',
								icon: 'success'
							})
							setTimeout(() => {
								this.isChecked = true
								wx.removeStorageSync('orderId')
							}, 1500)
						} catch(e) {
							console.error('结账失败:', e)
							wx.showToast({
								title: '结账失败',
								icon: 'none'
							})
						}
					}
				}
			})
		},
		goHome() {
			uni.switchTab({
				url: '/pages/home-page/page'
			})
		}
	},
	onLoad(options) {
		this.baseUrl = getBaseUrl()
		this.get_menu(options.orderId)
	}
}
</script>

<style>
page{background-color: #f4f4f4;}
.details-view{position: relative; min-height: 100vh;}
.order-top{
	background:linear-gradient(to bottom, #f7d45f,#f7d562,#f8d561,#f9db76, #f9de80);
	height: 300rpx;
}
.order-top.checked{
	background:linear-gradient(to bottom, #52c41a,#73d13d,#95de64,#b7eb8f, #d9f7be);
}
.order-remind view:nth-child(1){
	font-size: 35rpx;
	font-weight: bold;
	padding-bottom: 20rpx;
}
.order-remind{
	height: 200rpx;
	padding: 50rpx 0 0 50rpx;
}
.checked-view{
	padding: 100rpx 0;
	text-align: center;
}
.checked-view text{
	display: block;
	padding: 15rpx 0;
}
.checked-view text:nth-child(1){
	font-size: 35rpx;
	font-weight: bold;
	color: #52c41a;
}
.food-list{
	margin-top: -100rpx;
	padding: 0 20rpx;
}
.foot-back{
	background-color: #fefefe;
	border-radius: 10rpx;
	padding: 0 20rpx;
	margin-bottom: 30rpx;
}
.foot-til{
	height: 100rpx;
	color: #999999;
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.foot-deta image{
	display: block;
	width: 130rpx;
	height: 130rpx;
	border-radius: 10rpx;
}
.foot-deta{
	display: flex;
	justify-content: space-between;
	height: 130rpx;
	margin: 40rpx 0;
}
.foot-name{
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	padding: 0 20rpx;
	font-size: 30rpx;
}
.foot-name text:nth-child(1){font-size: 31rpx !important;font-weight: bold;}
.foot-name text:nth-child(2){color: #666666;}
.foot-total{font-weight: bold;}
.total-price{
	display: flex;
	align-items: center;
	color: #333333;
	padding-left: 40rpx;
}
.total-price text:nth-child(2){
	font-size: 35rpx;
	font-weight: bold;
	padding-left: 30rpx;
}
.total-view{
	display: flex;
	justify-content: flex-end;
	align-items: center;
	padding: 30rpx 0;
}
.total-view view:nth-child(1){
	color: #999999;
}
.order-number text{
	display: block;
	padding: 15rpx 0;
	font-size: 28rpx;
	color: #999999;
}
.add-a-dish{
	background-color: #fefefe;
	height: 120rpx;
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	box-shadow: 0rpx -1.9rpx 1rpx 1rpx #f9f9f9;
	padding: 0 20rpx;
	z-index: 9;
	display: flex;
	justify-content: flex-end;
	align-items: center;
}
.add-a-dish view{
	background:linear-gradient(to right,#f8da81,#f8d771,#f7d362,#f6cb4a);
	width: 200rpx;
	height: 75rpx;
	line-height: 75rpx;
	text-align: center;
	border-radius: 50rpx;
	font-weight: bold;
	margin-left: 20rpx;
}
.add-a-dish view:first-child{
	margin-left: 0;
}
.go-home-btn{
	background: #f0f0f0 !important;
	color: #666;
}
.review-btn{
	background: linear-gradient(to right, #52c41a, #73d13d) !important;
	color: white;
}
</style>
