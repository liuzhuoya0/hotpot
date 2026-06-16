<template>
	<div class="ordering" v-loading.fullscreen.lock="loading">
		<div class="back-button" @click="goBack">
			<i class="el-icon-back"></i> 返回
		</div>
		<div class="heading">订单处理</div>
		<div class="content-view">
			<div class="button-view" style="display: flex; justify-content: space-between; align-items: center;">
				<div style="display: flex; align-items: center;">
					<span style="margin-right: 10px; font-size: 16px;">订单状态</span>
					<el-select v-model="statevalue" placeholder="请选择" style="width: 200px; margin-right: 20px;">
					    <el-option
					      v-for="item in options"
					      :key="item.value"
					      :label="item.label"
					      :value="item.value">
					    </el-option>
					  </el-select>
					<el-button type="success" size="medium" @click="queryFun()">查询</el-button>
				</div>
				<div style="display: flex; gap: 10px;">
					<el-button type="warning" size="small" @click="refresh_order()">刷新订单</el-button>
				</div>
			</div>
			<div v-if="nodatas">
				<div class="tab-list">
					<span v-for="(item,index) in tablist" :key="index" style="flex: 1; text-align: center;">{{item}}</span>
				</div>
				<div class="tab-table" v-for="(item,index) in tabcont" :key="index">
					<div style="flex: 1; text-align: center;">{{item.order_time}}</div>
					<div style="flex: 1; text-align: center;">{{item.table_number || '-'}}</div>
					<div style="flex: 1; text-align: center;">{{item.number_of_diners || '-'}}</div>
					<div style="flex: 1; text-align: center;">
						<el-button type="primary" size="medium" :loading="index == deta_load ? true : false" @click="detailed_menu(index,item.id)" class="detail-menu-btn">详细菜单</el-button>
					</div>
					<div style="flex: 1; text-align: center;">{{Price(item.sett_amount || 0)}}</div>
					<div style="flex: 1; text-align: center;">
						<el-button type="info" size="small" v-if="item.order_receiving == 'mis_orders'" :loading="index == rece_load ? true : false" @click="receiving(index,item.id)">待接单</el-button>
						<el-button size="small" type="success" disabled v-if="item.transac_status == 'success'">已结账</el-button>
						<el-button size="small" type="primary" v-if="item.transac_status == 'unsettled' && item.order_receiving == 'rec_order' && item.order_status != 1" :loading="index == complete_load ? true : false" @click="completeOrder(index,item.id)">完成订单</el-button>
						<el-button size="small" type="success" disabled v-if="item.order_status == 1">已完成</el-button>
						<el-button size="small" type="success" v-if="item.transac_status == 'unsettled' && item.order_receiving == 'rec_order' && item.order_status != 1" :loading="index == check_load ? true : false" @click="checkout(index,item.id)">待结账</el-button>
						<el-button size="small" type="default" disabled v-if="!item.order_receiving && !item.transac_status">-</el-button>
					</div>
				</div>
				<el-pagination
				  background
				  layout="prev, pager, next"
				  :hide-on-single-page="true"
				  :total="total"
				  @current-change="currentchange">
				</el-pagination>
			</div>
			<div class="nodatas" v-if="!nodatas">{{nodvalue}}</div>
			<el-dialog
			  title="详细菜单"
			  :visible.sync="dialogVisible"
			  width="600px"
			  :center="true">
			  <div>
				  <div class="menu-padd" v-for="(item,index) in user_menu" :key="index">
						  <div class="Menu-details menu-margin">
							  <span style="flex: 2;">{{item.name}}</span>
							  <span style="flex: 1; text-align: center;">{{item.quantity}}{{item.unit}}</span>
							  <el-button 
								  type="danger" 
								  size="small" 
								  :loading="returnDishLoading === index" 
								  @click="returnDish(index, item.id)"
								  style="margin-left: 20px;">
								  退菜
							  </el-button>
						  </div>
				  </div>
			  </div>
			</el-dialog>
		</div>
		<div style="height: 120px;"></div>
	</div>
</template>

