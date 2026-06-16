package com.java1234.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java1234.entity.R;
import com.java1234.entity.ServiceRequest;
import com.java1234.entity.Feedback;
import com.java1234.service.IServiceRequestService;
import com.java1234.service.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/waiter")
public class AdminWaiterServiceController {

    @Autowired
    private IServiceRequestService serviceRequestService;

    @Autowired
    private IFeedbackService feedbackService;

    @PostMapping("/serviceRequest/list")
    public R getServiceRequestList(@RequestBody Map<String, Object> params) {
        try {
            Integer pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
            Integer pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            Integer state = null;
            if (params.get("state") != null && !params.get("state").toString().isEmpty()) {
                try {
                    state = Integer.parseInt(params.get("state").toString());
                } catch (NumberFormatException e) {
                    state = null;
                }
            }

            Page<ServiceRequest> page = new Page<>(pageNum, pageSize);
            QueryWrapper<ServiceRequest> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("create_time");
            
            if (state != null) {
                queryWrapper.eq("state", state);
            }

            Page<ServiceRequest> result = serviceRequestService.page(page, queryWrapper);
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("list", result.getRecords());
            resultMap.put("total", result.getTotal());
            
            return R.ok(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取服务请求列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/serviceRequest/updateState")
    public R updateServiceRequestState(Integer id, Integer state, String handleRemark) {
        try {
            ServiceRequest request = serviceRequestService.getById(id);
            if (request != null) {
                request.setState(state);
                if (state == 2) {
                    request.setHandleTime(new Date());
                    request.setHandleRemark(handleRemark);
                }
                serviceRequestService.updateById(request);
                return R.ok();
            } else {
                return R.error("服务请求不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("更新服务请求状态失败：" + e.getMessage());
        }
    }

    @PostMapping("/feedback/list")
    public R getFeedbackList(@RequestBody Map<String, Object> params) {
        try {
            Integer pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
            Integer pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            Integer state = null;
            if (params.get("state") != null && !params.get("state").toString().isEmpty()) {
                try {
                    state = Integer.parseInt(params.get("state").toString());
                } catch (NumberFormatException e) {
                    state = null;
                }
            }

            Page<Feedback> page = new Page<>(pageNum, pageSize);
            QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("create_time");
            
            if (state != null) {
                queryWrapper.eq("state", state);
            }

            Page<Feedback> result = feedbackService.page(page, queryWrapper);
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("list", result.getRecords());
            resultMap.put("total", result.getTotal());
            
            return R.ok(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取反馈列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/feedback/updateState")
    public R updateFeedbackState(Integer id, Integer state, String handleResult) {
        try {
            Feedback feedback = feedbackService.getById(id);
            if (feedback != null) {
                feedback.setState(state);
                if (state == 2) {
                    feedback.setHandleTime(new Date());
                    feedback.setHandleResult(handleResult);
                }
                feedbackService.updateById(feedback);
                return R.ok();
            } else {
                return R.error("反馈不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("更新反馈状态失败：" + e.getMessage());
        }
    }
}
