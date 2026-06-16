<template>
	<div class="ordering" v-loading.fullscreen.lock="loading">
		<div class="heading">评论管理</div>
		<div class="content-view">
			<div class="button-view">
				<el-button type="warning" size="small" @click="refresh">刷新</el-button>
			</div>
			<div v-if="nodatas">
				<div class="tab-list">
					<span v-for="(item,index) in tablist" :key="index">{{item}}</span>
				</div>
				<div class="tab-table" v-for="(item,index) in tabcont" :key="index">
					<div>{{item.id || '-'}}</div>
					<div>{{item.orderId || '-'}}</div>
					<div>{{item.tableId || '-'}}</div>
					<div>{{item.potTasteScore || '-'}}</div>
					<div>{{item.dishFreshnessScore || '-'}}</div>
					<div>{{item.diningExperienceScore || '-'}}</div>
					<div class="content-text">{{item.content || '-'}}</div>
					<div class="review-images">
						<el-image
							v-for="(img, imgIndex) in getImages(item)"
							:key="imgIndex"
							:src="img"
							:preview-src-list="getImages(item)"
							fit="cover"
							style="width: 60px; height: 60px; margin-right: 5px; border-radius: 4px;">
						</el-image>
						<span v-if="!getImages(item) || getImages(item).length === 0">-</span>
					</div>
					<div>{{item.createTime || '-'}}</div>
				</div>
			</div>
			<div class="nodatas" v-if="!nodatas">{{nodvalue}}</div>
		</div>
		<div style="height: 120px;"></div>
	</div>
</template>

<script>
export default{
	data() {
		return {
			loading: true,
			nodatas: true,
			nodvalue: '暂无评论数据',
			tablist: ['评论ID', '订单ID', '桌号', '锅底口味', '菜品新鲜度', '用餐体验', '评价内容', '评论图片', '评论时间'],
			tabcont: [],
			baseUrl: 'http://localhost:81'
		}
	},
	methods:{
		getImages(item) {
			if (!item || !item.images) {
				return []
			}
			if (typeof item.images === 'string') {
				try {
					const imgs = JSON.parse(item.images)
					return imgs.map(img => this.baseUrl + img)
				} catch(e) {
					return [this.baseUrl + item.images]
				}
			}
			if (Array.isArray(item.images)) {
				return item.images.map(img => this.baseUrl + img)
			}
			return []
		},
		async loadList(){
			try{
				let res = await new this.Request(this.Urls.m().reviewList || 'order-review/list').modeget()
				console.log('评论列表返回:', res)
				if (res && res.data) {
					this.nodatas = (res.data.data && res.data.data.length > 0) ? true : false
					this.tabcont = res.data.data || []
				} else {
					this.nodatas = false
					this.tabcont = []
				}
				this.loading = false
			}catch(e){
				console.error('加载评论列表失败:', e)
				this.loading = false
				this.nodatas = false
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		},
		refresh(){
			this.loading = true
			this.loadList()
		}
	},
	created() {
		this.loadList()
	}
}
</script>

<style scoped="scoped">
@import url("../../../style/pubiss.css");
::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active {
	background-color: #00be06;
	color: #fff;
}
.content-text {
	max-width: 200px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
.review-images {
	display: flex;
	align-items: center;
	justify-content: center;
	flex-wrap: wrap;
	gap: 5px;
}
</style>
