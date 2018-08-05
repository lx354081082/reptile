package com.lx.reptile.controller;

import com.lx.reptile.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/userdetail")
@ResponseBody
public class UserDetailController {
    @Autowired
    private UserService userService;
    @GetMapping("/{where}/{id}")
    Object index(@PathVariable("where") String where, @PathVariable("id") String id) {
        return userService.selectByWhereAndUid(where, id);
    }
}
