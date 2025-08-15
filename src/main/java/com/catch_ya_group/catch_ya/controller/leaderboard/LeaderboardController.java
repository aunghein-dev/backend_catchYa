package com.catch_ya_group.catch_ya.controller.leaderboard;

import com.catch_ya_group.catch_ya.service.leaderboard.LeaderboardHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Record profile view", description = "Tracks when a user views another user's profile and updates leaderboard counts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "View recorded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user IDs")
    })
    @PostMapping("/view")
    public ResponseEntity<?> viewProfileAction(@RequestParam Long fromUserId, @RequestParam Long toUserId){
        System.out.println(fromUserId + "-" +toUserId);
        return ResponseEntity.ok(leaderboardHistoryService.viewProfileAction(fromUserId,toUserId));
    }

    @Operation(summary = "Get viewers of a user", description = "Returns a list of users who have viewed the specified user's profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of viewers retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID")
    })
    @GetMapping("/viewers")
    public ResponseEntity<?> getViewersOfUser(@RequestParam Long userId){
        return ResponseEntity.ok(leaderboardHistoryService.getViewersOfUser(userId));
    }

    @Operation(summary = "Get full leaderboard", description = "Returns the complete leaderboard with all users and their view counts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard retrieved successfully")
    })
    @GetMapping("/board")
    public ResponseEntity<?> getWholeBoard(){
        return ResponseEntity.ok(leaderboardHistoryService.getWholeBoard());
    }
}
