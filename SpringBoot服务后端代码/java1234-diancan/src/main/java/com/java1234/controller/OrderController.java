package com.java1234.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java1234.entity.Dish;
import com.java1234.entity.Order;
import com.java1234.entity.OrderDetail;
import com.java1234.entity.R;
import com.java1234.entity.ServiceRequest;
import com.java1234.entity.Feedback;
import com.java1234.service.IServiceRequestService;
import com.java1234.service.IFeedbackService;
import com.java1234.mapper.OrderMapper;
import com.java1234.mapper.OrderDetailMapper;
import com.java1234.service.IDishService;
import com.java1234.service.IOrderDetailService;
import com.java1234.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderDetailService orderDetailService;

    @Autowired
    private IDishService dishService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private IServiceRequestService serviceRequestService;

    @Autowired
    private IFeedbackService feedbackService;

    @RequestMapping("/create")
    public R create(@RequestBody Order order){
        System.out.println("接收到的订单数据: " + order);
        if (order.getGoods_list() != null) {
            System.out.println("接收到的菜品列表数量: " + order.getGoods_list().size());
            for (int i = 0; i < order.getGoods_list().size(); i++) {
                OrderDetail od = order.getGoods_list().get(i);
                System.out.println("  菜品 " + i + ": name=" + od.getName() + ", dish_id=" + od.getDish_id() + ", quantity=" + od.getQuantity() + ", price=" + od.getPrice());
            }
        }
        
        // 先检查库存是否足够
        if (order.getGoods_list() != null) {
            for(OrderDetail od : order.getGoods_list()){
                Integer dishId = null;
                if (od.getDish_id() != null) {
                    dishId = od.getDish_id();
                } else if (od.getName() != null) {
                    List<Dish> dishList = dishService.list(new QueryWrapper<Dish>().eq("name", od.getName()));
                    if (dishList != null && !dishList.isEmpty()) {
                        dishId = dishList.get(0).getId();
                    }
                }
                
                if (dishId != null) {
                    Dish dish = dishService.getById(dishId);
                    if (dish != null) {
                        Integer quantity = 1;
                        try {
                            if (od.getQuantity() != null) {
                                quantity = Integer.parseInt(od.getQuantity().toString());
                            }
                        } catch (NumberFormatException e) {
                            quantity = 1;
                        }
                        
                        // 检查库存
                        if (dish.getStock() == null || dish.getStock() < quantity) {
                            return R.error("菜品【" + dish.getName() + "】库存不足！");
                        }
                    }
                }
            }
        }
        
        Order newOrder = new Order();
        newOrder.setOrder_time(new Date());
        try {
            if (order.getTable_number() != null) {
                newOrder.setTable_id(Integer.parseInt(order.getTable_number()));
            }
        } catch (NumberFormatException e) {
            newOrder.setTable_id(1);
        }
        System.out.println("保存的订单 - table_id=" + newOrder.getTable_id() + ", total_price=" + order.getSett_amount());
        newOrder.setTotal_price(order.getSett_amount());
        newOrder.setPay_state(0);
        newOrder.setNumber_of_diners(order.getNumber_of_diners());
        newOrder.setReview_state(0);
        newOrder.setOrder_status(0);
        
        orderService.save(newOrder);
        System.out.println("新订单ID: " + newOrder.getId());
        
        if (order.getGoods_list() != null) {
            for(OrderDetail od : order.getGoods_list()){
                OrderDetail newOrderDetail = new OrderDetail();
                newOrderDetail.setOrder_id(newOrder.getId());
                
                Integer dishId = 1;
                if (od.getDish_id() != null) {
                    dishId = od.getDish_id();
                } else if (od.getName() != null) {
                    List<Dish> dishList = dishService.list(new QueryWrapper<Dish>().eq("name", od.getName()));
                    if (dishList != null && !dishList.isEmpty()) {
                        dishId = dishList.get(0).getId();
                    }
                }
                newOrderDetail.setDish_id(dishId);
                
                Integer quantity = 1;
                try {
                    if (od.getQuantity() != null) {
                        quantity = Integer.parseInt(od.getQuantity().toString());
                    } else {
                        quantity = 1;
                    }
                } catch (NumberFormatException e) {
                    quantity = 1;
                }
                newOrderDetail.setQuantity(quantity);
                newOrderDetail.setPrice(od.getPrice());
                System.out.println("保存订单详情 - order_id=" + newOrder.getId() + ", dish_name=" + od.getName() + ", dish_id=" + dishId + ", quantity=" + quantity + ", price=" + newOrderDetail.getPrice());
                orderDetailService.save(newOrderDetail);
                
                // 减少库存
                Dish dish = dishService.getById(dishId);
                if (dish != null && dish.getStock() != null) {
                    dish.setStock(dish.getStock() - quantity);
                    dishService.updateById(dish);
                    System.out.println("减少库存 - dish_id=" + dishId + ", name=" + dish.getName() + ", 减少数量=" + quantity + ", 剩余库存=" + dish.getStock());
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", newOrder.getId());
        return R.ok(map);
    }

    @RequestMapping("/get")
    public R get(Integer table_id, Integer pay_state){
        if (pay_state == null) {
            pay_state = 0;
        }
        
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("pay_state", pay_state);
            
            List<Order> orderList = orderMapper.list(paramMap);
            if (orderList == null || orderList.isEmpty()) {
                return R.ok(new HashMap<String, Object>());
            }
            
            Order targetOrder = null;
            for (Order order : orderList) {
                if (order.getTable_id() != null && order.getTable_id().equals(table_id)) {
                    if (targetOrder == null || 
                        (order.getOrder_time() != null && targetOrder.getOrder_time() != null && 
                         order.getOrder_time().after(targetOrder.getOrder_time()))) {
                        targetOrder = order;
                    }
                }
            }
            
            if (targetOrder == null) {
                return R.ok(new HashMap<String, Object>());
            }
            
            System.out.println("从OrderMapper查询的订单:" + targetOrder);
            
            Map<String, Object> menuMap = new HashMap<>();
            menuMap.put("id", targetOrder.getId());
            menuMap.put("table_id", targetOrder.getTable_id());
            menuMap.put("total_price", targetOrder.getTotal_price());
            menuMap.put("order_time", targetOrder.getOrder_time());
            menuMap.put("pay_state", targetOrder.getPay_state());
            menuMap.put("review_state", targetOrder.getReview_state() != null ? targetOrder.getReview_state() : 0);
            menuMap.put("table_number", targetOrder.getTable_id() != null ? targetOrder.getTable_id().toString() : "");
            menuMap.put("number_of_diners", targetOrder.getNumber_of_diners());
            menuMap.put("sett_amount", targetOrder.getTotal_price());
            menuMap.put("transac_status", "unsettled");
            menuMap.put("order_receiving", "mis_orders");
            menuMap.put("order_no", String.valueOf(targetOrder.getId()));
            
            List<OrderDetail> goods_listFromDb = orderDetailMapper.listByOrderId(targetOrder.getId());
            System.out.println("从OrderDetailMapper查询的订单详情:" + goods_listFromDb);
            
            List<Map<String, Object>> goods_list = new java.util.ArrayList<>();
            if (goods_listFromDb != null && !goods_listFromDb.isEmpty()) {
                for (OrderDetail odFromDb : goods_listFromDb) {
                    System.out.println("处理订单详情项: id=" + odFromDb.getId() + ", dish_id=" + odFromDb.getDish_id());
                    
                    Map<String, Object> odMap = new HashMap<>();
                    odMap.put("id", odFromDb.getId());
                    odMap.put("order_id", odFromDb.getOrder_id());
                    odMap.put("dish_id", odFromDb.getDish_id());
                    odMap.put("quantity", odFromDb.getQuantity());
                    odMap.put("price", odFromDb.getPrice());
                    odMap.put("total_price", odFromDb.getPrice());
                    
                    if (odFromDb.getDish_id() != null) {
                        Dish dish = dishService.getById(odFromDb.getDish_id());
                        if (dish != null) {
                            System.out.println("找到菜品信息: name=" + dish.getName() + ", image=" + dish.getImage());
                            odMap.put("name", dish.getName());
                            odMap.put("image", dish.getImage());
                            odMap.put("unit", "份");
                        } else {
                            System.out.println("未找到菜品信息，dish_id=" + odFromDb.getDish_id());
                            odMap.put("name", "未知菜品");
                            odMap.put("image", "");
                            odMap.put("unit", "份");
                        }
                    } else {
                        System.out.println("dish_id 为 null");
                        odMap.put("name", "未知菜品");
                        odMap.put("image", "");
                        odMap.put("unit", "份");
                    }
                    goods_list.add(odMap);
                }
            }
            
            System.out.println("最终返回的goods_list数量:" + goods_list.size());
            System.out.println("最终返回的goods_list内容:" + goods_list);
            
            Map<String,Object> map=new HashMap<>();
            map.put("goods_list", goods_list);
            map.put("menu", menuMap);
            return R.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询订单失败：" + e.getMessage());
        }
    }

    @RequestMapping("/checkout")
    public R checkout(Integer orderId){
        if (orderId == null) {
            return R.error("订单ID不能为空");
        }
        Order order = orderService.getById(orderId);
        if (order == null) {
            return R.error("订单不存在");
        }
        order.setPay_state(1);
        order.setOrder_status(1);
        orderService.updateById(order);
        return R.ok();
    }

    @RequestMapping("/history")
    public R history(Integer table_id){
        try {
            System.out.println("=== 历史订单查询 ===");
            System.out.println("接收到的 table_id 参数: " + table_id);
            
            Map<String, Object> paramMap = new HashMap<>();
            if (table_id != null) {
                paramMap.put("table_id", table_id);
                System.out.println("添加查询条件: table_id = " + table_id);
            } else {
                System.out.println("table_id 为空，查询所有订单");
            }
            
            List<Order> orderList = orderMapper.list(paramMap);

            System.out.println("查询到的订单数量: " + (orderList != null ? orderList.size() : 0));
            if (orderList != null && !orderList.isEmpty()) {
                for (Order order : orderList) {
                    System.out.println("  订单详情 - id=" + order.getId() + 
                                       ", table_id=" + order.getTable_id() + 
                                       ", total_price=" + order.getTotal_price() +
                                       ", order_time=" + order.getOrder_time() +
                                       ", pay_state=" + order.getPay_state() +
                                       ", order_status=" + order.getOrder_status());
                }
            }

            List<Map<String, Object>> resultList = new java.util.ArrayList<>();
            for (Order order : orderList) {
                System.out.println("处理订单 - id=" + order.getId() + ", total_price=" + order.getTotal_price());
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", order.getId());
                orderMap.put("table_id", order.getTable_id());
                
                Double totalPrice = order.getTotal_price();
                
                orderMap.put("order_time", order.getOrder_time());
                orderMap.put("pay_state", order.getPay_state());
                orderMap.put("review_state", order.getReview_state() != null ? order.getReview_state() : 0);
                orderMap.put("order_status", order.getOrder_status() != null ? order.getOrder_status() : 0);
                orderMap.put("number_of_diners", order.getNumber_of_diners());

                List<OrderDetail> goods_listFromDb = orderDetailMapper.listByOrderId(order.getId());
                List<Map<String, Object>> goods_list = new java.util.ArrayList<>();
                
                Double calculatedTotal = 0.0;
                if (goods_listFromDb != null && !goods_listFromDb.isEmpty()) {
                    for (OrderDetail odFromDb : goods_listFromDb) {
                        Map<String, Object> odMap = new HashMap<>();
                        odMap.put("id", odFromDb.getId());
                        odMap.put("order_id", odFromDb.getOrder_id());
                        odMap.put("dish_id", odFromDb.getDish_id());
                        odMap.put("quantity", odFromDb.getQuantity());
                        odMap.put("price", odFromDb.getPrice());

                        if (odFromDb.getDish_id() != null) {
                            Dish dish = dishService.getById(odFromDb.getDish_id());
                            if (dish != null) {
                                odMap.put("name", dish.getName());
                                odMap.put("image", dish.getImage());
                            } else {
                                odMap.put("name", "未知菜品");
                                odMap.put("image", "");
                            }
                        } else {
                            odMap.put("name", "未知菜品");
                            odMap.put("image", "");
                        }
                        
                        if (odFromDb.getQuantity() != null && odFromDb.getPrice() != null) {
                            calculatedTotal += odFromDb.getQuantity() * odFromDb.getPrice();
                        }
                        
                        goods_list.add(odMap);
                    }
                }
                
                if (totalPrice == null || totalPrice == 0.0) {
                    orderMap.put("total_price", calculatedTotal);
                } else {
                    orderMap.put("total_price", totalPrice);
                }
                
                orderMap.put("goods_list", goods_list);
                resultList.add(orderMap);
            }

            Map<String, Object> map = new HashMap<>();
            map.put("data", resultList);
            
            System.out.println("返回的完整响应: " + map);
            System.out.println("返回的订单数量: " + resultList.size());
            
            return R.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询历史订单失败：" + e.getMessage());
        }
    }

    @RequestMapping("/getById")
    public R getById(Integer orderId){
        try {
            if (orderId == null) {
                return R.error("订单ID不能为空");
            }
            
            Order order = orderService.getById(orderId);
            if (order == null) {
                return R.error("订单不存在");
            }
            
            System.out.println("查询到订单 - id=" + order.getId() + ", total_price=" + order.getTotal_price());
            
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("table_id", order.getTable_id());
            
            Double totalPrice = order.getTotal_price();
            
            orderMap.put("order_time", order.getOrder_time());
            orderMap.put("pay_state", order.getPay_state());
            orderMap.put("review_state", order.getReview_state() != null ? order.getReview_state() : 0);
            orderMap.put("order_status", order.getOrder_status() != null ? order.getOrder_status() : 0);
            orderMap.put("number_of_diners", order.getNumber_of_diners());
            orderMap.put("table_number", order.getTable_id() != null ? order.getTable_id().toString() : "");
            orderMap.put("sett_amount", order.getTotal_price());
            orderMap.put("order_no", String.valueOf(order.getId()));

            List<OrderDetail> goods_listFromDb = orderDetailMapper.listByOrderId(order.getId());
            List<Map<String, Object>> goods_list = new java.util.ArrayList<>();
            
            Double calculatedTotal = 0.0;
            if (goods_listFromDb != null && !goods_listFromDb.isEmpty()) {
                for (OrderDetail odFromDb : goods_listFromDb) {
                    Map<String, Object> odMap = new HashMap<>();
                    odMap.put("id", odFromDb.getId());
                    odMap.put("order_id", odFromDb.getOrder_id());
                    odMap.put("dish_id", odFromDb.getDish_id());
                    odMap.put("quantity", odFromDb.getQuantity());
                    odMap.put("price", odFromDb.getPrice());

                    if (odFromDb.getDish_id() != null) {
                        Dish dish = dishService.getById(odFromDb.getDish_id());
                        if (dish != null) {
                            odMap.put("name", dish.getName());
                            odMap.put("image", dish.getImage());
                        } else {
                            odMap.put("name", "未知菜品");
                            odMap.put("image", "");
                        }
                    } else {
                        odMap.put("name", "未知菜品");
                        odMap.put("image", "");
                    }
                    
                    if (odFromDb.getQuantity() != null && odFromDb.getPrice() != null) {
                        calculatedTotal += odFromDb.getQuantity() * odFromDb.getPrice();
                    }
                    
                    goods_list.add(odMap);
                }
            }
            
            if (totalPrice == null || totalPrice == 0.0) {
                orderMap.put("total_price", calculatedTotal);
            } else {
                orderMap.put("total_price", totalPrice);
            }
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("menu", orderMap);
            resultMap.put("goods_list", goods_list);
            
            return R.ok(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询订单详情失败：" + e.getMessage());
        }
    }

    @RequestMapping("/service-request/create")
    public R createServiceRequest(@RequestBody ServiceRequest serviceRequest){
        try {
            System.out.println("接收到的服务请求: " + serviceRequest);
            
            serviceRequest.setCreateTime(new Date());
            serviceRequest.setState(0);
            
            serviceRequestService.save(serviceRequest);
            
            Map<String, Object> map = new HashMap<>();
            map.put("id", serviceRequest.getId());
            return R.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("创建服务请求失败：" + e.getMessage());
        }
    }

    @RequestMapping("/service-request/list")
    public R getServiceRequestList(Integer tableId){
        try {
            System.out.println("=== 查询服务请求列表 ===");
            System.out.println("接收到的 table_id 参数: " + tableId);
            
            QueryWrapper<ServiceRequest> queryWrapper = new QueryWrapper<>();
            if (tableId != null) {
                queryWrapper.eq("table_id", tableId);
            }
            queryWrapper.orderByDesc("create_time");
            
            List<ServiceRequest> list = serviceRequestService.list(queryWrapper);
            
            System.out.println("查询到的服务请求数量: " + (list != null ? list.size() : 0));
            
            Map<String, Object> map = new HashMap<>();
            map.put("data", list);
            
            System.out.println("返回的完整响应: " + map);
            
            return R.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询服务请求列表失败：" + e.getMessage());
        }
    }

    @RequestMapping("/feedback/create")
    public R createFeedback(@RequestBody Feedback feedback){
        try {
            System.out.println("接收到的反馈: " + feedback);
            
            feedback.setCreateTime(new Date());
            feedback.setState(0);
            
            feedbackService.save(feedback);
            
            Map<String, Object> map = new HashMap<>();
            map.put("id", feedback.getId());
            return R.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("创建反馈失败：" + e.getMessage());
        }
    }

    @RequestMapping("/feedback/list")
    public R getFeedbackList(Integer tableId){
        try {
            System.out.println("=== 查询反馈列表 ===");
            System.out.println("接收到的 table_id 参数: " + tableId);
            
            QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
            if (tableId != null) {
                queryWrapper.eq("table_id", tableId);
            }
            queryWrapper.orderByDesc("create_time");
            
            List<Feedback> list = feedbackService.list(queryWrapper);
            
            System.out.println("查询到的反馈数量: " + (list != null ? list.size() : 0));
            
            Map<String, Object> map = new HashMap<>();
            map.put("data", list);
            
            System.out.println("返回的完整响应: " + map);
            
            return R.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询反馈列表失败：" + e.getMessage());
        }
    }

}
