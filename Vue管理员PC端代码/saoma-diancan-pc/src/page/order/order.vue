<template>
	<div class="ordering" v-loading.fullscreen.lock="loading">
		<div class="heading">订单管理</div>
		<div class="content-view">
			<!-- 查询 -->
			<div class="query-view">
				<!-- 状态 -->
				<div class="quotation-query">
					<div>交易状态</div>
					<div>
						<el-select v-model="statevalue" placeholder="请选择">
						    <el-option
						      v-for="item in options"
						      :key="item.value"
						      :label="item.label"
						      :value="item.value">
						    </el-option>
						  </el-select>
					</div>
				</div>
				<!-- 日期范围 -->
				<div class="quotation-query">
					<div>交易日期</div>
					<div>
						<el-date-picker
						      v-model="dateRange"
						      type="daterange"
						      range-separator="至"
						      start-placeholder="开始日期"
						      end-placeholder="结束日期"
						      value-format="yyyy-MM-dd">
						    </el-date-picker>
					</div>
				</div>
				<!-- 按钮 -->
				<div class="quotation-query"><el-button type="success" size="medium" @click="queryFun()">查询</el-button></div>
			</div>
			<div class="button-view">

				  <el-button type="warning" size="small" @click="refresh_order()">刷新订单</el-button>
				  <el-button type="primary" size="small" @click="exportOrder()">导出Excel</el-button>

			</div>
			<!-- 是否有数据 -->
			<div v-if="nodatas">
				<!-- 表头 -->
				<div class="tab-list">
					<span v-for="(item,index) in tablist" :key="index">{{item}}</span>
				</div>
				<!-- 表格 -->
				<div class="tab-table-quo" v-for="(item,index) in tabcont" :key="index">
					<div>{{item.order_time}}</div>
					<div>{{item.table_number || '-'}}</div>
					<div>{{item.number_of_diners || '-'}}</div>
					<div class="remarks-text">
						<el-button type="small" :loading="index == deta_load ? true : false" @click="detailed_menu(index,item.id)">详细菜单</el-button>
						</div>
					<div>{{Price(item.sett_amount || 0)}}</div>
					<div>
						<el-button type="info" size="small" v-if="item.order_receiving == 'mis_orders'" :loading="index == rece_load ? true : false" @click="receiving(index,item.id)">待接单</el-button>
						<el-button size="small" type="success" disabled v-if="item.transac_status == 'success'">已结账</el-button>
						<el-button size="small" type="success" v-if="item.transac_status == 'unsettled' && item.order_receiving=='rec_order'" :loading="index == check_load ? true : false" @click="checkout(index,item.id)">待结账</el-button>
						<el-button size="small" type="default" disabled v-if="!item.order_receiving && !item.transac_status">-</el-button>
					</div>
				</div>
				<!-- 分页 -->
				<el-pagination
				  background
				  layout="prev, pager, next"
				  :hide-on-single-page="true"
				  :total="total"
				  @current-change="currentchange"
				  >
				</el-pagination>
			</div>
			<!-- 没有数据 -->
			<div class="nodatas" v-if="!nodatas">{{nodvalue}}</div>
			<!-- 订单详细弹窗 -->
			<el-dialog
			  title="详细菜单"
			  :visible.sync="dialogVisible"
			  width="600px"
			  :center="true"
			  >
			  <div>
				  <!-- 表头 -->
				  <div class="menu-header menu-padd">
					  <span class="header-name">菜品名称</span>
					  <span class="header-quantity">数量</span>
					  <span class="header-price">单价(元)</span>
					  <span class="header-total">小计(元)</span>
				  </div>
				  <!-- 菜品列表 -->
				  <div class="menu-padd" v-for="(item,index) in user_menu" :key="index">
						  <div class="Menu-details menu-margin">
							  <span class="item-name">{{item.name}}</span>
							  <span class="item-quantity">{{item.quantity}}{{item.unit}}</span>
							  <span class="item-price">{{item.price}}</span>
							  <span class="item-total">{{(item.quantity * item.price).toFixed(2)}}</span>
						  </div>
				  </div>
				  <!-- 总计 -->
				  <div class="menu-total menu-padd" v-if="user_menu && user_menu.length > 0">
					  <span class="total-label">总计：</span>
					  <span class="total-amount">{{calculateTotal()}}元</span>
				  </div>
			  </div>
			</el-dialog>
		</div>
		<div style="height: 120px;"></div>
	</div>
</template>

