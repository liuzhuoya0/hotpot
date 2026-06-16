package com.java1234.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java1234.constant.SystemConstant;
import com.java1234.entity.Admin;
import com.java1234.entity.R;
import com.java1234.entity.Waiter;
import com.java1234.service.IAdminService;
import com.java1234.service.IWaiterService;
import com.java1234.util.JwtUtils;
import com.java1234.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一登录Controller
 */
@RestController
public class LoginController {

    @Autowired
    private IAdminService adminService;

    @Autowired
    private IWaiterService waiterService;

    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * 统一登录
     * @param params
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody Map<String, String> params) {
        String role = params.get("role"); // admin或waiter
        String userName = params.get("userName");
        String password = params.get("password");

        if (StringUtil.isEmpty(role)) {
            return R.error("角色不能为空！");
        }
        if (StringUtil.isEmpty(userName)) {
            return R.error("用户名不能为空！");
        }
        if (StringUtil.isEmpty(password)) {
            return R.error("密码不能为空！");
        }

        Map<String, Object> resultMap = new HashMap<>();

        if ("admin".equals(role)) {
            // 管理员登录
            Admin resultAdmin = adminService.getOne(new QueryWrapper<Admin>().eq("user_name", userName));
            if (resultAdmin == null) {
                return R.error("用户名不存在！");
            }
            if (!resultAdmin.getPassword().trim().equals(password)) {
                return R.error("用户名或者密码错误！");
            }
            String token = JwtUtils.createJWT("-1", "admin", SystemConstant.JWT_TTL);
            resultMap.put("token", token);
            resultMap.put("userInfo", resultAdmin);
            resultMap.put("role", "admin");
        } else if ("waiter".equals(role)) {
            // 服务员登录
            Waiter resultWaiter = waiterService.getOne(new QueryWrapper<Waiter>().eq("user_name", userName));
            if (resultWaiter == null) {
                return R.error("用户名不存在！");
            }
            if (!resultWaiter.getPassword().trim().equals(password)) {
                return R.error("用户名或者密码错误！");
            }
            if (resultWaiter.getState() != null && resultWaiter.getState() == 0) {
                return R.error("账号已被禁用！");
            }
            String token = JwtUtils.createJWT(String.valueOf(resultWaiter.getId()), "waiter", SystemConstant.JWT_TTL);
            resultMap.put("token", token);
            resultMap.put("userInfo", resultWaiter);
            resultMap.put("role", "waiter");
        } else {
            return R.error("角色不存在！");
        }

        return R.ok(resultMap);
    }
}
