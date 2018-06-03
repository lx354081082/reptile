package com.lx.reptile.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lx.reptile.po.RedisBarrage;
import com.lx.reptile.po.RedisUser;
import com.lx.reptile.service.RedisService;
import com.lx.reptile.util.BarrageConstant;
import com.lx.reptile.util.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * redis任务队列消费线程
 */
@Component
@Scope("prototype")
@Slf4j
public class RedisConsumerThread implements Runnable {
    @Autowired
    RedisService redisService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    //私有全局变量
    private static ArrayList<RedisBarrage> redisBarrages = new ArrayList<>();
    //全局用户信息
//    private static Map<String, RedisUser> pandaUser = new HashMap<>();
    private static Map<String, RedisUser> douyuUser = new HashMap<>();

    @Override
    public void run() {
        long l = System.currentTimeMillis();

        while (!Thread.interrupted()) {
            RedisBarrage rpop = redisService.rpop(BarrageConstant.BARRAGE);

            rpop.setBarrage(rpop.getBarrage());
            //弹幕数据放入数组待用
            redisBarrages.add(rpop);

            //统计弹幕信息
//            count(rpop);

            //数量超过?? 批量存数据库
//            if (1 == 1) {
//                log.info(rpop.getWhere()+"Time:["+DateFormatUtils.fmtToString(rpop.getDate(),DateFormatUtils.DATE_TIME_PATTERN)+"]"+rpop.getBarrage().toString());
//                continue;
//            }
            if (redisBarrages.size() >= 1000) {
                try {
                    toDb();
                } catch (Exception e) {
                    log.error(e.getLocalizedMessage());
                }
                long l1 = System.currentTimeMillis();
                Double time = (l1 - l) * 1.0 / 1000;
                l = l1;
//                log.info(redisBarrages.size() + "条弹幕保存成功,执行时间" + time + "s");
                redisBarrages.clear();
            }
            //panda用户信息持久化
//            if (pandaUser.size() >= 1000) {
//                try {
//                    toPandaDb();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                pandaUser.clear();
//            }
            //去','
            if (douyuUser.size() >= 1000) {
                try {
                    toDouyuDb();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                douyuUser.clear();
            }
        }
    }

    private void toDouyuDb() throws Exception {
        StringBuffer sql = new StringBuffer("REPLACE INTO douyu_user (u_id, level, u_name) VALUES ");

        Set<String> strings = douyuUser.keySet();
        for (String key : strings) {
            RedisUser redisUser = douyuUser.get(key);
            if (redisUser.getLevel() == null) {
                continue;
            }
            sql.append("('" + redisUser.getUid() + "'," + redisUser.getLevel() + ",'" + redisUser.getName() + "'),");
        }
        //去','
        if (',' == sql.charAt(sql.length() - 1)) {
            sql = sql.deleteCharAt(sql.length() - 1);
            jdbcTemplate.execute(sql.toString());
        }
    }


    /**
     * 持久化弹幕数据
     */
    private void toDb() throws Exception {
//        StringBuffer pandaSql = new StringBuffer("insert into panda_barrage (content, date, level, nickname, rid, roomid) values ");
        StringBuffer douyuSql = new StringBuffer("insert into douyu_barrage (date, roomid, txt, uid) values ");

        for (RedisBarrage r : redisBarrages) {
            if (r.getWhere().equals(BarrageConstant.DOUYU)) {
                JSONObject jsonObject = JSON.parseObject(r.getBarrage().toString());
                douyuSql.append(douyuSql(jsonObject, r.getDate()));
            }
        }

        //最后一个字符是,就删掉 执行sql
        try {
//            if (',' == pandaSql.charAt(pandaSql.length() - 1)) {
//                pandaSql = pandaSql.deleteCharAt(pandaSql.length() - 1);
//                jdbcTemplate.execute(pandaSql.toString());
//            }
            if (',' == douyuSql.charAt(douyuSql.length() - 1)) {
                douyuSql = douyuSql.deleteCharAt(douyuSql.length() - 1);
                jdbcTemplate.execute(douyuSql.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 拼接 insert sql字符串 数据异常返回 ""
     */
//    String pandaSql(PandaBarrage pandaBarrage) {
//        StringBuffer sb = new StringBuffer("(");
//        sb.append("'" + pandaBarrage.getContent() + "',");
//        sb.append("'" + DateFormatUtils.fmtToString(pandaBarrage.getDate(),DateFormatUtils.DATE_TIME_PATTERN) + "',");
//        sb.append("" + pandaBarrage.getLevel() + ",");
//        sb.append("'" + pandaBarrage.getNickname() + "',");
//        sb.append("'" + pandaBarrage.getRid() + "',");
//        sb.append("'" + pandaBarrage.getRoomid() + "'),");
//        return sb.toString();
//    }
    String douyuSql(JSONObject jsonObject, Date date) {
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
                return "";
            }


            try {
                int i = Integer.parseInt(level);
                if (i > 0) {
                    douyuUser.put(uid, new RedisUser(uid, name, i));
                }
            } catch (Exception e) {
                log.debug("等级解析失败");
            }

            StringBuffer sb = new StringBuffer("(");
            sb.append("'" + DateFormatUtils.fmtToString(date, DateFormatUtils.DATE_TIME_PATTERN) + "',");
            sb.append("'" + rid + "',");
            sb.append("'" + stringFmt(txt) + "',");
            sb.append("'" + uid + "'),");
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    //

    /**
     * 清除特定字符 (')
     */
    private String stringFmt(String s) throws Exception {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '\'') {
                chars[i] = ' ';
            }
        }
        String s1 = new String(chars);
        return s1;
    }

    /**
     * 统计用户信息
     */
    private void count(RedisBarrage redisBarrage) {
//        if (redisBarrage.getWhere().equals(BarrageConstant.PANDA)) {
//            PandaBarrage pandaBarrage = JSON.parseObject(redisBarrage.getBarrage().toString(), PandaBarrage.class);
//            //用户统计
//            String rid = pandaBarrage.getRid();
//            Integer level = pandaBarrage.getLevel();
//            pandaUser.put(pandaBarrage.getNickname(), new RedisUser(rid, level));
//
//        }
//        if (redisBarrage.getWhere().equals(BarrageConstant.DOUYU)) {
//            DouyuBarrage douyuBarrage = JSON.parseObject(redisBarrage.getBarrage().toString(), DouyuBarrage.class);
//            //用户统计
//            String uid = douyuBarrage.getUid();
//            Integer level = douyuBarrage.getLevel();
//            douyuUser.put(douyuBarrage.getUname(), new RedisUser(uid, level));
//        }
    }

    private void userCount(RedisBarrage redisBarrage) {

    }
}
