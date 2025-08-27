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
@Tag(name = "Chat Management", description = "APIs for retrieving recent chat lists and chat history between users.")
public class ChatRestController {

    private final ChatService chatService;

    @Operation(summary = "Get recent chat list", description = "Latest conversations for the user (Redis → DB fallback).")
    @GetMapping("/list")
    public ResponseEntity<?> getRecentChatListForUser(@RequestParam Long userId, @RequestParam int rows){
        return ResponseEntity.ok(chatService.getRecentChatListForUser(userId, rows));
    }

    @Operation(summary = "Get chat history (full)", description = "Full thread history (DB). Prefer /history/page for performance.")
    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(@RequestParam Long currentUserId, @RequestParam Long targetUserId){
        return ResponseEntity.ok(chatService.getChatHistory(currentUserId, targetUserId));
    }

    @Operation(summary = "Get chat history (paged, fast)", description = "Redis hot-tail → DB fallback. Use beforeTs for infinite scroll.")
    @GetMapping("/history/page")
    public ResponseEntity<?> getChatHistoryPage(
            @RequestParam Long currentUserId,
            @RequestParam Long targetUserId,
            @RequestParam(required = false) Long beforeTs,
            @RequestParam(defaultValue = "50") int limit
    ){
        return ResponseEntity.ok(chatService.getChatHistoryPaged(currentUserId, targetUserId, beforeTs, limit));
    }
}
