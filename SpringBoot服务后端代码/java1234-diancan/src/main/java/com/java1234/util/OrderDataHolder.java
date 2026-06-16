package com.java1234.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderDataHolder {
    private static final Map<Integer, String> dinerCountMap = new ConcurrentHashMap<>();

    public static void setDinerCount(Integer orderId, String dinerCount) {
        dinerCountMap.put(orderId, dinerCount);
    }

    public static String getDinerCount(Integer orderId) {
        return dinerCountMap.get(orderId);
    }
}
