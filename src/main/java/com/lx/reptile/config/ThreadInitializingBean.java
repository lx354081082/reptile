package com.lx.reptile.config;

import com.lx.reptile.thread.RedisConsumerThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 启动加载线程配置
 */
@Component
@Slf4j
public class ThreadInitializingBean implements InitializingBean {
    @Autowired
    RedisConsumerThread redisConsumerThread;
    @Override
    public void afterPropertiesSet() throws Exception {
//        if (1 == 1) {
//            return;
//        }
        Thread thread = new Thread(redisConsumerThread,"redis任务队列消费线程");
        thread.start();
        log.info("redis任务队列消费线程,启动成功!");
    }

}
