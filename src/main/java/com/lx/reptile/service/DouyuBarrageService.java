package com.lx.reptile.service;

import com.lx.reptile.pojo.DouyuBarrage;

import java.util.List;

public interface DouyuBarrageService {
    List<DouyuBarrage> get(Integer start, Integer end);
}
