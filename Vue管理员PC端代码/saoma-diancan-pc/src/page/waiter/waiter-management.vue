<template>
	<div class="ordering" v-loading.fullscreen.lock="loading">
		<div class="heading">服务员管理</div>
		<div class="content-view">
			<div class="button-view">
				<el-button type="success" @click="showAddDialog">
					<i class="el-icon-plus"></i> 添加服务员
				</el-button>
			</div>
			<div v-if="nodatas">
				<div class="tab-list">
					<span v-for="(item,index) in tablist" :key="index">{{item}}</span>
				</div>
				<div class="tab-table" v-for="(item,index) in tabcont" :key="index">
					<div>{{item.id}}</div>
					<div>{{item.userName}}</div>
					<div>{{item.name}}</div>
					<div>{{item.password}}</div>
					<div>
						<el-tag :type="item.state === 1 ? 'success' : 'danger'">
							{{ item.state === 1 ? '正常' : '禁用' }}
						</el-tag>
					</div>
					<div>
						<el-button size="small" type="primary" @click="showEditDialog(item)">编辑</el-button>
						<el-button size="small" type="danger" @click="handleDelete(item)">删除</el-button>
					</div>
				</div>
				<el-pagination
				  background
				  layout="prev, pager, next"
				  :hide-on-single-page="true"
				  :total="total"
				  :current-page="currentnum"
				  @current-change="currentchange">
				</el-pagination>
			</div>
			<div class="nodatas" v-if="!nodatas">{{nodvalue}}</div>
		</div>

		<el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="550px">
			<div style="padding: 10px;">
				<div class="form-item">
					<span class="form-label">用户名：</span>
					<el-input v-model="waiterForm.userName" placeholder="请输入用户名" style="width: 300px;"></el-input>
				</div>
				<div class="form-item">
					<span class="form-label">密码：</span>
					<el-input v-model="waiterForm.password" type="text" placeholder="请输入密码" style="width: 300px;"></el-input>
				</div>
				<div class="form-item">
					<span class="form-label">姓名：</span>
					<el-input v-model="waiterForm.name" placeholder="请输入姓名" style="width: 300px;"></el-input>
				</div>
				<div class="form-item">
					<span class="form-label">状态：</span>
					<el-select v-model="waiterForm.state" placeholder="请选择状态" style="width: 300px;">
						<el-option label="正常" :value="1"></el-option>
						<el-option label="禁用" :value="0"></el-option>
					</el-select>
				</div>
			</div>
			<div slot="footer">
				<el-button @click="dialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="handleSubmit">确 定</el-button>
			</div>
		</el-dialog>
		<div style="height: 120px;"></div>
	</div>
</template>

<script>
export default {
	data() {
		return {
			loading: true,
			nodatas: true,
			tablist: ['编号', '用户名', '姓名', '密码', '状态', '操作'],
			tabcont: [],
			total: 0,
			pagenum: 0,
			currentnum: 1,
			dialogVisible: false,
			dialogTitle: '添加服务员',
			isEdit: false,
			passwordVisible: false,
			waiterForm: {
				id: null,
				userName: '',
				password: '',
				name: '',
				state: 1
			},
			nodvalue: '暂无服务员数据'
		}
	},
	methods: {
		currentchange(e) {
			this.pagenum = e - 1
			this.currentnum = e
			this.loadWaiterList()
		},
		async loadWaiterList() {
			this.loading = true
			try {
				let res = await new this.Request(this.Urls.m().waiterList + '?page=' + this.currentnum + '&pageSize=10').modeget()
				if (res.data.code === 0) {
					this.tabcont = res.data.list || []
					this.total = res.data.total || 0
					this.nodatas = this.tabcont.length > 0
				}
			} catch (e) {
				console.error(e)
			} finally {
				this.loading = false
			}
		},
		showAddDialog() {
			this.dialogTitle = '添加服务员'
			this.isEdit = false
			this.passwordVisible = false
			this.waiterForm = {
				id: null,
				userName: '',
				password: '',
				name: '',
				state: 1
			}
			this.dialogVisible = true
		},
		showEditDialog(row) {
			this.dialogTitle = '编辑服务员'
			this.isEdit = true
			this.passwordVisible = false
			this.waiterForm = { ...row }
			this.dialogVisible = true
		},
		togglePasswordVisible() {
			this.passwordVisible = !this.passwordVisible
		},
		async handleSubmit() {
			try {
				let url = this.isEdit ? this.Urls.m().waiterUpdate : this.Urls.m().waiterAdd
				let res = await new this.Request(url, this.waiterForm).modepost()
				if (res.data.code === 0) {
					new this.mytitle(this.$message, 'success', this.isEdit ? '修改成功' : '添加成功').funtitle()
					this.dialogVisible = false
					this.loadWaiterList()
				} else {
					new this.mytitle(this.$message, 'error', res.data.msg || '操作失败').funtitle()
				}
			} catch (e) {
				console.error(e)
				new this.mytitle(this.$message, 'error', '服务器发生错误,请重试').funtitle()
			}
		},
		handleDelete(row) {
			this.$confirm('确定要删除该服务员吗？', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(async () => {
				try {
					let res = await new this.Request(this.Urls.m().waiterDelete + "?id=" + row.id).modeget()
					if (res.data.code === 0) {
						new this.mytitle(this.$message, 'success', '删除成功').funtitle()
						this.loadWaiterList()
					} else {
						new this.mytitle(this.$message, 'error', res.data.msg || '删除失败').funtitle()
					}
				} catch (e) {
					console.error(e)
					new this.mytitle(this.$message, 'error', '服务器发生错误，请重试').funtitle()
				}
			}).catch(() => {
			})
		}
	},
	mounted() {
		this.loadWaiterList()
	}
}
</script>

<style scoped="scoped">
@import url("../../../style/pubiss.css");

.el-pagination {
	text-align: center;
	margin: 30px 0;
}

.form-item {
	display: flex;
	align-items: center;
	margin-bottom: 20px;
}

.form-label {
	width: 120px;
	text-align: right;
	margin-right: 15px;
	font-size: 14px;
	color: #333;
}

.form-value {
	font-size: 14px;
	color: #666;
}
</style>
