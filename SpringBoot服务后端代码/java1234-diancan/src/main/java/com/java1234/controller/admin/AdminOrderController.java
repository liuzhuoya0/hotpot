package com.java1234.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java1234.entity.*;
import com.java1234.service.IDishService;
import com.java1234.service.IOrderDetailService;
import com.java1234.service.IOrderService;
import com.java1234.util.OrderDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端-订单Controller控制器
 */
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderDetailService orderDetailService;

    @Autowired
    private IDishService dishService;

    /**
     * 根据条件分页查询
     * @param pageBean
     * @return
     */
    @RequestMapping("/list")
    public R list(@RequestBody PageBean pageBean){
        System.out.println(pageBean);
        
        Map<String,Object> map=new HashMap<>();
        String query = pageBean.getQuery() != null ? pageBean.getQuery().trim() : "";
        // 根据前端传递的状态参数，转换为 pay_state 查询条件
        if ("unsettled".equals(query)) {
            map.put("pay_state", 0);
        } else if ("success".equals(query)) {
            map.put("pay_state", 1);
        }
        // 添加日期范围查询条件
        if (pageBean.getStartDate() != null && !pageBean.getStartDate().trim().isEmpty()) {
            map.put("startDate", pageBean.getStartDate());
        }
        if (pageBean.getEndDate() != null && !pageBean.getEndDate().trim().isEmpty()) {
            map.put("endDate", pageBean.getEndDate());
        }
        map.put("start",pageBean.getStart());
        map.put("pageSize",pageBean.getPageSize());
        List<Order> list = orderService.list(map);
        Long total = orderService.getTotal(map);
        
        System.out.println("从数据库查询到的订单数量: " + (list != null ? list.size() : 0));
        
        // 手动设置前端需要的字段
        for (Order order : list) {
            System.out.println("处理订单 ID: " + order.getId());
            System.out.println("  原始数据 - table_id: " + order.getTable_id() + ", total_price: " + order.getTotal_price() + ", pay_state: " + order.getPay_state());
            
            if (order.getTable_id() != null) {
                order.setTable_number(order.getTable_id().toString());
                System.out.println("  设置 table_number: " + order.getTable_number());
            } else {
                order.setTable_number("-");
            }
            if (order.getTotal_price() != null) {
                order.setSett_amount(order.getTotal_price());
                System.out.println("  设置 sett_amount: " + order.getSett_amount());
            } else {
                order.setSett_amount(0.0);
            }
            if (order.getPay_state() != null && order.getPay_state() == 1) {
                order.setTransac_status("success");
            } else {
                order.setTransac_status("unsettled");
            }
            System.out.println("  设置 transac_status: " + order.getTransac_status());
            order.setOrder_receiving("rec_order");
            if (order.getOrder_status() == null) {
                order.setOrder_status(0);
            }
            if (order.getNumber_of_diners() != null) {
                System.out.println("  从数据库中读取到就餐人数: " + order.getNumber_of_diners());
            } else {
                order.setNumber_of_diners("1");
                System.out.println("  未找到就餐人数，设置为默认值 1");
            }
        }
        
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("orderList",list);
        resultMap.put("total",total);
        
        System.out.println("返回的订单列表: " + list);
        return R.ok(resultMap);
    }

    /**
     * 更新接单状态
     * @param order
     * @return
     */
    @PostMapping("/receiving")
    public R receiving(@RequestBody Order order){
        Order resultOrder = orderService.getById(order.getId());
        resultOrder.setOrder_receiving(order.getOrder_receiving());
        orderService.updateById(resultOrder);
        return R.ok();
    }

    /**
     * 更新结账状态
     * @param order
     * @return
     */
    @PostMapping("/checkout")
    public R checkout(@RequestBody Order order){
        Order resultOrder = orderService.getById(order.getId());
        resultOrder.setTransac_status(order.getTransac_status());
        resultOrder.setPay_state(1); // 设置支付状态为已支付
        resultOrder.setOrder_status(1); // 设置订单状态为已完成
        orderService.updateById(resultOrder);
        return R.ok();
    }

    /**
     * 更新订单完成状态
     * @param order
     * @return
     */
    @PostMapping("/complete")
    public R complete(@RequestBody Order order){
        Order resultOrder = orderService.getById(order.getId());
        resultOrder.setOrder_status(1);
        orderService.updateById(resultOrder);
        return R.ok();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public R delete(@PathVariable(value = "id") Integer id){
        // 删除订单细表的数据
        orderDetailService.remove(new QueryWrapper<OrderDetail>().eq("mId",id));
        orderService.removeById(id);
        return R.ok();
    }

    /**
     *数据分析：销售额
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/salesvolume")
    public R salesvolume(String startDate, String endDate){
       List<SalesVolume> salesVolumeList = orderService.getSalesVolume(startDate, endDate);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("salesVolumeList",salesVolumeList);
        return R.ok(resultMap);
    }

    /**
     * 数据分析：热门菜品排行
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/hotDishes")
    public R hotDishes(String startDate, String endDate){
        List<HotDish> hotDishList = orderService.getHotDishes(startDate, endDate);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("hotDishList",hotDishList);
        return R.ok(resultMap);
    }

    /**
     * 数据分析：实时经营数据
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/realTimeData")
    public R realTimeData(String startDate, String endDate){
        Map<String, Object> realTimeData = orderService.getRealTimeData(startDate, endDate);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("realTimeData",realTimeData);
        return R.ok(resultMap);
    }

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    @GetMapping("/vieworder")
    public R vieworder(Integer id){
        if (id == null) {
            return R.error("订单ID不能为空");
        }
        System.out.println("id=" + id);
        List<OrderDetail> list = orderDetailService.list(new QueryWrapper<OrderDetail>().eq("order_id", id));
        System.out.println("查询到的订单详情数量: " + (list != null ? list.size() : 0));
        
        // 手动设置前端需要的字段，从菜品表查询真实数据
        for (OrderDetail detail : list) {
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
        resultMap.put("list", list);
        return R.ok(resultMap);
    }

    /**
     * 退菜
     * @param orderDetailId
     * @return
     */
    @GetMapping("/returnDish")
    public R returnDish(Integer orderDetailId){
        if (orderDetailId == null) {
            return R.error("订单详情ID不能为空");
        }
        try {
            OrderDetail orderDetail = orderDetailService.getById(orderDetailId);
            if (orderDetail == null) {
                return R.error("订单详情不存在");
            }
            
            Integer orderId = orderDetail.getOrder_id();
            Double dishPrice = orderDetail.getPrice();
            Integer quantity = orderDetail.getQuantity();
            Double totalReturnPrice = dishPrice * quantity;
            
            // 删除订单详情
            orderDetailService.removeById(orderDetailId);
            
            // 更新订单总金额
            Order order = orderService.getById(orderId);
            if (order != null) {
                Double newTotalPrice = order.getTotal_price() - totalReturnPrice;
                if (newTotalPrice < 0) {
                    newTotalPrice = 0.0;
                }
                order.setTotal_price(newTotalPrice);
                order.setSett_amount(newTotalPrice);
                orderService.updateById(order);
            }
            
            // 恢复菜品库存
            Dish dish = dishService.getById(orderDetail.getDish_id());
            if (dish != null && dish.getStock() != null) {
                dish.setStock(dish.getStock() + quantity);
                dishService.updateById(dish);
            }
            
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("退菜失败：" + e.getMessage());
        }
    }

    /**
     * 导出订单Excel
     * @param query
     * @param startDate
     * @param endDate
     * @param response
     */
    @GetMapping("/export")
    public void export(String query, String startDate, String endDate, HttpServletResponse response) {
        try {
            Map<String,Object> map=new HashMap<>();
            if ("unsettled".equals(query)) {
                map.put("pay_state", 0);
            } else if ("success".equals(query)) {
                map.put("pay_state", 1);
            }
            // 添加日期范围查询条件
            if (startDate != null && !startDate.trim().isEmpty()) {
                map.put("startDate", startDate);
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                map.put("endDate", endDate);
            }
            List<Order> list = orderService.list(map);

            // 检查是否有数据
            if (list == null || list.isEmpty()) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":500,\"msg\":\"该时间范围内无订单数据，无法导出\"}");
                return;
            }

            // 转换为导出VO
            List<OrderExportVO> exportList = new ArrayList<>();
            for (Order order : list) {
                OrderExportVO vo = new OrderExportVO();
                vo.setId(order.getId());
                vo.setOrderTime(order.getOrder_time());
                vo.setTableNumber(order.getTable_id() != null ? order.getTable_id().toString() : "-");
                vo.setNumberOfDiners(order.getNumber_of_diners() != null ? order.getNumber_of_diners() : "1");
                vo.setSettAmount(order.getTotal_price() != null ? order.getTotal_price() : 0.0);
                vo.setTransacStatus(order.getPay_state() != null && order.getPay_state() == 1 ? "已结账" : "待结账");
                exportList.add(vo);
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("订单数据_" + System.currentTimeMillis(), "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 写出Excel
            EasyExcel.write(response.getOutputStream(), OrderExportVO.class)
                    .sheet("订单数据")
                    .doWrite(exportList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
