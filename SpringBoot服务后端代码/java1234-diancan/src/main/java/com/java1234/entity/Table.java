package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 桌号实体
 */
@TableName("t_table")
@Data
public class Table implements Serializable {

    private Integer id; // 编号

    @TableField("table_name")
    private String number; // 桌号

    @TableField("qr_code")
    private String qrcode; // 二维码地址

    @TableField("state")
    private Integer state; // 状态


}
