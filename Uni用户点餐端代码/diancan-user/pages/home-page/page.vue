<template>
	<view>
		<!-- 打烊提示 -->
		<view v-if="!shopIsOpen" class="closed-view">
			<view class="closed-icon">
				<image src="/static/tab/guanbi.png" mode="widthFix"></image>
			</view>
			<view class="closed-text">门店已打烊</view>
			<view class="closed-desc">暂不接受点餐，请稍后再来</view>
			<view class="closed-hours" v-if="shopInfo.openTime && shopInfo.closeTime">
				营业时间：{{shopInfo.openTime}} - {{shopInfo.closeTime}}
			</view>
		</view>
		<view v-else>
		<!-- 顶部 -->
		<view class="top-view">
			<view class="people-text">{{number_people}}人就餐</view>
			<view class="search-input-view">
				<image src="/static/tab/sousuo.png" class="search-icon"></image>
				<input 
					class="search-input" 
					placeholder="搜索菜品" 
					v-model="searchKeyword" 
					@input="handleSearch"
					placeholder-class="search-placeholder">
				</input>
				<view v-if="searchKeyword" class="clear-btn" @click="clearSearch">
					<text class="clear-text">×</text>
				</view>
			</view>
		</view>
		 <view class="order-view">
			<view class="commodity">
				<!-- 左 -->
				<view class="order-left">
					<scroll-view scroll-y="true" class="scroll-Hei" :scroll-with-animation="true" :enhanced="true" :show-scrollbar="false">
						<view class="itemize-text" :class="{active: trigger == -1}" @click="itemIze(-1,'category-hot')">
							<text>🔥 热门</text>
							<text v-if="getHotCategoryQuantity() > 0">{{getHotCategoryQuantity()}}</text>
						</view>
						<block v-for="(item,index) in itemize" :key="index">
							<view class="itemize-text" :class="{active: index == trigger}" @click="itemIze(index,'category-'+item.cid)">
								<text>{{item.label}}</text>
								<text v-if="item.sele_quantity > 0">{{item.sele_quantity}}</text>
							</view>
						</block>
					</scroll-view>
				</view> 
				<!-- 右 -->
				<view class="order-right">
					<scroll-view scroll-y="true" class="scroll-Hei"  :scroll-with-animation="true" :enhanced="true" :show-scrollbar="false" :scroll-into-view="scroll_into" @scroll="scroLl">
						<!-- 搜索结果 -->
						<view v-if="searchKeyword" id="search-result" class="rig-height">
							<view class="classif">🔍 搜索结果</view>
							<view v-if="searchResults.length === 0" class="no-search-result">
								<text>没有找到相关菜品</text>
							</view>
							<view class="classif-goods" v-for="(itemgood,good_index) in searchResults" :key="good_index" @click="popup_search_item(true,itemgood)">
								<view class="goods-image"><image :src="baseUrl+'/image/dish/'+itemgood.image" mode="aspectFill"></image></view>
								<view class="goods-Price">
									<view class="goods-name">
										<text class="Bold">{{itemgood.name}}</text>
										<text class="Thinning">已售 {{itemgood.monthlysale}}</text>
									</view>
									<view class="unit-price">
										<text class="Symbol">¥</text>
										<text class="Bold">{{itemgood.price}}</text>
										<text class="Thinning">/{{itemgood.unit || '份'}}</text>
									</view>
								</view>
								<view class="quantity">
									<view><image v-if="itemgood.quantity > 0" src="/static/tab/jianhao.png" mode="widthFix" @click.stop="reduce_search_item(itemgood)"></image></view>
									<view><text v-if="itemgood.quantity > 0">{{itemgood.quantity}}</text></view>
									<view><image src="/static/tab/jia.png" mode="widthFix" @click.stop="plus_search_item(itemgood)"></image></view>
								</view>
							</view>
						</view>
						<!-- 正常分类菜品 -->
						<view v-else>
							<view v-if="trigger === -1" id="category-hot" class="rig-height">
								<view class="classif">🔥 热门推荐</view>
								<view class="classif-goods" v-for="(itemgood,good_index) in hotDishes" :key="good_index" @click="popup_item(true,-1,good_index,-1,itemgood)">
									<view class="goods-image"><image :src="baseUrl+'/image/dish/'+itemgood.image" mode="aspectFill"></image></view>
									<view class="goods-Price">
										<view class="goods-name">
											<text class="Bold">{{itemgood.name}}</text>
											<text class="Thinning">已售 {{itemgood.monthlysale}}</text>
										</view>
										<view class="unit-price">
											<text class="Symbol">¥</text>
											<text class="Bold">{{itemgood.price}}</text>
											<text class="Thinning">/{{itemgood.unit || '份'}}</text>
										</view>
									</view>
									<view class="quantity">
										<view><image v-if="itemgood.quantity > 0" src="/static/tab/jianhao.png" mode="widthFix" @click.stop="reduce(-1,good_index,-1,itemgood)"></image></view>
										<view><text v-if="itemgood.quantity > 0">{{itemgood.quantity}}</text></view>
										<view><image src="/static/tab/jia.png" mode="widthFix" @click.stop="plus(-1,good_index,-1,itemgood)"></image></view>
									</view>
								</view>
							</view>
							<block v-for="(item,index) in goods" :key="index">
							<view :id="'category-'+item.cid" class="rig-height">
								<view class="classif">{{item.label}}</view>
								<view class="classif-goods" v-for="(itemgood,good_index) in item.dishList" :key="good_index" @click="popup_item(true,index,good_index,item.cid,itemgood)">
									<view class="goods-image"><image :src="baseUrl+'/image/dish/'+itemgood.image" mode="aspectFill"></image></view>
									<view class="goods-Price">
										<view class="goods-name">
											<text class="Bold">{{itemgood.name}}</text>
											<text class="Thinning">已售 {{itemgood.monthlysale}}</text>
										</view>
										<view class="unit-price">
								<text class="Symbol">¥</text>
								<text class="Bold">{{itemgood.price}}</text>
								<text class="Thinning">/{{itemgood.unit || '份'}}</text>
							</view>
									</view>
									<view class="quantity">
										<view><image v-if="itemgood.quantity > 0" src="/static/tab/jianhao.png" mode="widthFix" @click.stop="reduce(index,good_index,item.cid,itemgood)"></image></view>
										<view><text v-if="itemgood.quantity > 0">{{itemgood.quantity}}</text></view>
										<view><image src="/static/tab/jia.png" mode="widthFix" @click.stop="plus(index,good_index,item.cid,itemgood)"></image></view>
									</view>
								</view>
							</view>
							</block>
						</view>
						<view style="height: 400rpx;"></view>
					</scroll-view>
				</view>
			</view>
			<!-- 底部 -->
			<view class="order-bottom" @click="pop_Shopping()" :style="{'padding-bottom': Modelmes ? '68rpx' : ''}">
				<view class="Shopping" style="width: 115rpx;">
					<view class="Shopping-left">
						<image src="/static/tab/gouwuche.png" mode="widthFix"></image>
					</view>
					<view class="Shopping-number" v-if="total_quantity > 0">{{total_quantity}}</view>
				</view>
				<view class="Shopping-title" v-if="total_quantity > 0">已点{{total_quantity}}份菜品</view>
				<view class="place-order" @click.stop="placean_order()">
					<button plain="true" open-type="getUserInfo">选好了</button>
				</view>
			</view>
		</view>
	</view>
	<!-- 购物车 -->
	<Cart v-if="card" :shopping_card="shopping_card"></Cart>
	<!-- 直接在父组件写详情弹窗，绕过子组件问题！ -->
	<view class="details-back" v-if="popupitem">
		<view class="goods-details coup-anim">
			<view class="goods-image">
				<image :src="'http://localhost:81/image/dish/'+currentDish.image" mode="aspectFit"></image>
				<image src="/static/tab/guanbi.png" mode="widthFix" @click="popup_item(false)"></image>
			</view>
			<view class="details-padd">
				<view class="details-name">{{currentDish.name}}</view>
				<view class="details-Thinning">已售 {{currentDish.monthlysale}}</view>
				<view class="details-info" v-if="currentDish.origin">
					<view class="info-label">食材产地：</view>
					<view class="info-value">{{currentDish.origin}}</view>
				</view>
				<view class="details-info" v-if="currentDish.detail">
					<view class="info-label">菜品详情：</view>
					<view class="info-value">{{currentDish.detail}}</view>
				</view>
				<view class="describe">
					<view class="details-unit-price">
						<text>¥</text>
						<text>{{currentDish.unitprice}}</text>
						<text>/{{currentDish.unit}}</text>
					</view>
					<view class="details-quantity">
						<view><image v-if="currentDish.quantity > 0" src="/static/tab/jianhao.png" @click="reduce(currentIndex,currentGoodIndex,currentCid,currentDish)"></image></view>
						<view><text v-if="currentDish.quantity > 0">{{currentDish.quantity}}</text></view>
						<view><image src="/static/tab/jia.png" @click="plus(currentIndex,currentGoodIndex,currentCid,currentDish)"></image></view>
					</view>
				</view>
			</view>
		</view>
	</view>
	<!-- 骨架屏 -->
	<Home v-if="exist"></Home>
	
		</view>
	</view>
