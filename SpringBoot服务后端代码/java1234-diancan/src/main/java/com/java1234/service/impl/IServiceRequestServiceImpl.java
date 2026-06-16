package com.java1234.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java1234.entity.ServiceRequest;
import com.java1234.mapper.ServiceRequestMapper;
import com.java1234.service.IServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("serviceRequestService")
public class IServiceRequestServiceImpl extends ServiceImpl<ServiceRequestMapper, ServiceRequest> implements IServiceRequestService {

    @Autowired
    private ServiceRequestMapper serviceRequestMapper;

    @Override
    public List<ServiceRequest> list(Map<String, Object> map) {
        return serviceRequestMapper.selectByMap(map);
    }

    @Override
    public Long getTotal(Map<String, Object> map) {
        return (long) serviceRequestMapper.selectByMap(map).size();
    }
}
