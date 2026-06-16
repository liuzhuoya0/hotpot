package com.java1234.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java1234.entity.Feedback;
import com.java1234.mapper.FeedbackMapper;
import com.java1234.service.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("feedbackService")
public class IFeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements IFeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public List<Feedback> list(Map<String, Object> map) {
        return feedbackMapper.selectByMap(map);
    }

    @Override
    public Long getTotal(Map<String, Object> map) {
        return (long) feedbackMapper.selectByMap(map).size();
    }
}
