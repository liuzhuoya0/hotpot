package com.java1234.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java1234.entity.Waiter;
import com.java1234.mapper.WaiterMapper;
import com.java1234.service.IWaiterService;
import org.springframework.stereotype.Service;

/**
 * 服务员Service实现类
 */
@Service
public class IWaiterServiceImpl extends ServiceImpl<WaiterMapper, Waiter> implements IWaiterService {
}
