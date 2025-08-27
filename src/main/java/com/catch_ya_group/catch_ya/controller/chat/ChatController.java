package com.catch_ya_group.catch_ya.controller.chat;

import com.catch_ya_group.catch_ya.modal.chatpayload.ChatAck;
import com.catch_ya_group.catch_ya.modal.chatpayload.ChatMessage;
import com.catch_ya_group.catch_ya.modal.chatpayload.ChatReact;
import com.catch_ya_group.catch_ya.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chat;

    @MessageMapping("/chat.send")   public void send(ChatMessage message) { chat.saveAndBroadcast(message); }
    @MessageMapping("/chat.ack")    public void ack(ChatAck ack)          { chat.handleAck(ack); }
    @MessageMapping("/chat.react")  public void react(ChatReact react)     { chat.handleReaction(react); }
}
