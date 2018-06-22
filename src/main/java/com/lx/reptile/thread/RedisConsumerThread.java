package com.lx.reptile.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lx.reptile.po.RedisBarrage;
import com.lx.reptile.po.RedisUser;
import com.lx.reptile.service.RedisService;
import com.lx.reptile.util.BarrageConstant;
import com.lx.reptile.util.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * redis任务队列消费线程
 */
@Component
@Scope("prototype")
@Slf4j
public class RedisConsumerThread implements Runnable {
    @Autowired
    RedisService redisService;
    @Autowired
    BarrageConsumer barrageConsumer;


    @Override
    public void run() {
        while (!Thread.interrupted()) {
            RedisBarrage rpop = redisService.rpop(BarrageConstant.BARRAGE);
            barrageConsumer.rPop(rpop);
        }
    }
}
