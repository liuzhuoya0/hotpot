<template>
	<div class="ordering">
		<div class="heading">系统设置</div>
		
		<div class="content-view">
			<div class="form-section">
				<div class="section-title">
					<i class="el-icon-office-building"></i>
					<span>门店基础信息</span>
				</div>
				
				<div class="form-item">
					<label>门店名称</label>
					<el-input 
						v-model="shopInfo.shopName" 
						placeholder="请输入门店名称">
					</el-input>
				</div>
				
				<div class="form-row">
					<div class="form-item">
						<label>营业开始时间</label>
						<el-time-picker
							v-model="shopInfo.openTime"
							placeholder="选择开始时间"
							format="HH:mm"
							value-format="HH:mm"
							:clearable="false">
						</el-time-picker>
					</div>
					<div class="form-item">
						<label>营业结束时间</label>
						<el-time-picker
							v-model="shopInfo.closeTime"
							placeholder="选择结束时间"
							format="HH:mm"
							value-format="HH:mm"
							:clearable="false">
						</el-time-picker>
					</div>
				</div>
			</div>

			<div class="form-section">
				<div class="section-title">
					<i class="el-icon-switch-button"></i>
					<span>门店状态</span>
				</div>
				
				<div class="status-card">
					<div class="status-display">
						<div class="status-icon" :class="{ 'open': shopInfo.isOpen, 'closed': !shopInfo.isOpen }">
							<i :class="shopInfo.isOpen ? 'el-icon-success' : 'el-icon-close'"></i>
						</div>
						<div class="status-text">
							<div class="status-title">{{ shopInfo.isOpen ? '营业中' : '已打烊' }}</div>
							<div class="status-desc">{{ shopInfo.isOpen ? '顾客可以正常点餐' : '暂不接受点餐' }}</div>
						</div>
					</div>
					<div class="status-switch">
						<el-button 
							:type="shopInfo.isOpen ? 'danger' : 'success'" 
							size="medium"
							@click="toggleShopStatus">
							<i :class="shopInfo.isOpen ? 'el-icon-close' : 'el-icon-check'"></i>
							{{ shopInfo.isOpen ? '点击打烊' : '点击营业' }}
						</el-button>
					</div>
				</div>
				
				<div class="warning-tip" v-if="!shopInfo.isOpen">
					<i class="el-icon-warning-outline"></i>
					<span>当前门店已打烊，顾客进入点餐页面将看到打烊提示</span>
				</div>
			</div>

			<div class="form-section">
				<div class="section-title">
					<i class="el-icon-lock"></i>
					<span>修改密码</span>
				</div>
				
				<div class="form-item">
					<label>原密码</label>
					<el-input 
						v-model="passwordForm.oldPassword" 
						type="password"
						placeholder="请输入原密码"
						show-password>
					</el-input>
				</div>
				
				<div class="form-item">
					<label>新密码</label>
					<el-input 
						v-model="passwordForm.newPassword" 
						type="password"
						placeholder="请输入新密码"
						show-password>
					</el-input>
				</div>
				
				<div class="form-item">
					<label>确认密码</label>
					<el-input 
						v-model="passwordForm.confirmPassword" 
						type="password"
						placeholder="请再次输入新密码"
						show-password>
					</el-input>
				</div>
				

			</div>

			<div class="form-actions">
				<el-button type="primary" size="medium" @click="saveSettings" :loading="saving">
					<i class="el-icon-check"></i> 保存设置
				</el-button>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	data() {
		return {
			saving: false,
			shopInfo: {
				shopName: '123火锅店',
				openTime: '09:00',
				closeTime: '22:00',
				isOpen: true
			},
			passwordForm: {
				oldPassword: '',
				newPassword: '',
				confirmPassword: ''
			}
		}
	},
	methods: {
		toggleShopStatus() {
			this.shopInfo.isOpen = !this.shopInfo.isOpen;
			if (this.shopInfo.isOpen) {
				new this.mytitle(this.$message, 'success', '门店已开始营业！').funtitle();
			} else {
				new this.mytitle(this.$message, 'warning', '门店已打烊！').funtitle();
			}
		},
		
		async saveSettings() {
			if (!this.shopInfo.shopName) {
				new this.mytitle(this.$message, 'error', '请输入门店名称！').funtitle();
				return;
			}
			
			this.saving = true;
			try {
				let res = await new this.Request(this.Urls.m().saveShopInfo, this.shopInfo).modepost();
				
				if (res && res.data && res.data.code === 0) {
					localStorage.setItem('shopInfo', JSON.stringify(this.shopInfo));
					
					if (this.passwordForm.oldPassword || this.passwordForm.newPassword || this.passwordForm.confirmPassword) {
						if (!this.passwordForm.oldPassword) {
							new this.mytitle(this.$message, 'error', '请输入原密码！').funtitle();
							this.saving = false;
							return;
						}
						if (!this.passwordForm.newPassword) {
							new this.mytitle(this.$message, 'error', '请输入新密码！').funtitle();
							this.saving = false;
							return;
						}
						if (!this.passwordForm.confirmPassword) {
							new this.mytitle(this.$message, 'error', '请再次输入新密码！').funtitle();
							this.saving = false;
							return;
						}
						if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
							new this.mytitle(this.$message, 'error', '两次输入的新密码不一致！').funtitle();
							this.saving = false;
							return;
						}
						
						let currentUser = null;
						try {
							currentUser = JSON.parse(localStorage.getItem('currentUser'));
						} catch (e) {
							console.error('获取用户信息失败', e);
						}
						
						if (!currentUser || !currentUser.userName) {
							new this.mytitle(this.$message, 'error', '获取用户信息失败，请重新登录！').funtitle();
							this.saving = false;
							return;
						}
						
						let passwordRes = await new this.Request(this.Urls.m().updatePassword, {
							userName: currentUser.userName,
							password: this.passwordForm.oldPassword,
							newPassword: this.passwordForm.newPassword
						}).modepost();
						
						if (passwordRes && passwordRes.data && passwordRes.data.code === 0) {
							this.passwordForm = {
								oldPassword: '',
								newPassword: '',
								confirmPassword: ''
							};
							new this.mytitle(this.$message, 'success', '设置保存成功，密码已修改！').funtitle();
						} else {
							new this.mytitle(this.$message, 'error', (passwordRes && passwordRes.data && passwordRes.data.msg) || '密码修改失败！').funtitle();
						}
					} else {
						new this.mytitle(this.$message, 'success', '设置保存成功！').funtitle();
					}
				} else {
					new this.mytitle(this.$message, 'error', (res && res.data && res.data.msg) || '保存失败，请重试！').funtitle();
				}
			} catch (e) {
				console.error('保存设置失败', e);
				new this.mytitle(this.$message, 'error', '保存失败，请重试！').funtitle();
			} finally {
				this.saving = false;
			}
		},
		
		async loadSettings() {
			try {
				let res = await new this.Request(this.Urls.m().getShopInfo, {}).modeget();
				
				console.log('加载设置接口响应:', res);
				
				if (res && res.data && res.data.code === 0 && res.data.data && res.data.data.shopInfo) {
					console.log('从后端获取到的门店信息:', res.data.data.shopInfo);
					this.shopInfo = Object.assign(this.shopInfo, res.data.data.shopInfo);
					localStorage.setItem('shopInfo', JSON.stringify(this.shopInfo));
					return;
				}
			} catch (e) {
				console.error('加载门店信息失败', e);
			}
			
			const saved = localStorage.getItem('shopInfo');
			if (saved) {
				try {
					this.shopInfo = Object.assign(this.shopInfo, JSON.parse(saved));
				} catch (err) {
					console.error('解析设置失败', err);
				}
			}
		}
	},
	created() {
		this.loadSettings();
	}
}
</script>

