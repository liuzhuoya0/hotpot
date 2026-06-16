<template>
<view>
	<view class="welcome">
		<text>欢迎来到</text>
		<text>123火锅店</text>
	</view>
	<!-- 方块 -->
	<view class="people-view">
		<view class="hello">您好，请选择就餐人数</view>
		<view class="table-num">
			<view class="table-num-display">桌号：{{table_number || '请扫码或选择'}}</view>
			<view class="table-actions">
				<view class="scan-btn" @click="scanQrCode">
					<text>扫码选桌</text>
				</view>
				<view class="select-btn" @click="showTableSelect">
					<text>手动选桌</text>
				</view>
			</view>
		</view>
		<!-- 桌号选择弹窗 -->
		<view class="table-select-popup" v-if="showTablePopup">
			<view class="popup-mask" @click="showTablePopup = false"></view>
			<view class="popup-content">
				<view class="popup-title">请选择桌号</view>
				<view class="table-legend">
					<view class="legend-item">
						<view class="legend-color free"></view>
						<text>空闲</text>
					</view>
					<view class="legend-item">
						<view class="legend-color occupied"></view>
						<text>占用</text>
					</view>
				</view>
				<view class="table-list">
					<view v-for="(table, index) in tableList" :key="index" 
						:class="['table-item', getTableClass(table)]" 
						@click="selectTable(table)">
						{{table.number}}
						<view v-if="table.state !== 0" class="table-status">{{getTableStatusText(table.state)}}</view>
					</view>
				</view>
			</view>
		</view>
		<!-- 滑动 -->
		<view class="table-num table-scroll">
			<scroll-view scroll-x="true" class="scroll-view_H" :enable-flex="true">
				<view class="table-block">
					<block v-for="(item,index) in people" :key="index">
					<view @click="choIce(index,item)" :class="{activetext: index === num}">{{item}}</view>
					</block>
				</view>
			</scroll-view>
		</view>
		<!-- 按钮 -->
		<view class="start-diancan" :class="[num > -1 && table_number ? 'start-activ' : '']" @click="stArt()">开始点餐</view>
	</view>
</view>
</template>

<script>
const baseUrl = 'http://localhost:81'

export default {
	data() {
		return {
			num: -1,
			people: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
			table_number: null,
			showTablePopup: false,
			tableList: []
		}
	},
	methods: {
		getTableList() {
			wx.request({
				url: baseUrl + '/table/listAll',
				method: 'GET',
				success: (res) => {
					if (res.data.code === 0) {
						this.tableList = res.data.tableListAll || []
					}
				},
				fail: (err) => {
					console.error('获取桌位列表失败:', err)
				}
			})
		},

		getTableClass(table) {
			let classes = []
			if (this.table_number === table.number) {
				classes.push('active')
			}
			if (table.state === 0) {
				classes.push('free')
			} else if (table.state === 1 || table.state === 2) {
				classes.push('occupied')
			}
			return classes.join(' ')
		},

		getTableStatusText(state) {
			if (state === 0) return '空闲'
			if (state === 1 || state === 2) return '占用'
			return ''
		},

		choIce(index, item) {
			this.num = index
			wx.setStorageSync('number_of_diners', item)
		},

		scanQrCode() {
			wx.scanCode({
				success: (res) => {
					console.log('扫码结果：', res)
					let qrContent = res.result
					let tableNum = this.parseTableNumber(qrContent)
					if (tableNum) {
						let table = this.tableList.find(t => t.number === tableNum)
						if (table && (table.state === 1 || table.state === 2)) {
							wx.showToast({
								title: '该桌位已被占用',
								icon: 'none'
							})
							return
						}
						this.table_number = tableNum
						wx.setStorageSync('table_num', tableNum)
						wx.showToast({
							title: '桌号设置成功：' + tableNum,
							icon: 'success'
						})
					} else {
						wx.showToast({
							title: '二维码格式不正确',
							icon: 'error'
						})
					}
				},
				fail: (err) => {
					console.log('扫码失败：', err)
				}
			})
		},

		parseTableNumber(content) {
			let match = content.match(/table[:=](\d+)/i)
			if (match && match[1]) {
				return match[1].padStart(3, '0')
			}
			match = content.match(/(\d+)/)
			if (match && match[1]) {
				return match[1].padStart(3, '0')
			}
			return null
		},

		showTableSelect() {
			this.getTableList()
			this.showTablePopup = true
		},

		selectTable(table) {
			if (table.state === 1 || table.state === 2) {
				wx.showToast({
					title: '该桌位已被占用',
					icon: 'none'
				})
				return
			}
			this.table_number = table.number
			wx.setStorageSync('table_num', table.number)
			this.showTablePopup = false
		},

		stArt() {
			if (this.num <= -1) return false
			if (!this.table_number) {
				wx.showToast({
					title: '请先选择桌号',
					icon: 'none'
				})
				return false
			}
			let table = this.tableList.find(t => t.number === this.table_number)
			if (table && (table.state === 1 || table.state === 2)) {
				wx.showToast({
					title: '该桌位已被占用',
					icon: 'none'
				})
				return
			}
			wx.reLaunch({
				url: '/pages/home-page/page'
			})
		}

	},
	onLoad(e) {
		console.log("e:" + e)
		this.getTableList()
		if (e.number) {
			this.table_number = e.number
			wx.setStorageSync('table_num', e.number)
		}
	}
}
</script>

