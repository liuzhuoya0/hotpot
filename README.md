微信小程序扫码点餐系统 - 项目部署说明

一、系统环境要求
1.1 硬件环境
组件	最低配置	推荐配置
服务器	2核CPU / 4GB内存 / 50GB硬盘	4核CPU / 8GB内存 / 100GB硬盘
数据库服务器	2核CPU / 4GB内存 / 100GB硬盘	4核CPU / 8GB内存 / 200GB SSD

1.2 软件环境
软件	版本要求	用途
JDK	1.8+	后端运行环境
Maven	3.6+	后端依赖管理
MySQL	5.7+	数据库
Node.js	14.0+	前端运行环境
npm	6.0+	前端包管理
Vue CLI	3.0+	前端构建工具
HBuilderX	3.0+	小程序开发工具
微信开发者工具	最新版	小程序调试
Nginx	1.18+	反向代理（可选）

二、数据库部署
2.1 安装 MySQL
Windows 系统：
1. 下载 MySQL 5.7 安装包：https://dev.mysql.com/downloads/mysql/5.7.html
2. 运行安装程序，选择"Developer Default"
3. 设置 root 密码（建议：root123456）
4. 配置环境变量，将 MySQL bin 目录添加到 PATH

2.2 创建数据库
mysql -u root -p
CREATE DATABASE db_diancan DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE db_diancan;
source /path/to/db_diancan.sql;

2.3 数据库包含的表
•	t_admin - 管理员表
•	t_waiter - 服务员表
•	t_category - 菜品类目表
•	t_dish - 菜品表
•	t_table - 桌位表
•	t_order - 订单表
•	t_order_detail - 订单详情表
•	t_shop_info - 门店信息表
•	t_comment - 评论表
•	t_service_request - 服务请求表

2.4 数据库配置
修改后端配置文件 application.yml：
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_diancan?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的数据库密码
    driver-class-name: com.mysql.cj.jdbc.Driver

三、后端部署
3.1 安装 JDK
1. 下载 JDK 1.8：https://www.oracle.com/java/technologies/javase-jdk8-downloads.html
2. 安装到默认路径
3. 配置环境变量：JAVA_HOME 和 PATH
验证安装：java -version

3.2 安装 Maven
1. 下载 Maven：https://maven.apache.org/download.cgi
2. 解压到 C:\apache-maven-3.6.3
3. 配置环境变量：MAVEN_HOME 和 PATH
验证安装：mvn -version

3.3 配置 Maven 镜像（加速下载）
修改 settings.xml，添加阿里云镜像：
<mirror>
  <id>aliyunmaven</id>
  <mirrorOf>*</mirrorOf>
  <name>阿里云公共仓库</name>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>

3.4 后端项目配置
1. 修改数据库配置（application.yml）
2. 修改服务器端口（可选，默认81）
3. 配置 JWT 密钥（SystemConstant.java）

3.5 后端打包
方式一：IDEA 打包
- 打开项目，点击右侧 Maven 面板
- 双击 package 命令
- 在 target 目录生成 .jar 文件

方式二：命令行打包
cd SpringBoot服务后端代码/java1234-diancan
mvn clean package -DskipTests

3.6 后端运行
开发环境：java -jar target/java1234-diancan-1.0-SNAPSHOT.jar
生产环境：nohup java -jar app.jar > app.log 2>&1 &
验证：访问 http://localhost:81

四、Vue 管理端部署
4.1 安装 Node.js
1. 下载 Node.js 14.0+：https://nodejs.org/zh-cn/download/
2. 运行安装程序
验证：node -v 和 npm -v

4.2 配置 npm 镜像
npm config set registry https://registry.npm.taobao.org

4.3 安装 Vue CLI
npm install -g @vue/cli
验证：vue --version

4.4 前端项目配置
1. 修改 API 接口地址（src/config/urls.js）
2. 修改 GoEasy 配置（如果使用）

4.5 安装依赖并运行
cd Vue管理员PC端代码/saoma-diancan-pc
npm install
npm run serve
访问地址：http://localhost:8080

4.6 生产环境打包
npm run build
生成的文件在 dist 目录，使用 Nginx 托管

4.7 Nginx 配置
server {
    listen 80;
    server_name your-domain.com;
    location / {
        root /path/to/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    location /api/ {
        proxy_pass http://localhost:81/;
    }
}

五、uni-app 小程序端部署
5.1 安装 HBuilderX
1. 下载 HBuilderX：https://www.dcloud.io/hbuilderx.html
2. 解压到任意目录
3. 运行 HBuilderX.exe

5.2 导入项目
1. 打开 HBuilderX
2. 文件 -> 打开目录
3. 选择 Uni用户点餐端代码/diancan-user

5.3 配置项目
1. 修改 API 接口地址（common/config.js）
2. 配置微信小程序 AppID（manifest.json）

5.4 运行到微信小程序
1. 在 HBuilderX 中点击"运行" -> "运行到小程序模拟器" -> "微信开发者工具"
2. 首次运行会提示配置微信开发者工具路径
3. 自动打开微信开发者工具并编译

5.5 发布小程序
1. 在 HBuilderX 中：发行 -> 小程序-微信
2. 在微信公众平台提交审核：https://mp.weixin.qq.com/
3. 审核通过后发布

六、服务员端部署
服务员端与管理员端共用同一个 Vue 项目，通过登录时的角色区分：
- 管理员登录 -> 跳转到管理员界面
- 服务员登录 -> 跳转到服务员界面

访问地址：http://localhost:8080/#/login（同一地址，不同账号）

默认账号：
管理员：用户名 admin，密码 123456
服务员：用户名 waiter001，密码 123456

七、完整部署流程
7.1 开发环境部署步骤
1.	步骤1：安装 MySQL 5.7
2.	步骤2：创建数据库 db_diancan，导入 SQL 脚本
3.	步骤3：安装 JDK 1.8，配置环境变量
4.	步骤4：安装 Maven 3.6，配置阿里云镜像
5.	步骤5：启动后端项目（端口 81）
6.	步骤6：安装 Node.js 14+
7.	步骤7：安装 Vue CLI 3.0
8.	步骤8：启动 Vue 管理端（端口 8080）
9.	步骤9：安装 HBuilderX
10.	步骤10：导入 uni-app 项目，运行到微信开发者工具
11.	步骤11：测试完整流程

