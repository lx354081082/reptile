package com.lx.reptile.service;

import com.lx.reptile.po.RedisBarrage;
import com.lx.reptile.pojo.Job;

import java.util.List;
import java.util.Map;

public interface RedisService {

    void lPush(String barrage, RedisBarrage redisBarrage);

    RedisBarrage rpop(String barrage);

    void barrageAdd(String key);

    Integer getval(String key);

    void clean(String key);

    Long rpopSize(String barrage);

    void pubLish(String barrage, RedisBarrage redisBarrage);

    void cleanUpdateHash(String jobhash, List<Job> allJob);

    Map<String, Object> getHash(String jobhash);
}
