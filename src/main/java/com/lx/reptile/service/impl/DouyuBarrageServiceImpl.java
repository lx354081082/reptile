package com.lx.reptile.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lx.reptile.mapper.DouyuBarrageMapper;
import com.lx.reptile.mapper.DouyuUserMapper;
import com.lx.reptile.po.RedisBarrage;
import com.lx.reptile.po.RedisUser;
import com.lx.reptile.pojo.DouyuBarrage;
import com.lx.reptile.service.DouyuBarrageService;
import com.lx.reptile.util.BarrageConstant;
import com.lx.reptile.util.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class DouyuBarrageServiceImpl implements DouyuBarrageService {
    @Autowired
    private DouyuBarrageMapper douyuBarrageMapper;
    @Autowired
    private DouyuUserMapper douyuUserMapper;

    @Override
    public List<DouyuBarrage> get(Integer start, Integer end) {
        return douyuBarrageMapper.get(start, end);
    }

    @Override
    public void insert(List<RedisBarrage> list) {
        List<Map<String, Object>> barrageList = new ArrayList<>();
        Map<String, RedisUser> map = new HashMap<>();
        for (RedisBarrage r : list) {
            if (r.getWhere().equals(BarrageConstant.DOUYU)) {
                JSONObject jsonObject = JSON.parseObject(r.getBarrage().toString());
                barrageList.add(douyuSql(jsonObject, r.getDate(), map));
            }
        }
        douyuBarrageMapper.ins(barrageList);
        if (map.size() > 0) {
            List<Map<String, Object>> ulist = new ArrayList<>();
            Set<String> ks = map.keySet();
            for (String s : ks) {
                RedisUser redisUser = map.get(s);
                Map<String, Object> um = new HashMap<>();
                um.put("uid",s);
                um.put("level",redisUser.getLevel());
                um.put("uname",redisUser.getName());
                ulist.add(um);
            }
            douyuUserMapper.replace(ulist);
        }
    }
    private Map<String, Object> douyuSql(JSONObject jsonObject, Date date, Map<String, RedisUser> map) {
        Map<String, Object> bmap = new HashMap<>();
        try {
            //用户名
            String name = (String) jsonObject.get("nn");
            //用户id
            String uid = (String) jsonObject.get("uid");
            //用户等级
            String level = (String) jsonObject.get("level");
            //房间id
            String rid = (String) jsonObject.get("rid");
            //弹幕信息
            String txt = null;
            try {
                txt = (String) jsonObject.get("txt");
            } catch (Exception e) {
                txt = jsonObject.get("txt").toString();
                txt = txt.substring(1, txt.length() - 2);
                log.debug("弹幕解析失败" + e.getMessage());
            }
            if (txt == null) {
                return null;
            }
            try {
                int i = Integer.parseInt(level);
                if (i > 0) {
                    map.put(uid, new RedisUser(uid, name, i));
                }
            } catch (Exception e) {
                log.debug("等级解析失败");
            }

//            StringBuffer sb = new StringBuffer("(");
//            sb.append("'" +  + "',");
//            sb.append("'" + rid + "',");
//            sb.append("'" + stringFmt(txt) + "',");
//            sb.append("'" + uid + "'),");
            bmap.put("date", DateFormatUtils.fmtToString(date, DateFormatUtils.DATE_TIME_PATTERN));
            bmap.put("roomid", rid);
            bmap.put("txt", txt);
            bmap.put("uid", uid);
            return bmap;
        } catch (Exception e) {
            return null;
        }
    }
}
