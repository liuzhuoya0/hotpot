package com.java1234.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.java1234.entity.Feedback;

import java.util.List;
import java.util.Map;

public interface IFeedbackService extends IService<Feedback> {
    public List<Feedback> list(Map<String,Object> map);
    public Long getTotal(Map<String,Object> map);
}
