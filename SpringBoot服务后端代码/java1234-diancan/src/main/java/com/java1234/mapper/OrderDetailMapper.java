package com.java1234.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java1234.entity.OrderDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 订单细表Mapper接口
 */
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

    List<OrderDetail> listByOrderId(@Param("orderId") Integer orderId);

    List<Map<String, Object>> getDishSalesCount();

}
