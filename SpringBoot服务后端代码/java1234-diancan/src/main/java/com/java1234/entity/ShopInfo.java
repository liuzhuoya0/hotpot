package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("t_shop_info")
@Data
public class ShopInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("shop_name")
    private String shopName;

    @TableField("open_time")
    private String openTime;

    @TableField("close_time")
    private String closeTime;

    @TableField("is_open")
    private Boolean isOpen;
}
