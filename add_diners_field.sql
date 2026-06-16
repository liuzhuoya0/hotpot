-- 为订单表添加用餐人数字段
ALTER TABLE t_order ADD COLUMN number_of_diners VARCHAR(10) DEFAULT '1' COMMENT '用餐人数';
