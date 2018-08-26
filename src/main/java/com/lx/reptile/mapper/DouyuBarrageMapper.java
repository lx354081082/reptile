package com.lx.reptile.mapper;

import com.lx.reptile.pojo.DouyuBarrage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DouyuBarrageMapper {
    @Select("SELECT * FROM douyu_barrage LIMIT #{start},#{end}")
    List<DouyuBarrage> get(@Param("start") Integer start, @Param("end") Integer end);

    @Insert("<script>" +
            "INSERT INTO douyu_barrage (date, roomid, txt, uid) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.date},#{item.roomid},#{item.txt},#{item.uid})" +
            "</foreach>" +
            "</script>")
    int ins(@Param("list") List<Map<String, Object>> list);
}
//INSERT INTO douyu_barrage (date, roomid, txt, uid) VALUES <foreach collection='list' item='item' separator=','>(?,?,?,?)</foreach>