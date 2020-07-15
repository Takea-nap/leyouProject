package com.itheima.leyou.service;

import com.itheima.leyou.dao.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements IUserService {
    @Autowired
    private IUserDao iUserDao;

    public Map<String, Object> getUser(String username, String password){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //1、判断传入的参数是否有误
        if (username==null||username.equals("")){
            resultMap.put("result", false);
            resultMap.put("msg", "用户名不能为空！");
            return resultMap;
        }

        //2、取会员列表
        ArrayList<Map<String, Object>> list = iUserDao.getUser(username, password);
        if (list==null||list.isEmpty()){
            resultMap.put("result", false);
            resultMap.put("msg", "没找到会员信息！");
            return resultMap;
        }

        resultMap = list.get(0);
        resultMap.put("result", true);
        resultMap.put("msg", "");
        return resultMap;
    }

    @Transactional
    public Map<String, Object> insertUser(String username, String password){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //1、判断传入的参数是否有误
        if (username==null||username.equals("")){
            resultMap.put("result", false);
            resultMap.put("msg", "用户名不能为空！");
            return resultMap;
        }

        int user_id = iUserDao.insertUser(username, password);

        if (user_id<=0){
            resultMap.put("result", false);
            resultMap.put("msg", "数据库没有执行成功！");
            return resultMap;
        }

        resultMap.put("user_id", user_id);
        resultMap.put("username", username);
        resultMap.put("phone", username);
        resultMap.put("password", password);
        resultMap.put("result", true);
        resultMap.put("msg", "");
        return resultMap;
    }
}
