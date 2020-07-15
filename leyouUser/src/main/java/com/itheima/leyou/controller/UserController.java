package com.itheima.leyou.controller;

import com.alibaba.fastjson.JSON;
import com.itheima.leyou.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String, Object> login(String username, String password, HttpServletRequest httpServletRequest){
        //1、取会员
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap = iUserService.getUser(username, password);

        //2、没取到会员，写入会员
        if (!(Boolean) userMap.get("result")){
            userMap = iUserService.insertUser(username, password);
        }

        //3、写入session
        HttpSession httpSession = httpServletRequest.getSession();
        String user = JSON.toJSONString(userMap);
        httpSession.setAttribute("user", user);

        Object o = httpSession.getAttribute("user");

        //4、返回信息
        return userMap;
    }
}
