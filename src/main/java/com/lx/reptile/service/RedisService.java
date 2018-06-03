package com.lx.reptile.service;

import com.lx.reptile.po.RedisBarrage;

public interface RedisService {

    void lPush(String barrage, RedisBarrage redisBarrage);

    RedisBarrage rpop(String barrage);

    void barrageAdd(String key);

    Integer getval(String key);

    void clean(String key);

    Long rpopSize(String barrage);
}
