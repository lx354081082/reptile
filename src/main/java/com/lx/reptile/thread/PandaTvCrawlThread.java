package com.lx.reptile.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lx.reptile.po.RedisBarrage;
import com.lx.reptile.service.RedisService;
import com.lx.reptile.thread.util.panda.PandaTvMessageHandler;
import com.lx.reptile.util.BarrageConstant;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;

/**
 * @author: Lx
 * @create: 2018-08-29 10:53
 **/
@Slf4j
@Component
@Scope("prototype")
public class PandaTvCrawlThread implements Runnable, Cloneable {
    //WebSocket
    @Autowired
    SimpMessagingTemplate template;
    @Autowired
    RedisService redisService;


    private String roomId = "20641";

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    //连接弹幕服务器的必要信息
    private String rid;
    private String appid;
    private String ts;
    private String sign;
    private String authType;

    //与弹幕服务器联系的socket
    private Socket socket;
    //弹幕服务器ip
    private String serverIp;
    //弹幕服务器端口
    private int port;

    /**
     * 初始化一些信息，注意是获取登录弹幕服务器的必要信息
     *
     * @return 返回结果表示是否初始化成功
     */
    private boolean init() {
        String url = "https://riven.panda.tv/chatroom/getinfo?roomid=" + roomId + "&app=1&_caller=panda-pc_web&_=" + System.currentTimeMillis();
        Document document;
        try {
            document = Jsoup.connect(url).get();
            log.debug("从[" + url + "]获取登录弹幕服务器的必要信息");
            log.debug("登录数据Json串：" + document.body().text());
        } catch (IOException e) {
            log.error("获取登录服务器的必要数据出错", e);
            return false;
        }
        JSONObject jsonObject = JSON.parseObject(document.body().text());

        int errno = jsonObject.getInteger("errno");
        if (errno == 0) {
            JSONObject tempJsonObject = jsonObject.getJSONObject("data");
            rid = String.valueOf(tempJsonObject.getLong("rid"));
            appid = tempJsonObject.getString("appid");
            ts = String.valueOf(tempJsonObject.getLong("ts"));
            sign = tempJsonObject.getString("sign");
            authType = tempJsonObject.getString("authType");

            JSONArray chatAddressList = tempJsonObject.getJSONArray("chat_addr_list");
            log.debug("弹幕服务器数据：" + chatAddressList);
            //选第一个服务器登录
            serverIp = chatAddressList.getString(0).split(":", 2)[0];
            port = Integer.parseInt(chatAddressList.getString(0).split(":", 2)[1]);
        } else {
            log.error("获取登录弹幕服务器的必要信息出错,程序将退出");
            return false;
        }

        return true;
    }

    /**
     * 与弹幕服务器取得联系,相当于登录弹幕服务器
     */
    private void login() throws IOException {
        socket = new Socket(serverIp, port);
        log.info("登录弹幕服务器:" + serverIp + ":" + port + "成功");
        String msg = "u:" + rid + "@" + appid + "\n" +
                "k:1\n" +
                "t:300\n" +
                "ts:" + ts + "\n" +
                "sign:" + sign + "\n" +
                "authtype:" + authType;
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        byte[] b = new byte[]{0x00, 0x06, 0x00, 0x02, 0x00, (byte) msg.length()};
        byteArray.write(b);

        byteArray.write(msg.getBytes("ISO-8859-1"));
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(byteArray.toByteArray());

        b = new byte[]{0x00, 0x06, 0x00, 0x00};
        outputStream.write(b);
    }

    public void run() {
        PandaTvMessageHandler pandaTvMessageHandler = null;
        OutputStream outputStream;

        try {
            if (!init()) {
                return;
            }
            login();
            pandaTvMessageHandler = new PandaTvMessageHandler(socket);
            outputStream = socket.getOutputStream();
            long start = System.currentTimeMillis();
            //Thread.interrupted线程结束标记
            while (!Thread.interrupted()) {
                List<String> messages = pandaTvMessageHandler.read();
                for (String msg : messages) {
                    if (msg.equals("")) {
                        continue;
                    }
                    try {
                        JSONObject msgJsonObject = JSON.parseObject(msg);
                        String type = msgJsonObject.getString("type");
                        //发言弹幕type为1
                        if (type.equals("1")) {
                            handlerChatmsg(msgJsonObject);
                        } else {
                            //TODO 306礼物
//                            log.info(msgJsonObject.toJSONString());
                        }
                    } catch (Exception e) {
                        log.error("获取消息内容时出错：" + msg, e);
                    }
                }

                //心跳包
                if (System.currentTimeMillis() - start > 60000) {
                    outputStream.write(new byte[]{0x00, 0x06, 0x00, 0x00});
                    start = System.currentTimeMillis();
                }
            }
        } catch (IOException e) {
            log.error("获取弹幕时出错", e);
        } finally {
            try {
                if (pandaTvMessageHandler != null) {
                    pandaTvMessageHandler.close();
                }
            } catch (IOException e) {
                log.error("调用MessageHandler close()方法时出错");
            }
            return;
        }
    }


    /**
     * 弹幕信息处理
     */
    private void handlerChatmsg(JSONObject msgJsonObject) {
//        log.info(msgJsonObject.toJSONString());
//        String time = msgJsonObject.getString("time");
        String nickname = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("nickName");
        String uid = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("rid");
//        String roomid = msgJsonObject.getJSONObject("data").getJSONObject("to").getString("toroom");
        String content = msgJsonObject.getJSONObject("data").getString("content");
//        String level = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("level");
//        String identity = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("identity");
//        String spidentity = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("sp_identity");

        //广播
        redisService.pubLish(BarrageConstant.BARRAGE, new RedisBarrage(BarrageConstant.PANDA, msgJsonObject, new Date()));


        template.convertAndSend("/topic/panda/" + roomId,
                "<a href='/userdetail/panda/" + uid + "'>" + nickname + ":</a>" + content);
        template.convertAndSend("/topic/all",
                "panda[" + roomId + "]<a href='/userdetail.html?w=panda&i=" + uid + "'>" + nickname + ":</a>" + content);
    }

    /**
     * 克隆对象 循环创建该类线程会出现多个线程引用同一对象的问题
     */
    @Override
    public PandaTvCrawlThread clone() throws CloneNotSupportedException {
        return (PandaTvCrawlThread) super.clone();
    }
}
