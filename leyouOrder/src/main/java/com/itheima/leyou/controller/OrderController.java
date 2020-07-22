package com.itheima.leyou.controller;

import com.alibaba.fastjson.JSONObject;
import com.itheima.leyou.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderController {
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping(value = "/createOrder/{sku_id}")
    public Map<String, Object> createOrder(@PathVariable("sku_id") String sku_id, HttpServletRequest httpServletRequest){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        HttpSession httpSession = httpServletRequest.getSession();
        Object userObj = httpSession.getAttribute("user");

        if (userObj==null){
            resultMap.put("result", false);
            resultMap.put("msg", "会员没有登录不能购买！");
            return resultMap;
        }
        Map<String, Object> userMap = JSONObject.parseObject(userObj.toString(), Map.class);
        return iOrderService.createOrder(sku_id, userMap.get("user_id").toString());
    }
}
