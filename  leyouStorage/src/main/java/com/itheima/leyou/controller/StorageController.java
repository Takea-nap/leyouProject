package com.itheima.leyou.controller;


import com.itheima.leyou.service.IStrorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Configuration
public class StorageController {
    @Autowired
    private IStrorageService iStrorageService;

    @RequestMapping(value = "/getStockStorage/{sku_id}")
    public Map<String,Object> getStorage(@PathVariable("sku_id")String sku_id){
        return  iStrorageService.getStockStorage(sku_id);
    }

}