<script>
import {staff} from '../../../config/state-type.js'
const Price = require('e-commerce_price')
export default{
	data() {
		return {
			Price:Price,
			options:staff(),
			loading: true,
			nodatas:true,
			dialogVisible:false,
			deta_load:-1,
			rece_load:-1,
			check_load:-1,
			complete_load:-1,
			returnDishLoading:-1,
			currentOrderIndex:-1,
			statevalue:'',
			nodvalue:'没有订单数据',
			total:0,
			pagenum:0,
			tablist:['交易时间','桌号','用餐人数','菜单详情','交易金额(元)','交易状态'],
			tabcont:[],
			user_menu:[]
		}
	},
	methods:{
		goBack() {
			this.$router.push('/waiter-index').catch(() => {})
		},
		currentchange(e){
			this.pagenum = e - 1
			this.obtainorder(0)
		},
		async obtainorder(vle){
			try{
				let res = await new this.Request(this.Urls.m().obtainorder,{"pageNum":this.pagenum+1,"query":this.statevalue}).modepost()
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
		async detailed_menu(index,id){
			this.currentOrderIndex = index
			this.deta_load = index
			try{
				let res = await new this.Request(this.Urls.m().vieworder + '?id=' + id).modeget()
				this.user_menu = res.data.list
				this.deta_load = -1
				this.dialogVisible = true
			}catch(e){
				this.deta_load = -1
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		},
		async returnDish(index, orderDetailId){
			this.returnDishLoading = index
			try{
				this.$confirm('确定要退掉这道菜吗？', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(async () => {
					let res = await new this.Request(this.Urls.m().returnDish + '?orderDetailId=' + orderDetailId).modeget()
					if(res.status === 200 && res.data.code === 0){
						new this.mytitle(this.$message,'success','退菜成功').funtitle()
						// 移除退掉的菜品
						this.user_menu.splice(index, 1)
						// 重新获取订单列表更新金额
						if(this.currentOrderIndex !== -1){
							await this.obtainorder(0)
						}
						// 如果没有菜品了，关闭弹窗
						if(this.user_menu.length === 0){
							this.dialogVisible = false
						}
					}else{
						new this.mytitle(this.$message,'error',res.data.msg || '退菜失败').funtitle()
					}
				}).catch(() => {})
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			} finally {
				this.returnDishLoading = -1
			}
		},
		async receiving(index,id){
			this.rece_load = index
			try{
				let res = await new this.Request(this.Urls.m().receiving,{id:id,order_receiving:'rec_order'}).modepost()
				this.$set(this.tabcont[index],'order_receiving','rec_order')
				new this.mytitle(this.$message,'success','接单成功').funtitle()
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			} finally {
				this.rece_load = -1
			}
		},
		async completeOrder(index,id){
			this.complete_load = index
			try{
				let res = await new this.Request(this.Urls.m().complete,{id:id}).modepost()
				this.$set(this.tabcont[index],'order_status',1)
				new this.mytitle(this.$message,'success','订单已完成').funtitle()
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			} finally {
				this.complete_load = -1
			}
		},
		async checkout(index,id){
			this.check_load = index
			try{
				let res = await new this.Request(this.Urls.m().checkout,{id:id,transac_status:'success'}).modepost()
				this.$set(this.tabcont[index],'transac_status','success')
				new this.mytitle(this.$message,'success','结账成功').funtitle()
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			} finally {
				this.check_load = -1
			}
		},
		refresh_order(){
			this.loading = true
			this.deta_load = -1
			this.rece_load = -1
			this.check_load = -1
			this.complete_load = -1
			this.obtainorder(1)
		},
		queryFun(){
			this.pagenum = 0
			this.obtainorder(0)
		}
	},
	created() {
		this.obtainorder(0)
	}
}
</script>

<style>
.tab-list span {
    width: auto !important;
}
.tab-table div {
    width: auto !important;
}
</style>

<style scoped="scoped">
@import url("../../../style/pubiss.css");
@import url("../../../style/popup.css");

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
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    padding: 20px;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    border-radius: 12px;
}

.button-view .el-select {
    margin-right: 12px;
}

.button-view .el-select .el-input__inner {
    border-radius: 8px;
    border: 2px solid #e2e8f0;
    transition: all 0.3s;
}

.button-view .el-select .el-input__inner:focus {
    border-color: #2563eb;
    box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.button-view span {
    font-size: 15px;
    color: #475569;
    font-weight: 500;
    margin-right: 12px;
}

.button-view .el-button {
    border-radius: 8px;
    font-weight: 500;
    padding: 10px 20px;
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

.tab-list {
    border: none;
    font-size: 14px;
    color: #64748b;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    height: 56px;
    border-radius: 12px 12px 0 0;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    font-size: 12px;
    display: flex;
    align-items: center;
}

.tab-list span {
    color: #475569;
}

.tab-table {
    background-color: #ffffff;
    padding: 18px 0;
    border-bottom: 1px solid #f1f5f9;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    display: flex;
    align-items: center;
}

.tab-table:hover {
    background: linear-gradient(135deg, #f8fafc 0%, #fefefe 100%);
    transform: scale(1.01);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.tab-table:first-of-type {
    border-radius: 0 0 12px 12px;
}

.tab-table .el-button {
    border-radius: 8px;
    font-weight: 500;
    padding: 8px 16px;
    transition: all 0.3s;
    border: none;
}

.tab-table .el-button--success {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.tab-table .el-button--success:hover {
    background: linear-gradient(135deg, #059669 0%, #047857 100%);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.tab-table .el-button--primary {
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.tab-table .el-button--primary:hover {
    background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.tab-table .el-button--info {
    background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
    color: white;
}

.tab-table .el-button--info:hover {
    background: linear-gradient(135deg, #4f46e5 0%, #4338ca 100%);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}

::v-deep .el-pagination.is-background .el-pager li:not(.disabled).active {
    background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
    color: #fff;
    border-radius: 8px;
    font-weight: 600;
}

::v-deep .el-pagination.is-background .el-pager li {
    border-radius: 8px;
    margin: 0 4px;
    font-weight: 500;
    transition: all 0.3s;
}

::v-deep .el-pagination.is-background .el-pager li:hover:not(.disabled) {
    background: #e0f2fe;
    color: #0369a1;
}

::v-deep .el-pagination button {
    border-radius: 8px;
    margin: 0 4px;
}

.el-pagination {
    text-align: center;
    margin: 40px 0 20px 0;
}

.nodatas {
    text-align: center;
    padding: 60px 20px;
    color: #94a3b8;
    font-size: 16px;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    border-radius: 12px;
    margin-top: 20px;
}

.menu-padd {
    border-bottom: 1px solid #f8f8f8;
}

.Menu-details {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 50px;
}

.menu-margin {
    margin: 20px 0;
}

.detail-menu-btn {
    background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%) !important;
    box-shadow: 0 4px 12px rgba(245, 158, 11, 0.4) !important;
    font-weight: 600 !important;
    letter-spacing: 1px !important;
    border: none !important;
}

.detail-menu-btn:hover {
    background: linear-gradient(135deg, #d97706 0%, #b45309 100%) !important;
    transform: translateY(-2px) !important;
    box-shadow: 0 6px 16px rgba(245, 158, 11, 0.5) !important;
}


</style>
