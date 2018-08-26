package com.lx.reptile.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
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

    @Insert("<script>" +
            "replace into douyu_user (u_id,`level`,u_name) values " +
            "<foreach collection='list' item='user' separator=','>" +
            "(#{user.uid},#{user.level},#{user.uname})" +
            "</foreach>" +
            "</script>")
    int replace(@Param("list") List<Map<String, Object>> list);
}
