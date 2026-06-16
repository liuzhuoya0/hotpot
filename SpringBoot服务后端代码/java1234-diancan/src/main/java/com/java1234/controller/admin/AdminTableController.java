package com.java1234.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.java1234.entity.PageBean;
import com.java1234.entity.R;
import com.java1234.entity.Table;
import com.java1234.properties.WeixinProperties;
import com.java1234.service.ITableService;
import com.java1234.util.DateUtil;
import com.java1234.util.HttpClientUtil;
import com.java1234.util.QrCodeGenerator;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 桌号管理
 */
@RestController
@RequestMapping("/admin/table")
public class AdminTableController {

    @Autowired
    private ITableService tableService;

    @Autowired
    private WeixinProperties weixinProperties;

    @Autowired
    HttpClientUtil httpClientUtil;

    @Autowired
    QrCodeGenerator qrCodeGenerator;

    @Value("${qrcodeImgsFilePath}")
    private String qrcodeImgsFilePath;

    /**
     * 添加
     * @param table
     * @return
     */
    @PostMapping("/add")
    public R add(@RequestBody Table table){
        try {
            System.out.println("正在添加桌号：" + table.getNumber());
            String qrcodeFileName = qrCodeGenerator.generateNormalQrCode(table.getNumber());
            table.setQrcode(qrcodeFileName);
            table.setState(0);
            tableService.save(table);
            System.out.println("桌号添加成功！");
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("添加桌号失败：" + e.getMessage());
        }
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("/delete")
    public R delete(Integer id){
        tableService.removeById(id);
        return R.ok();
    }

    /**
     * 分页显示
     * @param pageBean
     * @return
     */
    @RequestMapping("/list")
    public R list(@RequestBody PageBean pageBean){
        System.out.println(pageBean);
        Map<String,Object> map=new HashMap<>();
        map.put("start",pageBean.getStart());
        map.put("pageSize",pageBean.getPageSize());
        List<Table> list=tableService.list(map);
        Long total =tableService.getTotal(map);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("tableList",list);
        resultMap.put("total",total);
        return R.ok(resultMap);
    }



    private String getAccessToken(){
        String url="https://api.weixin.qq.com/cgi-bin/token" + "?grant_type=client_credential&appid=" + weixinProperties.getAppid()+ "&secret=" + weixinProperties.getSecret();
        System.out.println(url);
        String token = httpClientUtil.sendHttpGet(url);
        JSONObject jsonObject = JSON.parseObject(token);
        return jsonObject.get("access_token").toString();
    }

    private String genQrCodeImage(String accessToken,String table){
        Map<String, Object> body =new HashMap<>();
        body.put("path","pages/index/index?number="+table);
        String url="https://api.weixin.qq.com/wxa/getwxacode?access_token="+accessToken;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(getWechatQrcodeByHttpClient(url,body));
            BufferedImage image = ImageIO.read(bis);
            String newFileName=table+"_"+DateUtil.getCurrentDateStr()+".png";
            File output = new File(qrcodeImgsFilePath+newFileName);
            ImageIO.write(image, "png", output);
            return newFileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private  byte[] getWechatQrcodeByHttpClient(String url, Map<String, Object> body) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        try {
            StringEntity entity = new StringEntity(JSONObject.toJSONString(body));
            entity.setContentType("image/png");
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            try (InputStream inputStream = response.getEntity().getContent();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                return out.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 批量生成所有餐桌的小程序码
     * @return
     */
    @GetMapping("/generateAllQrCodes")
    public R generateAllQrCodes(){
        try {
            qrCodeGenerator.generateAllTableQrCodes();
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("批量生成二维码失败：" + e.getMessage());
        }
    }

    /**
     * 单独生成某个餐桌的小程序码
     * @param id
     * @return
     */
    @GetMapping("/generateQrCode")
    public R generateQrCode(Integer id){
        try {
            String qrcode = qrCodeGenerator.generateSingleTableQrCode(id);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("生成二维码失败：" + e.getMessage());
        }
    }

    /**
     * 更新桌位状态
     * @param id
     * @param state
     * @return
     */
    @GetMapping("/updateState")
    public R updateState(Integer id, Integer state){
        try {
            Table table = tableService.getById(id);
            if (table != null) {
                table.setState(state);
                tableService.updateById(table);
                return R.ok();
            } else {
                return R.error("桌位不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("更新状态失败：" + e.getMessage());
        }
    }
}
