package com.java1234.controller.admin;

import com.java1234.entity.Dish;
import com.java1234.entity.PageBean;
import com.java1234.entity.R;
import com.java1234.entity.Table;
import com.java1234.service.IDishService;
import com.java1234.util.DateUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
public class AdminDishController {

    @Autowired
    private IDishService dishService;

    @Value("${dishImgsFilePath}")
    private String dishImgsFilePath;

    /**
     * 分页显示
     * @param pageBean
     * @return
     */
    @RequestMapping("/list")
    public R list(@RequestBody Map<String, Object> params){
        System.out.println("接收到的参数: " + params);
        Map<String,Object> map=new HashMap<>();
        
        int pageNum = 1;
        int pageSize = 10;
        boolean hasCategoryFilter = false;
        
        if (params.get("pageNum") != null) {
            try {
                pageNum = Integer.parseInt(params.get("pageNum").toString());
            } catch (Exception e) {
                pageNum = 1;
            }
        }
        
        map.put("start", (pageNum - 1) * pageSize);
        map.put("pageSize", pageSize);
        
        if (params.get("categoryId") != null && !params.get("categoryId").toString().isEmpty()) {
            try {
                Integer categoryId = Integer.parseInt(params.get("categoryId").toString());
                map.put("categoryId", categoryId);
                hasCategoryFilter = true;
                System.out.println("筛选菜品类目: " + categoryId);
            } catch (Exception e) {
                System.out.println("categoryId 格式错误: " + params.get("categoryId"));
            }
        }
        
        List<Dish> list=dishService.list(map);
        System.out.println("查询到的菜品数量: " + (list != null ? list.size() : 0));
        
        List<Map<String, Object>> resultList = new java.util.ArrayList<>();
        Map<Integer, Integer> categoryIndexMap = new HashMap<>();
        int globalIndex = 0;
        
        for (Dish dish : list) {
            Map<String, Object> item = new HashMap<>();
            
            int displayId;
            if (hasCategoryFilter) {
                Integer categoryId = dish.getCategory_id();
                if (categoryId == null) {
                    categoryId = 0;
                }
                
                if (categoryIndexMap.containsKey(categoryId)) {
                    displayId = categoryIndexMap.get(categoryId) + 1;
                } else {
                    displayId = 1;
                }
                categoryIndexMap.put(categoryId, displayId);
            } else {
                globalIndex++;
                displayId = globalIndex;
            }
            
            item.put("id", displayId);
            item.put("originalId", dish.getId());
            item.put("name", dish.getName());
            item.put("image", dish.getImage());
            item.put("price", dish.getPrice());
            item.put("unitprice", dish.getPrice());
            item.put("type", dish.getType());
            item.put("onsale", dish.getState() != null && dish.getState() == 1);
            item.put("time", "");
            item.put("detail", dish.getDetail());
            item.put("origin", dish.getOrigin());
            item.put("stock", dish.getStock());
            resultList.add(item);
            System.out.println("菜品 - originalId=" + dish.getId() + ", displayId=" + displayId + ", name=" + dish.getName() + ", price=" + dish.getPrice());
        }
        
        Long total =dishService.getTotal(map);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("dishList",resultList);
        resultMap.put("total",total);
        return R.ok(resultMap);
    }

