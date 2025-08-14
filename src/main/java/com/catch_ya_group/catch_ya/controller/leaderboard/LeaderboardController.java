package com.catch_ya_group.catch_ya.controller.leaderboard;

import com.catch_ya_group.catch_ya.service.leaderboard.LeaderboardHistoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/v1/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "APIs to track profile views, peer-to-peer view counts, and leaderboard stats")
public class LeaderboardController {

    private final LeaderboardHistoryService leaderboardHistoryService;

    @PostMapping("/view")
    public ResponseEntity<?> viewProfileAction(@RequestParam Long fromUserId, @RequestParam Long toUserId){
        System.out.println(fromUserId + "-" +toUserId);
        return ResponseEntity.ok(leaderboardHistoryService.viewProfileAction(fromUserId,toUserId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getViewersOfUser(@PathVariable Long userId){
        return ResponseEntity.ok(leaderboardHistoryService.getViewersOfUser(userId));
    }
}
