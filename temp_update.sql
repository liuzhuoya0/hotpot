ALTER TABLE t_order ADD COLUMN review_state INT DEFAULT 0 COMMENT '评价状态：0未评价，1已评价';

CREATE TABLE IF NOT EXISTS t_order_review (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    order_id INT NOT NULL COMMENT '订单ID',
    table_id INT NOT NULL COMMENT '桌号',
    pot_taste_score INT NOT NULL COMMENT '火锅锅底口味评分(1-5)',
    dish_freshness_score INT NOT NULL COMMENT '菜品新鲜度评分(1-5)',
    dining_experience_score INT NOT NULL COMMENT '用餐体验评分(1-5)',
    content TEXT COMMENT '文字评价内容',
    images TEXT COMMENT '评价图片，多个图片用逗号分隔',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_table_id (table_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单评价表';