</template>

<script>
	import {getBaseUrl, requestUtil} from "../../utils/requestUtil.js"
const app = getApp()
const {Modelmes} = app.globalData
// 骨架屏
import Home from '../skeleton-view/home.vue'
// 小程序端一次性只返回20条数据；云函数段100条；外部nodejs，java返回10条
// 引入购物车子组件
import Cart from './components/shopping-cart.vue'
// 引入单个商品弹出
import Details from './components/goods-details.vue'
// 订单编号
import {Code} from '../../config/order.js'
// 计算当天的销售额
import {analysis} from '../../config/Date_analysis.js'
const db = wx.cloud.database()
const _ = db.command
const good_collect= db.collection('order-data')
const dishes_data = db.collection('dishes-data')
export default{
	components:{Cart,Details,Home},
	data() {
		return {
			baseUrl:'',
			exist:true,
			Modelmes,
			itemize:[],//类目
			trigger:-1,//类目选中的值，-1表示热门
			goods:[],//所有菜品
			hotDishes:[],//热门菜品（销量前三）
			heightset:[],//存储右边每一个分类菜品的高度
			tophei:0,//滚动时距离顶部的高度
			scroll_into:'',
			card:false,//购物车隐藏
			shopping_card:[],//购物车里的数据
			popupitem:false,//单个商品弹出框隐藏
			pro_details:{},//单个商品弹出框里的数据
			currentDish:{},//当前菜品
			currentIndex:0,
			currentGoodIndex:0,
			currentCid:0,
			tmplIds:'FANEJh9NPNhJrLpqQx7UhNerntR5GwEsLKK-95tuvNM',//模板id
			number_people:0,//用餐人数
			shopIsOpen:true,//门店是否营业
			shopInfo:{},//门店信息
			searchKeyword:'',//搜索关键词
			searchResults:[]//搜索结果
		}
	},
	methods:{
		// 加载门店信息
		async loadShopInfo() {
			console.log('开始加载门店信息...')
			try {
				const res = await requestUtil({url: "/shopInfo", method: "get"})
				console.log('门店信息接口响应:', res)
				if (res && res.code === 0 && res.shopInfo) {
					this.shopInfo = res.shopInfo
					this.shopIsOpen = this.shopInfo.isOpen !== undefined ? this.shopInfo.isOpen : true
					console.log('设置门店状态:', this.shopIsOpen)
					wx.setStorageSync('shopInfo', JSON.stringify(this.shopInfo))
				}
			} catch (e) {
				console.error('从服务器加载门店信息失败', e)
				try {
					const shopInfoStr = wx.getStorageSync('shopInfo')
					if (shopInfoStr) {
						this.shopInfo = JSON.parse(shopInfoStr)
						this.shopIsOpen = this.shopInfo.isOpen !== undefined ? this.shopInfo.isOpen : true
					}
				} catch (err) {
					console.error('从本地加载门店信息失败', err)
				}
			}
		},
		// 点击类目加上背景色
		itemIze(index,cid){
			this.trigger = index
			this.scroll_into = cid
			setTimeout(()=>{
				this.scroll_into = ''
			},200)
		},
		// 获取热门分类的数量
		getHotCategoryQuantity() {
			let count = 0
			this.hotDishes.forEach(item => {
				count += item.quantity || 0
			})
			return count
		},
		// 搜索菜品
		handleSearch() {
			if (!this.searchKeyword.trim()) {
				this.searchResults = []
				return
			}
			const keyword = this.searchKeyword.toLowerCase().trim()
			const results = []
			// 搜索热门菜品
			this.hotDishes.forEach(dish => {
				if (dish.name.toLowerCase().includes(keyword)) {
					results.push(dish)
				}
			})
			// 搜索所有分类菜品
			this.goods.forEach(category => {
				category.dishList.forEach(dish => {
					if (dish.name.toLowerCase().includes(keyword)) {
						// 避免重复添加
						const exists = results.find(r => r.id === dish.id)
						if (!exists) {
							results.push(dish)
						}
					}
				})
			})
			this.searchResults = results
		},
		// 清空搜索
		clearSearch() {
			this.searchKeyword = ''
			this.searchResults = []
		},
		// 搜索结果菜品点击
		popup_search_item(value, itemgood) {
			this.popupitem = value
			if (value && itemgood) {
				this.currentDish = {
					...itemgood,
					unitprice: itemgood.price
				}
			}
		},
		// 搜索结果菜品减
		reduce_search_item(itemgood) {
			const QU = itemgood.quantity - 1
			itemgood.quantity = QU
			// 同步更新原始数据中的数量
			this.syncDishQuantity(itemgood.id, QU)
			const arr = {
				image: itemgood.image,
				name: itemgood.name,
				unitprice: itemgood.price,
				quantity: QU,
				unit: itemgood.unit,
				total_price: itemgood.price * QU,
				id: itemgood.id,
				cid: itemgood.cid,
				good_index: itemgood.good_index
			}
			this.shopping_Cart(arr)
		},
		// 搜索结果菜品加
		plus_search_item(itemgood) {
			const QU = itemgood.quantity + 1
			itemgood.quantity = QU
			// 同步更新原始数据中的数量
			this.syncDishQuantity(itemgood.id, QU)
			const arr = {
				image: itemgood.image,
				name: itemgood.name,
				unitprice: itemgood.price,
				quantity: QU,
				unit: itemgood.unit,
				total_price: itemgood.price * QU,
				id: itemgood.id,
				cid: itemgood.cid,
				good_index: itemgood.good_index
			}
			this.shopping_Cart(arr)
		},
		// 同步更新原始菜品数据中的数量
		syncDishQuantity(id, quantity) {
			// 更新热门菜品
			const hotIndex = this.hotDishes.findIndex(d => d.id === id)
			if (hotIndex !== -1) {
				this.hotDishes[hotIndex].quantity = quantity
			}
			// 更新分类菜品
			this.goods.forEach(category => {
				const dishIndex = category.dishList.findIndex(d => d.id === id)
				if (dishIndex !== -1) {
					category.dishList[dishIndex].quantity = quantity
				}
			})
		},
		// 右边菜品滚动时触发
		scroLl(event){
			// console.log(event.detail.scrollTop)
			let scrollTop = event.detail.scrollTop
			if(scrollTop >= this.tophei){//上拉
				// 当前分类商品的高度小于滚动高度时跳转下一个分类
				if(scrollTop >= this.heightset[this.trigger + 1]){
					this.trigger += 1
				}
			}else{//下拉
				// 当前分类商品的高度大于滚动高度时跳转下一个分类
				if(this.trigger > -1 && scrollTop < this.heightset[this.trigger]){
					this.trigger -= 1
				}
			}
			this.tophei = scrollTop
		},
		
		// 单个商品+
		plus(index,good_index,cid,itemgood){
			const {quantity,image,name,unitprice,unit,id} = itemgood
			const QU = quantity + 1
			if (index === -1) {
				this.$set(this.hotDishes[good_index],'quantity',QU)
			} else {
				this.$set(this.goods[index].dishList[good_index],'quantity',QU)
			}
			const arr = {image,name,unitprice,quantity:QU,unit,total_price:unitprice * QU,id,cid,good_index}
			this.shopping_Cart(arr)
		},
		
		// 单个商品-
		reduce(index,good_index,cid,itemgood){
			const {quantity,image,name,unitprice,unit,id} = itemgood
			const QU = quantity - 1
			if (index === -1) {
				this.$set(this.hotDishes[good_index],'quantity',QU)
			} else {
				this.$set(this.goods[index].dishList[good_index],'quantity',QU)
			}
			const arr = {image,name,unitprice,quantity:QU,unit,total_price:unitprice * QU,id,cid,good_index}
			this.shopping_Cart(arr)
		},
		
		// 添加进购物车的商品
		shopping_Cart(arr){
			// 一：购物车没有数据，空数组：
				// 直接添加进数据
			// 二：购物车里有数据
				// 1.没有相同的菜品存在
				// 2.有相同的菜品存在
			if(this.shopping_card.length == 0){
				// 一：购物车没有数据，空数组：
				this.shopping_card.push(arr)
			}else{
				// 二：购物车里有数据
				let itemindex = this.shopping_card.findIndex(item => item.id == arr.id)
				if(itemindex == -1){
					// 没有相同的菜品存在
					this.shopping_card.unshift(arr)
				}else{
					this.$set(this.shopping_card[itemindex],'quantity',arr.quantity)
					this.$set(this.shopping_card[itemindex],'total_price',arr.total_price)
				}
			}
			console.log(this.shopping_card)
			this.qunint_of_goods()
		},
		
		// 计算左边各分类下添加了多少菜品
		qunint_of_goods(){
			let array = this.shopping_card
			let res = {}
			array.forEach(item=>{
				if(res[item.cid]){
					res[item.cid] += item.quantity
				}else{
					res[item.cid] = item.quantity
				}
			})
			let M = []
			for(let k in res){
				M.push({cid:k,value:res[k]})
			}
			M.forEach(item=>{
				let res_index = this.itemize.findIndex(iteming => iteming.cid == item.cid)
				this.$set(this.itemize[res_index],'sele_quantity', item.value)
			})
		},
		
		
		//购物车商品加减数量
		shopping_Cart_add_sub(index,QU,id,cid,good_index,unitprice){
			this.$set(this.shopping_card[index],'quantity',QU)
			this.$set(this.shopping_card[index],'total_price',QU * unitprice)
			// 根据id唯一标识查询商品的数量做到商品加减同步
			if (cid === -1) {
				// 热门分类
				const itemIndex = this.hotDishes.findIndex(item => item.id === id)
				if (itemIndex !== -1) {
					this.$set(this.hotDishes[itemIndex], 'quantity', QU)
				}
			} else {
				// 普通分类
				const itemcid = this.goods.findIndex(item=> item.cid == cid)
				this.$set(this.goods[itemcid].dishList[good_index],'quantity',QU)
			}
			this.qunint_of_goods()
		},
		
		// 清空已点：被子组件调用
		empty_data(){
			this.shopping_card = []
			this.itemize.forEach(item=>{item.sele_quantity = 0})
			this.goods.forEach(item=>{
				item.dishList.forEach(T=>{
					T.quantity = 0
				})
			})
			this.hotDishes.forEach(item=>{
				item.quantity = 0
			})
			this.pop_Shopping(false)
		},
		
		// 弹出或隐藏单个商品弹出框
		popup_item(value = true,index,good_index,cid,itemgood){
			this.popupitem = value
			if (value && itemgood) {
				this.currentIndex = index
				this.currentGoodIndex = good_index
				this.currentCid = cid
				this.currentDish = {
					...itemgood,
					unitprice: itemgood.price
				}
				console.log('=== 当前菜品数据 ===:', this.currentDish)
				console.log('=== 图片路径 ===:', 'http://localhost:81/image/dish/' + this.currentDish.image)
			}
		},
		// 显示购物车组件
		pop_Shopping(value = true){
			this.card = value
		},

		// 请求数据
		async dishEs(){
			try {
				console.log("开始请求数据...")
				const res=await requestUtil({url:"/category/listAll",method:"get"})
				console.log("分类数据:", res)
				const res2=await requestUtil({url:"/dish/listAll",method:"get"})
				console.log("菜品数据:", res2)
				
				if (res && res.categoryListAll) {
					this.itemize = res.categoryListAll//类目
				} else {
					this.itemize = []
					console.error("分类数据格式错误")
					uni.showToast({
						icon:'error',
						title:'分类数据错误',
						duration:2000
					})
				}
				
				if (res2 && res2.allDish) {
					this.goods = res2.allDish//所有菜品
					// 计算热门菜品（销量前三）
					let allDishes = []
					this.goods.forEach(category => {
						if (category.dishList) {
							category.dishList.forEach(dish => {
								allDishes.push({...dish, cid: category.cid})
							})
						}
					})
					// 按销量排序，取前三名
					allDishes.sort((a, b) => (b.monthlysale || 0) - (a.monthlysale || 0))
					this.hotDishes = allDishes.slice(0, 3).map(dish => ({...dish, quantity: 0}))
				} else {
					this.goods = []
					this.hotDishes = []
					console.error("菜品数据格式错误")
					uni.showToast({
						icon:'error',
						title:'菜品数据错误',
						duration:2000
					})
				}
				
				this.$nextTick(()=>{
					this.goods_height()
				})
			} catch (error) {
				console.error("请求数据失败:", error)
				this.itemize = []
				this.goods = []
				this.hotDishes = []
				this.exist = false
				uni.showToast({
					icon:'error',
					title:'连接服务器失败',
					duration:3000
				})
			}
		},
		// 计算右边每个分类菜品的高度
		goods_height(){
			this.heightset = []
			let cate_height = 0
			const query = wx.createSelectorQuery()
			query.selectAll('.rig-height').boundingClientRect()
			query.exec((res)=>{
				if (res && res[0] && res[0].length > 0) {
					res[0].forEach((item)=>{
						cate_height += item.height
						this.heightset.push(cate_height)
					})
				}
				// 无论是否有数据都隐藏骨架屏
				this.exist = false
			})
		},
		
		// 弹出订阅消息弹窗
		placean_order(){
			// 检查购物车是否为空
			if (this.total_quantity === 0) {
				wx.showToast({
					icon: 'none',
					title: '购物车为空，请先添加菜品',
					duration: 2000
				})
				return
			}
			
			// 检查门店状态
			if (!this.shopIsOpen) {
				wx.showToast({
					icon: 'none',
					title: '门店已打烊，暂不接受点餐',
					duration: 2000
				})
				return
			}
			
			// 消息弹窗
			wx.requestSubscribeMessage({
			  tmplIds: [this.tmplIds],
			  success:(res)=>{
				  this.sub_database()
			  },
			  fail:(err)=>{
				  this.sub_database()
			  }
			})
		},
		
		
		// 提交订单
		async sub_database(){
			wx.showLoading({title: '正在下单',mask:true})
			// 1.过滤掉总价为0的购物车里的菜品;filter:过滤
			let filteredGoods = this.shopping_card.filter(item => item.total_price != 0)
			console.log('过滤后的购物车数据:', filteredGoods)
			// 2.计算总价
			let sett_amount = 0
			filteredGoods.forEach(item => {sett_amount += item.total_price})
			// 3.转换goods_list格式，确保与后端OrderDetail实体类匹配
			let goods_list = filteredGoods.map(item => ({
				name: item.name,
				quantity: item.quantity.toString(), // 后端期望String类型
				unit: item.unit || '份',
				image: item.image,
				price: item.total_price, // 后端使用price字段
				dish_id: item.id // 菜品ID
			}))
			console.log('转换后的goods_list:', goods_list)
			// 取出本地缓存的桌号和用餐人数
			let table_number = wx.getStorageSync('table_num') || '1' // 默认桌号为1
			let number_of_diners = wx.getStorageSync('number_of_diners') || '1' // 默认用餐人数为1
			
			// 保存桌号到本地缓存，确保后续查询能够使用
			wx.setStorageSync('table_num', table_number)
			
			let order = {
				table_number,//桌号
				number_of_diners,//用餐人数
				sett_amount,
				order_no:Code(),
				transac_status:'unsettled',//结账状态
				order_receiving:'mis_orders',//接单状态
				goods_list: goods_list
			}
			console.log('提交的订单数据:', order)
			try {
				const res2=await requestUtil({url:"/order/create",data:order,method:"post"})
				console.log('订单创建结果:', res2)
				if(res2.code==0){
					// 保存订单ID和订单数据到本地缓存
					wx.setStorageSync('orderId', res2.orderId);
					wx.setStorageSync('orderData', order);
					console.log('保存订单ID成功:', res2.orderId);
					console.log('保存订单数据成功:', order);
					// 跳转到订单详情页面，同时携带订单ID作为参数
					wx.redirectTo({
					  url: '/pages/order-details/details?orderId=' + res2.orderId
					})
				}
			} catch (error) {
				console.error('订单创建失败:', error)
				wx.showToast({
					icon:'error',
					title:'订单创建失败',
					duration:3000
				})
			} finally {
				wx.hideLoading()
			}
		},
		
		
		// 推送订单提醒
		push_message(){
			var pubsub = this.goeasy.pubsub;
			pubsub.publish({
			    channel: "my_channel",//替换为您自己的channel
			    message: "小程序端来的",//替换为您想要发送的消息内容
			    onSuccess:()=>{
			        console.log("消息发布成功。");
			    },
			    onFailed:(error)=> {
			        console.log("消息发送失败，错误编码："+error.code+" 错误信息："+error.content);
			    }
			});
		},
		
		// 我的订单
		my_order(){
			wx.navigateTo({
				url:'/pages/my-order/my-order'
			})
		},
		// 服务请求
		goToServiceRequest(){
			wx.navigateTo({
				url:'/pages/service-request/service-request'
			})
		}
	},
	onLoad() {
		// 获取用餐人数
		this.number_people = wx.getStorageSync('number_of_diners')
		this.baseUrl=getBaseUrl()
		// 加载门店信息
		this.loadShopInfo()
		this.dishEs()
	},
	computed:{
		// 计算购物车的菜品总数
		total_quantity(){
			// var 
			// let
			// const
			let quantity = 0
			this.shopping_card.forEach(item=>{
				quantity += item.quantity
			})
			return quantity
		}
	}
}
</script>

