package com.java1234.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java1234.entity.Category;
import com.java1234.entity.Dish;
import com.java1234.entity.R;
import com.java1234.service.ICategoryService;
import com.java1234.service.IDishService;
import com.java1234.service.IOrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 菜品控制器
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private IDishService dishService;

    @Autowired
    private ICategoryService categoryService;
    
    @Autowired
    private IOrderDetailService orderDetailService;


    /**
     * 查询所有菜单分类
     * @return
     */
    @RequestMapping("/listAll")
    public R listAll(){
        List<Category> categoryList = categoryService.list();
        
        Map<Integer, Integer> dishSalesMap = new HashMap<>();
        try {
            List<Map<String, Object>> salesList = orderDetailService.getDishSalesCount();
            if (salesList != null) {
                for (Map<String, Object> item : salesList) {
                    Integer dishId = ((Number) item.get("dish_id")).intValue();
                    Integer total = ((Number) item.get("total")).intValue();
                    dishSalesMap.put(dishId, total);
                }
            }
        } catch (Exception e) {
            System.out.println("获取销售统计失败: " + e.getMessage());
        }
        
        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Category category : categoryList){
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("cid", category.getId());
            categoryMap.put("label", category.getLabel());
            
            List<Dish> dishList = dishService.list(new QueryWrapper<Dish>().eq("category_id", category.getId()).eq("state", 1));
            System.out.println("分类 " + category.getLabel() + " 的菜品数量: " + (dishList != null ? dishList.size() : 0));

            List<Map<String, Object>> dishMapList = new ArrayList<>();
            if (dishList != null) {
                for (Dish dish : dishList) {
                    System.out.println("  菜品 - id=" + dish.getId() + ", name=" + dish.getName() + ", price=" + dish.getPrice() + ", state=" + dish.getState());
                    
                    Integer monthlySale = dishSalesMap.getOrDefault(dish.getId(), 0);
                    
                    Map<String, Object> dishMap = new HashMap<>();
                    dishMap.put("id", dish.getId());
                    dishMap.put("name", dish.getName());
                    dishMap.put("image", dish.getImage());
                    dishMap.put("price", dish.getPrice());
                    dishMap.put("unitprice", dish.getPrice());
                    if(dish.getUnit_id() != null && dish.getUnit_id()==1){
                        dishMap.put("unit", "份");
                    }else if(dish.getUnit_id() != null && dish.getUnit_id()==2){
                        dishMap.put("unit", "瓶");
                    }else {
                        dishMap.put("unit", "份");
                    }
                    dishMap.put("monthlysale", monthlySale);
                    dishMap.put("quantity", 0);
                    dishMap.put("detail", dish.getDetail());
                    dishMap.put("origin", dish.getOrigin());
                    dishMapList.add(dishMap);
                }
            }
            categoryMap.put("dishList", dishMapList);
            resultList.add(categoryMap);
        }
        
        Map<String,Object> map=new HashMap<>();
        map.put("allDish",resultList);
        return R.ok(map);
    }

}
