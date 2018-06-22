package com.lx.reptile.service;


public interface ActivemqService {
    void sendMessage(String destinationName, final String message);
}
