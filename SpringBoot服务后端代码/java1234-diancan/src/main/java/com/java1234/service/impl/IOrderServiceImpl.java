package com.java1234.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java1234.entity.HotDish;
import com.java1234.entity.Order;
import com.java1234.entity.SalesVolume;
import com.java1234.mapper.OrderMapper;
import com.java1234.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 订单主表Service实现类
 */
@Service("orderService")
public class IOrderServiceImpl extends ServiceImpl<OrderMapper,Order> implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<Order> list(Map<String, Object> map) {
        return orderMapper.list(map);
    }

    @Override
    public Long getTotal(Map<String, Object> map) {
        return orderMapper.getTotal(map);
    }

    @Override
    public List<SalesVolume> getSalesVolume(String startDate, String endDate) {
        Map<String, Object> map = new HashMap<>();
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        return orderMapper.getSalesVolume(map);
    }

    @Override
    public List<HotDish> getHotDishes(String startDate, String endDate) {
        Map<String, Object> map = new HashMap<>();
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        return orderMapper.getHotDishes(map);
    }

    @Override
    public Map<String, Object> getRealTimeData(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        
        List<Order> allOrders = orderMapper.list(map);
        long totalOrders = allOrders != null ? allOrders.size() : 0;
        result.put("totalOrders", totalOrders);
        
        long paidOrders = 0;
        long unpaidOrders = 0;
        long ongoingOrders = 0;
        long completedOrders = 0;
        double totalRevenue = 0;
        
        if (allOrders != null) {
            for (Order order : allOrders) {
                if (order.getPay_state() != null && order.getPay_state() == 1) {
                    paidOrders++;
                    if (order.getTotal_price() != null) {
                        totalRevenue += order.getTotal_price();
                    }
                } else {
                    unpaidOrders++;
                }
                
                if (order.getOrder_status() != null && order.getOrder_status() == 1) {
                    completedOrders++;
                } else {
                    ongoingOrders++;
                }
            }
        }
        
        result.put("paidOrders", paidOrders);
        result.put("unpaidOrders", unpaidOrders);
        result.put("ongoingOrders", ongoingOrders);
        result.put("completedOrders", completedOrders);
        result.put("totalRevenue", totalRevenue);
        
        return result;
    }
}
