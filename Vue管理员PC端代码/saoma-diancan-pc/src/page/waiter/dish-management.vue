<template>
	<div class="ordering" v-loading.fullscreen.lock="loading">
		<div class="back-button" @click="goBack">
			<i class="el-icon-back"></i> 返回
		</div>
		<div class="heading">菜品管理</div>
		<div class="content-view">
			<div class="button-view">
				<div class="search-section">
					<span class="search-label">菜品类目</span>
					<el-select v-model="selectedCategory" placeholder="请选择菜品类目" class="category-select">
						<el-option label="全部" :value="null"></el-option>
						<el-option
							v-for="item in categoryList"
							:key="item.id"
							:label="item.label"
							:value="item.id">
						</el-option>
					</el-select>
					<el-button type="success" size="medium" @click="searchDish" class="search-btn">查询</el-button>
				</div>
				<div class="action-section">
					<el-button type="success" size="small" @click="batchOnSale" :disabled="selectedIds.length === 0" class="batch-btn">
						<i class="el-icon-check"></i> 批量上架
					</el-button>
					<el-button type="warning" size="small" @click="batchOffSale" :disabled="selectedIds.length === 0" class="batch-btn">
						<i class="el-icon-close"></i> 批量下架
					</el-button>
					<el-button type="warning" size="small" @click="refresh" class="refresh-btn">刷新</el-button>
				</div>
			</div>
			<div v-if="nodatas" class="table-wrapper">
				<div class="tab-list">
					<span class="checkbox-col">
						<el-checkbox v-model="selectAll" @change="handleSelectAll"></el-checkbox>
					</span>
					<span v-for="(item,index) in tablist" :key="index" class="table-col">{{item}}</span>
				</div>
				<div class="tab-table" v-for="(item,index) in tabcont" :key="index">
					<div class="checkbox-col">
						<el-checkbox v-model="item.selected" @change="handleSelectChange"></el-checkbox>
					</div>
					<div class="table-col">{{item.name}}</div>
					<div class="table-col">{{item.price}}</div>
					<div class="table-col">{{item.stock || '-'}}</div>
					<div class="table-col">
						<el-tag :type="item.onsale ? 'success' : 'info'">
							{{ item.onsale ? '在售' : '下架' }}
						</el-tag>
					</div>
					<div class="table-col">
						<el-button size="small" :type="item.onsale ? 'warning' : 'success'" :loading="loadState[index]" @click="toggleSale(index, item)">
							{{ item.onsale ? '下架' : '上架' }}
						</el-button>
					</div>
				</div>
				<el-pagination
				  background
				  layout="prev, pager, next"
				  :hide-on-single-page="true"
				  :total="total"
				  @current-change="currentchange"
				  class="pagination">
				</el-pagination>
			</div>
			<div class="nodatas" v-if="!nodatas">{{nodvalue}}</div>
		</div>
		<div class="footer-spacer"></div>
	</div>
</template>

