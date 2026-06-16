<template>
	<div class="ordering">
		<div class="back-button" @click="goBack">
			<i class="el-icon-back"></i> 返回
		</div>
		<div class="heading">服务请求</div>
		<div class="content-view">
			<div class="query-view">
				<div class="quotation-query">
					<div>请求状态</div>
					<div>
						<el-select v-model="statevalue" placeholder="请选择">
						    <el-option label="全部" value=""></el-option>
						    <el-option label="待处理" :value="0"></el-option>
						    <el-option label="处理中" :value="1"></el-option>
						    <el-option label="已完成" :value="2"></el-option>
						  </el-select>
					</div>
				</div>
				<div class="quotation-query"><el-button type="success" size="medium" @click="queryFun()">查询</el-button></div>
			</div>
			<div class="button-view">
				<el-button type="warning" size="small" @click="refresh()">刷新</el-button>
			</div>
			<div v-if="nodatas">
				<div class="tab-list">
					<span v-for="(item,index) in tablist" :key="index">{{item}}</span>
				</div>
				<div class="tab-table-quo" v-for="(item,index) in tabcont" :key="index">
					<div>{{item.tableId || '-'}}</div>
					<div>{{getTypeText(item.type)}}</div>
					<div>{{item.content || '-'}}</div>
					<div>
						<el-tag :type="getStateType(item.state)">
							{{ getStateText(item.state) }}
						</el-tag>
					</div>
					<div>{{item.createTime ? formatDate(item.createTime) : '-'}}</div>
					<div>
						<el-button size="small" type="primary" v-if="item.state === 0" :loading="loadState[index]" @click="handleRequest(index, item, 1)">处理中</el-button>
						<el-button size="small" type="success" v-if="item.state === 1" :loading="loadState[index]" @click="completeRequest(index, item)">完成</el-button>
						<el-button size="small" disabled v-if="item.state === 2">已完成</el-button>
					</div>
				</div>
				<el-pagination
				  background
				  layout="prev, pager, next"
				  :hide-on-single-page="true"
				  :total="total"
				  @current-change="currentchange"
				  >
				</el-pagination>
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
			nodatas:true,
			statevalue:'',
			nodvalue:'没有服务请求',
			total:0,
			pagenum:0,
			tablist:['桌号','类型','内容','状态','创建时间','操作'],
			tabcont:[],
			loadState: {}
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
		getTypeText(type) {
			const typeMap = {
				'add_soup': '加汤',
				'urge': '催菜',
				'change_plate': '换碟',
				'other': '其他'
			}
			return typeMap[type] || type
		},
		getStateType(state) {
			const typeMap = {
				0: 'danger',
				1: 'warning',
				2: 'success'
			}
			return typeMap[state] || 'info'
		},
		getStateText(state) {
			const textMap = {
				0: '待处理',
				1: '处理中',
				2: '已完成'
			}
			return textMap[state] || '未知'
		},
		formatDate(dateStr) {
			if (!dateStr) return '-'
			const date = new Date(dateStr)
			return date.toLocaleString()
		},
		async loadList(){
			try{
				let res = await new this.Request(this.Urls.m().serviceRequestList || 'admin/waiter/serviceRequest/list',{"pageNum":this.pagenum+1,"state":this.statevalue}).modepost()
				this.nodatas = res.data.total == 0 ? false : true
				this.tabcont = res.data.list || []
				this.total = res.data.total
				this.loading = false
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		},
		async handleRequest(index, item, state) {
			this.$set(this.loadState, index, true)
			try {
				let res = await new this.Request(`${this.Urls.m().updateServiceRequestState || 'admin/waiter/serviceRequest/updateState'}?id=${item.id}&state=${state}`).modeget()
				if(res.status === 200 && res.data.code === 0){
					new this.mytitle(this.$message,'success','状态更新成功').funtitle()
					this.$set(this.tabcont[index], 'state', state)
				}else{
					new this.mytitle(this.$message,'warning',res.data.msg || '更新失败').funtitle()
				}
			} catch (e) {
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			} finally {
				this.$set(this.loadState, index, false)
			}
		},
		completeRequest(index, item) {
			this.$prompt('请输入处理备注', '完成服务请求', {
			  confirmButtonText: '确定',
			  cancelButtonText: '取消'
			}).then(({ value }) => {
				this.$set(this.loadState, index, true)
				new this.Request(`${this.Urls.m().updateServiceRequestState || 'admin/waiter/serviceRequest/updateState'}?id=${item.id}&state=2&handleRemark=${encodeURIComponent(value || '')}`).modeget().then(res => {
					if(res.status === 200 && res.data.code === 0){
						new this.mytitle(this.$message,'success','已完成').funtitle()
						this.$set(this.tabcont[index], 'state', 2)
					}else{
						new this.mytitle(this.$message,'warning',res.data.msg || '更新失败').funtitle()
					}
					this.$set(this.loadState, index, false)
				}).catch(() => {
					this.$set(this.loadState, index, false)
				})
			}).catch(() => {
			})
		},
		refresh(){
			this.loading = true
			this.loadList()
		},
		queryFun(){
			this.pagenum = 0
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

.query-view {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 30px;
    padding: 20px;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    border-radius: 12px;
}

.quotation-query {
    display: flex;
    align-items: center;
    gap: 12px;
}

.quotation-query > div:first-child {
    font-size: 15px;
    color: #475569;
    font-weight: 500;
}

.query-view .el-select .el-input__inner {
    border-radius: 8px;
    border: 2px solid #e2e8f0;
    transition: all 0.3s;
}

.query-view .el-select .el-input__inner:focus {
    border-color: #2563eb;
    box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.query-view .el-button {
    border-radius: 8px;
    font-weight: 500;
    padding: 10px 20px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border: none;
}

.query-view .el-button--success {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.query-view .el-button--success:hover {
    background: linear-gradient(135deg, #059669 0%, #047857 100%);
    box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4);
    transform: translateY(-2px);
}

.button-view {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 20px;
}

.button-view .el-button {
    border-radius: 8px;
    font-weight: 500;
    padding: 10px 20px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border: none;
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
    justify-content: space-between;
}

.tab-list span {
    color: #475569;
    flex: 1 !important;
    text-align: center;
    width: auto !important;
}

.tab-table-quo {
    background-color: #ffffff;
    padding: 18px 0;
    border-bottom: 1px solid #f1f5f9;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.tab-table-quo div {
    flex: 1 !important;
    text-align: center;
    width: auto !important;
}

.tab-table-quo:hover {
    background: linear-gradient(135deg, #f8fafc 0%, #fefefe 100%);
    transform: scale(1.01);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.tab-table-quo:first-of-type {
    border-radius: 0 0 12px 12px;
}

.tab-table-quo .el-tag {
    border-radius: 20px;
    padding: 6px 14px;
    font-weight: 600;
    font-size: 12px;
    border: none;
}

.tab-table-quo .el-tag--danger {
    background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
    color: #991b1b;
}

.tab-table-quo .el-tag--warning {
    background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
    color: #92400e;
}

.tab-table-quo .el-tag--success {
    background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
    color: #065f46;
}

.tab-table-quo .el-button {
    border-radius: 8px;
    font-weight: 500;
    padding: 8px 16px;
    transition: all 0.3s;
    border: none;
}

.tab-table-quo .el-button--primary {
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.tab-table-quo .el-button--primary:hover {
    background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.tab-table-quo .el-button--success {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.tab-table-quo .el-button--success:hover {
    background: linear-gradient(135deg, #059669 0%, #047857 100%);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
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


</style>