<style scoped>
.closed-view{
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	height: 100vh;
	background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
	padding: 40rpx;
}

.closed-icon{
	width: 200rpx;
	height: 200rpx;
	background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 40rpx;
	box-shadow: 0 10px 30px rgba(239, 68, 68, 0.3);
}

.closed-icon image{
	width: 100rpx;
	height: 100rpx;
}

.closed-text{
	font-size: 48rpx;
	font-weight: bold;
	color: #1e293b;
	margin-bottom: 20rpx;
}

.closed-desc{
	font-size: 28rpx;
	color: #64748b;
	margin-bottom: 40rpx;
}

.closed-hours{
	font-size: 26rpx;
	color: #475569;
	background: #ffffff;
	padding: 20rpx 40rpx;
	border-radius: 50rpx;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.top-view{
	background:linear-gradient(to bottom, #f7d45f,#f7d562,#f8d561,#f9db76, #f9de80);
	height: 120rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 0 20rpx;
	position: fixed;
	top: 0;
	right: 0;
	left: 0;
}

.people-text{
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.top-view image{
	display: block;
	width: 35rpx;
	height: 35rpx;
}
.top-view-flex{
	display: flex;
	align-items: center;
}

.search-input-view{
	background: #fff;
	border-radius: 30rpx;
	padding: 8rpx 16rpx;
	display: flex;
	align-items: center;
	box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.1);
	width: 50%;
	margin-left: 16rpx;
}

.search-icon{
	width: 24rpx;
	height: 24rpx;
	margin-right: 8rpx;
}

.search-input{
	flex: 1;
	font-size: 24rpx;
	color: #333;
}

.search-placeholder{
	color: #999;
}

.clear-btn{
	width: 32rpx;
	height: 32rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-left: 8rpx;
	background: #f0f0f0;
	border-radius: 50%;
}

.clear-text{
	font-size: 20rpx;
	color: #999;
	line-height: 1;
}

.no-search-result{
	padding: 80rpx 0;
	text-align: center;
	color: #999;
	font-size: 28rpx;
}
.my-order-btn{
	background-color: rgba(255,255,255,0.3);
	padding: 8rpx 20rpx;
	border-radius: 30rpx;
	font-size: 26rpx;
	margin-right: 20rpx;
}
.top-search{padding-right: 50rpx;}
/* 点餐界面 */
.order-view{margin-top: 120rpx;}
.commodity{
	display: flex;
	position: fixed;
	top: 120rpx;
	left: 0;
	right: 0;
	}
.order-left{
	background-color: #fafafa;
	width: 150rpx;
	overflow-y: auto;
}
.itemize-text{
	font-size: 27rpx;
	padding: 30rpx 10rpx;
	display: flex;
	align-items: center;
	color: #797979;
}
.itemize-text text:nth-child(1){flex: 1;}
.itemize-text text:nth-child(2){
	background-color: #eb5941;
	border-radius: 50%;
	font-size: 20rpx;
	color: #FFFFFF;
	width: 30rpx;
	height: 30rpx;
	display: flex;
	justify-content: center;
	align-items: center;
	margin-left: 2rpx;
}
.scroll-Hei{
	height: 100vh;
	/* white-space: nowrap; */
}
.order-right{
	background-color: #FFFFFF;
	flex: 1;
	overflow-y: auto;
	
}
.classif{
	font-size: 27rpx;
	padding: 30rpx 20rpx;
	color: #797979;
}
/* 分类商品 */
.classif-goods{
	display: flex;
	justify-content: space-between;
	padding: 0 20rpx;
	height: 150rpx;
	font-size: 30rpx;
	margin-bottom: 45rpx;
}

.goods-image image{
	display: block;
	width: 150rpx;
	height: 150rpx;
	border-radius: 10rpx;
}
.goods-Price{
	flex: 1;
	position: relative;
	padding: 0 20rpx;
}
.goods-Price text{display: block;}
.goods-name{
	display: flex;
	flex-direction: column;
	position: relative;
	top: 0;
}
.goods-name text:nth-child(1){padding-bottom: 9rpx;}
.unit-price{
	position: absolute;
	bottom: 0;
	display: flex;
	align-items: baseline;
}
.Bold{font-weight: bold;}
.Symbol{font-size: 20rpx;}
.Thinning{font-size: 25rpx;
color: #cccccc;
}
.quantity image{width: 50rpx; height: 50rpx;}
.quantity view{
	width: 50rpx;
	height: 50rpx;
	text-align: center;
    line-height: 50rpx;
}
.quantity{
	display: flex;
	align-items: center;
	align-self: flex-end;
	width: 200rpx;
	justify-content: space-between;
}
.order-bottom{
	background-color: #fefefe;
	height: 120rpx;
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	box-shadow: 0rpx -1.9rpx 1rpx 1rpx #f9f9f9;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 20rpx;
	z-index: 9;
}
.Shopping image{width: 75rpx; height: 75rpx; display: block;}
.Shopping-left{width: 75rpx; height: 75rpx;}
.Shopping{
	display: flex;
	align-items: center;
	/* height: 120rpx; */
}
.Shopping-number{
	align-self: flex-start;
	background: #eb5941;
	color: #ffff;
	width: 40rpx;
	border-radius: 50rpx;
	text-align: center;
	font-size: 20rpx;
	/* margin-top: 15rpx; */
}
.Shopping-title{
	flex: 1;
	padding: 0 25rpx;
	color: #999999;
	/* height: 120rpx;
	line-height: 120rpx; */
}
.place-order button{
	border: none;
	background:linear-gradient(to right,#f8da81,#f8d771,#f7d362,#f6cb4a);
	width: 200rpx;
	height: 75rpx;
	line-height: 75rpx;
	border-radius: 50rpx;
	font-weight: bold;
	z-index: 9;
}
/* 点击分类列表加上背景色 */
.active{
	background-color: #FFFFFF;
	color: #000000 !important;
}
/* 详情弹窗样式 */
.details-back{
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0,0,0,0.5);
	display: flex;
	align-items: flex-end;
	z-index: 999;
}
.goods-details{
	background-color: #FFFFFF;
	width: 100%;
	border-top-left-radius: 20rpx;
	border-top-right-radius: 20rpx;
}
.details-back .goods-image{
	width: 100%;
	height: 280rpx;
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #f8f8f8;
}
.details-back .goods-image image:nth-child(1){
	display: block;
	width: 100%;
	height: 280rpx;
	border-top-left-radius: 20rpx;
	border-top-right-radius: 20rpx;
}
.details-back .goods-image image:nth-child(2){
	display: block;
	width: 50rpx;
	height: 50rpx;
	position: absolute;
	top: 20rpx;
	right: 20rpx;
	border-radius: 50%;
}
.details-padd{
	padding: 20rpx 20rpx 0 20rpx;
}
.describe{
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.details-quantity image{
	width: 50rpx;
	height: 50rpx;
	display: block;
}
.details-quantity{
	display: flex;
	align-items: center;
	width: 200rpx;
	justify-content: space-between;
}
.details-name{
	font-size: 35rpx;
	font-weight: bold;
}
.details-Thinning{
	font-size: 30rpx;
	color: #a4a4a4;
	padding: 20rpx 0;
}
.details-unit-price{
	font-size: 30rpx;
	color: #ec702d;
	display: flex;
	align-items: baseline;
}
.details-unit-price text:nth-child(2){
	font-size: 35rpx;
}
.details-unit-price text:nth-child(3){
	color: #999999 !important;
}
.details-info{
	display: flex;
	flex-direction: row;
	align-items: center;
	padding: 15rpx 0;
	border-bottom: 1px solid #f0f0f0;
}
.info-label{
	font-size: 28rpx;
	color: #666;
	font-weight: bold;
	white-space: nowrap;
}
.info-value{
	font-size: 26rpx;
	color: #333;
	margin-left: 10rpx;
	line-height: 1.6;
	flex: 1;
}
/* 动画 */
.coup-anim{
	animation: coup 0.3s ease-out;
}
@keyframes coup{
	0%{
		transform: translateY(100%);
	}
	100%{
		transform: translateY(0);
	}
}
</style>
