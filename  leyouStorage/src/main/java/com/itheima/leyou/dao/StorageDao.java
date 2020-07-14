package com.itheima.leyou.dao;

import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Repository
public class StorageDao implements IStorageDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //返回指定ID商品的库存
    public ArrayList<Map<String, Object>> getStockStorage(String sku_id){
        //1、SQL取值
        String sql = "SELECT sku_id, quanty FROM tb_stock_storage WHERE sku_id = ?";

        //2、返回数据
        return (ArrayList<Map<String,Object>>) jdbcTemplate.queryForList(sql, sku_id);
    }
    public Map<String, Object> insertStorage(String sku_id, double in_quanty, double out_quanty){
        Map<String,Object> resultMap = new HashMap<String,Object>();

        //查询主表是否有仓库
        //1、查询库存主表是否有库存
        String sql = "SELECT id FROM tb_stock_storage WHERE sku_id = ?";
        ArrayList<Map<String, Object>> list =
                (ArrayList<Map<String, Object>>) jdbcTemplate.queryForList(sql, sku_id);
        int new_id = 0;
        double thisQuanty = in_quanty - out_quanty;
        boolean result = false;

        //2、如果有库存，获取id，作用一写入历史表，作用二反回来更新
        if (list!=null&&list.size()>0){
            new_id = Integer.parseInt(list.get(0).get("id").toString());
        }else {
            //3、如果没有库存，写入主表库存，并且得到id，作用写入历史表
            sql = "INSERT INTO tb_stock_storage (warehouse_id, sku_id, quanty) VALUES (1, "+sku_id+", "+thisQuanty+")";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            final String finalSql = sql;
            result = jdbcTemplate.update(new PreparedStatementCreator() {

                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement preparedStatement = connection.prepareStatement(finalSql, Statement.RETURN_GENERATED_KEYS);
                    return preparedStatement;
                }

            }, keyHolder)==1;
        }

        //4、写入历史表
        sql = "INSERT INTO tb_stock_storage_history (stock_storage_id, in_quanty, out_quanty) " +
                "VALUES (?, ?, ?)";
        result = jdbcTemplate.update(sql, new_id, in_quanty, out_quanty)==1;
        //4.1、如果写入失败，返回错误信息 msg
        if (!result){
            resultMap.put("result", false);
            resultMap.put("msg", "写入库存历史表失败了！");
            return resultMap;
        }
        //5、如果有库存，反回来更新主表
        if (list!=null&&list.size()>0){
            sql = "UPDATE tb_stock_storage SET quanty = quanty + ? " +
                    "WHERE id = ? AND quanty + ? >= 0";
            result = jdbcTemplate.update(sql, thisQuanty, new_id, thisQuanty)==1;
            //5.1、如果写入失败，返回错误信息 msg
            if (!result){
                resultMap.put("result", false);
                resultMap.put("msg", "更新库存主表失败了！");
                return resultMap;
            }

        }
        //6、返回正常数据
        resultMap.put("result", true);
        resultMap.put("msg", "");
        return resultMap;
    }

}
