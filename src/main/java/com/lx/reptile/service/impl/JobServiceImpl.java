package com.lx.reptile.service.impl;

import com.lx.reptile.mapper.JobMapper;
import com.lx.reptile.pojo.Job;
import com.lx.reptile.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private JobMapper jobMapper;

    @Override
    public Boolean isHave(String roomid) {
        Job one = jobMapper.findOne(roomid);
        if (one == null) {
            return false;
        }
        return true;
    }

    @Override
    public void save(Job job) {
        jobMapper.save(job);

    }

    @Override
    public Long getThreadIdByRoomId(String roomid) {
        Job one = jobMapper.findOne(roomid);
        if (one != null) {
            return one.getThreadid();
        }
        return null;
    }

    @Override
    public List<Job> getAll() {
        return jobMapper.findAll();
    }

    @Override
    public void update(Job job) {
        jobMapper.save(job);
    }

    @Override
    public void delByRoomid(String rommid) {
        jobMapper.delete(rommid);
    }
}
