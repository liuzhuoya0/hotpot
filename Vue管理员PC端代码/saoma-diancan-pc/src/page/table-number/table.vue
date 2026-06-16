<template>
<!--	<div class="ordering" v-loading.fullscreen.lock="loading">-->
  <div class="ordering">
		<div class="heading">桌号管理</div>
		<div class="content-view">
			<div class="button-view">
				<el-button type="success" size="medium" @click="open">添加桌号</el-button>
				<el-button type="primary" size="medium" @click="generateAllQrCodes">批量生成所有二维码</el-button>
			</div>
			<div v-if="nodatas">
				<!-- 表头 -->
				<div class="tab-list">
					<span v-for="(item,index) in tablist" :key="index">{{item}}</span>
				</div>
				<!-- 表格 -->
				<div class="tab-table" v-for="(item,index) in tabcont" :key="index">
					<div>{{item.number}}</div>
					<div>
						<el-image class="elimg"
						:src="getServerUrl()+'image/qrcode/'+item.qrcode"
						fit="cover"
						:lazy="true"
						:preview-src-list="[getServerUrl()+'image/qrcode/'+item.qrcode]">
						</el-image>
					</div>
          <div> 
            <el-button type="primary" size="small"  @click="generateQrCode(item)">生成二维码</el-button>
            <el-button type="warning" size="small"  @click="deleteTable(item.id)">删除</el-button>
          </div>
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
			<div class="nodatas" v-if="!nodatas">还没有桌号</div>
		</div>
		<div style="height: 120px;"></div>
	</div>
</template>

<script setup>
import {getServerUrl} from '../../../config/sys.js'
export default{
	data() {
		return {
			loading: true,
			nodatas:true,
			tablist: ['桌号','点餐二维码','操作'],
			tabcont:[],
			total:0,//总条数
			pagenum:0,
			currentnum:1,//当前页数
		}
	},
	methods:{
    getServerUrl(){
      return getServerUrl()
    },
		// 分页
		currentchange(e){
      console.log("change")
			this.pagenum = e - 1
      this.getqrcode()
		},
		// 添加桌号
		open(){
			this.$prompt('请输入桌台号', '提示', {
			  confirmButtonText: '确定',
			  cancelButtonText: '取消',
			  inputPattern: /^[0-9]*$/,
			  inputErrorMessage: '桌号格式不正确,应为数字类型'
			}).then(({ value }) => {
				console.log(value)
				this.add_table(value)
			}).catch((err) => {
				console.log(err)
			});
		},

    // 删除
     deleteTable(id){
      this.$confirm('您确定要删除这个记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async({ value }) => {
        let res = await new this.Request(this.Urls.m().tableDelete+"?id="+id).modeget()
        if(res.status != 200){
          new this.mytitle(this.$message,'warning',"删除失败").funtitle()
        }else{
          new this.mytitle(this.$message,'success',"删除成功！").funtitle()
          this.getqrcode()
        }
      }).catch((err) => {
        console.log(err)
      });
    },

		// 添加桌号：发起请求
		async add_table(table){
			try{
				let res = await new this.Request(this.Urls.m().qrcode,{"number":table}).modepost()
				if(res.status != 200){
					new this.mytitle(this.$message,'warning',res.data.msg).funtitle()
				}else{
					new this.mytitle(this.$message,'success',res.data.msg).funtitle()
					this.currentnum = 1
					this.pagenum = 0
					this.getqrcode()
				}
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		},
		// 获取桌号
		async getqrcode(){
			try{
				let res = await new this.Request(this.Urls.m().getqrcode,{"pageNum":this.pagenum+1}).modepost()
        console.log(res)
				this.nodatas = res.data.total == 0  ? false : true
				this.tabcont = res.data.tableList
				this.total = res.data.total
				this.loading = false
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		},
		// 批量生成所有二维码
		async generateAllQrCodes(){
			this.$confirm('您确定要为所有餐桌生成小程序码吗？', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消'
			}).then(async({ value }) => {
				try{
					let res = await new this.Request(this.Urls.m().generateAllQrCodes).modeget()
					if(res.status != 200 || res.data.code !== 0){
						new this.mytitle(this.$message,'warning',res.data.msg || '生成失败').funtitle()
					}else{
						new this.mytitle(this.$message,'success','批量生成成功！').funtitle()
						this.getqrcode()
					}
				}catch(e){
					new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
				}
			}).catch((err) => {
				console.log(err)
			})
		},
		// 单独生成某个餐桌的二维码
		async generateQrCode(item){
			this.$confirm('您确定要为餐桌 ' + item.number + ' 生成小程序码吗？', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消'
			}).then(async({ value }) => {
				try{
					let res = await new this.Request(this.Urls.m().generateQrCode+"?id="+item.id).modeget()
					if(res.status != 200 || res.data.code !== 0){
						new this.mytitle(this.$message,'warning',res.data.msg || '生成失败').funtitle()
					}else{
						new this.mytitle(this.$message,'success','生成成功！').funtitle()
						this.getqrcode()
					}
				}catch(e){
					new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
				}
			}).catch((err) => {
				console.log(err)
			})
		}
	},
	created() {
		this.getqrcode()
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
