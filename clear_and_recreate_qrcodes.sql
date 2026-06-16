-- 清空所有餐桌的二维码
UPDATE t_table SET qrcode = NULL;

-- 查看清空后的数据
SELECT id, number, qrcode, time FROM t_table;
