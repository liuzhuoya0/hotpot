<template>
	<div class="ordering" v-loading.fullscreen.lock="loading">
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
				<router-link :to="{name:'upload'}" class="add-btn-link">
					<el-button type="success" size="medium" class="add-btn">添加菜品</el-button>
				</router-link>
			</div>
			<div v-if="nodatas" class="table-wrapper">
				<div class="tab-list">
					<span v-for="(item,index) in tablist" :key="index" class="table-col">{{item}}</span>
				</div>
				<div class="tab-table" v-for="(goods_item,index) in tabcont" :key="index">
					<div class="table-col">{{goods_item.id}}</div>
					<div class="table-col">{{goods_item.type ? goods_item.type.label : '-'}}</div>
					<div class="table-col">{{goods_item.name}}</div>
					<div class="table-col dish-images">
						<el-image
						style="width: 80px; height: 80px; border-radius: 4px;"
						:src="getServerUrl()+'/image/dish/'+(goods_item.image || '')"
						fit="contain"
						:preview-src-list="[getServerUrl()+'/image/dish/'+(goods_item.image || '')]">
						</el-image>
					</div>
					<div class="table-col">{{goods_item.unitprice}}</div>
					<div class="table-col">{{goods_item.stock || '-'}}</div>
					<div class="table-col action-col">
						<el-button size="small" @click="edIt(goods_item)" :disabled="goods_item.onsale ? false : true ">编辑</el-button>
						<el-button v-if="goods_item.onsale" type="danger" size="small" @click="shelf(goods_item.originalId || goods_item.id,false,index)" :loading="index == shelfload ? true : false" :disabled="index == shelfload ? true : false">下架</el-button>
						<el-button v-else type="warning" size="small"  @click="shelf(goods_item.originalId || goods_item.id,true,index)">上架</el-button>
						<el-button type="warning" size="small"  @click="deleteDish(goods_item)">删除</el-button>
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
			<div class="nodatas" v-if="!nodatas">还没有菜品数据</div>
		</div>
		<div class="footer-spacer"></div>
	</div>
</template>

<script>
import {getServerUrl} from '../../../config/sys.js'
export default{
	data() {
		return {
			loading: true,
			nodatas:true,
			shelfload:-1,
			tablist: ['排序id','类目','菜品名称','菜品图片','价格(元)','库存','操作'],
			tabcont:[],
			currentnum:1,
			total:0,
			pagenum:0,
			categoryList: [],
			selectedCategory: null,
		}
	},
	methods:{
    getServerUrl(){
      return getServerUrl()
    },
		async getCategoryList(){
			try{
				let res = await new this.Request(this.Urls.m().listAllcategory).modeget()
				this.categoryList = res.data.categoryList
			}catch(e){
				console.log(e)
			}
		},
		searchDish(){
			this.pagenum = 0
			this.obtaindishes()
		},
		currentchange(e){
			this.pagenum = e - 1
			this.obtaindishes()
		},
    deleteDish(item){
      let id = item.originalId || item.id
      this.$confirm('您确定要删除这个记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async({ value }) => {
        let res = await new this.Request(this.Urls.m().dishDelete+"?id="+id).modeget()
        if(res.status != 200){
          new this.mytitle(this.$message,'warning',"删除失败").funtitle()
        }else{
          new this.mytitle(this.$message,'success',"删除成功！").funtitle()
          this.obtaindishes()
        }
      }).catch((err) => {
        console.log(err)
      });
    },
		async obtaindishes(){
			try{
				let params = {"pageNum":this.pagenum+1}
				if (this.selectedCategory != null && this.selectedCategory != '') {
					params.categoryId = this.selectedCategory
				}
				let res = await new this.Request(this.Urls.m().obtaindishes, params).modepost()
				this.nodatas = res.data.total == 0  ? false : true
				this.tabcont = res.data.dishList
				this.total = res.data.total
				this.loading = false
			}catch(e){
				this.loading = false
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		},
		edIt(item){
			this.$router.push({name:'upload',params:{item}});
		},
		async shelf(id,onsale,index){
			this.shelfload = index
			try{
				await new this.Request(this.Urls.m().fromsale,{id:id,onsale:onsale}).modepost()
				this.$set(this.tabcont[index],'onsale',onsale)
				this.shelfload = -1
			}catch(e){
				this.shelfload = -1
				new this.mytitle(this.$message,'error','操作失败，重试').funtitle()
			}
		}
	},
	created() {
		this.getCategoryList()
		this.obtaindishes()
	},
}
</script>

<style scoped="scoped">
@import url("../../../style/pubiss.css");

::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active {
	background-color: #00be06;
	color: #fff;
}

.el-pagination {
	text-align: center;
	margin: 30px 0;
}

.button-view {
	justify-content: space-between;
}

.tab-list {
	justify-content: flex-start;
}

.tab-list span {
	width: auto;
	min-width: 150px;
}

.tab-table {
	justify-content: flex-start;
}

.tab-table div {
	width: auto;
	min-width: 150px;
}

.dish-images {
	display: flex;
	align-items: center;
	justify-content: center;
}
</style>
