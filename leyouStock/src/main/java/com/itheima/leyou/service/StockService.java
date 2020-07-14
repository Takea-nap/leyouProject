package com.itheima.leyou.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itheima.leyou.dao.IStockDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class StockService implements IStockService {
    @Autowired
    private IStockDao iStockDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    public Map<String, Object> getStockList(){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //1、取IstockDao的方法
        ArrayList<Map<String, Object>> list = iStockDao.getStockList();
        //2、如果没取出数据，返回错误信息
        if (list==null||list.size()==0){
            resultMap.put("result", false);
            resultMap.put("msg", "我们也不知道为啥没取出数据！");
            return resultMap;
        }
        //3、从redis里取数据
        //resultMap = getLimitPolicy(list);

        //4、返回正常信息
        resultMap.put("sku_list", list);
//        resultMap.put("result", true);
////        resultMap.put("msg", "");
        return resultMap;
    }
    public Map<String,Object> getStock(String sku_id){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //1、传入参数
        if (sku_id==null||sku_id.equals("")){
            resultMap.put("result", false);
            resultMap.put("msg", "前端传过来的什么东东？");
            return resultMap;
        }
        //2、取IstockDao的方法
        ArrayList<Map<String, Object>> list = iStockDao.getStock(sku_id);

        //3、如果没取出数据，返回错误信息
        if (list==null||list.size()==0){
            resultMap.put("result", false);
            resultMap.put("msg", "数据库咋回事，还取不出来数据了！");
            return resultMap;
        }

        //3、从redis里取数据
        resultMap = getLimitPolicy(list);

        //4、返回正常信息
        resultMap.put("sku", list);
//        resultMap.put("result", true);
//        resultMap.put("msg", "");
        return resultMap;
    }


    @Transactional
    public Map<String, Object> insertLimitPolicy(Map<String, Object> policyMap){
        //1、判断传入的参数是不是合法
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (policyMap==null||policyMap.isEmpty()){
            resultMap.put("result", false);
            resultMap.put("msg", "传入什么东东");
            return resultMap;
        }

        //2、从StockDao接口中调用insertLimitPolicy方法
        boolean result = iStockDao.insertLimitPolicy(policyMap);

        //3、判断执行成功或失败，如果失败，返回错误信息
        if (!result){
            resultMap.put("result", false);
            resultMap.put("msg", "数据执行咋又失败了");
            return resultMap;
        }

        //4、如果成功，写入redis，需要写入有效期，key取名：LIMIT_POLICY_{sku_id}
        long diff = 0;
        String now = restTemplate.getForObject("http://leyou-time-server/getTime", String.class);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //结束日期减去当前日期得到政策的有效期
        try {
            Date end_time = simpleDateFormat.parse(policyMap.get("end_time").toString());
            Date now_time = simpleDateFormat.parse(now);
            diff = (end_time.getTime() - now_time.getTime())/1000;

            if (diff<0){
                resultMap.put("result", false);
                resultMap.put("msg", "结束时间不能小于当前时间");
                return resultMap;
            }
        } catch (ParseException e) {
            resultMap.put("result", false);
            resultMap.put("msg", "日期转换又失败了");
            return resultMap;
        }

        String policy = JSON.toJSONString(policyMap);
        stringRedisTemplate.opsForValue().set("LIMIT_POLICY_"+policyMap.get("sku_id").toString(), policy, diff, TimeUnit.SECONDS);
        ArrayList<Map<String, Object>> list = iStockDao.getStock(policyMap.get("sku_id").toString());
        String sku = JSON.toJSONString(list.get(0));
        stringRedisTemplate.opsForValue().set("SKU_"+policyMap.get("sku_id").toString(), sku, diff, TimeUnit.SECONDS);

        //5、返回正常信息
        resultMap.put("result", true);
        resultMap.put("msg", "");
        return resultMap;
    }

    private Map<String, Object> getLimitPolicy(ArrayList<Map<String, Object>> list) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        for (Map<String, Object> skuMap: list){
            //3.1、从redis取出政策
            String policy = stringRedisTemplate.opsForValue().get("LIMIT_POLICY_"+skuMap.get("sku_id").toString());
            //3.2、判断有政策的才继续
            if (policy!=null&&!policy.equals("")){
                Map<String, Object> policyInfo = JSONObject.parseObject(policy, Map.class);

                //3.3、开始时间小于等于当前时间，并且当前时间小于等于结束时间
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String now = restTemplate.getForObject("http://leyou-time-server/getTime", String.class);
                try {
                    Date end_time = simpleDateFormat.parse(policyInfo.get("end_time").toString());
                    Date begin_time = simpleDateFormat.parse(policyInfo.get("begin_time").toString());
                    Date now_time = simpleDateFormat.parse(now);

                    if (begin_time.getTime()<=now_time.getTime()&&now_time.getTime()<=end_time.getTime()){
                        skuMap.put("limitPrice", policyInfo.get("price"));
                        skuMap.put("limitQuanty", policyInfo.get("quanty"));
                        skuMap.put("limitBeginTime", policyInfo.get("begin_time"));
                        skuMap.put("limitEndTime", policyInfo.get("end_time"));
                        skuMap.put("nowTime", now);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        resultMap.put("result", true);
        resultMap.put("msg", "");
        return resultMap;
    }
}
