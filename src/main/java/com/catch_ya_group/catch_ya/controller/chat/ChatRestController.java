package com.catch_ya_group.catch_ya.controller.chat;

import com.catch_ya_group.catch_ya.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/v1/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping("/list")
    public ResponseEntity<?> getRecentChatListForUser(@RequestParam Long userId, @RequestParam int rows){
        return ResponseEntity.ok(chatService.getRecentChatListForUser(userId, rows));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(@RequestParam Long currentUserId, @RequestParam Long targetUserId){
        return ResponseEntity.ok(chatService.getChatHistory(currentUserId, targetUserId));
    }
}
