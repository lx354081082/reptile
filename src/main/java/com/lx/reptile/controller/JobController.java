package com.lx.reptile.controller;

import com.lx.reptile.pojo.Job;
import com.lx.reptile.service.JobService;
import com.lx.reptile.thread.DouyuTvCrawlThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class JobController {
    @Autowired
    DouyuTvCrawlThread douyuTvCrawlThread;
    @Autowired
    JobService jobService;

    /**
     * 创建任务
     */
    @GetMapping("/douyu/{roomid}")
    Object rid(@PathVariable("roomid") String roomid) {
        douyuTvCrawlThread.setRoomId(roomid);
        //判断是否有次线程
        if (jobService.isHave("douyu" + roomid))
            return true;
        Thread thread = new Thread(douyuTvCrawlThread, "douyu" + roomid);
        thread.start();
        Job job = new Job();
        job.setRoomid("douyu" + roomid);
        job.setThreadid(thread.getId());
        jobService.save(job);
        return true;
    }
}
