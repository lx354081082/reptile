package com.lx.reptile.mapper;

import com.lx.reptile.pojo.DouyuBarrage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DouyuBarrageMapper {
    @Select("SELECT * FROM douyu_barrage LIMIT #{start},#{end}")
    List<DouyuBarrage> get(@Param("start") Integer start, @Param("end") Integer end);
}