<style>
page {
	background-image: url('http://localhost:81/image/qrcode/beijing-a.jpg');
	background-attachment: fixed;
	background-repeat: no-repeat;
	background-size: cover;
}

.welcome {
	color: #FFFFFF;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding-top: 270rpx;
}

.welcome text {
	display: block;
	font-size: 50rpx;
}

.welcome text:nth-child(1) {
	font-family: monospace;
	padding-bottom: 20rpx;
}

.people-view {
	background-color: #FFFFFF;
	position: fixed;
	left: 30rpx;
	right: 30rpx;
	bottom: 100rpx;
	height: 650rpx;
	border-radius: 15rpx;
}

.hello {
	height: 80rpx;
	font-size: 35rpx;
	font-weight: bold;
	line-height: 80rpx;
	padding-left: 20rpx;
}

.table-num {
	font-size: 30rpx;
	padding-left: 20rpx;
}

.table-num-display {
	font-size: 30rpx;
	padding-bottom: 10rpx;
}

.table-actions {
	display: flex;
	gap: 20rpx;
	margin-bottom: 20rpx;
}

.scan-btn,
.select-btn {
	padding: 15rpx 30rpx;
	background-color: #f8da81;
	border-radius: 10rpx;
	font-size: 28rpx;
}

.table-scroll {
	margin-top: 20rpx;
}

.scroll-view_H {
	white-space: nowrap;
	width: 100%;
	height: 75rpx;
}

.table-block {
	display: flex;
	align-items: center;
}

.table-block view {
	height: 75rpx;
	line-height: 75rpx;
	text-align: center;
	background-color: #f7f8f9;
	margin-right: 25rpx;
	padding: 0 70rpx;
	border-radius: 15rpx;
}

.start-diancan {
	height: 90rpx;
	line-height: 90rpx;
	background-color: #fdf4d7;
	color: #c2c2c2;
	text-align: center;
	margin: 80rpx 20rpx 0 20rpx;
	border-radius: 15rpx;
}

.activetext {
	background-color: #f9dd89 !important;
}

.start-activ {
	color: #000000 !important;
	background: linear-gradient(to right, #f8da81, #f8d771, #f7d362, #f6cb4a) !important;
}

.table-select-popup {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 999;
}

.popup-mask {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.5);
}

.popup-content {
	position: absolute;
	left: 50%;
	top: 50%;
	transform: translate(-50%, -50%);
	background-color: #FFFFFF;
	width: 600rpx;
	border-radius: 20rpx;
	padding: 40rpx;
}

.popup-title {
	font-size: 35rpx;
	font-weight: bold;
	text-align: center;
	margin-bottom: 20rpx;
}

.table-legend {
	display: flex;
	justify-content: center;
	gap: 30rpx;
	margin-bottom: 20rpx;
}

.legend-item {
	display: flex;
	align-items: center;
	gap: 10rpx;
	font-size: 24rpx;
}

.legend-color {
	width: 30rpx;
	height: 30rpx;
	border-radius: 6rpx;
}

.legend-color.free {
	background-color: #67c23a;
}

.legend-color.occupied {
	background-color: #f56c6c;
}

.table-list {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
	justify-content: center;
}

.table-item {
	width: 120rpx;
	height: 120rpx;
	line-height: 120rpx;
	text-align: center;
	background-color: #f7f8f9;
	border-radius: 10rpx;
	font-size: 30rpx;
	position: relative;
}

.table-item.free {
	background-color: #e8f5e9;
	color: #2e7d32;
}

.table-item.occupied {
	background-color: #ffebee;
	color: #c62828;
}

.table-item.active {
	background-color: #f8da81 !important;
	color: #000 !important;
}

.table-status {
	position: absolute;
	bottom: 5rpx;
	left: 0;
	right: 0;
	font-size: 20rpx;
	line-height: 20rpx;
}
</style>
