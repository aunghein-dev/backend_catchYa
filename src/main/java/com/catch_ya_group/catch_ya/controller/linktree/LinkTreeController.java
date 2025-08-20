package com.catch_ya_group.catch_ya.controller.linktree;

import com.catch_ya_group.catch_ya.modal.entity.LinkTree;
import com.catch_ya_group.catch_ya.service.linktree.LinkTreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/v1/link")
@RequiredArgsConstructor
@Tag(
        name = "LinkTree Management",
        description = "APIs for managing user social media links (Facebook, Instagram, Telegram, TikTok, etc.)."
)
public class LinkTreeController {

    private final LinkTreeService linkTreeService;

    @Operation(
            summary = "Get user's LinkTree",
            description = "Retrieves the LinkTree (set of social media/profile links) for the specified user."
    )
    @GetMapping("/{userId}")
    public ResponseEntity<?> getAllLinkTree(@PathVariable Long userId){
        return ResponseEntity.ok(linkTreeService.getAllLinkTree(userId));
    }

    @Operation(
            summary = "Create or update LinkTree",
            description = "Creates a new LinkTree if one does not exist for the user, or updates the existing LinkTree with the provided social media links."
    )
    @PostMapping
    public ResponseEntity<?> createOrUpdateLinkTree(@RequestBody LinkTree linkTree){
        return ResponseEntity.ok(linkTreeService.createOrUpdateLinkTree(linkTree));
    }
}