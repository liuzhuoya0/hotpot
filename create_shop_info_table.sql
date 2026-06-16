-- 创建门店信息表
CREATE TABLE IF NOT EXISTS `t_shop_info` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `shop_name` VARCHAR(100) DEFAULT NULL COMMENT '门店名称',
  `open_time` VARCHAR(20) DEFAULT NULL COMMENT '营业开始时间',
  `close_time` VARCHAR(20) DEFAULT NULL COMMENT '营业结束时间',
  `is_open` TINYINT(1) DEFAULT 1 COMMENT '是否营业：1-营业中，0-打烊',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店信息表';

-- 插入默认数据
INSERT INTO `t_shop_info` (`shop_name`, `open_time`, `close_time`, `is_open`) 
VALUES ('123火锅店', '09:00', '22:00', 1);
