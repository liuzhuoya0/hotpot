package com.java1234.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.java1234.entity.Table;
import com.java1234.mapper.TableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批量生成餐桌二维码（普通二维码）
 */
@Component
public class QrCodeGenerator {

    @Autowired
    private TableMapper tableMapper;

    @Value("${qrcodeImgsFilePath}")
    private String qrcodeImgsFilePath;

    /**
     * 批量生成所有餐桌的普通二维码
     */
    public void generateAllTableQrCodes() {
        Map<String, Object> map = new HashMap<>();
        List<Table> tableList = tableMapper.list(map);
        System.out.println("开始生成餐桌二维码，共 " + tableList.size() + " 个餐桌");

        for (Table table : tableList) {
            if (table.getQrcode() == null || table.getQrcode().isEmpty()) {
                System.out.println("正在生成餐桌 " + table.getNumber() + " 的二维码...");
                try {
                    String qrcodeFileName = generateNormalQrCode(table.getNumber());
                    if (qrcodeFileName != null) {
                        table.setQrcode(qrcodeFileName);
                        tableMapper.updateById(table);
                        System.out.println("餐桌 " + table.getNumber() + " 二维码生成成功：" + qrcodeFileName);
                    }
                } catch (Exception e) {
                        System.out.println("餐桌 " + table.getNumber() + " 二维码生成失败：" + e.getMessage());
                        e.printStackTrace();
                }
            } else {
                System.out.println("餐桌 " + table.getNumber() + " 已有二维码，跳过：" + table.getQrcode());
            }
        }
        System.out.println("所有餐桌二维码生成完成！");
    }

    /**
     * 单独生成某个餐桌的普通二维码
     */
    public String generateSingleTableQrCode(Integer tableId) {
        Table table = tableMapper.getById(tableId);
        if (table == null) {
            throw new RuntimeException("餐桌不存在");
        }
        System.out.println("正在生成餐桌 " + table.getNumber() + " 的二维码...");
        try {
            String qrcodeFileName = generateNormalQrCode(table.getNumber());
            if (qrcodeFileName != null) {
                table.setQrcode(qrcodeFileName);
                tableMapper.updateById(table);
                System.out.println("餐桌 " + table.getNumber() + " 二维码生成成功：" + qrcodeFileName);
                return qrcodeFileName;
            }
        } catch (Exception e) {
            System.out.println("餐桌 " + table.getNumber() + " 二维码生成失败：" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("生成二维码失败：" + e.getMessage());
        }
        return null;
    }

    /**
     * 生成普通二维码（使用 ZXing）
     * @param tableNumber 桌号
     * @return 二维码文件名
     */
    public String generateNormalQrCode(String tableNumber) {
        try {
            String content = tableNumber;
            int width = 300;
            int height = 300;
            String format = "png";

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            String newFileName = tableNumber + "_" + DateUtil.getCurrentDateStr() + ".png";
            File output = new File(qrcodeImgsFilePath + newFileName);
            ImageIO.write(image, format, output);
            return newFileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
