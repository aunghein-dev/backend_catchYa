package com.catch_ya_group.catch_ya.controller.image;

import com.catch_ya_group.catch_ya.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/private/v1/change")
@RequiredArgsConstructor
@Tag(
        name = "User Images",
        description = "Endpoints for managing user profile and cover images, including upload and replacement of existing images."
)
public class ImageController {

    private final UserService userService;

    @Operation(
            summary = "Change cover image",
            description = "Uploads a new cover image for the given user. If an old cover image exists, it will be deleted before saving the new one."
    )
    @PostMapping("/cover-image/{userId}")
    public ResponseEntity<String> changeCoverImage(@PathVariable Long userId,
                                                   @RequestPart("file") MultipartFile file) {
        String url = userService.updateUserImage(userId, file, UserService.ImageType.COVER);
        return ResponseEntity.ok(url);
    }

    @Operation(
            summary = "Change profile image",
            description = "Uploads a new profile image (avatar) for the given user. Any existing profile image will be replaced with the newly uploaded one."
    )
    @PostMapping("/profile-image/{userId}")
    public ResponseEntity<String> changeProfileImage(@PathVariable Long userId,
                                                     @RequestPart("file") MultipartFile file) {
        String url = userService.updateUserImage(userId, file, UserService.ImageType.PROFILE);
        return ResponseEntity.ok(url);
    }
}
