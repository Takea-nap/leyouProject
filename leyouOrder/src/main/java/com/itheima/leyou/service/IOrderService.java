package com.itheima.leyou.service;

import java.util.Map;

public interface IOrderService {
    public Map<String, Object> createOrder(String sku_id, String user_id);
}
