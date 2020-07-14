package com.itheima.leyou.service;


import com.itheima.leyou.dao.IStorageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class StorageService implements IStorageService {
    @Autowired
    private IStorageDao iStorageDao;
    public Map<String, Object> getStockStorage(String sku_id){
        //1、先取得一个商品的库存
        ArrayList<Map<String ,Object>> list = new ArrayList<Map<String, Object>>();
        list = iStorageDao.getStockStorage(sku_id);

        //2、判断如果stockDao取出的商品为空，返回一个提示
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (list==null||list.isEmpty()){
            resultMap.put("result", false);
            resultMap.put("msg", "完了，服务器挂了，数据没取出来！");
            return resultMap;
        }

        //3、判断如果取出的商品不为空，返回数据
        resultMap.put("storage", list);
        resultMap.put("result", true);
        resultMap.put("msg", "");
        return resultMap;
    }

    @Transactional
    public Map<String, Object> insertStorage(String sku_id, double in_quanty, double out_quanty){
        Map<String, Object> resultMap = new HashMap<String, Object>();

        //1、传入的参数
        if (sku_id.equals("")){
            resultMap.put("result", false);
            resultMap.put("msg", "商品的sku不能为空！");
            return resultMap;
        }

        if (in_quanty==0&&out_quanty==0){
            resultMap.put("result", false);
            resultMap.put("msg", "入库数量和出库数量不能同时为0！");
            return resultMap;
        }

        //2、调StorageDao的方法
        resultMap = iStorageDao.insertStorage(sku_id, in_quanty, out_quanty);

        //3、返回
        return resultMap;
    }

}
