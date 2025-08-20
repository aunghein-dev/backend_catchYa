package com.catch_ya_group.catch_ya.controller.chat;

import com.catch_ya_group.catch_ya.service.chat.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/v1/chat")
@RequiredArgsConstructor
@Tag(
        name = "Chat Management",
        description = "APIs for retrieving recent chat lists and chat history between users."
)
public class ChatRestController {

    private final ChatService chatService;

    @Operation(
            summary = "Get recent chat list",
            description = "Fetches the most recent conversations for the given user. "
                    + "Returns a list of the latest messages per conversation, limited by the provided `rows` parameter."
    )
    @GetMapping("/list")
    public ResponseEntity<?> getRecentChatListForUser(@RequestParam Long userId, @RequestParam int rows){
        return ResponseEntity.ok(chatService.getRecentChatListForUser(userId, rows));
    }

    @Operation(
            summary = "Get chat history",
            description = "Fetches the full chat history between the current user and a target user. "
                    + "Includes messages and any associated reactions, ordered by creation time."
    )
    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(@RequestParam Long currentUserId, @RequestParam Long targetUserId){
        return ResponseEntity.ok(chatService.getChatHistory(currentUserId, targetUserId));
    }
}

