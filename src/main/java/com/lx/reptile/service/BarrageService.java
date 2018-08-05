package com.lx.reptile.service;

import com.lx.reptile.util.PageBean;

import java.util.Map;

public interface BarrageService {
    PageBean<Map> selectByUid(String where, String uid, Integer offset, Integer limit);
}
