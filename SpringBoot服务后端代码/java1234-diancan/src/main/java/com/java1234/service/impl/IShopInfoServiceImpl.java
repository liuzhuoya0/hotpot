package com.java1234.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java1234.entity.ShopInfo;
import com.java1234.mapper.ShopInfoMapper;
import com.java1234.service.IShopInfoService;
import org.springframework.stereotype.Service;

@Service("shopInfoService")
public class IShopInfoServiceImpl extends ServiceImpl<ShopInfoMapper, ShopInfo> implements IShopInfoService {

}