<style scoped="scoped">
@import url("../../../style/pubiss.css");

.ordering {
	background: #f5f7fa;
	min-height: 100vh;
	padding: 20px;
}

.heading {
	font-size: 20px;
	font-weight: bold;
	margin: 0 20px 30px 20px;
}

.content-view {
	background: #ffffff;
	border-radius: 5px;
	padding: 30px;
	margin: 0 20px;
}

.form-section {
	margin-bottom: 32px;
}

.section-title {
	display: flex;
	align-items: center;
	font-size: 16px;
	font-weight: bold;
	color: #333;
	margin-bottom: 20px;
	padding-bottom: 10px;
	border-bottom: 1px solid #eee;
}

.section-title i {
	font-size: 18px;
	margin-right: 8px;
	color: #409eff;
}

.form-item {
	display: flex;
	align-items: center;
	margin-bottom: 20px;
}

.form-item label {
	width: 120px;
	font-size: 14px;
	color: #606266;
	flex-shrink: 0;
}

.form-item >>> .el-input {
	flex: 1;
	max-width: 400px;
}

.form-item >>> .el-time-picker {
	flex: 1;
	max-width: 400px;
	width: 100%;
}

.form-item >>> .el-time-picker .el-input {
	max-width: none;
}

.form-row {
	display: flex;
	gap: 20px;
}

