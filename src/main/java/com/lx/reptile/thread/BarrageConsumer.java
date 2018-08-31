package com.lx.reptile.thread;

import com.lx.reptile.po.RedisBarrage;
import com.lx.reptile.service.DouyuBarrageService;
import com.lx.reptile.service.PandaBarragesService;
import com.lx.reptile.util.BarrageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Slf4j
public class BarrageConsumer {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private DouyuBarrageService douyuBarrageService;
    @Autowired
    private PandaBarragesService pandaBarragesService;
    //私有全局变量
    private static ArrayList<RedisBarrage> douyuBarrages = new ArrayList<>();
    private static ArrayList<RedisBarrage> pandaBarrages = new ArrayList<>();

    public synchronized void rPop(RedisBarrage redisBarrage) {
        if (redisBarrage.getWhere().equals(BarrageConstant.DOUYU)) {
            douyuBarrages.add(redisBarrage);
        }
        if (redisBarrage.getWhere().equals(BarrageConstant.PANDA)) {
            pandaBarrages.add(redisBarrage);
        }
        if (douyuBarrages.size() >= 200) {
            try {
                toDb(BarrageConstant.DOUYU);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
            }
            douyuBarrages.clear();
        }
        if (pandaBarrages.size() >= 200) {
            try {
                toDb(BarrageConstant.PANDA);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
            }
            pandaBarrages.clear();
        }
    }

    /**
     * 持久化弹幕数据
     */
    private void toDb(String where) throws Exception {
        if (where.equals(BarrageConstant.DOUYU)) {
            douyuBarrageService.insert(douyuBarrages);
        }
        if (where.equals(BarrageConstant.PANDA)) {
            pandaBarragesService.insert(pandaBarrages);
        }
    }
}
