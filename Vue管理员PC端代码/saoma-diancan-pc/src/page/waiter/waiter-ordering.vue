<template>
	<div class="waiter-ordering" v-loading.fullscreen.lock="loading">
		<div class="back-button" @click="goBack">
			<i class="el-icon-back"></i> 返回
		</div>
		
		<!-- 打烊提示 -->
		<div v-if="!shopIsOpen" class="closed-view">
			<div class="closed-icon">
				<i class="el-icon-close"></i>
			</div>
			<div class="closed-text">门店已打烊</div>
			<div class="closed-desc">暂不接受点餐，请稍后再来</div>
			<div class="closed-hours" v-if="shopInfo.openTime && shopInfo.closeTime">
				营业时间：{{shopInfo.openTime}} - {{shopInfo.closeTime}}
			</div>
		</div>
		
		<div v-else>
			<div class="heading">服务员点餐</div>
		
		<div v-if="!selectedTable" class="table-select">
			<div class="section-title">请选择桌位</div>
			<div class="table-grid">
				<div 
					v-for="table in tableList" 
					:key="table.id"
					class="table-item"
					:class="{ 'table-occupied': table.state === 1 }"
					@click="selectTable(table)">
					<div class="table-number">{{ table.number }}</div>
					<div class="table-status">
						{{ table.state === 0 ? '空闲' : '占用' }}
					</div>
				</div>
			</div>
		</div>
		
		<div v-else class="ordering-view">
			<div class="selected-info">
				<span class="info-label">当前桌位：</span>
				<span class="info-value">{{ selectedTable.number }}</span>
				<el-button type="text" @click="clearTable">更换桌位</el-button>
			</div>
			
			<div class="people-select">
				<span class="info-label">用餐人数：</span>
				<div class="people-quick-select">
					<span 
						v-for="num in quickPeopleNumbers" 
						:key="num"
						class="people-quick-btn"
						:class="{ 'people-quick-active': numberOfDiners === num }"
						@click="selectPeople(num)">
						{{ num }}人
					</span>
				</div>
				<el-input-number v-model="numberOfDiners" :min="1" :max="4" size="small"></el-input-number>
			</div>
			
			<div class="order-content">
				<div class="order-left">
					<div 
						class="category-item"
						:class="{ 'category-active': activeCategory === -1 }"
						@click="selectCategory(-1)">
						🔥 热门
						<span v-if="getHotQuantity() > 0" class="category-count">{{ getHotQuantity() }}</span>
					</div>
					<div 
						v-for="(category, index) in categoryList" 
						:key="category.cid"
						class="category-item"
						:class="{ 'category-active': activeCategory === index }"
						@click="selectCategory(index)">
						{{ category.label }}
						<span v-if="category.quantity > 0" class="category-count">{{ category.quantity }}</span>
					</div>
				</div>
				
				<div class="order-right" ref="orderRight" @scroll="handleRightScroll">
					<div id="category-hot" class="category-section">
						<div class="category-title">🔥 热门推荐</div>
						<div 
							v-for="(dish, index) in hotDishes" 
							:key="dish.id"
							class="dish-item"
							@click="showDishDetail(dish, -1, index)">
							<div class="dish-image">
								<img :src="baseUrl + '/image/dish/' + dish.image" :alt="dish.name" />
							</div>
							<div class="dish-info">
								<div class="dish-name">{{ dish.name }}</div>
								<div class="dish-sales">已售 {{ dish.monthlysale || 0 }}</div>
								<div class="dish-price">
									<span class="price-symbol">¥</span>
									<span class="price-value">{{ dish.price }}</span>
									<span class="price-unit">/{{ dish.unit || '份' }}</span>
								</div>
							</div>
							<div class="dish-actions">
								<div 
									v-if="dish.quantity > 0"
									class="action-btn"
									@click.stop="reduceDish(dish, -1, index)">
									-
								</div>
								<div v-if="dish.quantity > 0" class="dish-quantity">
									{{ dish.quantity }}
								</div>
								<div 
									class="action-btn add-btn"
									@click.stop="addDish(dish, -1, index)">
									+
								</div>
							</div>
						</div>
					</div>
					
					<div 
						v-for="(category, categoryIndex) in categoryList" 
						:key="category.cid"
						:id="'category-' + category.cid"
						class="category-section">
						<div class="category-title">{{ category.label }}</div>
						<div 
							v-for="(dish, dishIndex) in category.dishList" 
							:key="dish.id"
							class="dish-item"
							@click="showDishDetail(dish, categoryIndex, dishIndex)">
							<div class="dish-image">
								<img :src="baseUrl + '/image/dish/' + dish.image" :alt="dish.name" />
							</div>
							<div class="dish-info">
								<div class="dish-name">{{ dish.name }}</div>
								<div class="dish-sales">已售 {{ dish.monthlysale || 0 }}</div>
								<div class="dish-price">
									<span class="price-symbol">¥</span>
									<span class="price-value">{{ dish.price }}</span>
									<span class="price-unit">/{{ dish.unit || '份' }}</span>
								</div>
							</div>
							<div class="dish-actions">
								<div 
									v-if="dish.quantity > 0"
									class="action-btn"
									@click.stop="reduceDish(dish, categoryIndex, dishIndex)">
									-
								</div>
								<div v-if="dish.quantity > 0" class="dish-quantity">
									{{ dish.quantity }}
								</div>
								<div 
									class="action-btn add-btn"
									@click.stop="addDish(dish, categoryIndex, dishIndex)">
									+
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="order-bottom">
				<div class="cart-info" @click="showCart = true">
					<div class="cart-icon">
						<i class="el-icon-shopping-cart-2"></i>
						<div v-if="totalQuantity > 0" class="cart-badge">{{ totalQuantity }}</div>
					</div>
					<div v-if="totalQuantity > 0" class="cart-text">已点 {{ totalQuantity }} 份</div>
					<div v-else class="cart-text">购物车是空的</div>
				</div>
				<div class="total-price">
					<span class="price-label">合计：</span>
					<span class="price-number">¥{{ totalPrice.toFixed(2) }}</span>
				</div>
				<el-button 
					type="success" 
					size="medium"
					:disabled="totalQuantity === 0"
					@click="submitOrder">
					下单
				</el-button>
			</div>
		</div>
		
		<el-dialog
			title="菜品详情"
			:visible.sync="dishDetailVisible"
			width="500px"
			:center="true">
			<div v-if="currentDish" class="dish-detail-content">
				<div class="detail-image">
					<img :src="baseUrl + '/image/dish/' + currentDish.image" :alt="currentDish.name" />
				</div>
				<div class="detail-info">
					<div class="detail-name">{{ currentDish.name }}</div>
					<div class="detail-sales">已售 {{ currentDish.monthlysale || 0 }}</div>
					<div class="detail-price">
						<span>¥</span>
						<span>{{ currentDish.price }}</span>
						<span>/{{ currentDish.unit || '份' }}</span>
					</div>
					<div class="detail-actions">
						<div 
							class="detail-action-btn"
							@click="reduceDetailDish">
							-
						</div>
						<div class="detail-quantity">{{ currentDish.quantity || 0 }}</div>
						<div 
							class="detail-action-btn detail-add-btn"
							@click="addDetailDish">
							+
						</div>
					</div>
				</div>
			</div>
		</el-dialog>
		
		<el-dialog
			title="购物车"
			:visible.sync="showCart"
			width="600px"
			:center="true">
			<div v-if="shoppingCart.length === 0" class="empty-cart">
				购物车是空的
			</div>
			<div v-else class="cart-list">
				<div v-for="(item, index) in shoppingCart" :key="item.id" class="cart-item">
					<div class="cart-item-name">{{ item.name }}</div>
					<div class="cart-item-price">¥{{ item.unitprice }}</div>
					<div class="cart-item-actions">
						<div class="cart-action-btn" @click="reduceCartItem(index)">-</div>
						<div class="cart-item-count">{{ item.quantity }}</div>
						<div class="cart-action-btn cart-add-btn" @click="addCartItem(index)">+</div>
					</div>
					<div class="cart-item-total">¥{{ item.total_price.toFixed(2) }}</div>
				</div>
			</div>
			<div slot="footer" class="dialog-footer">
				<el-button @click="emptyCart">清空购物车</el-button>
				<el-button type="primary" @click="showCart = false">确定</el-button>
			</div>
		</el-dialog>
		
		<div class="footer-spacer"></div>
		</div>
	</div>
