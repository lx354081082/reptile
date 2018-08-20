package com.lx.reptile.config;

import com.alibaba.fastjson.JSON;
import com.lx.reptile.po.RedisBarrage;
import com.lx.reptile.thread.BarrageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: reptile
 * @author: Lx
 * @create: 2018-08-17 09:04
 **/
@Slf4j
@Component
public class RedisMessageReceiver {
    @Autowired
    BarrageConsumer barrageConsumer;
    /**接收消息的方法*/
    public void receiveMessage(Object message){
        try {
            String str = message.toString();
            //todo 消息前有乱码
            str = message.toString().substring(str.indexOf('{'));
            RedisBarrage redisBarrage = JSON.parseObject(str, RedisBarrage.class);
            //持久化
            barrageConsumer.rPop(redisBarrage);
        } catch (Exception e) {
            log.error("弹幕解析异常-->" + e.getMessage());
        }
    }
}
