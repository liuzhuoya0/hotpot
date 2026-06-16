package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 菜品单位实体
 */
@TableName("t_unit")
@Data
public class Unit {

    private Integer id; // 编号

    private String name; // 名称

    @TableField(exist = false)
    private String label; // 标签

    @TableField(exist = false)
    private String value; // 值

}
