package com.itheima.leyou.queue;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.itheima.leyou.service.IStorageService;
import com.netflix.discovery.converters.Auto;
import com.netflix.discovery.converters.jackson.EurekaXmlJacksonCodec;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StorageQueue {

    @Autowired
    private IStorageService iStorageService;

    @RabbitListener(queues = "storage_queue")
    public void getStorageQueue(String msg){
        System.out.println("storage_queue接受信息:"+msg);

        Map<String,Object> result = new HashMap<String, Object>();
        try {
            result = iStorageService.insertStorage(msg, 0, 1);

            if (!(Boolean) result.get("result")) {
                System.out.println("storage_queue信息处理失败" + result.get(msg));
            }
        }catch(Exception e){
            System.out.println("storage_queue信息处理失败"+e.getMessage());
        }
        System.out.println("storage_queue信息处理完毕");
    }
}
