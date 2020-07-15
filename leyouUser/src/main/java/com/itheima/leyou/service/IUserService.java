package com.itheima.leyou.service;

import java.util.Map;

public interface IUserService {
    public Map<String, Object> getUser(java.lang.String username, java.lang.String password);

    public Map<String, Object> insertUser(java.lang.String username, java.lang.String password);
}
