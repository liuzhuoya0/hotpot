package com.java1234.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.util.Date;

@Data
public class OrderExportVO {

    @ExcelProperty("订单编号")
    @ColumnWidth(15)
    private Integer id;

    @ExcelProperty("交易时间")
    @ColumnWidth(20)
    private Date orderTime;

    @ExcelProperty("桌号")
    @ColumnWidth(10)
    private String tableNumber;

    @ExcelProperty("用餐人数")
    @ColumnWidth(12)
    private String numberOfDiners;

    @ExcelProperty("交易金额(元)")
    @ColumnWidth(15)
    private Double settAmount;

    @ExcelProperty("交易状态")
    @ColumnWidth(12)
    private String transacStatus;
}
