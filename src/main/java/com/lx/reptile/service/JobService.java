package com.lx.reptile.service;

import com.lx.reptile.pojo.Job;

import java.util.List;

public interface JobService {
    Boolean isHave(String roomid);

    void save(Job job);

    Long getThreadIdByRoomId(String roomid);

    List<Job> getAll();

    void update(Job job);

    void delByRoomid(String rommid);
}
