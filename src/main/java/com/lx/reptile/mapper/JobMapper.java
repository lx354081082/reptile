package com.lx.reptile.mapper;

import com.lx.reptile.pojo.Job;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface JobMapper {

    @Select("select * from job where roomid=#{roomid}")
    Job findOne(@Param("roomid") String roomid);

    @Insert("REPLACE INTO job(roomid, threadid) VALUE (#{job.roomid},#{job.threadid})")
    int save(@Param("job") Job job);

    @Select("select * from job")
    List<Job> findAll();

    @Delete("delete job where roomid=#{roomid}")
    int delete(@Param("rommid") String rommid);
}
