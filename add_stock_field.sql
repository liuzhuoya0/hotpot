-- 添加库存字段到菜品表
ALTER TABLE t_dish ADD COLUMN stock INT DEFAULT 0 COMMENT '库存';

-- 查询验证
SELECT id, name, stock FROM t_dish;
