package com.catch_ya_group.catch_ya.controller.notification;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    //server application

    //-> /app/sendMessage
    @MessageMapping("/sendMessage")
    @SendTo("/topic/notification")
    public String sendMessage(String message){
        System.out.println("message " + message);
        return message;
    }
}
