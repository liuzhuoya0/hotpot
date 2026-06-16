package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 订单详细表
 */
@TableName("t_order_detail")
@Data
public class OrderDetail {

    private Integer id; // 编号

    @TableField("order_id")
    private Integer order_id; // 订单主表Id

    @TableField("dish_id")
    private Integer dish_id; // 菜品Id

    @TableField("quantity")
    private Integer quantity; // 数量

    @TableField("price")
    private Double price; // 价格

    // 以下字段用于前端传递数据，不存储到数据库
    @TableField(exist = false)
    private String name; // 菜品名称

    @TableField(exist = false)
    private String unit; // 单位

    @TableField(exist = false)
    private String image; // 菜品图片

}
