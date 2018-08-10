package com.lx.reptile.service.impl;

import com.alibaba.fastjson.JSON;
import com.lx.reptile.po.RedisBarrage;
import com.lx.reptile.pojo.DouyuBarrage;
import com.lx.reptile.service.RedisService;
import com.lx.reptile.util.BarrageConstant;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Log4j
public class RedisServiceImpl implements RedisService {
    @Autowired
    RedisTemplate redisTemplate;
    /**
     * 消息入列
     */
    @Override
    public void lPush(String barrage, RedisBarrage redisBarrage) {
        redisTemplate.opsForList().leftPush(barrage, JSON.toJSONString(redisBarrage));
        //统计弹幕信息
        count(redisBarrage);
    }

    /**
     * 消息出列
     */
    @Override
    public RedisBarrage rpop(String barrage) {
        //无消息会阻塞 0 等待时间 无限等待
        //todo 容错
        Object o = redisTemplate.opsForList().rightPop(barrage, 0, TimeUnit.SECONDS);
        RedisBarrage redisBarrage = JSON.parseObject(o.toString(), RedisBarrage.class);
        return redisBarrage;
    }

    /**
     * 根据key自增
     */
    @Override
    public void barrageAdd(String key) {
        redisTemplate.boundValueOps(key).increment(1);
    }

    @Override
    public Integer getval(String key) {
        /**
         * 在spring中redis的string 类型，当值采用JdkSerializationRedisSerializer序列化操作后
         * 使用get获取失败。这是redis的一个bug.有两种解决办法。
         * 第一，采用StringRedisSerializer序列化其值。
         * 第二，采用boundValueOps(key).get(0,-1)获取计数key的值
         */
        String s = redisTemplate.boundValueOps(key).get(0, -1);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    @Override
    public void clean(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Long rpopSize(String barrage) {
        Long size = redisTemplate.opsForList().size(barrage);
        return size;
    }


    /**
     * 统计弹幕信息
     */
    private void count(RedisBarrage redisBarrage) {
        //弹幕总数++
        barrageAdd(BarrageConstant.ALLBARRAGE);

//        //根据房间id统计
//        if (redisBarrage.getWhere().equals(BarrageConstant.PANDA)) {
//            PandaBarrage barrage = (PandaBarrage) redisBarrage.getBarrage();
//            //房间id 自增
//            barrageAdd(BarrageConstant.PANDA + barrage.getRoomid());
//        }
        if (redisBarrage.getWhere().equals(BarrageConstant.DOUYU)) {
            try {
                Map map = (Map) redisBarrage.getBarrage();
                barrageAdd(BarrageConstant.DOUYU + map.get("rid"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //房间id 自增
        }
    }

//    private void countByRoom(RedisBarrage redisBarrage) {
//        Boolean add = redisTemplate.boundZSetOps("DMPH").add("rid", 1);
//    }

}
