package com.lx.reptile.config;

import com.alibaba.fastjson.JSON;
import com.lx.reptile.pojo.Job;
import com.lx.reptile.service.JobService;
import com.lx.reptile.service.RedisService;
import com.lx.reptile.thread.DouyuTvCrawlThread;
import com.lx.reptile.thread.PandaTvCrawlThread;
import com.lx.reptile.util.BarrageConstant;
import com.lx.reptile.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * webSocket
 * 定时任务
 */
@Controller
@EnableScheduling
@Slf4j
public class ScheduleConfig {
    @Autowired
    SimpMessagingTemplate template;
    //    @Autowired
//    SimpMessagingTemplate messagingTemplate;
    @Autowired
    JobService jobService;
    @Autowired
    PandaTvCrawlThread pandaTvCrawlThread;
    @Autowired
    DouyuTvCrawlThread douyuTvCrawlThread;
    //    @Autowired
//    UserService userService;
    @Autowired
    RedisService redisService;
    @Autowired
    Sigar sigar;


    /**
     * 爬虫线程守护
     * 任务信息缓存
     */
    @Scheduled(fixedRate = 60000)
    public void callback() throws CloneNotSupportedException {
        List<Job> allJob = jobService.getAll();
        try {
            //Job信息缓存到Redis
            redisService.cleanUpdateHash(BarrageConstant.JOBHASH, allJob);
        } catch (Exception e) {
            log.error("Job缓存异常" + e.getMessage());
        }
        for (Job j : allJob) {
            //通过线程id 获取线程name 没有返回null
            String threadName = ThreadUtils.isRunnableByThreadId(j.getThreadid());
            //无此线程就创建相应线程
            if (threadName != null && threadName.equals(j.getRoomid())) {
                continue;
            } else {
                //克隆对象 建立线程
                if (j.getRoomid().indexOf("panda") >= 0) {
                    PandaTvCrawlThread clonePandaTvCrawlThread = pandaTvCrawlThread.clone();
                    clonePandaTvCrawlThread.setRoomId(j.getRoomid().substring("panda".length()));
                    Thread thread = new Thread(clonePandaTvCrawlThread, j.getRoomid());
                    thread.start();
                    j.setThreadid(thread.getId());
                    jobService.update(j);
                } else if (j.getRoomid().indexOf("douyu") >= 0) {
                    //todo
                    DouyuTvCrawlThread cloneDouyuTvCrawlThread = douyuTvCrawlThread.clone();
                    cloneDouyuTvCrawlThread.setRoomId(j.getRoomid().substring("douyu".length()));
                    Thread thread = new Thread(cloneDouyuTvCrawlThread, j.getRoomid());
                    thread.start();
                    j.setThreadid(thread.getId());
                    jobService.update(j);
                }
                log.info("创建任务线程-->" + j.getRoomid());
            }
        }
        log.debug("线程守护-->" + allJob.size() + "个任务正在运行");
    }


    /**
     * 统计弹幕
     */
    @Scheduled(fixedRate = 1000)
    public void count() {
        Map<String, Integer> map = new HashMap<>();
        //所有弹幕
        Integer getval = redisService.getval(BarrageConstant.ALLBARRAGE);
        redisService.clean(BarrageConstant.ALLBARRAGE);
        map.put("allRoom", getval);
        //房间弹幕统计
        try {
            //Redis中读取缓存信息
            Map<String, Object> jobHash = redisService.getHash(BarrageConstant.JOBHASH);
            Set<String> keySet = jobHash.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                Integer roomVal = redisService.getval(next);
                redisService.clean(next);
                template.convertAndSend("/topic/count/" + next, roomVal);
                map.put(next, roomVal);
            }
            template.convertAndSend("/topic/barrage/count", JSON.toJSONString(map));
        } catch (Exception e) {
            //直接DB
            log.error("job缓存读取异常：" + e.getMessage());
            List<Job> all = jobService.getAll();
            for (Job j : all) {
                Integer roomVal = redisService.getval(j.getRoomid());
                redisService.clean(j.getRoomid());
                template.convertAndSend("/topic/count/" + j.getRoomid(), roomVal);
                map.put(j.getRoomid(), roomVal);
            }
        }
    }

    /**
     * 系统状态
     *
     * @throws SigarException
     */
    @Scheduled(fixedRate = 5000)
    public void sys() {
        NumberFormat nf = NumberFormat.getNumberInstance();

        // 保留两位小数
        nf.setMaximumFractionDigits(2);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.UP);
        try {
            Map<String, Object> map = new HashMap<>();
            Mem mem = sigar.getMem();
            long total = mem.getTotal() / 1024L / 1024L;
            long l = mem.getUsed() / 1024L / 1024L;
            String format1 = nf.format(Double.valueOf(l + "") / Double.valueOf(total + "") * 100.0D);
            // 内存使用百分比
            map.put("ram", format1);
            Cpu cpu = sigar.getCpu();
            CpuPerc cpuList[] = sigar.getCpuPercList();
            for (int i = 0; i < cpuList.length; i++) {
                String format = nf.format(cpuList[i].getCombined() * 100.0D);
                Double d = Double.parseDouble(format);
                //cpu使用量
                map.put("cpu" + i, d);
            }
            map.put("cpusize", cpuList.length);
            template.convertAndSend("/topic/sys/data", JSON.toJSONString(map));
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }

    }

}