package com.lx.reptile.controller;

import com.lx.reptile.service.BarrageService;
import com.lx.reptile.service.UserService;
import com.lx.reptile.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private BarrageService barrageService;
    /**
     * 模糊查用户
     *
     * @param limit    步长
     * @param offset   起始角标
     * @param username 用户名
     * @param where    平台(douyu,panda)
     */
    @GetMapping("/listByName")
    @ResponseBody
    Object listByName(Integer limit, Integer offset, String username, String where) {

        PageBean<Map> pageBean = userService.selectUserByWhere(where, username, offset, limit);

        return pageBean;
    }

    /**
     * 查用户发的弹幕
     *
     * @param limit  步长
     * @param offset 起始角标
     */
    @GetMapping("/barrage/{where}/{uid}")
    @ResponseBody
    Object fingBarrage(@PathVariable("where") String where, @PathVariable("uid") String uid, Integer offset, Integer limit) {
        PageBean<Map> pageBean = barrageService.selectByUid(where, uid, offset, limit);
        return pageBean;
    }
}