</template>

<script>
export default {
	data() {
		return {
			loading: true,
			baseUrl: 'http://localhost:81',
			selectedTable: null,
			numberOfDiners: 1,
			quickPeopleNumbers: [1, 2, 3, 4],
			tableList: [],
			categoryList: [],
			hotDishes: [],
			activeCategory: -1,
			shoppingCart: [],
			totalQuantity: 0,
			totalPrice: 0,
			dishDetailVisible: false,
			currentDish: null,
			currentCategoryIndex: -1,
			currentDishIndex: 0,
			showCart: false,
			shopIsOpen: true,
			shopInfo: {}
		}
	},
	methods: {
		async loadShopInfo() {
			try {
				let res = await new this.Request('http://localhost:81/shopInfo', {}).modeget()
				
				if (res && res.data && res.data.code === 0 && res.data.data && res.data.data.shopInfo) {
					this.shopInfo = res.data.data.shopInfo
					this.shopIsOpen = this.shopInfo.isOpen !== undefined ? this.shopInfo.isOpen : true
					localStorage.setItem('shopInfo', JSON.stringify(this.shopInfo))
				}
			} catch (e) {
				console.error('从服务器加载门店信息失败', e)
				try {
					const shopInfoStr = localStorage.getItem('shopInfo')
					if (shopInfoStr) {
						this.shopInfo = JSON.parse(shopInfoStr)
						this.shopIsOpen = this.shopInfo.isOpen !== undefined ? this.shopInfo.isOpen : true
					}
				} catch (err) {
					console.error('从本地加载门店信息失败', err)
				}
			}
		},
		selectPeople(num) {
			this.numberOfDiners = num
		},
		goBack() {
			this.$router.push('/waiter-index').catch(() => {})
		},
		async loadTableList() {
			try {
				let res = await new this.Request(this.Urls.m().getqrcode, {"pageNum": 1, "pageSize": 100}).modepost()
				if (res && res.data && res.data.tableList) {
					this.tableList = res.data.tableList
				}
			} catch (e) {
				console.error('加载桌位列表失败:', e)
				new this.mytitle(this.$message, 'error', '加载桌位列表失败').funtitle()
			}
		},
		async loadCategoryAndDishes() {
			try {
				console.log('开始加载分类和菜品数据...')
				
				let dishRes = await new this.Request(this.Urls.m().dishListAll).modeget()
				console.log('小程序端菜品API响应:', dishRes)
				
				let allDishes = []
				let data = dishRes
				
				if (data && data.allDish) {
					this.categoryList = data.allDish.map(cat => ({
						...cat,
						quantity: 0
					}))
					console.log('分类列表:', this.categoryList)
					
					this.categoryList.forEach(category => {
						if (category.dishList) {
							category.dishList.forEach(dish => {
								allDishes.push({...dish, cid: category.cid, quantity: 0, price: dish.unitprice})
							})
							
							category.dishList = category.dishList.map(dish => ({
								...dish,
								quantity: 0,
								price: dish.unitprice
							}))
							console.log(`分类 ${category.label} 的菜品:`, category.dishList)
						}
					})
					
					if (allDishes.length > 0) {
						allDishes.sort((a, b) => (b.monthlysale || 0) - (a.monthlysale || 0))
						this.hotDishes = allDishes.slice(0, 5)
						console.log('热门菜品:', this.hotDishes)
					}
				} else if (data && data.data && data.data.allDish) {
					this.categoryList = data.data.allDish.map(cat => ({
						...cat,
						quantity: 0
					}))
					console.log('分类列表:', this.categoryList)
					
					this.categoryList.forEach(category => {
						if (category.dishList) {
							category.dishList.forEach(dish => {
								allDishes.push({...dish, cid: category.cid, quantity: 0, price: dish.unitprice})
							})
							
							category.dishList = category.dishList.map(dish => ({
								...dish,
								quantity: 0,
								price: dish.unitprice
							}))
							console.log(`分类 ${category.label} 的菜品:`, category.dishList)
						}
					})
					
					if (allDishes.length > 0) {
						allDishes.sort((a, b) => (b.monthlysale || 0) - (a.monthlysale || 0))
						this.hotDishes = allDishes.slice(0, 5)
						console.log('热门菜品:', this.hotDishes)
					}
				} else {
					console.log('小程序端API没有返回数据，尝试管理员端API')
					
					let categoryRes = await new this.Request(this.Urls.m().listAllcategory).modeget()
					console.log('分类API响应:', categoryRes)
					
					if (categoryRes && categoryRes.status === 200 && categoryRes.data && categoryRes.data.categoryList) {
						this.categoryList = categoryRes.data.categoryList.map(cat => ({
							...cat,
							quantity: 0,
							dishList: []
						}))
						console.log('分类列表:', this.categoryList)
					}
					
					let dishResAdmin = await new this.Request(this.Urls.m().obtaindishes, {pageNum: 1, pageSize: 1000}).modepost()
					console.log('菜品API响应:', dishResAdmin)
					
					if (dishResAdmin && dishResAdmin.data && dishResAdmin.data.dishList) {
						let dishes = dishResAdmin.data.dishList
						console.log('所有菜品:', dishes)
						
						this.categoryList.forEach(cat => {
							cat.dishList = dishes
								.filter(dish => dish.type && dish.type.id === cat.cid)
								.map(dish => ({
									...dish,
									quantity: 0,
									price: dish.unitprice
								}))
							console.log(`分类 ${cat.label} 的菜品:`, cat.dishList)
						})
						
						allDishes = dishes.map(dish => ({
							...dish,
							quantity: 0,
							price: dish.unitprice
						}))
						
						if (allDishes.length > 0) {
							allDishes.sort((a, b) => (b.monthlysale || 0) - (a.monthlysale || 0))
							this.hotDishes = allDishes.slice(0, 5)
							console.log('热门菜品:', this.hotDishes)
						}
					}
				}
				
				this.loading = false
			} catch (e) {
				console.error('加载菜品数据失败:', e)
				new this.mytitle(this.$message, 'error', '加载菜品数据失败').funtitle()
				this.loading = false
			}
		},
		selectTable(table) {
			this.selectedTable = table
		},
		clearTable() {
			this.selectedTable = null
			this.emptyCart()
		},
		selectCategory(index) {
			console.log('选择分类:', index)
			console.log('分类列表:', this.categoryList)
			this.activeCategory = index
			
			this.$nextTick(() => {
				let targetId = index === -1 ? 'category-hot' : 'category-' + this.categoryList[index].cid
				console.log('目标元素ID:', targetId)
				let targetElement = document.getElementById(targetId)
				console.log('目标元素:', targetElement)
				if (targetElement && this.$refs.orderRight) {
					console.log('滚动到:', targetElement.offsetTop)
					this.$refs.orderRight.scrollTo({
						top: targetElement.offsetTop - 10,
						behavior: 'smooth'
					})
				} else {
					console.log('找不到目标元素或滚动容器')
				}
			})
		},
		handleRightScroll() {
			if (!this.$refs.orderRight) return
			
			let scrollTop = this.$refs.orderRight.scrollTop
			let hotElement = document.getElementById('category-hot')
			
			if (hotElement && hotElement.offsetTop <= scrollTop + 50) {
				let nextCategoryIndex = -1
				
				for (let i = 0; i < this.categoryList.length; i++) {
					let categoryId = 'category-' + this.categoryList[i].cid
					let categoryElement = document.getElementById(categoryId)
					if (categoryElement && categoryElement.offsetTop <= scrollTop + 50) {
						nextCategoryIndex = i
					}
				}
				
				if (this.activeCategory !== nextCategoryIndex) {
					this.activeCategory = nextCategoryIndex
				}
			}
		},
		getHotQuantity() {
			let count = 0
			this.hotDishes.forEach(dish => {
				count += dish.quantity || 0
			})
			return count
		},
		addDish(dish, categoryIndex, dishIndex) {
			const newQuantity = (dish.quantity || 0) + 1
			
			if (categoryIndex === -1) {
				this.$set(this.hotDishes[dishIndex], 'quantity', newQuantity)
			} else {
				this.$set(this.categoryList[categoryIndex].dishList[dishIndex], 'quantity', newQuantity)
			}
			
			this.updateShoppingCart({
				...dish,
				quantity: newQuantity,
				total_price: dish.price * newQuantity
			})
		},
		reduceDish(dish, categoryIndex, dishIndex) {
			if (dish.quantity <= 0) return
			
			const newQuantity = dish.quantity - 1
			
			if (categoryIndex === -1) {
				this.$set(this.hotDishes[dishIndex], 'quantity', newQuantity)
			} else {
				this.$set(this.categoryList[categoryIndex].dishList[dishIndex], 'quantity', newQuantity)
			}
			
			this.updateShoppingCart({
				...dish,
				quantity: newQuantity,
				total_price: dish.price * newQuantity
			})
		},
		showDishDetail(dish, categoryIndex, dishIndex) {
			this.currentDish = { ...dish }
			this.currentCategoryIndex = categoryIndex
			this.currentDishIndex = dishIndex
			this.dishDetailVisible = true
		},
		addDetailDish() {
			if (this.currentDish) {
				this.addDish(this.currentDish, this.currentCategoryIndex, this.currentDishIndex)
				this.currentDish.quantity = (this.currentDish.quantity || 0) + 1
			}
		},
		reduceDetailDish() {
			if (this.currentDish && this.currentDish.quantity > 0) {
				this.reduceDish(this.currentDish, this.currentCategoryIndex, this.currentDishIndex)
				this.currentDish.quantity = this.currentDish.quantity - 1
			}
		},
		updateShoppingCart(dish) {
			let cartIndex = this.shoppingCart.findIndex(item => item.id === dish.id)
			
			if (dish.quantity <= 0) {
				if (cartIndex !== -1) {
					this.shoppingCart.splice(cartIndex, 1)
				}
			} else {
				if (cartIndex === -1) {
					this.shoppingCart.push({
						...dish,
						unitprice: dish.price
					})
				} else {
					this.$set(this.shoppingCart[cartIndex], 'quantity', dish.quantity)
					this.$set(this.shoppingCart[cartIndex], 'total_price', dish.total_price)
				}
			}
			
			this.updateCategoryQuantities()
			this.calculateTotals()
		},
		updateCategoryQuantities() {
			this.categoryList.forEach(cat => {
				cat.quantity = 0
			})
			
			this.shoppingCart.forEach(item => {
				if (item.cid !== -1) {
					let catIndex = this.categoryList.findIndex(cat => cat.cid === item.cid)
					if (catIndex !== -1) {
						this.categoryList[catIndex].quantity += item.quantity
					}
				}
			})
		},
		calculateTotals() {
			this.totalQuantity = this.shoppingCart.reduce((sum, item) => sum + item.quantity, 0)
			this.totalPrice = this.shoppingCart.reduce((sum, item) => sum + item.total_price, 0)
		},
		reduceCartItem(index) {
			let item = this.shoppingCart[index]
			item.quantity -= 1
			item.total_price = item.unitprice * item.quantity
			
			let categoryIndex = this.categoryList.findIndex(cat => cat.cid === item.cid)
			if (categoryIndex !== -1) {
				let dishIndex = this.categoryList[categoryIndex].dishList.findIndex(dish => dish.id === item.id)
				if (dishIndex !== -1) {
					this.$set(this.categoryList[categoryIndex].dishList[dishIndex], 'quantity', item.quantity)
				}
			}
			
			let hotIndex = this.hotDishes.findIndex(dish => dish.id === item.id)
			if (hotIndex !== -1) {
				this.$set(this.hotDishes[hotIndex], 'quantity', item.quantity)
			}
			
			if (item.quantity <= 0) {
				this.shoppingCart.splice(index, 1)
			}
			
			this.updateCategoryQuantities()
			this.calculateTotals()
		},
		addCartItem(index) {
			let item = this.shoppingCart[index]
			item.quantity += 1
			item.total_price = item.unitprice * item.quantity
			
			let categoryIndex = this.categoryList.findIndex(cat => cat.cid === item.cid)
			if (categoryIndex !== -1) {
				let dishIndex = this.categoryList[categoryIndex].dishList.findIndex(dish => dish.id === item.id)
				if (dishIndex !== -1) {
					this.$set(this.categoryList[categoryIndex].dishList[dishIndex], 'quantity', item.quantity)
				}
			}
			
			let hotIndex = this.hotDishes.findIndex(dish => dish.id === item.id)
			if (hotIndex !== -1) {
				this.$set(this.hotDishes[hotIndex], 'quantity', item.quantity)
			}
			
			this.updateCategoryQuantities()
			this.calculateTotals()
		},
		generateOrderNo() {
			let date = new Date()
			let year = date.getFullYear()
			let month = (date.getMonth() + 1).toString().padStart(2, '0')
			let day = date.getDate().toString().padStart(2, '0')
			let hour = date.getHours().toString().padStart(2, '0')
			let minute = date.getMinutes().toString().padStart(2, '0')
			let second = date.getSeconds().toString().padStart(2, '0')
			let random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
			return year + month + day + hour + minute + second + random
		},
		emptyCart() {
			this.shoppingCart = []
			
			this.hotDishes.forEach(dish => {
				dish.quantity = 0
			})
			
			this.categoryList.forEach(cat => {
				cat.quantity = 0
				if (cat.dishList) {
					cat.dishList.forEach(dish => {
						dish.quantity = 0
					})
				}
			})
			
			this.calculateTotals()
			this.showCart = false
		},
		async submitOrder() {
			if (this.shoppingCart.length === 0) {
				new this.mytitle(this.$message, 'warning', '请先选择菜品').funtitle()
				return
			}
			
			if (!this.selectedTable) {
				new this.mytitle(this.$message, 'warning', '请先选择桌位').funtitle()
				return
			}
			
			this.$confirm('确定要提交订单吗？', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(async () => {
				try {
					let orderData = {
						table_number: this.selectedTable.number,
						number_of_diners: this.numberOfDiners,
						sett_amount: this.totalPrice,
						order_no: this.generateOrderNo(),
						transac_status: 'unsettled',
						order_receiving: 'mis_orders',
						goods_list: this.shoppingCart.map(item => ({
							name: item.name,
							quantity: item.quantity.toString(),
							unit: item.unit || '份',
							image: item.image,
							price: item.total_price,
							dish_id: item.id
						}))
					}
					
					let res = await new this.Request('http://localhost:81/order/create', orderData).modepost()
					
					if (res && (res.code === 0 || (res.data && res.data.code === 0))) {
						new this.mytitle(this.$message, 'success', '下单成功').funtitle()
						this.emptyCart()
						this.selectedTable = null
						this.$router.push('/waiter-order')
					} else {
						new this.mytitle(this.$message, 'warning', (res && res.msg) || (res && res.data && res.data.msg) || '下单失败').funtitle()
					}
				} catch (e) {
					console.error('下单失败:', e)
					new this.mytitle(this.$message, 'error', '下单失败，请重试').funtitle()
				}
			}).catch(() => {})
		}
	},
	created() {
		this.loadShopInfo()
		this.loadTableList()
		this.loadCategoryAndDishes()
	}
}
</script>

<style scoped>
@import url("../../../style/pubiss.css");

.closed-view {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	height: 70vh;
	margin: 0 20px;
}

.closed-icon {
	width: 120px;
	height: 120px;
	background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 30px;
	box-shadow: 0 10px 30px rgba(239, 68, 68, 0.3);
}

.closed-icon i {
	font-size: 60px;
	color: #ffffff;
}

.closed-text {
	font-size: 32px;
	font-weight: bold;
	color: #1e293b;
	margin-bottom: 15px;
}

.closed-desc {
	font-size: 18px;
	color: #64748b;
	margin-bottom: 30px;
}

.closed-hours {
	font-size: 16px;
	color: #475569;
	background: #ffffff;
	padding: 12px 30px;
	border-radius: 50px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.waiter-ordering {
	background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
	min-height: 100vh;
	padding: clamp(10px, 2vw, 20px);
}

.back-button {
	display: inline-flex;
	align-items: center;
	padding: clamp(8px, 1.5vw, 12px) clamp(16px, 2vw, 24px);
	background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
	border-radius: 10px;
	cursor: pointer;
	margin: clamp(10px, 2vw, 20px);
	font-size: clamp(12px, 1.5vw, 14px);
	color: #475569;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
	border: 1px solid #e2e8f0;
}

.back-button:hover {
	background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
	color: #2563eb;
	box-shadow: 0 4px 12px rgba(37, 99, 235, 0.15);
	transform: translateY(-2px);
}

.back-button i {
	margin-right: 8px;
	font-size: clamp(14px, 1.8vw, 16px);
}

.heading {
	font-size: clamp(20px, 3vw, 28px);
	font-weight: 700;
	color: #1e293b;
	margin: 0 clamp(10px, 2vw, 20px) clamp(20px, 3vw, 30px) clamp(10px, 2vw, 20px);
	padding-left: clamp(10px, 1.5vw, 16px);
	border-left: 4px solid #2563eb;
	letter-spacing: -0.5px;
}

.table-select {
	background: #ffffff;
	border-radius: 16px;
	padding: clamp(20px, 3vw, 30px);
	margin: 0 clamp(10px, 2vw, 20px);
	box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.section-title {
	font-size: clamp(16px, 2vw, 20px);
	font-weight: 600;
	color: #1e293b;
	margin-bottom: 20px;
}

.table-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
	gap: 15px;
}

.table-item {
	background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
	border: 2px solid #7dd3fc;
	border-radius: 12px;
	padding: 20px 10px;
	text-align: center;
	cursor: pointer;
	transition: all 0.3s;
}

.table-item:hover {
	transform: translateY(-3px);
	box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.table-item.table-occupied {
	background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
	border-color: #fca5a5;
}

.table-number {
	font-size: 24px;
	font-weight: 700;
	color: #1e40af;
	margin-bottom: 8px;
}

.table-item.table-occupied .table-number {
	color: #dc2626;
}

.table-status {
	font-size: 14px;
	color: #3b82f6;
}

.table-item.table-occupied .table-status {
	color: #ef4444;
}

.ordering-view {
	background: #ffffff;
	border-radius: 16px;
	margin: 0 clamp(10px, 2vw, 20px);
	box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
	display: flex;
	flex-direction: column;
	height: calc(100vh - 150px);
}

.selected-info,
.people-select {
	padding: 15px 20px;
	display: flex;
	align-items: center;
	gap: 15px;
	border-bottom: 1px solid #f1f5f9;
	flex-wrap: wrap;
}

.people-quick-select {
	display: flex;
	gap: 8px;
	flex-wrap: wrap;
}

.people-quick-btn {
	padding: 8px 16px;
	background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
	border: 2px solid #e2e8f0;
	border-radius: 20px;
	font-size: 14px;
	font-weight: 500;
	color: #64748b;
	cursor: pointer;
	transition: all 0.3s;
}

.people-quick-btn:hover {
	background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
	border-color: #3b82f6;
	color: #2563eb;
	transform: translateY(-2px);
	box-shadow: 0 4px 12px rgba(37, 99, 235, 0.15);
}

.people-quick-btn.people-quick-active {
	background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
	border-color: #2563eb;
	color: white;
	box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.info-label {
	font-size: 15px;
	font-weight: 500;
	color: #475569;
}

.info-value {
	font-size: 18px;
	font-weight: 600;
	color: #2563eb;
}

.order-content {
	flex: 1;
	display: flex;
	overflow: hidden;
}

.order-left {
	width: 160px;
	background: #f8fafc;
	overflow-y: auto;
	border-right: 1px solid #e2e8f0;
}

.category-item {
	padding: 16px 15px;
	font-size: 15px;
	color: #64748b;
	cursor: pointer;
	transition: all 0.3s;
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.category-item:hover {
	background: #f1f5f9;
}

.category-item.category-active {
	background: #ffffff;
	color: #2563eb;
	font-weight: 600;
	border-left: 3px solid #2563eb;
}

.category-count {
	background: #f59e0b;
	color: white;
	font-size: 12px;
	padding: 2px 8px;
	border-radius: 10px;
	min-width: 20px;
	text-align: center;
}

.order-right {
	flex: 1;
	overflow-y: auto;
	padding: 20px;
}

.category-section {
	margin-bottom: 30px;
}

.category-title {
	font-size: 16px;
	font-weight: 600;
	color: #334155;
	margin-bottom: 15px;
}

.dish-item {
	display: flex;
	align-items: center;
	padding: 15px;
	background: #fafafa;
	border-radius: 10px;
	margin-bottom: 12px;
	cursor: pointer;
	transition: all 0.3s;
}

.dish-item:hover {
	background: #f0f9ff;
	transform: translateX(5px);
}

.dish-image {
	width: 80px;
	height: 80px;
	border-radius: 8px;
	overflow: hidden;
	flex-shrink: 0;
}

.dish-image img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.dish-info {
	flex: 1;
	padding: 0 15px;
}

.dish-name {
	font-size: 16px;
	font-weight: 600;
	color: #1e293b;
	margin-bottom: 5px;
}

.dish-sales {
	font-size: 13px;
	color: #94a3b8;
	margin-bottom: 8px;
}

.dish-price {
	display: flex;
	align-items: baseline;
}

.price-symbol {
	font-size: 14px;
	color: #ef4444;
	margin-right: 2px;
}

.price-value {
	font-size: 20px;
	font-weight: 700;
	color: #ef4444;
}

.price-unit {
	font-size: 14px;
	color: #64748b;
	margin-left: 3px;
}

.dish-actions {
	display: flex;
	align-items: center;
	gap: 10px;
}

.action-btn {
	width: 32px;
	height: 32px;
	border-radius: 50%;
	background: #e2e8f0;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 20px;
	font-weight: 600;
	color: #475569;
	cursor: pointer;
	transition: all 0.3s;
}

.action-btn:hover {
	background: #cbd5e1;
}

.action-btn.add-btn {
	background: linear-gradient(135deg, #10b981 0%, #059669 100%);
	color: white;
}

.action-btn.add-btn:hover {
	background: linear-gradient(135deg, #059669 0%, #047857 100%);
	transform: scale(1.1);
}

.dish-quantity {
	min-width: 24px;
	text-align: center;
	font-size: 16px;
	font-weight: 600;
	color: #1e293b;
}

.order-bottom {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 15px 20px;
	background: #ffffff;
	border-top: 1px solid #e2e8f0;
	box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.05);
}

.cart-info {
	display: flex;
	align-items: center;
	gap: 12px;
	cursor: pointer;
}

.cart-icon {
	position: relative;
	width: 50px;
	height: 50px;
	background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	color: white;
	font-size: 24px;
}

.cart-badge {
	position: absolute;
	top: -5px;
	right: -5px;
	background: #ef4444;
	color: white;
	font-size: 12px;
	font-weight: 600;
	width: 22px;
	height: 22px;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.cart-text {
	font-size: 15px;
	color: #64748b;
}

.total-price {
	display: flex;
	align-items: baseline;
	gap: 5px;
}

.price-label {
	font-size: 14px;
	color: #64748b;
}

.price-number {
	font-size: 24px;
	font-weight: 700;
	color: #ef4444;
}

.dish-detail-content {
	text-align: center;
}

.detail-image {
	width: 200px;
	height: 200px;
	margin: 0 auto 20px;
	border-radius: 12px;
	overflow: hidden;
}

.detail-image img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.detail-name {
	font-size: 22px;
	font-weight: 700;
	color: #1e293b;
	margin-bottom: 10px;
}

.detail-sales {
	font-size: 14px;
	color: #94a3b8;
	margin-bottom: 15px;
}

.detail-price {
	font-size: 28px;
	color: #ef4444;
	margin-bottom: 20px;
}

.detail-price span:nth-child(1) {
	font-size: 18px;
}

.detail-price span:nth-child(2) {
	font-weight: 700;
}

.detail-price span:nth-child(3) {
	font-size: 16px;
	color: #64748b;
}

.detail-actions {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 15px;
}

.detail-action-btn {
	width: 40px;
	height: 40px;
	border-radius: 50%;
	background: #e2e8f0;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 24px;
	font-weight: 700;
	color: #475569;
	cursor: pointer;
	transition: all 0.3s;
}

.detail-action-btn:hover {
	background: #cbd5e1;
}

.detail-action-btn.detail-add-btn {
	background: linear-gradient(135deg, #10b981 0%, #059669 100%);
	color: white;
}

.detail-action-btn.detail-add-btn:hover {
	background: linear-gradient(135deg, #059669 0%, #047857 100%);
	transform: scale(1.1);
}

.detail-quantity {
	min-width: 40px;
	text-align: center;
	font-size: 22px;
	font-weight: 700;
	color: #1e293b;
}

.empty-cart {
	text-align: center;
	padding: 40px;
	color: #94a3b8;
	font-size: 16px;
}

.cart-list {
	max-height: 400px;
	overflow-y: auto;
}

.cart-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 15px;
	border-bottom: 1px solid #f1f5f9;
}

.cart-item:last-child {
	border-bottom: none;
}

.cart-item-name {
	flex: 2;
	font-weight: 500;
	color: #1e293b;
}

.cart-item-price {
	flex: 1;
	color: #64748b;
}

.cart-item-actions {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 10px;
}

.cart-action-btn {
	width: 28px;
	height: 28px;
	border-radius: 50%;
	background: #e2e8f0;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 16px;
	font-weight: 600;
	color: #475569;
	cursor: pointer;
	transition: all 0.3s;
}

.cart-action-btn:hover {
	background: #cbd5e1;
}

.cart-action-btn.cart-add-btn {
	background: linear-gradient(135deg, #10b981 0%, #059669 100%);
	color: white;
}

.cart-action-btn.cart-add-btn:hover {
	background: linear-gradient(135deg, #059669 0%, #047857 100%);
}

.cart-item-count {
	min-width: 30px;
	text-align: center;
	font-size: 15px;
	font-weight: 600;
}

.cart-item-total {
	flex: 1;
	text-align: right;
	font-weight: 600;
	color: #ef4444;
}

.dialog-footer {
	display: flex;
	justify-content: space-between;
}

.footer-spacer {
	height: clamp(80px, 12vw, 120px);
}


</style>