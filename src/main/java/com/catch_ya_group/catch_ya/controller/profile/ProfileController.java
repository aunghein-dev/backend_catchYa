package com.catch_ya_group.catch_ya.controller.profile;

import com.catch_ya_group.catch_ya.modal.dto.ProfileUpdaterRequest;
import com.catch_ya_group.catch_ya.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/v1/profile")
@RequiredArgsConstructor
@Tag(
        name = "User Profile",
        description = "Endpoints for managing and updating user profile data, such as full name and unique username."
)
public class ProfileController {

    private final UserService userService;

    @Operation(
            summary = "Update profile data",
            description = "Updates a user’s profile information including their full name and unique username. "
                    + "Requires the user ID and updated profile data in the request body."
    )
    @PostMapping("/update")
    public ResponseEntity<?> updateProfileData(@RequestBody ProfileUpdaterRequest profileUpdaterRequest) {
        return ResponseEntity.ok(userService.updateProfileData(profileUpdaterRequest));
    }
}
