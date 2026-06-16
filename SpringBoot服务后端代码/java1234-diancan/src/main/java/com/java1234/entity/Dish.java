package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Date;

/**
 * 菜品实体
 */
@TableName("t_dish")
@Data
public class Dish {

    private Integer id; // 编号

    private String name; // 菜品名称

    private String image; // 菜品图片

    private Integer category_id; // 类别ID

    @TableField(select = false)
    private Category type; // 类别

    private Float price; // 商品单价

    private Integer unit_id; // 单位ID

    private Integer state = 1; // 状态 1：上架 0：下架

    private String detail; // 菜品详情

    private String origin; // 食材产地

    private Integer stock; // 库存



}
