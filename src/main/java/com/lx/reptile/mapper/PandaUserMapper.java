package com.lx.reptile.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PandaUserMapper {
    @Select("SELECT u_id userid,u_name username,level level FROM `panda_user` WHERE u_name LIKE #{username} LIMIT #{offset},#{limit}")
    List<Map> selectByName(@Param("username") String username, @Param("offset") Integer offset, @Param("limit") Integer limit);


    @Select("SELECT count(*) FROM `panda_user` WHERE u_name LIKE #{username}")
    int selectCountByName(@Param("username") String username);

    @Insert("<script>" +
            "replace into panda_user (u_id,`level`,u_name) values " +
            "<foreach collection='list' item='user' separator=','>" +
            "(#{user.uid},#{user.level},#{user.uname})" +
            "</foreach>" +
            "</script>")
    int replace(@Param("list") List<Map<String, Object>> list);
}
