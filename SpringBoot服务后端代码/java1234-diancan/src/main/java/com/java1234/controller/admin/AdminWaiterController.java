package com.java1234.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java1234.entity.R;
import com.java1234.entity.Waiter;
import com.java1234.service.IWaiterService;
import com.java1234.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务员管理Controller
 */
@RestController
@RequestMapping("/admin/waiter")
public class AdminWaiterController {

    @Autowired
    private IWaiterService waiterService;

    /**
     * 分页查询服务员
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/list")
    public R list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                  @RequestParam(value = "name", required = false) String name) {
        Page<Waiter> pageParam = new Page<>(page, pageSize);
        QueryWrapper<Waiter> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(name)) {
            queryWrapper.like("name", name).or().like("user_name", name);
        }
        queryWrapper.orderByDesc("id");
        Page<Waiter> resultPage = waiterService.page(pageParam, queryWrapper);
        
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", resultPage.getRecords());
        resultMap.put("total", resultPage.getTotal());
        return R.ok(resultMap);
    }

    /**
     * 添加服务员
     * @param waiter
     * @return
     */
    @PostMapping("/add")
    public R add(@RequestBody Waiter waiter) {
        if (StringUtil.isEmpty(waiter.getUserName())) {
            return R.error("用户名不能为空！");
        }
        if (StringUtil.isEmpty(waiter.getPassword())) {
            return R.error("密码不能为空！");
        }
        // 检查用户名是否已存在
        Waiter existWaiter = waiterService.getOne(new QueryWrapper<Waiter>().eq("user_name", waiter.getUserName()));
        if (existWaiter != null) {
            return R.error("用户名已存在！");
        }
        if (waiter.getState() == null) {
            waiter.setState(1);
        }
        waiterService.save(waiter);
        return R.ok();
    }

    /**
     * 修改服务员
     * @param waiter
     * @return
     */
    @PostMapping("/update")
    public R update(@RequestBody Waiter waiter) {
        if (waiter.getId() == null) {
            return R.error("ID不能为空！");
        }
        // 检查用户名是否重复
        if (StringUtil.isNotEmpty(waiter.getUserName())) {
            Waiter existWaiter = waiterService.getOne(new QueryWrapper<Waiter>().eq("user_name", waiter.getUserName()));
            if (existWaiter != null && !existWaiter.getId().equals(waiter.getId())) {
                return R.error("用户名已存在！");
            }
        }
        waiterService.updateById(waiter);
        return R.ok();
    }

    /**
     * 删除服务员
     * @param id
     * @return
     */
    @GetMapping("/delete")
    public R delete(@RequestParam Integer id) {
        if (id == null) {
            return R.error("ID 不能为空！");
        }
        waiterService.removeById(id);
        return R.ok();
    }

    /**
     * 获取服务员详情
     * @param id
     * @return
     */
    @GetMapping("/detail")
    public R detail(@RequestParam Integer id) {
        if (id == null) {
            return R.error("ID不能为空！");
        }
        Waiter waiter = waiterService.getById(id);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("waiter", waiter);
        return R.ok(resultMap);
    }
}
