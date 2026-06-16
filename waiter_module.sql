-- 服务员模块数据库表结构
-- 创建时间: 2026-02-24

USE `db_diancan`;

-- 1. 服务员表
DROP TABLE IF EXISTS `t_waiter`;
CREATE TABLE `t_waiter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `state` int(11) DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `t_waiter` (`user_name`, `password`, `name`, `phone`, `state`) 
VALUES ('waiter', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138000', 1);

-- 2. 服务请求表
DROP TABLE IF EXISTS `t_service_request`;
CREATE TABLE `t_service_request` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `table_id` int(11) DEFAULT NULL,
  `order_id` int(11) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `content` varchar(500) DEFAULT NULL,
  `state` int(11) DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `handle_time` datetime DEFAULT NULL,
  `handle_remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_table_id` (`table_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_state` (`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 反馈表
DROP TABLE IF EXISTS `t_feedback`;
CREATE TABLE `t_feedback` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `table_id` int(11) DEFAULT NULL,
  `order_id` int(11) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `content` text,
  `state` int(11) DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `handle_time` datetime DEFAULT NULL,
  `handle_result` text,
  PRIMARY KEY (`id`),
  KEY `idx_table_id` (`table_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_state` (`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SELECT 'Database tables created successfully!' AS message;
SELECT 'Default waiter account: waiter / 123456' AS waiter_account;
