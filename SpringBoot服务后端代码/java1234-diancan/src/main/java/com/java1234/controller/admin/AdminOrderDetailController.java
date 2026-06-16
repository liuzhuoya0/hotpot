package com.java1234.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java1234.entity.Dish;
import com.java1234.entity.OrderDetail;
import com.java1234.entity.R;
import com.java1234.service.IDishService;
import com.java1234.service.IOrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端-订单详情Controller控制器
 */
@RestController
@RequestMapping("/admin/orderDetail")
public class AdminOrderDetailController {

    @Autowired
    private IOrderDetailService orderDetailService;

    @Autowired
    private IDishService dishService;

    /**
     * 根据订单号查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/list")
    public R listByOrderId(Integer id){
        System.out.println("id="+id);
        List<OrderDetail> orderDetailList = orderDetailService.listByOrderId(id);
        System.out.println("查询到的订单详情数量: " + (orderDetailList != null ? orderDetailList.size() : 0));
        
        // 手动设置前端需要的字段，从菜品表查询真实数据
        for (OrderDetail detail : orderDetailList) {
            System.out.println("处理订单详情 ID: " + detail.getId() + ", dish_id: " + detail.getDish_id());
            
            // 从菜品表查询菜品信息
            Dish dish = dishService.getById(detail.getDish_id());
            System.out.println("查询到的菜品: " + dish);
            
            if (dish != null) {
                detail.setName(dish.getName());
                detail.setImage(dish.getImage());
                detail.setUnit("份");
            } else {
                detail.setName("菜品" + detail.getDish_id());
                detail.setUnit("份");
            }
            
            System.out.println("  设置 name: " + detail.getName() + ", unit: " + detail.getUnit() + ", image: " + detail.getImage());
        }
        
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("list",orderDetailList);
        return R.ok(resultMap);
    }

}