.form-row .form-item {
	flex: 1;
}

.form-row .form-item label {
	width: 120px;
}

.status-card {
	background: #f9f9f9;
	border-radius: 5px;
	padding: 20px;
	display: flex;
	justify-content: space-between;
	align-items: center;
	border: 1px solid #eee;
}

.status-display {
	display: flex;
	align-items: center;
	gap: 16px;
}

.status-icon {
	width: 50px;
	height: 50px;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.status-icon.open {
	background: #67c23a;
}

.status-icon.closed {
	background: #f56c6c;
}

.status-icon i {
	font-size: 24px;
	color: #ffffff;
}

.status-text {
	display: flex;
	flex-direction: column;
}

.status-title {
	font-size: 18px;
	font-weight: bold;
	color: #303133;
	margin-bottom: 4px;
}

.status-desc {
	font-size: 14px;
	color: #909399;
}

.status-switch {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 12px;
}

.warning-tip {
	display: flex;
	align-items: center;
	background: #fdf6ec;
	border: 1px solid #f5dab1;
	border-radius: 5px;
	padding: 12px 16px;
	margin-top: 20px;
}

.warning-tip i {
	font-size: 18px;
	color: #e6a23c;
	margin-right: 10px;
	flex-shrink: 0;
}

.warning-tip span {
	font-size: 14px;
	color: #e6a23c;
}

.form-actions {
	display: flex;
	gap: 12px;
	padding-left: 120px;
	margin-top: 8px;
}

.form-actions >>> .el-button {
	padding: 10px 24px;
	font-size: 14px;
	border-radius: 4px;
}

@media (max-width: 768px) {
	.content-view {
		padding: 20px;
		margin: 0 10px;
	}
	
	.heading {
		font-size: 18px;
		margin: 0 10px 20px 10px;
	}
	
	.form-item {
		flex-direction: column;
		align-items: flex-start;
	}
	
	.form-item label {
		width: 100%;
		margin-bottom: 8px;
	}
	
	.form-item >>> .el-input,
	.form-item >>> .el-time-picker {
		max-width: 100%;
		width: 100%;
	}
	
	.form-row {
		flex-direction: column;
		gap: 0;
	}
	
	.form-row .form-item {
		flex: none;
	}
	
	.status-card {
		flex-direction: column;
		gap: 20px;
		align-items: stretch;
	}
	
	.status-switch {
		align-items: flex-start;
	}
	
	.form-actions {
		padding-left: 0;
	}
}
</style>
