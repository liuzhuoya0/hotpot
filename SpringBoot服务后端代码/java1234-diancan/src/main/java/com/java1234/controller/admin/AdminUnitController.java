package com.java1234.controller.admin;

import com.java1234.entity.Category;
import com.java1234.entity.Unit;
import com.java1234.entity.PageBean;
import com.java1234.entity.R;
import com.java1234.service.IUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜品单位管理
 */
@RestController
@RequestMapping("/admin/unit")
public class AdminUnitController {

    @Autowired
    private IUnitService unitService;

    /**
     * 查询所有菜品单位
     * @return
     */
    @RequestMapping("/listAll")
    public R listAll(){
        System.out.println("开始查询菜品单位列表...");
        List<Unit> list = null;
        try {
            list = unitService.list();
            System.out.println("查询到的菜品单位数量: " + (list != null ? list.size() : 0));
            if (list != null) {
                for (Unit unit : list) {
                    System.out.println("  单位 - id=" + unit.getId() + ", name=" + unit.getName());
                    if (unit.getName() != null) {
                        unit.setLabel(unit.getName());
                        unit.setValue(unit.getName());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("查询菜品单位时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("unitList",list);
        return R.ok(resultMap);
    }

    /**
     * 添加
     * @param unit
     * @return
     */
    @PostMapping("/add")
    public R add(@RequestBody Unit unit){
        unit.setValue(unit.getLabel());
        unitService.save(unit);
        return R.ok();
    }


}
