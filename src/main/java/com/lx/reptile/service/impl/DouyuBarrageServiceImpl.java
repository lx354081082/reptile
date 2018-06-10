package com.lx.reptile.service.impl;

import com.lx.reptile.mapper.DouyuBarrageMapper;
import com.lx.reptile.pojo.DouyuBarrage;
import com.lx.reptile.service.DouyuBarrageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DouyuBarrageServiceImpl implements DouyuBarrageService {
    @Autowired
    private DouyuBarrageMapper douyuBarrageMapper;

    @Override
    public List<DouyuBarrage> get(Integer start, Integer end) {
        return douyuBarrageMapper.get(start, end);
    }
}
