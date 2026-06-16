package com.java1234.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java1234.entity.HotDish;
import com.java1234.entity.Order;
import com.java1234.entity.SalesVolume;

import java.util.List;
import java.util.Map;


/**
 * 订单主表Mapper接口
 */
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据条件 分页查询
     * @param map
     * @return
     */
    public List<Order> list(Map<String,Object> map);

    /**
     * 根据条件，查询订单总记录数
     * @param map
     * @return
     */
    public Long getTotal(Map<String,Object> map);

    public List<SalesVolume> getSalesVolume(Map<String, Object> map);

    public List<HotDish> getHotDishes(Map<String, Object> map);

}
