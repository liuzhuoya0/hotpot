package com.java1234.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.java1234.entity.OrderDetail;

import java.util.List;
import java.util.Map;


/**
 * 订单细表Service接口
 */
public interface IOrderDetailService extends IService<OrderDetail> {

    List<OrderDetail> listByOrderId(Integer orderId);

    List<Map<String, Object>> getDishSalesCount();

}
