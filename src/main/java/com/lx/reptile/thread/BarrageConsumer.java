package com.lx.reptile.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lx.reptile.po.RedisBarrage;
import com.lx.reptile.po.RedisUser;
import com.lx.reptile.pojo.BrUser;
import com.lx.reptile.util.BarrageConstant;
import com.lx.reptile.util.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class BarrageConsumer {

    @Autowired
    JdbcTemplate jdbcTemplate;
    //私有全局变量
    private static ArrayList<RedisBarrage> redisBarrages = new ArrayList<>();

    public synchronized void rPop(RedisBarrage redisBarrage) {
        redisBarrages.add(redisBarrage);
        if (redisBarrages.size() >= 500) {
            try {
                toDb();
            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
            }
            redisBarrages.clear();
        }
    }


    /**
     * 持久化弹幕数据
     */
    private void toDb() throws Exception {
        StringBuffer douyuSql = new StringBuffer("insert into douyu_barrage (date, roomid, txt, uid) values ");
        Map<String, RedisUser> map = new HashMap<>();
        for (RedisBarrage r : redisBarrages) {
            if (r.getWhere().equals(BarrageConstant.DOUYU)) {
                JSONObject jsonObject = JSON.parseObject(r.getBarrage().toString());
                douyuSql.append(douyuSql(jsonObject, r.getDate(),map));
            }
        }
        execute(douyuSql);
        StringBuffer userSql = new StringBuffer("replace into douyu_user (u_id,`level`,u_name) values ");
        Set<String> ks = map.keySet();
        for (String s : ks) {
            RedisUser redisUser = map.get(s);
            StringBuffer sb = new StringBuffer("(");
            sb.append("'" + s + "',");
            sb.append("'" + redisUser.getLevel() + "',");
            sb.append("'" + redisUser.getName() + "'),");
            userSql.append(sb.toString());
        }
        execute(userSql);
    }

    /**
     * 去掉 ','执行sql
     * @param sql
     */
    private void execute(StringBuffer sql) {
        try {
            if (',' == sql.charAt(sql.length() - 1)) {
                sql = sql.deleteCharAt(sql.length() - 1);
                jdbcTemplate.execute(sql.toString());
                log.info("SQL = "+sql.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    String douyuSql(JSONObject jsonObject, Date date, Map<String, RedisUser> map) {
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
                    map.put(uid, new RedisUser(uid, name, i));
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
}
