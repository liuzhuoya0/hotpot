package com.java1234.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java1234.constant.SystemConstant;
import com.java1234.entity.Admin;
import com.java1234.entity.R;
import com.java1234.entity.ShopInfo;
import com.java1234.service.IAdminService;
import com.java1234.service.IShopInfoService;
import com.java1234.util.JwtUtils;
import com.java1234.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员 Controller
 */
@RestController
public class AdminController {

    @Autowired
    private IAdminService adminService;

    @Autowired
    private IShopInfoService shopInfoService;

    private final static Logger logger= LoggerFactory.getLogger(AdminController.class);

    /**
     * 管理员登录
     * @param admin
     * @return
     */
    @PostMapping("/adminLogin")
    public R adminLogin(@RequestBody Admin admin){
        if(admin==null){
            return R.error();
        }
        if(StringUtil.isEmpty(admin.getUserName())){
            return R.error("用户名不能为空！");
        }
        if(StringUtil.isEmpty(admin.getPassword())){
            return R.error("密码不能为空！");
        }
        Admin resultAdmin = adminService.getOne(new QueryWrapper<Admin>().eq("user_name", admin.getUserName()));
        System.out.println("查询到的管理员：" + resultAdmin);
        if(resultAdmin==null){
            return R.error("用户名不存在！");
        }
        if(!resultAdmin.getPassword().trim().equals(admin.getPassword())){
            return R.error("用户名或者密码错误！");
        }
        String token = JwtUtils.createJWT("-1", "admin", SystemConstant.JWT_TTL);
        Map<String,Object> resultMap=new HashMap<String,Object>();
        resultMap.put("token",token);
        resultMap.put("resultAdmin",resultAdmin);
        resultMap.put("userInfo",resultAdmin);  // 添加 userInfo 字段
        resultMap.put("role","admin");  // 添加 role 字段
        return R.ok(resultMap);
    }

    /**
     * 修改密码
     * @param admin
     * @return
     */
    @PostMapping("/admin/modifyPassword")
    public R modifyPassword(@RequestBody Admin admin){
        if(StringUtil.isEmpty(admin.getUserName())){
            return R.error("用户名不能为空！");
        }
        if(StringUtil.isEmpty(admin.getPassword())){
            return R.error("原密码不能为空！");
        }
        if(StringUtil.isEmpty(admin.getNewPassword())){
            return R.error("新密码不能为空！");
        }
        
        // 查询管理员
        Admin existingAdmin = adminService.getOne(new QueryWrapper<Admin>().eq("user_name", admin.getUserName()));
        if(existingAdmin == null){
            return R.error("用户名不存在！");
        }
        
        // 验证原密码
        if(!existingAdmin.getPassword().trim().equals(admin.getPassword())){
            return R.error("原密码错误！");
        }
        
        // 更新密码
        existingAdmin.setPassword(admin.getNewPassword());
        adminService.updateById(existingAdmin);
        return R.ok();
    }

    /**
     * 获取门店信息
     * @return
     */
    @GetMapping("/admin/shopInfo")
    public R getShopInfo(){
        ShopInfo shopInfo = shopInfoService.getById(1);
        if(shopInfo == null) {
            shopInfo = new ShopInfo();
            shopInfo.setShopName("123火锅店");
            shopInfo.setOpenTime("09:00");
            shopInfo.setCloseTime("22:00");
            shopInfo.setIsOpen(true);
            shopInfo.setId(1);
            shopInfoService.save(shopInfo);
        }
        
        System.out.println("获取门店信息: " + shopInfo);
        System.out.println("isOpen值: " + shopInfo.getIsOpen());
        
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("shopInfo", shopInfo);
        return R.ok(resultMap);
    }

    /**
     * 保存门店信息
     * @param shopInfo
     * @return
     */
    @PostMapping("/admin/saveShopInfo")
    public R saveShopInfo(@RequestBody ShopInfo shopInfo){
        System.out.println("接收到的门店信息: " + shopInfo);
        System.out.println("接收到的isOpen值: " + shopInfo.getIsOpen());
        
        shopInfo.setId(1);
        ShopInfo existing = shopInfoService.getById(1);
        System.out.println("数据库中已存在的记录: " + existing);
        
        if(existing != null) {
            boolean updateResult = shopInfoService.updateById(shopInfo);
            System.out.println("更新结果: " + updateResult);
        } else {
            boolean saveResult = shopInfoService.save(shopInfo);
            System.out.println("保存结果: " + saveResult);
        }
        
        ShopInfo afterSave = shopInfoService.getById(1);
        System.out.println("保存后从数据库重新读取的记录: " + afterSave);
        System.out.println("保存后的isOpen值: " + (afterSave != null ? afterSave.getIsOpen() : null));
        
        return R.ok();
    }

    /**
     * 获取门店信息（小程序端）
     * @return
     */
    @GetMapping("/shopInfo")
    public R getShopInfoForUser(){
        ShopInfo shopInfo = shopInfoService.getById(1);
        if(shopInfo == null) {
            shopInfo = new ShopInfo();
            shopInfo.setShopName("123火锅店");
            shopInfo.setOpenTime("09:00");
            shopInfo.setCloseTime("22:00");
            shopInfo.setIsOpen(true);
            shopInfo.setId(1);
            shopInfoService.save(shopInfo);
        }
        
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("shopInfo", shopInfo);
        return R.ok(resultMap);
    }

}
