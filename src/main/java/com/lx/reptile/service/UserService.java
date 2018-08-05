package com.lx.reptile.service;

import com.lx.reptile.util.PageBean;

import java.util.Map;

public interface UserService {
    PageBean<Map> selectUserByWhere(String where, String username, Integer offset, Integer limit);

    Map selectByWhereAndUid(String where, String id);

}
