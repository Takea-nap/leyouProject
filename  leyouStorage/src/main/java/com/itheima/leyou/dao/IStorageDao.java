package com.itheima.leyou.dao;

import java.util.ArrayList;
import java.util.Map;

public interface IStorageDao {
    public ArrayList<Map<String, Object>> getStockStorage(String sku_id);
}