    /**
     * 添加或者更新
     * @param dish
     * @return
     */
    @PostMapping("/save")
    public R save(@RequestBody Map<String, Object> params){
        System.out.println("接收到的原始参数: " + params);
        
        Dish dish = new Dish();
        if (params.get("id") != null && !params.get("id").toString().isEmpty()) {
            try {
                dish.setId(Integer.parseInt(params.get("id").toString()));
            } catch (Exception e) {
                dish.setId(null);
            }
        }
        if (params.get("name") != null) {
            dish.setName(params.get("name").toString());
        }
        if (params.get("unitprice") != null && !params.get("unitprice").toString().isEmpty()) {
            try {
                dish.setPrice(Float.parseFloat(params.get("unitprice").toString()));
            } catch (Exception e) {
                dish.setPrice(0.0f);
            }
        }
        if (params.get("typeId") != null && !params.get("typeId").toString().isEmpty()) {
            try {
                dish.setCategory_id(Integer.parseInt(params.get("typeId").toString()));
            } catch (Exception e) {
                dish.setCategory_id(null);
            }
        }
        if (params.get("image") != null) {
            dish.setImage(params.get("image").toString());
        }
        if (params.get("detail") != null) {
            dish.setDetail(params.get("detail").toString());
        }
        if (params.get("origin") != null) {
            dish.setOrigin(params.get("origin").toString());
        }
        if (params.get("stock") != null && !params.get("stock").toString().isEmpty()) {
            try {
                dish.setStock(Integer.parseInt(params.get("stock").toString()));
            } catch (Exception e) {
                dish.setStock(null);
            }
        }
        
        System.out.println("转换后的菜品数据: " + dish);
        System.out.println("  name: " + dish.getName());
        System.out.println("  price: " + dish.getPrice());
        System.out.println("  category_id: " + dish.getCategory_id());
        System.out.println("  unit_id: " + dish.getUnit_id());
        
        if(dish.getId()!=null){
            dishService.updateById(dish);
        }else{
            dish.setState(1); // 1表示上架
            dishService.save(dish);
        }
        return R.ok();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("/delete")
    public R delete(Integer id){
        dishService.removeById(id);
        return R.ok();
    }

    /**
     * 更新上架 下架 状态
     * @return
     */
    @PostMapping("/updateOnSale")
    public R updateOnSale(@RequestBody Map<String, Object> params){
        System.out.println("接收到的更新状态参数: " + params);
        Integer id = null;
        Integer state = null;
        
        if (params.get("id") != null) {
            try {
                id = Integer.parseInt(params.get("id").toString());
            } catch (Exception e) {
                System.out.println("id 格式错误: " + params.get("id"));
            }
        }
        
        if (params.get("onsale") != null) {
            Object onsaleObj = params.get("onsale");
            if (onsaleObj instanceof Boolean) {
                state = (Boolean) onsaleObj ? 1 : 0;
            } else {
                try {
                    String onsaleStr = onsaleObj.toString();
                    state = "true".equalsIgnoreCase(onsaleStr) || "1".equals(onsaleStr) ? 1 : 0;
                } catch (Exception e) {
                    System.out.println("onsale 格式错误: " + params.get("onsale"));
                }
            }
        }
        
        System.out.println("  解析后 - id=" + id + ", state=" + state);
        
        if (id != null) {
            Dish d = dishService.getById(id);
            if (d != null) {
                d.setState(state);
                System.out.println("准备更新菜品 - id=" + id + ", state=" + state);
                dishService.updateById(d);
            }
        }
        return R.ok();
    }

    /**
     * 批量上架菜品
     * @return
     */
    @PostMapping("/batchOnSale")
    public R batchOnSale(@RequestBody Map<String, Object> params){
        System.out.println("接收到的批量上架参数: " + params);
        
        if (params.get("ids") != null) {
            List<Integer> ids = (List<Integer>) params.get("ids");
            for (Integer id : ids) {
                Dish d = dishService.getById(id);
                if (d != null) {
                    d.setState(1);
                    dishService.updateById(d);
                }
            }
            System.out.println("批量上架成功，共上架 " + ids.size() + " 个菜品");
        }
        
        return R.ok();
    }

    /**
     * 批量下架菜品
     * @return
     */
    @PostMapping("/batchOffSale")
    public R batchOffSale(@RequestBody Map<String, Object> params){
        System.out.println("接收到的批量下架参数: " + params);
        
        if (params.get("ids") != null) {
            List<Integer> ids = (List<Integer>) params.get("ids");
            for (Integer id : ids) {
                Dish d = dishService.getById(id);
                if (d != null) {
                    d.setState(0);
                    dishService.updateById(d);
                }
            }
            System.out.println("批量下架成功，共下架 " + ids.size() + " 个菜品");
        }
        
        return R.ok();
    }

    /**
     * 上传菜品图片
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping("/uploadImage")
    public Map<String,Object> uploadImage(MultipartFile file)throws Exception{
        Map<String,Object> map=new HashMap<String,Object>();
        if(!file.isEmpty()){
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String newFileName= DateUtil.getCurrentDateStr()+suffixName;

            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(dishImgsFilePath+newFileName));
            map.put("code", 0);
            map.put("msg", "上传成功");
            Map<String,Object> map2=new HashMap<String,Object>();
            map2.put("imageName", newFileName);
            map2.put("src", "/image/dish/"+newFileName);
            map.put("data", map2);
        }
        return map;
    }

}
