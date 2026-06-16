<template>
	<div class="waiter-container">
		<div class="header">
			<h2>服务员端</h2>
			<div class="user-info">
				<span>欢迎，{{currentUser.name || currentUser.userName}}</span>
				<el-button type="danger" size="small" @click="logout">退出</el-button>
			</div>
		</div>
		<div class="content">
			<div class="menu-grid">
				<div class="menu-item" @click="goTo('table')">
					<i class="el-icon-house" style="font-size: 40px; color: #409EFF;"></i>
					<span>桌位管理</span>
				</div>
				<div class="menu-item" @click="goTo('order')">
					<i class="el-icon-document" style="font-size: 40px; color: #67C23A;"></i>
					<span>订单处理</span>
				</div>
				<div class="menu-item" @click="goTo('service')">
					<i class="el-icon-bell" style="font-size: 40px; color: #E6A23C;"></i>
					<span>服务请求</span>
				</div>
				<div class="menu-item" @click="goTo('dish')">
					<i class="el-icon-goods" style="font-size: 40px; color: #F56C6C;"></i>
					<span>菜品管理</span>
				</div>
				<div class="menu-item" @click="goTo('ordering')">
					<i class="el-icon-plus" style="font-size: 40px; color: #67C23A;"></i>
					<span>现场点餐</span>
				</div>
				<div class="menu-item" @click="goTo('feedback')">
					<i class="el-icon-chat-dot-round" style="font-size: 40px; color: #909399;"></i>
					<span>反馈处理</span>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	name: 'WaiterIndex',
	data() {
		return {
			currentUser: {}
		}
	},
	mounted() {
		this.currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
	},
	methods: {
		goTo(page) {
			const routeMap = {
				'table': '/waiter-table',
				'order': '/waiter-order',
				'service': '/waiter-service',
				'dish': '/waiter-dish',
				'ordering': '/waiter-ordering',
				'feedback': '/waiter-feedback'
			}
			if (routeMap[page]) {
				this.$router.push(routeMap[page]).catch(() => {})
			} else {
				this.$message.info('功能开发中...')
			}
		},
		logout() {
			this.$confirm('确定要退出登录吗？', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				localStorage.removeItem('token')
				localStorage.removeItem('currentUser')
				localStorage.removeItem('role')
				this.$router.push({ name: 'login' })
			}).catch(() => {})
		}
	}
}
</script>

<style scoped>
.waiter-container {
	min-height: 100vh;
	background-color: #f5f7fa;
}

.header {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding: 30px 50px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.header h2 {
	color: white;
	margin: 0;
}

.user-info {
	display: flex;
	align-items: center;
	gap: 20px;
}

.user-info span {
	color: white;
	font-size: 16px;
}

.content {
	padding: 50px;
}

.menu-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
	gap: 30px;
	max-width: 1200px;
	margin: 0 auto;
}

.menu-item {
	background: white;
	border-radius: 12px;
	padding: 40px 20px;
	text-align: center;
	cursor: pointer;
	transition: all 0.3s;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.menu-item:hover {
	transform: translateY(-5px);
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.menu-item span {
	display: block;
	margin-top: 20px;
	font-size: 18px;
	color: #333;
}
</style>
