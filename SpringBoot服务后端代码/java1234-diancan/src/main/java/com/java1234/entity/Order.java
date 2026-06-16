package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单主表
 */
@TableName("t_order")
@Data
public class Order {

    private Integer id; // 编号

    @TableField("table_id")
    private Integer table_id; // 桌号

    @TableField("total_price")
    private Double total_price; // 总价

    @TableField("order_time")
    @JsonSerialize(using=CustomDateTimeSerializer.class)
    private Date order_time; // 下单时间

    @TableField("pay_state")
    private Integer pay_state; // 支付状态

    @TableField("review_state")
    private Integer review_state; // 评价状态：0未评价，1已评价

    @TableField("order_status")
    private Integer order_status; // 订单状态：0进行中，1已完成

    @TableField(select = false,exist = false)
    private List<OrderDetail> goods_list; // 订单详情

    // 以下字段用于前端传递数据，不存储到数据库
    @TableField(exist = false)
    private String table_number; // 桌号

    @TableField("number_of_diners")
    private String number_of_diners; // 用餐人数

    @TableField(exist = false)
    private Double sett_amount; // 计算总价

    @TableField(exist = false)
    private String transac_status; // 结账状态

    @TableField(exist = false)
    private String order_receiving;  // 接单状态

    @TableField(exist = false)
    private String order_no; // 订单号

}
