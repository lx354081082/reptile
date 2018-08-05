package com.lx.reptile.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DouyuUserMapper {
    @Select("SELECT u_id userid,u_name username,level level FROM `douyu_user` WHERE u_name LIKE #{username} LIMIT #{offset},#{limit}")
    List<Map> selectByName(@Param("username") String username, @Param("offset") Integer offset, @Param("limit") Integer limit);


    @Select("SELECT count(*) FROM `douyu_user` WHERE u_name LIKE #{username}")
    int selectCountByName(@Param("username") String username);

    @Select("select * from `${where}` where u_id=#{id}")
    Map findOne(@Param("where") String where, @Param("id") String id);
}
