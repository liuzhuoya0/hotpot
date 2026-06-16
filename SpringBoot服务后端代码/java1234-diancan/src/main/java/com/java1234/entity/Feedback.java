package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Date;

/**
 * 反馈实体（顾客投诉与建议）
 */
@TableName("t_feedback")
@Data
public class Feedback {

    @TableId(type = IdType.AUTO)
    private Integer id; // 编号

    @TableField("table_id")
    private Integer tableId; // 桌台ID

    @TableField("order_id")
    private Integer orderId; // 订单ID

    private String type; // 类型：complaint-投诉, suggestion-建议

    private String content; // 内容

    private Integer state; // 状态 0:待处理 1:处理中 2:已处理

    @TableField("create_time")
    @JsonSerialize(using=CustomDateTimeSerializer.class)
    private Date createTime; // 创建时间

    @TableField("handle_time")
    @JsonSerialize(using=CustomDateTimeSerializer.class)
    private Date handleTime; // 处理时间

    @TableField("handle_result")
    private String handleResult; // 处理结果
}
