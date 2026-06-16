package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 服务请求实体（加汤、催菜、换碟等）
 */
@TableName("t_service_request")
@Data
public class ServiceRequest {

    @TableId(type = IdType.AUTO)
    private Integer id; // 编号

    @TableField("table_id")
    private Integer tableId; // 桌台ID

    @TableField("order_id")
    private Integer orderId; // 订单ID

    private String type; // 请求类型：add_soup-加汤, urge-催菜, change_plate-换碟, other-其他

    private String content; // 请求内容

    private Integer state; // 状态 0:待处理 1:处理中 2:已完成

    @TableField("create_time")
    private Date createTime; // 创建时间

    @TableField("handle_time")
    private Date handleTime; // 处理时间

    @TableField("handle_remark")
    private String handleRemark; // 处理备注
}
