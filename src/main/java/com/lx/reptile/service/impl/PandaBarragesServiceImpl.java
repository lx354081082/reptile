package com.lx.reptile.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lx.reptile.mapper.PandaBarrageMapper;
import com.lx.reptile.mapper.PandaUserMapper;
import com.lx.reptile.po.RedisBarrage;
import com.lx.reptile.service.PandaBarragesService;
import com.lx.reptile.util.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: reptile
 * @description: ${description}
 * @author: Lx
 * @create: 2018-08-30 10:35
 **/
@Service
@Slf4j
public class PandaBarragesServiceImpl implements PandaBarragesService {
    @Autowired
    private PandaBarrageMapper pandaBarrageMapper;
    @Autowired
    private PandaUserMapper pandaUserMapper;

    @Override
    public void insert(List<RedisBarrage> pandaBarrages) {
        List<Map<String, Object>> barrageList = new ArrayList<>();
        List<Map<String, Object>> userList = new ArrayList<>();
        for (RedisBarrage r : pandaBarrages) {
            JSONObject msgJsonObject = (JSONObject) r.getBarrage();
            praceObj(msgJsonObject, barrageList, userList);
        }
        pandaBarrageMapper.ins(barrageList);
        pandaUserMapper.replace(userList);
    }

    private void praceObj(JSONObject msgJsonObject, List<Map<String, Object>> barrageList, List<Map<String, Object>> userList) {
        String time = msgJsonObject.getString("time");
        long l = 0;
        try {
            l = Long.parseLong(time);
        } catch (NumberFormatException e) {
//            log.error("");
        }
        String nickname = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("nickName");
        String rid = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("rid");
        String roomid = msgJsonObject.getJSONObject("data").getJSONObject("to").getString("toroom");
        String content = msgJsonObject.getJSONObject("data").getString("content");
        String level = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("level");
        String identity = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("identity");
        String spidentity = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("sp_identity");
        if (content != null && content.trim().length() > 0) {
            Map<String, Object> bmap = new HashMap<>();
            bmap.put("txt", content);
            bmap.put("roomid", roomid);
            bmap.put("uid", rid);
            bmap.put("date", DateFormatUtils.fmtToString(l > 0 ? new Date(l * 1000) : new Date(), DateFormatUtils.DATE_TIME_PATTERN));
            barrageList.add(bmap);
            Map<String, Object> umap = new HashMap<>();
            umap.put("uid", rid);
            umap.put("uname", nickname);
            umap.put("level", level);
            userList.add(umap);
        }
    }
}
