package com.lx.reptile.po;

import lombok.Data;

@Data
public class RedisUser {
    public RedisUser(String uid, String name, Integer level) {
        this.uid = uid;
        this.name = name;
        this.level = level;
    }

    private String uid;
    private String name;
    private Integer level;
}
