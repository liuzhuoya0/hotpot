package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * 菜单类别
 */
@TableName("t_category")
@Data
public class Category {

    @TableId(type = IdType.INPUT) // 手动输入 ID
    private Integer id; // 编号

    private String label; // 名称

    private String value; // 值

    private Integer count; // 数量

    private Integer sele_quantity; // 选择数量

    private String cid; //  id 标识

    @TableField(select = false)
    private List<Dish> dishList; // 拥有的菜品
}
