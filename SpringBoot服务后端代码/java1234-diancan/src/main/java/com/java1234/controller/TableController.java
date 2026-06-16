package com.java1234.controller;

import com.java1234.entity.R;
import com.java1234.entity.Table;
import com.java1234.service.ITableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 桌号控制器（小程序端）
 */
@RestController
@RequestMapping("/table")
public class TableController {

    @Autowired
    private ITableService tableService;

    /**
     * 查询所有桌号
     * @return
     */
    @RequestMapping("/listAll")
    public R listAll(){
        List<Table> list = tableService.list();
        Map<String,Object> map=new HashMap<>();
        map.put("tableListAll",list);
        return R.ok(map);
    }

}
