package com.lx.reptile.service;

import com.lx.reptile.po.RedisBarrage;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: reptile
 * @description: ${description}
 * @author: Lx
 * @create: 2018-08-30 10:35
 **/
public interface PandaBarragesService {
    void insert(List<RedisBarrage> pandaBarrages);
}
