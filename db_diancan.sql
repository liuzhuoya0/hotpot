-- 创建管理员表
CREATE TABLE IF NOT EXISTS t_admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL
);

-- 创建菜单类别表
CREATE TABLE IF NOT EXISTS t_category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    label VARCHAR(50) NOT NULL,
    value VARCHAR(50) NOT NULL,
    count INT DEFAULT 0,
    sele_quantity INT DEFAULT 0,
    cid VARCHAR(50)
);

-- 创建单位表
CREATE TABLE IF NOT EXISTS t_unit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

-- 创建餐桌表
CREATE TABLE IF NOT EXISTS t_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    qr_code VARCHAR(255),
    state INT DEFAULT 0
);

-- 创建菜品表
CREATE TABLE IF NOT EXISTS t_dish (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    image VARCHAR(255),
    category_id INT,
    unit_id INT,
    state INT DEFAULT 1
);

-- 创建订单表
CREATE TABLE IF NOT EXISTS t_order (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_id INT,
    total_price DECIMAL(10, 2) NOT NULL,
    order_time DATETIME NOT NULL,
    pay_state INT DEFAULT 0
);

-- 创建订单详情表
CREATE TABLE IF NOT EXISTS t_order_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    dish_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

-- 插入初始数据
INSERT INTO t_admin (user_name, password) VALUES ('admin', '123456');

INSERT INTO t_unit (name) VALUES ('份'), ('个'), ('瓶'), ('杯');

INSERT INTO t_category (label, value, cid) VALUES 
('热菜', '1', '1'),
('凉菜', '2', '2'),
('汤品', '3', '3'),
('主食', '4', '4'),
('饮品', '5', '5');

INSERT INTO t_table (table_name) VALUES 
('桌位1'),
('桌位2'),
('桌位3'),
('桌位4'),
('桌位5');

INSERT INTO t_dish (name, price, category_id, unit_id) VALUES 
('宫保鸡丁', 38.00, 1, 1),
('麻婆豆腐', 28.00, 1, 1),
('红烧肉', 48.00, 1, 1),
('拍黄瓜', 18.00, 2, 1),
('凉拌木耳', 22.00, 2, 1),
('西红柿鸡蛋汤', 20.00, 3, 1),
('酸辣汤', 22.00, 3, 1),
('米饭', 2.00, 4, 2),
('馒头', 1.00, 4, 2),
('可乐', 8.00, 5, 3),
('雪碧', 8.00, 5, 3);