<script>
export default{
	data() {
		return {
			loading: true,
			nodatas:true,
			selectedCategory: null,
			categoryList:[],
			nodvalue:'没有菜品数据',
			total:0,
			pagenum:0,
			tablist:['菜品名称','价格','库存','状态','操作'],
			tabcont:[],
			loadState: {},
			selectAll: false,
			selectedIds: []
		}
	},
	methods:{
		goBack() {
			this.$router.push('/waiter-index').catch(() => {})
		},
		currentchange(e){
			this.pagenum = e - 1
			this.loadList()
		},
		async loadCategoryList(){
			try{
				let res = await new this.Request(this.Urls.m().listAllcategory).modeget()
				if(res.status === 200 && res.data.code === 0){
					this.categoryList = res.data.categoryList || []
				}
			}catch(e){
				console.log(e)
			}
		},
		searchDish(){
			this.pagenum = 0
			this.loadList()
		},
		async loadList(){
			try{
				let params = {"pageNum":this.pagenum+1}
				if (this.selectedCategory != null && this.selectedCategory != '') {
					params.categoryId = this.selectedCategory
				}
				let res = await new this.Request(this.Urls.m().obtaindishes, params).modepost()
				this.nodatas = res.data.total == 0 ? false : true
				this.tabcont = (res.data.dishList || []).map(item => {
					return {
						...item,
						selected: false,
						id: item.originalId || item.id
					}
				})
				this.total = res.data.total
				this.loading = false
				this.selectAll = false
				this.selectedIds = []
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		},
		handleSelectAll(val) {
			this.tabcont.forEach(item => {
				this.$set(item, 'selected', val)
			})
			this.updateSelectedIds()
		},
		handleSelectChange() {
			this.updateSelectAll()
			this.updateSelectedIds()
		},
		updateSelectAll() {
			this.selectAll = this.tabcont.length > 0 && this.tabcont.every(item => item.selected)
		},
		updateSelectedIds() {
			this.selectedIds = this.tabcont
				.filter(item => item.selected)
				.map(item => item.id)
		},
		async batchOnSale() {
			if (this.selectedIds.length === 0) {
				new this.mytitle(this.$message,'warning','请先选择要上架的菜品').funtitle()
				return
			}
			this.$confirm('确定要上架选中的 ' + this.selectedIds.length + ' 个菜品吗？', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(async () => {
				try {
					let res = await new this.Request(this.Urls.m().batchOnSale, {ids: this.selectedIds}).modepost()
					if(res.status === 200 && res.data.code === 0){
						new this.mytitle(this.$message,'success','批量上架成功').funtitle()
						this.loadList()
					}else{
						new this.mytitle(this.$message,'warning',res.data.msg || '操作失败').funtitle()
					}
				} catch (e) {
					new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
				}
			}).catch(() => {})
		},
		async batchOffSale() {
			if (this.selectedIds.length === 0) {
				new this.mytitle(this.$message,'warning','请先选择要下架的菜品').funtitle()
				return
			}
			this.$confirm('确定要下架选中的 ' + this.selectedIds.length + ' 个菜品吗？', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(async () => {
				try {
					let res = await new this.Request(this.Urls.m().batchOffSale, {ids: this.selectedIds}).modepost()
					if(res.status === 200 && res.data.code === 0){
						new this.mytitle(this.$message,'success','批量下架成功').funtitle()
						this.loadList()
					}else{
						new this.mytitle(this.$message,'warning',res.data.msg || '操作失败').funtitle()
					}
				} catch (e) {
					new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
				}
			}).catch(() => {})
		},
		async toggleSale(index, item) {
			this.$set(this.loadState, index, true)
			try {
				let res = await new this.Request(this.Urls.m().fromsale,{id:item.originalId || item.id,onsale:!item.onsale}).modepost()
				if(res.status === 200 && res.data.code === 0){
					new this.mytitle(this.$message,'success','操作成功').funtitle()
					this.$set(this.tabcont[index], 'onsale', !item.onsale)
				}else{
					new this.mytitle(this.$message,'warning',res.data.msg || '操作失败').funtitle()
				}
			} catch (e) {
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			} finally {
				this.$set(this.loadState, index, false)
			}
		},
		refresh(){
			this.loading = true
			this.selectedCategory = null
			this.pagenum = 0
			this.loadList()
		}
	},
	created() {
		this.loadCategoryList()
		this.loadList()
	}
}
</script>

<style scoped="scoped">
@import url("../../../style/pubiss.css");

* {
	box-sizing: border-box;
}

.tab-list {
	justify-content: flex-start !important;
}

.tab-list span {
	width: auto !important;
}

.tab-table {
	justify-content: flex-start !important;
}

.tab-table div {
	width: auto !important;
}

.button-view {
	justify-content: space-between !important;
	margin-top: 0 !important;
}

.ordering {
    background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
    min-height: 100vh;
    padding: clamp(4px, 0.8vw, 10px);
    left: 0 !important;
}

.back-button {
	display: inline-flex;
	align-items: center;
	padding: clamp(8px, 1.2vw, 12px) clamp(16px, 2vw, 24px);
	background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
	border-radius: 10px;
	cursor: pointer;
	margin: clamp(8px, 1.5vw, 15px);
	font-size: clamp(14px, 1.8vw, 16px);
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
	font-size: clamp(16px, 2vw, 18px);
}

.heading {
	font-size: clamp(24px, 3vw, 32px);
	font-weight: 700;
	color: #1e293b;
	margin: 0 clamp(4px, 0.8vw, 10px) clamp(15px, 2.5vw, 25px) clamp(4px, 0.8vw, 10px);
	padding-left: clamp(10px, 1.5vw, 16px);
	border-left: 4px solid #2563eb;
	letter-spacing: -0.5px;
}

.content-view {
	background: #ffffff;
	border-radius: 16px;
	padding: clamp(15px, 2.5vw, 25px);
	margin: 0 clamp(4px, 0.8vw, 10px);
	box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.button-view {
	display: flex;
	flex-wrap: wrap;
	gap: 15px;
	justify-content: space-between;
	align-items: center;
	margin-bottom: clamp(15px, 2.5vw, 25px);
	padding: clamp(12px, 2vw, 20px);
	background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
	border-radius: 12px;
}

.search-section {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 10px;
}

.search-label {
	font-size: clamp(14px, 1.8vw, 17px);
	color: #475569;
	font-weight: 500;
	white-space: nowrap;
}

.category-select {
	width: clamp(160px, 25vw, 220px);
}

.action-section {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
}

.button-view .el-select .el-input__inner {
	border-radius: 10px;
	border: 2px solid #e2e8f0;
	transition: all 0.3s;
	font-size: 16px;
	height: 48px;
}

.button-view .el-select .el-input__inner:focus {
	border-color: #2563eb;
	box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.15);
}

.button-view .el-button {
	border-radius: 8px;
	font-weight: 500;
	font-size: 14px;
	padding: clamp(8px, 1.2vw, 12px) clamp(14px, 2vw, 20px);
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	border: none;
}

.button-view .el-button--success {
	background: linear-gradient(135deg, #10b981 0%, #059669 100%);
	box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.button-view .el-button--success:hover {
	background: linear-gradient(135deg, #059669 0%, #047857 100%);
	box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4);
	transform: translateY(-2px);
}

.button-view .el-button--warning {
	background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
	box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.button-view .el-button--warning:hover {
	background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
	box-shadow: 0 6px 16px rgba(245, 158, 11, 0.4);
	transform: translateY(-2px);
}

.table-wrapper {
	overflow-x: auto;
	-webkit-overflow-scrolling: touch;
}

.tab-list {
	display: flex;
	align-items: center;
	justify-content: flex-start;
	min-width: 600px;
	border: none;
	font-size: clamp(12px, 1.5vw, 15px);
	color: #64748b;
	background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
	height: clamp(48px, 6vw, 58px);
	border-radius: 12px 12px 0 0;
	font-weight: 600;
	text-transform: uppercase;
	letter-spacing: 0.5px;
}

.checkbox-col {
	width: clamp(50px, 6vw, 65px);
	flex-shrink: 0;
	text-align: center;
	display: flex;
	align-items: center;
	justify-content: center;
}

.table-col {
	flex: 1;
	min-width: 80px;
	text-align: center;
	padding: 0 5px;
}

.tab-list .table-col {
	color: #475569;
}

.tab-table {
	display: flex;
	align-items: center;
	justify-content: flex-start;
	min-width: 600px;
	background-color: #ffffff;
	padding: clamp(12px, 2vw, 18px) 0;
	border-bottom: 1px solid #f1f5f9;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tab-table:hover {
	background: linear-gradient(135deg, #f8fafc 0%, #fefefe 100%);
	transform: scale(1.015);
	box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
}

.tab-table:first-of-type {
	border-radius: 0 0 16px 16px;
}

.tab-table .el-tag {
	border-radius: 16px;
	padding: clamp(5px, 1vw, 8px) clamp(12px, 1.8vw, 16px);
	font-weight: 600;
	font-size: clamp(11px, 1.5vw, 14px);
	border: none;
}

.tab-table .el-tag--success {
	background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
	color: #065f46;
}

.tab-table .el-tag--info {
	background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
	color: #475569;
}

.tab-table .el-button {
	border-radius: 6px;
	font-weight: 500;
	padding: clamp(6px, 1vw, 10px) clamp(12px, 1.8vw, 18px);
	transition: all 0.3s;
	border: none;
	font-size: clamp(12px, 1.5vw, 14px);
}

.tab-table .el-button--success {
	background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.tab-table .el-button--success:hover {
	background: linear-gradient(135deg, #059669 0%, #047857 100%);
	transform: translateY(-2px);
	box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.tab-table .el-button--warning {
	background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.tab-table .el-button--warning:hover {
	background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
	transform: translateY(-2px);
	box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

::v-deep .el-checkbox {
	display: inline-flex !important;
	align-items: center !important;
	justify-content: center !important;
}

::v-deep .el-checkbox__input {
	width: auto !important;
	line-height: 1 !important;
}

::v-deep .el-checkbox__inner {
	width: clamp(18px, 2.5vw, 22px) !important;
	height: clamp(18px, 2.5vw, 22px) !important;
	border-radius: 6px;
	border: 2px solid #cbd5e1;
	transition: all 0.3s;
}

::v-deep .el-checkbox__inner:hover {
	border-color: #2563eb;
}

::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
	background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
	border-color: #2563eb;
}

::v-deep .el-checkbox__label {
	display: none !important;
}

.pagination {
	text-align: center;
	margin: clamp(20px, 3.5vw, 35px) 0 clamp(10px, 2vw, 18px) 0;
}

::v-deep .el-pagination {
	font-size: 14px;
}

::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active {
	background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
	color: #fff;
	border-radius: 8px;
	font-weight: 600;
	font-size: 14px;
	padding: 0 12px;
	height: 36px;
	line-height: 36px;
}

::v-deep .el-pagination.is-background .el-pager li {
	border-radius: 8px;
	margin: 0 4px;
	font-weight: 500;
	transition: all 0.3s;
	font-size: 13px;
	height: 36px;
	line-height: 36px;
	padding: 0 10px;
}

::v-deep .el-pagination.is-background .el-pager li:hover:not(.disabled) {
	background: #e0f2fe;
	color: #0369a1;
}

::v-deep .el-pagination button {
	border-radius: 8px;
	margin: 0 4px;
	height: 36px;
	font-size: 13px;
}

.nodatas {
	text-align: center;
	padding: clamp(40px, 6vw, 60px) clamp(15px, 2.5vw, 25px);
	color: #94a3b8;
	font-size: clamp(15px, 2vw, 18px);
	background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
	border-radius: 12px;
	margin-top: 20px;
}

.footer-spacer {
	height: clamp(60px, 10vw, 100px);
}

@media (max-width: 768px) {
	.button-view {
		flex-direction: column;
		align-items: stretch;
	}

	.search-section {
		justify-content: center;
	}

	.action-section {
		justify-content: center;
	}

	.category-select {
		width: 100%;
		max-width: 280px;
	}
}

@media (max-width: 480px) {
	.search-section {
		flex-direction: column;
		align-items: stretch;
	}

	.search-label {
		text-align: center;
	}

	.category-select {
		max-width: 100%;
	}

	.action-section {
		flex-direction: column;
	}

	.action-section .el-button {
		width: 100%;
	}
}


</style>
