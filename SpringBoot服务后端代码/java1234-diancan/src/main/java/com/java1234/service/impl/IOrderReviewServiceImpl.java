package com.java1234.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java1234.entity.OrderReview;
import com.java1234.mapper.OrderReviewMapper;
import com.java1234.service.IOrderReviewService;
import org.springframework.stereotype.Service;

@Service
public class IOrderReviewServiceImpl extends ServiceImpl<OrderReviewMapper, OrderReview> implements IOrderReviewService {
}
