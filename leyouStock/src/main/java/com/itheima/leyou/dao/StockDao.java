package com.itheima.leyou.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Map;

@Repository
public class StockDao implements IStockDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public ArrayList<Map<String,Object>> getStockList(){
        String sql = "select id AS sku_id, title, images, stock, price, indexes, own_spec " +
                "from tb_sku";
        ArrayList<Map<String, Object>> list =
                (ArrayList<Map<String, Object>>) jdbcTemplate.queryForList(sql);

        return list;
    }

    public ArrayList<Map<String,Object>> getStock(String sku_id){
        String sql = "select tb_sku.spu_id, tb_sku.title, tb_sku.images, tb_sku.stock, tb_sku.price, tb_sku.indexes, " +
                "tb_sku.own_spec, tb_sku.enable, tb_sku.create_time, tb_sku.update_time,tb_spu_detail.description," +
                "tb_sku.id AS sku_id,tb_spu_detail.special_spec " +
                "from tb_sku " +
                "INNER JOIN tb_spu_detail ON tb_spu_detail.spu_id=tb_sku.spu_id " +
                "where tb_sku.id = ?";
        ArrayList<Map<String, Object>> list =
                (ArrayList<Map<String, Object>>) jdbcTemplate.queryForList(sql,sku_id);
        return list;
    }
    public boolean insertLimitPolicy(Map<String, Object> policyInfo){
        //1、创建写入政策的语句
        String sql = "insert into tb_limit_policy (sku_id, quanty, price, begin_time, end_time) " +
                "Values (?, ?, ?, ?, ?)";

        //2、执行
        boolean result = jdbcTemplate.update(sql, policyInfo.get("sku_id"), policyInfo.get("quanty"), policyInfo.get("price"),
                policyInfo.get("begin_time"), policyInfo.get("end_time"))==1;

        //3、返回信息
        return result;
    }
}
