-- 更新菜品图片
UPDATE t_dish SET image = '白菜.jpg' WHERE name = '白菜';
UPDATE t_dish SET image = '生菜.jpg' WHERE name = '生菜';
UPDATE t_dish SET image = '西洋菜.jpg' WHERE name = '西洋菜';
UPDATE t_dish SET image = '梨.jpg' WHERE name = '梨';
UPDATE t_dish SET image = '葡萄.jpg' WHERE name = '葡萄';
UPDATE t_dish SET image = '牛肉.jpg' WHERE name = '牛肉';
UPDATE t_dish SET image = '猪肉.jpg' WHERE name = '猪肉';
UPDATE t_dish SET image = '可乐.jpg' WHERE name = '可乐';
UPDATE t_dish SET image = '啤酒.jpg' WHERE name = '啤酒';
UPDATE t_dish SET image = '麻辣.jpg' WHERE name = '麻辣';
UPDATE t_dish SET image = '清汤.jpg' WHERE name = '清汤';
UPDATE t_dish SET image = '苹果.jpg' WHERE name = '苹果';
UPDATE t_dish SET image = '羊肉.jpg' WHERE name = '羊肉';
UPDATE t_dish SET image = '凉茶.jpg' WHERE name = '凉茶';

-- 查询更新后的结果
SELECT id, name, image FROM t_dish;