<script>
// 交易状态
import {staff} from '../../../config/state-type.js'
// 价格补领
const Price = require('e-commerce_price')
export default{
	data() {
		return {
			Price:Price,
			options:staff(),
			loading: true,
			nodatas:true,
			dialogVisible:false,//弹出详细菜单
			deta_load:-1,//查看详细菜单
			rece_load:-1,//接单
			check_load:-1,//结账
			time:'',//交易时间
			statevalue:'',//交易状态
			dateRange:[],//日期范围
			nodvalue:'没有订单数据',
			total:0,//总条数
			pagenum:0,
			pickerOptions: {
			    disabledDate(time) {return time.getTime() > Date.now();}
			},
			tablist:['交易时间','桌号','用餐人数','菜单详情','交易金额(元)','交易状态'],
			tabcont:[],
			user_menu:[],//用户详细菜单
			dingdan:0,//订单提醒
		}
	},
	methods:{
		// 分页
		currentchange(e){
			this.pagenum = e - 1
			this.obtainorder(0)
		},
		// 获取订单
		async obtainorder(vle){
			try{
				let params = {"pageNum":this.pagenum+1,"query":this.statevalue}
				// 添加日期范围参数
				if(this.dateRange && this.dateRange.length == 2){
					params.startDate = this.dateRange[0]
					params.endDate = this.dateRange[1]
				}
				let res = await new this.Request(this.Urls.m().obtainorder, params).modepost()
				console.log(res)
        this.nodatas = res.data.total == 0  ? false : true
        this.tabcont = res.data.orderList
        this.total = res.data.total
        this.loading = false
				if(vle == 1){
					localStorage.setItem('order_num',0)
					this.$store.commit('order_remind',0)
				}
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		},
		// 查看详细菜单
		async detailed_menu(index,id){
			this.deta_load = index
			try{
				let res = await new this.Request(this.Urls.m().vieworder + '?id=' + id).modeget()
				console.log(res)
				this.user_menu = res.data.list
				this.deta_load = -1
				this.dialogVisible = true
			}catch(e){
				this.deta_load = -1
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		},
		// 接单
		async receiving(index,id){
			this.rece_load = index
			try{
				let res = await new this.Request(this.Urls.m().receiving,{id:id,order_receiving:'rec_order'}).modepost()
				this.$set(this.tabcont[index],'order_receiving','rec_order')
				new this.mytitle(this.$message,'success','执行成功').funtitle()
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		},
		// 结账
		async checkout(index,id){
			this.check_load = index
			try{
				let res = await new this.Request(this.Urls.m().checkout,{id:id,transac_status:'success'}).modepost()
				console.log(res)
				this.$set(this.tabcont[index],'transac_status','success')
				new this.mytitle(this.$message,'success','执行成功').funtitle()
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		},
		// 刷新订单
		refresh_order(){
			this.loading = true
			this.deta_load = -1,//查看详细菜单
			this.rece_load = -1,//接单
			this.check_load = -1,//结账
			this.obtainorder(1)
		},
		// 查询订单
		queryFun(){
			this.pagenum = 0
			this.obtainorder(0)
		},
		// 导出Excel
		exportOrder(){
			let url = this.Urls.m().exportOrder
			let params = []
			if(this.statevalue){
				params.push('query=' + this.statevalue)
			}
			if(this.dateRange && this.dateRange.length == 2){
				params.push('startDate=' + this.dateRange[0])
				params.push('endDate=' + this.dateRange[1])
			}
			if(params.length > 0){
				url += '?' + params.join('&')
			}
			
			// 使用 XMLHttpRequest 检查响应类型
			const xhr = new XMLHttpRequest()
			xhr.open('GET', url, true)
			xhr.responseType = 'blob'
			xhr.withCredentials = true
			
			xhr.onload = () => {
				if (xhr.status === 200) {
					const contentType = xhr.getResponseHeader('Content-Type')
					// 检查是否是 JSON 错误响应
					if (contentType && contentType.indexOf('application/json') !== -1) {
						const reader = new FileReader()
						reader.onload = (e) => {
							try {
								const result = JSON.parse(e.target.result)
								this.$message.error(result.msg || '导出失败')
							} catch (err) {
								this.$message.error('导出失败')
							}
						}
						reader.readAsText(xhr.response)
					} else {
						// 是 Excel 文件，直接下载
						const blob = new Blob([xhr.response], { type: contentType })
						const link = document.createElement('a')
						link.href = window.URL.createObjectURL(blob)
						link.download = '订单数据_' + Date.now() + '.xlsx'
						document.body.appendChild(link)
						link.click()
						document.body.removeChild(link)
						window.URL.revokeObjectURL(link.href)
					}
				} else {
					this.$message.error('导出失败')
				}
			}
			
			xhr.onerror = () => {
				this.$message.error('导出失败')
			}
			
			xhr.send()
		},
		// 计算订单总计
		calculateTotal(){
			if(!this.user_menu || this.user_menu.length === 0){
				return '0.00'
			}
			let total = 0
			for(let item of this.user_menu){
				if(item.quantity && item.price){
					total += item.quantity * item.price
				}
			}
			return total.toFixed(2)
		}
	},
	created() {
		// 获取订单
		this.obtainorder(0)
		// 订单提醒
		this.dingdan = localStorage.getItem("order_num")
	},
	watch: {
		// 监听订单提醒
		"$store.state.remind"(newValue, oldValue) {
			this.dingdan = newValue.num
		}
	},
}
</script>

<style scoped="scoped">
@import url("../../../style/pubiss.css");
@import url("../../../style/popup.css");
::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active {
	background-color: #00be06;
	color: #fff;
}
::v-deep .el-badge{
	margin-right: 5px;
}
.menu-padd{
	border-bottom: 1px solid #f8f8f8;
}
/* 表头样式 */
.menu-header{
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 15px 20px;
	background: #f5f7fa;
	font-weight: bold;
	color: #606266;
	border-bottom: 2px solid #e4e7ed;
}
.header-name, .item-name{
	flex: 3;
	text-align: left;
}
.header-quantity, .item-quantity{
	flex: 1;
	text-align: center;
}
.header-price, .item-price{
	flex: 1.5;
	text-align: right;
}
.header-total, .item-total{
	flex: 1.5;
	text-align: right;
}
.Menu-details{
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 12px 20px;
}
.menu-margin{margin: 0;}
.menu-span{
	font-weight: bold;
	font-size: 15px;
	padding-top: 20px;
}
/* 总计样式 */
.menu-total{
	display: flex;
	justify-content: flex-end;
	align-items: center;
	padding: 20px;
	background: #fafafa;
	border-top: 2px solid #e4e7ed;
	font-size: 16px;
}
.total-label{
	font-weight: bold;
	color: #303133;
	margin-right: 10px;
}
.total-amount{
	font-weight: bold;
	color: #f56c6c;
	font-size: 18px;
}
::v-deep .el-dialog{
	height: 600px;
	overflow-y: auto;
	border-radius: 5px !important;
}
</style>
