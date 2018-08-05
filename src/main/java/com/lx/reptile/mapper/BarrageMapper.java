package com.lx.reptile.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BarrageMapper {
    @Select("select txt,roomid,DATE_FORMAT(date,'%Y-%m-%d %H:%i:%s') date from `${where}_barrage` where uid=#{uid} limit #{offset},#{limit}")
    List<Map> getByUidAndwhere(@Param("where") String where, @Param("uid") String uid, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
