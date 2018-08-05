package com.lx.reptile.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class DouyuBarrage {
    private Long id;
    private String uid;
    private String txt;
    private String roomid;
    private Date date;
}