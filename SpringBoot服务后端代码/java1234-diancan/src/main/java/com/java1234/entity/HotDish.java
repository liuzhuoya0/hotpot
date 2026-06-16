package com.java1234.entity;

import lombok.Data;

@Data
public class HotDish {
    private Integer id;
    private String name;
    private String image;
    private Integer totalQuantity;
    private Double totalSales;
}
