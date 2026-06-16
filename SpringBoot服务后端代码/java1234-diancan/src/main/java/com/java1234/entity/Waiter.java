package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 服务员实体
 */
@TableName("t_waiter")
@Data
public class Waiter {

    @TableId(type = IdType.AUTO)
    private Integer id; // 编号

    @TableField("user_name")
    private String userName; // 用户名

    private String password; // 密码

    private String name; // 姓名

    private String phone; // 联系电话

    private Integer state; // 状态 1:正常 0:禁用
}
