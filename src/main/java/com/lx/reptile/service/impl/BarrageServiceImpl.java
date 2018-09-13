package com.lx.reptile.service.impl;

import com.lx.reptile.mapper.BarrageMapper;
import com.lx.reptile.service.BarrageService;
import com.lx.reptile.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BarrageServiceImpl implements BarrageService {
    @Autowired
    private BarrageMapper barrageMapper;

    @Override
    public PageBean<Map> selectByUid(String where, String uid, Integer offset, Integer limit) {
        PageBean<Map> pb = new PageBean<>();
        pb.setRows(barrageMapper.getByUidAndwhere(where, uid, offset, limit));
        return pb;
    }
}
