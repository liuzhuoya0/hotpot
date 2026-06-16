<template>
	<div class="ordering" v-loading.fullscreen.lock="loading">
		<div class="heading">菜品类目</div>
		<div class="content-view">
			<div class="button-view">
				<el-button type="success" size="medium" @click="open">添加类目</el-button>
			</div>
			<div v-if="nodatas">
				<!-- 表头 -->
				<div class="tab-list">
					<span v-for="(item,index) in tablist" :key="index">{{item}}</span>
				</div>
				<!-- 表格 -->
				<div class="tab-table" v-for="(item,index) in tabcont" :key="index">
					<div>{{item.id}}</div>
					<div>{{item.label}}</div>
          <div> <el-button type="warning" size="small"  @click="deleteCategory(item.id)">删除</el-button></div>
				</div>
				<!-- 分页 -->
				<el-pagination
				  background
				  layout="prev, pager, next"
				  :hide-on-single-page="true"
				  :total="total"
				  :current-page="currentnum"
				  @current-change="currentchange"
				  >
				</el-pagination>
			</div>
			<!-- 没有数据 -->
			<div class="nodatas" v-if="!nodatas">还没有菜品类目</div>
		</div>
		
		<!-- 添加类目弹窗 -->
		<el-dialog
		  title="添加菜品类目"
		  :visible.sync="dialogVisible"
		  width="400px"
		  :close-on-click-modal="false">
		  <div style="margin-bottom: 15px;">
		    <div style="font-weight: 500; margin-bottom: 5px;">类目 ID:</div>
		    <el-input v-model="form.id" placeholder="请输入数字 ID（如：6）"></el-input>
		  </div>
		  <div style="margin-bottom: 15px;">
		    <div style="font-weight: 500; margin-bottom: 5px;">类目名称:</div>
		    <el-input v-model="form.label" placeholder="请输入类目名称"></el-input>
		  </div>
		  <span slot="footer" class="dialog-footer">
		    <el-button @click="dialogVisible = false">取 消</el-button>
		    <el-button type="primary" @click="confirmAdd">确 定</el-button>
		  </span>
		</el-dialog>
		
		<div style="height: 120px;"></div>
	</div>
</template>

<script>
export default{
	data() {
		return {
			loading: true,
			nodatas:true,
			tablist: ['类目 id','类目','操作'],
			tabcont:[],
			currentnum:1,//当前页数
			total:0,//总条数
			pagenum:0,
			dialogVisible: false,
			form: {
				id: '',
				label: ''
			}
		}
	},
	methods:{
		// 分页
		currentchange(e){
			this.pagenum = e - 1
			this.obtaincate()
		},
    // 打开弹窗
    open(){
      this.form = {
        id: '',
        label: ''
      }
      this.dialogVisible = true
    },
    // 确认添加
    confirmAdd(){
      // 验证 ID
      if(!this.form.id || !/^[1-9]\d*$/.test(this.form.id)){
        this.$message.error('请输入有效的数字 ID')
        return
      }
      // 验证名称
      if(!this.form.label || this.form.label.trim() === ''){
        this.$message.error('请输入类目名称')
        return
      }
      this.addcate(this.form.label.trim(), parseInt(this.form.id))
      this.dialogVisible = false
    },
    // 删除
    deleteCategory(id){
      this.$confirm('您确定要删除这个记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async({ value }) => {
        let res = await new this.Request(this.Urls.m().deleteCategory+"?id="+id).modeget()
        if(res.data.code != 0){
          new this.mytitle(this.$message,'warning',res.data.msg).funtitle()
        }else{
          new this.mytitle(this.$message,'success',"删除成功！").funtitle()
          this.obtaincate()
        }
      }).catch((err) => {
        console.log(err)
      });
    },
    // 添加类目请求接口
    async addcate(category, id){
      try{
        let res = await new this.Request(this.Urls.m().addcategory,{"label":category,"id":id}).modepost()
        if(res.status != 200){
          new this.mytitle(this.$message,'warning',res.data.msg).funtitle()
        }else{
          new this.mytitle(this.$message,'success','添加成功！').funtitle()
          this.currentnum = 1
          this.pagenum = 0
          this.obtaincate()
        }
      }catch(e){
        new this.mytitle(this.$message,'error','服务器发生错误，请重试').funtitle()
      }
    },
		// 获取菜品类目
		async obtaincate(){
			try{
				let res = await new this.Request(this.Urls.m().obtaincate,{"pageNum":this.pagenum+1}).modepost()
				this.nodatas = res.data.total == 0  ? false : true
				this.tabcont = res.data.categoryList
				this.total = res.data.total
				this.loading = false
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		}
	},
	created() {
		this.obtaincate()
	}
}
</script>

<style scoped="scoped">
@import url("../../../style/pubiss.css");
::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active {
	background-color: #00be06;
	color: #fff;
}
/* 分页 */
.el-pagination {
	text-align: center;
	margin: 30px 0;
}
</style>
