-- 添加菜品详情和食材产地字段
ALTER TABLE t_dish ADD COLUMN detail TEXT COMMENT '菜品详情';
ALTER TABLE t_dish ADD COLUMN origin VARCHAR(255) COMMENT '食材产地';

-- 查询验证
SELECT id, name, detail, origin FROM t_dish;
