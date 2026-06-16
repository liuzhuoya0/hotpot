package com.java1234.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java1234.entity.Order;
import com.java1234.entity.OrderReview;
import com.java1234.entity.R;
import com.java1234.service.IOrderReviewService;
import com.java1234.service.IOrderService;
import com.java1234.util.DateUtil;
import com.java1234.util.JwtUtils;
import com.java1234.util.StringUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order-review")
public class OrderReviewController {

    @Autowired
    private IOrderReviewService orderReviewService;

    @Autowired
    private IOrderService orderService;

    @Value("${reviewImgsFilePath}")
    private String reviewImgsFilePath;

    @RequestMapping("/create")
    public R create(@RequestBody OrderReview orderReview){
        orderReview.setCreateTime(new Date());
        orderReviewService.save(orderReview);

        Order order = orderService.getById(orderReview.getOrderId());
        if (order != null) {
            order.setReview_state(1);
            orderService.updateById(order);
        }

        return R.ok();
    }

    @RequestMapping("/get-by-order-id")
    public R getByOrderId(Integer orderId){
        QueryWrapper<OrderReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        OrderReview review = orderReviewService.getOne(queryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("data", review);
        return R.ok(map);
    }

    @RequestMapping("/list")
    public R list(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if (StringUtil.isEmpty(token)) {
            return R.error("请先登录");
        }
        Claims claims = JwtUtils.validateJWT(token).getClaims();
        if (claims == null) {
            return R.error("鉴权失败");
        }
        String subject = claims.getSubject();
        if (!"admin".equals(subject) && !"waiter".equals(subject)) {
            return R.error("权限不足");
        }
        
        List<OrderReview> list = orderReviewService.list();
        Map<String, Object> map = new HashMap<>();
        map.put("data", list);
        return R.ok(map);
    }

    @RequestMapping("/list-by-table-id")
    public R listByTableId(Integer tableId){
        if (tableId == null) {
            return R.error("桌号不能为空");
        }
        QueryWrapper<OrderReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("table_id", tableId);
        queryWrapper.orderByDesc("create_time");
        List<OrderReview> list = orderReviewService.list(queryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("data", list);
        return R.ok(map);
    }

    @RequestMapping("/uploadImage")
    public Map<String,Object> uploadImage(MultipartFile file)throws Exception{
        Map<String,Object> map=new HashMap<String,Object>();
        if(!file.isEmpty()){
            String fileName = file.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String newFileName= DateUtil.getCurrentDateStr()+suffixName;

            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(reviewImgsFilePath+newFileName));
            map.put("code", 0);
            map.put("msg", "上传成功");
            Map<String,Object> map2=new HashMap<String,Object>();
            map2.put("imageName", newFileName);
            map2.put("src", "/image/review/"+newFileName);
            map.put("data", map2);
        }
        return map;
    }
}
