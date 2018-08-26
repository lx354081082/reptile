package com.lx.reptile.service;

import com.lx.reptile.po.RedisBarrage;
import com.lx.reptile.pojo.DouyuBarrage;

import java.util.List;

public interface DouyuBarrageService {
    List<DouyuBarrage> get(Integer start, Integer end);

    void insert(List<RedisBarrage> list);
}
