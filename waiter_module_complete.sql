-- 服务员模块完整数据库表结构
-- 创建时间: 2026-02-24

USE `db_diancan`;

-- 1. 服务员表
DROP TABLE IF EXISTS `t_waiter`;
CREATE TABLE `t_waiter` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_name` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（MD5加密）',
  `name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `state` int(11) DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务员表';

INSERT INTO `t_waiter` (`user_name`, `password`, `name`, `phone`, `state`) 
VALUES ('waiter', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138000', 1);

-- 2. 服务请求表
DROP TABLE IF EXISTS `t_service_request`;
CREATE TABLE `t_service_request` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `table_id` int(11) DEFAULT NULL COMMENT '桌台ID',
  `order_id` int(11) DEFAULT NULL COMMENT '订单ID',
  `type` varchar(50) DEFAULT NULL COMMENT '请求类型',
  `content` varchar(500) DEFAULT NULL COMMENT '请求内容',
  `state` int(11) DEFAULT '0' COMMENT '状态：0-待处理，1-处理中，2-已完成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_remark` varchar(500) DEFAULT NULL COMMENT '处理备注',
  PRIMARY KEY (`id`),
  KEY `idx_table_id` (`table_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_state` (`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务请求表';

-- 3. 反馈表
DROP TABLE IF EXISTS `t_feedback`;
CREATE TABLE `t_feedback` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `table_id` int(11) DEFAULT NULL COMMENT '桌台ID',
  `order_id` int(11) DEFAULT NULL COMMENT '订单ID',
  `type` varchar(50) DEFAULT NULL COMMENT '类型',
  `content` text COMMENT '内容',
  `state` int(11) DEFAULT '0' COMMENT '状态：0-待处理，1-处理中，2-已处理',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_result` text COMMENT '处理结果',
  PRIMARY KEY (`id`),
  KEY `idx_table_id` (`table_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_state` (`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈表';

SELECT '数据库表创建完成！' AS message;
SELECT '默认服务员账号：waiter / 123456' AS waiter_account;
