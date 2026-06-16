package com.java1234.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.java1234.entity.ServiceRequest;

import java.util.List;
import java.util.Map;

public interface IServiceRequestService extends IService<ServiceRequest> {
    public List<ServiceRequest> list(Map<String,Object> map);
    public Long getTotal(Map<String,Object> map);
}
