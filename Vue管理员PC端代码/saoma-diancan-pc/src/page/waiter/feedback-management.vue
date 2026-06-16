<template>
	<div class="ordering" v-loading.fullscreen.lock="loading">
		<div class="back-button" @click="goBack">
			<i class="el-icon-back"></i> 返回
		</div>
		<div class="heading">反馈处理</div>
		<div class="content-view">
			<div class="button-view" style="display: flex; justify-content: space-between; align-items: center;">
				<div style="display: flex; align-items: center;">
					<span style="margin-right: 10px; font-size: 16px;">反馈状态</span>
					<el-select v-model="statevalue" placeholder="请选择" style="width: 200px; margin-right: 20px;">
					    <el-option label="全部" value=""></el-option>
					    <el-option label="待处理" :value="0"></el-option>
					    <el-option label="处理中" :value="1"></el-option>
					    <el-option label="已处理" :value="2"></el-option>
					  </el-select>
					<el-button type="success" size="medium" @click="queryFun()">查询</el-button>
				</div>
				<div style="display: flex; gap: 10px;">
					<el-button type="warning" size="small" @click="refresh()">刷新</el-button>
				</div>
			</div>
			<div v-if="nodatas">
				<div class="tab-list">
					<span v-for="(item,index) in tablist" :key="index" style="flex: 1; text-align: center;">{{item}}</span>
				</div>
				<div class="tab-table" v-for="(item,index) in tabcont" :key="index">
					<div style="flex: 1; text-align: center;">{{item.tableId || '-'}}</div>
					<div style="flex: 1; text-align: center;">{{getTypeText(item.type)}}</div>
					<div style="flex: 1; text-align: center;">{{item.content || '-'}}</div>
					<div style="flex: 1; text-align: center;">
						<el-tag :type="getStateType(item.state)">
							{{ getStateText(item.state) }}
						</el-tag>
					</div>
					<div style="flex: 1; text-align: center;">{{item.createTime ? formatDate(item.createTime) : '-'}}</div>
					<div style="flex: 1; text-align: center;">
						<el-button size="small" type="primary" v-if="item.state === 0" :loading="loadState[index]" @click="handleFeedback(index, item, 1)">处理中</el-button>
						<el-button size="small" type="success" v-if="item.state === 1" :loading="loadState[index]" @click="completeFeedback(index, item)">完成</el-button>
						<el-button size="small" disabled v-if="item.state === 2">已处理</el-button>
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
			nodvalue:'没有反馈数据',
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
				'complaint': '投诉',
				'suggestion': '建议'
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
				2: '已处理'
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
				let res = await new this.Request(this.Urls.m().feedbackList || 'admin/waiter/feedback/list',{"pageNum":this.pagenum+1,"state":this.statevalue}).modepost()
				this.nodatas = res.data.total == 0 ? false : true
				this.tabcont = res.data.list || []
				this.total = res.data.total
				this.loading = false
			}catch(e){
				new this.mytitle(this.$message,'error','服务器发生错误,请重试').funtitle()
			}
		},
		async handleFeedback(index, item, state) {
			this.$set(this.loadState, index, true)
			try {
				let res = await new this.Request(`${this.Urls.m().updateFeedbackState || 'admin/waiter/feedback/updateState'}?id=${item.id}&state=${state}`).modeget()
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
		completeFeedback(index, item) {
			this.$prompt('请输入处理结果', '完成反馈处理', {
			  confirmButtonText: '确定',
			  cancelButtonText: '取消'
			}).then(({ value }) => {
				this.$set(this.loadState, index, true)
				new this.Request(`${this.Urls.m().updateFeedbackState || 'admin/waiter/feedback/updateState'}?id=${item.id}&state=2&handleResult=${encodeURIComponent(value || '')}`).modeget().then(res => {
					if(res.status === 200 && res.data.code === 0){
						new this.mytitle(this.$message,'success','已处理').funtitle()
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

.tab-table .el-tag {
    border-radius: 20px;
    padding: 6px 14px;
    font-weight: 600;
    font-size: 12px;
    border: none;
}

.tab-table .el-tag--danger {
    background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
    color: #991b1b;
}

.tab-table .el-tag--warning {
    background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
    color: #92400e;
}

.tab-table .el-tag--success {
    background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
    color: #065f46;
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
