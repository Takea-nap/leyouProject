package com.itheima.leyou.service;

import com.itheima.leyou.dao.IOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService implements IOrderService{
    @Autowired
    private IOrderDao iOrderDao;
}
