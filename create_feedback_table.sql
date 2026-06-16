-- 创建反馈表
CREATE TABLE IF NOT EXISTS t_feedback (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '编号',
    table_id INT COMMENT '桌台ID',
    order_id INT COMMENT '订单ID',
    type VARCHAR(50) COMMENT '类型：complaint-投诉, suggestion-建议',
    content TEXT COMMENT '内容',
    state INT DEFAULT 0 COMMENT '状态 0:待处理 1:处理中 2:已处理',
    create_time DATETIME COMMENT '创建时间',
    handle_time DATETIME COMMENT '处理时间',
    handle_result TEXT COMMENT '处理结果'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈表（顾客投诉与建议）';
