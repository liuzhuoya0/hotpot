package com.java1234.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java1234.entity.Dish;
import com.java1234.entity.Table;

import java.util.List;
import java.util.Map;

/**
 * 菜品Mapper接口
 */
public interface DishMapper extends BaseMapper<Dish> {


    List<Dish> list(Map<String, Object> map);

    Long getTotal(Map<String, Object> map);

}
