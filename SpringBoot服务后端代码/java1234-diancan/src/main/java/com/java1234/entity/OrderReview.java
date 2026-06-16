package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Date;

/**
 * 订单评价实体
 */
@TableName("t_order_review")
@Data
public class OrderReview {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("order_id")
    private Integer orderId;

    @TableField("table_id")
    private Integer tableId;

    @TableField("pot_taste_score")
    private Integer potTasteScore;

    @TableField("dish_freshness_score")
    private Integer dishFreshnessScore;

    @TableField("dining_experience_score")
    private Integer diningExperienceScore;

    private String content;

    private String images;

    @TableField("create_time")
    @JsonSerialize(using=CustomDateTimeSerializer.class)
    private Date createTime;
}
