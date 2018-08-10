//package com.lx.reptile.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.lx.reptile.po.RedisBarrage;
//import com.lx.reptile.service.ActivemqService;
//import com.lx.reptile.thread.BarrageConsumer;
//import org.apache.activemq.command.ActiveMQQueue;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.jms.core.JmsMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import javax.jms.Destination;
//
//
//@Service
//public class ActivemqServiceImpl implements ActivemqService {
//    @Autowired // 也可以注入JmsTemplate，JmsMessagingTemplate对JmsTemplate进行了封装
//    private JmsMessagingTemplate jmsTemplate;
//    @Autowired
//    private BarrageConsumer barrageConsumer;
//
//    // 发送消息，destination是发送到的队列，message是待发送的消息
//    public void sendMessage(String destinationName, final String message){
//        Destination destination = new ActiveMQQueue(destinationName);
//        jmsTemplate.convertAndSend(destination, message);
//    }
//
//    @JmsListener(destination = "barrage.queue")
//    public void subscribe(String text) {
//        RedisBarrage redisBarrage = JSON.parseObject(text, RedisBarrage.class);
//        barrageConsumer.rPop(redisBarrage);
//    }
//}
