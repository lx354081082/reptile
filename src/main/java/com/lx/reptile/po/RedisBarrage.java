package com.lx.reptile.po;

import lombok.Data;

import java.util.Date;

@Data
public class RedisBarrage{

    public RedisBarrage(String where, Object barrage,Date date) {
        this.where = where;
        this.barrage = barrage;
        this.date = date;
    }

    private String where;
    private Object barrage;
    private Date date;

}
