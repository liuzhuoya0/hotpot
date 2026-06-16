<template>
	<div id="backcont">
		<div class="meituan-content">
			<div class="login-cont">
				<div class="logo-section">
					<i class="el-icon-s-custom login-icon"></i>
					<div class="meituan-title">扫码点餐系统</div>
					<div class="subtitle">Restaurant Ordering System</div>
				</div>
				<div class="form-section">
					<div class="meituan-user">
						<i class="el-icon-s-custom input-icon"></i>
						<el-select class="inptflex" v-model="role" placeholder="请选择角色">
							<el-option label="管理员" value="admin"></el-option>
							<el-option label="服务员" value="waiter"></el-option>
						</el-select>
					</div>
					<div class="meituan-user">
						<i class="el-icon-user input-icon"></i>
						<el-input class="inptflex" v-model="userName" placeholder="请输入账号"></el-input>
					</div>
					<div class="meituan-user">
						<i class="el-icon-lock input-icon"></i>
						<el-input class="inptflex" v-model="password" placeholder="请输入密码" show-password></el-input>
					</div>
					<el-button type="primary" class="meituan-btn" @click="signin()" :loading="load" :disabled="load">
						<i class="el-icon-right"></i> 登录
					</el-button>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
export default{
	data() {
		return {
			load:false,
			role: 'admin',
			userName: '',
			password:''
		}
	},
	methods:{
		async signin(){
		  if(this.role==''){
        new this.mytitle(this.$message,'warning','请选择角色！').funtitle()
        return;
      }
		  if(this.userName==''){
        new this.mytitle(this.$message,'warning','请输入用户名！').funtitle()
        return;
      }
      if(this.password==''){
        new this.mytitle(this.$message,'warning','请输入密码！').funtitle()
        return;
      }
			this.load = true
			let obj = {role:this.role, userName:this.userName,password:this.password}
			try{
				let res = await new this.Request(this.Urls.m().login,obj).modepost()
        console.log(res)
				if(res.data.code != 0){
					new this.mytitle(this.$message,'warning',res.data.msg || '用户名或者密码错误！').funtitle()
				}else{
          // 清除旧的用户信息
          localStorage.removeItem('currentUser')
          localStorage.removeItem('role')
          localStorage.removeItem('token')
          
					let ids = '1'
          console.log("token:"+res.data.token)
					localStorage.setItem("nuvmenuid", JSON.stringify(ids))
					localStorage.setItem("token", res.data.token)
          localStorage.setItem("currentUser", JSON.stringify(res.data.userInfo))
          localStorage.setItem("role", res.data.role)
          if(res.data.role === 'admin'){
            this.$router.push({name:'index'})
          }else{
            this.$router.push({name:'waiter-index'})
          }
				}
				this.load = false
			}catch(e){
				console.log(e)
				this.load = false
				new this.mytitle(this.$message,'info','发生错误,重试').funtitle()
			}
		}
	}
}
</script>

<style scoped="scoped">
#backcont {
	background-image: url(../../../static/login/beijing.jpg);
	background-attachment: fixed;
	background-repeat: no-repeat;
	background-size: cover;
	-webkit-background-size: cover;
	-moz-background-size: cover;
	min-height: 100vh;
	position: relative;
}

#backcont::before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: linear-gradient(135deg, rgba(247, 212, 95, 0.85) 0%, rgba(249, 222, 128, 0.75) 50%, rgba(247, 213, 98, 0.85) 100%);
	z-index: 1;
}

.meituan-content {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	z-index: 10;
}

.login-cont {
	width: 450px;
	background: rgba(255, 255, 255, 0.95);
	border-radius: 20px;
	padding: 40px 50px;
	box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
	backdrop-filter: blur(10px);
	animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
	from {
		opacity: 0;
		transform: translateY(30px);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

.logo-section {
	text-align: center;
	margin-bottom: 40px;
}

.login-icon {
	font-size: 60px;
	color: #f7d45f;
	margin-bottom: 15px;
	text-shadow: 0 2px 10px rgba(247, 212, 95, 0.4);
}

.meituan-title {
	text-align: center;
	color: #1e293b;
	font-size: 32px;
	font-weight: 800;
	font-family: 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
	letter-spacing: 2px;
	margin-bottom: 8px;
}

.subtitle {
	color: #64748b;
	font-size: 14px;
	letter-spacing: 3px;
	text-transform: uppercase;
}

.form-section {
	margin-top: 30px;
}

.meituan-user {
	width: 100%;
	margin-bottom: 25px;
	height: 50px;
	display: flex;
	align-items: center;
	background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
	border-radius: 12px;
	padding: 0 20px;
	border: 2px solid #e2e8f0;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.meituan-user:focus-within {
	border-color: #f7d45f;
	box-shadow: 0 0 0 4px rgba(247, 212, 95, 0.15);
	background: #ffffff;
}

.input-icon {
	font-size: 20px;
	color: #94a3b8;
	margin-right: 15px;
	transition: color 0.3s;
}

.meituan-user:focus-within .input-icon {
	color: #f7d45f;
}

.meituan-user p {
	width: 60px;
	color: #475569;
	font-size: 15px;
	font-weight: 500;
}

.inptflex {
	flex: 1;
}

.inptflex >>> .el-input__inner {
	border: none !important;
	background: transparent !important;
	box-shadow: none !important;
	font-size: 15px;
	color: #1e293b;
	height: 46px;
	line-height: 46px;
	padding: 0;
}

.inptflex >>> .el-input__inner::placeholder {
	color: #94a3b8;
}

.inptflex >>> .el-input__suffix {
	right: 0;
}

.inptflex >>> .el-input__suffix-inner {
	display: flex;
	align-items: center;
}

.meituan-btn {
	width: 100%;
	height: 54px;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-top: 35px;
	font-size: 17px;
	font-weight: 600;
	border-radius: 12px;
	background: linear-gradient(135deg, #f7d45f 0%, #f9de80 50%, #f7d562 100%);
	color: #1e293b;
	border: none;
	box-shadow: 0 4px 15px rgba(247, 212, 95, 0.4);
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	letter-spacing: 1px;
}

.meituan-btn:hover {
	transform: translateY(-2px);
	box-shadow: 0 8px 25px rgba(247, 212, 95, 0.5);
	background: linear-gradient(135deg, #f9de80 0%, #f7d45f 50%, #f9db76 100%);
}

.meituan-btn:active {
	transform: translateY(0);
}

.meituan-btn >>> .el-icon-loading {
	color: #1e293b;
}

.meituan-btn i {
	margin-right: 8px;
}

.inptflex >>> .el-select .el-input__inner {
	padding-right: 30px !important;
}
</style>
