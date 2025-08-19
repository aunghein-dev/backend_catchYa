package com.catch_ya_group.catch_ya.controller.user;

import com.catch_ya_group.catch_ya.modal.dto.PasswordChangeRequest;
import com.catch_ya_group.catch_ya.modal.dto.UniqueNameRequest;
import com.catch_ya_group.catch_ya.modal.dto.UserLoginDTO;
import com.catch_ya_group.catch_ya.modal.dto.UserRegisterDTO;
import com.catch_ya_group.catch_ya.modal.entity.Leaderboard;
import com.catch_ya_group.catch_ya.modal.entity.UserInfos;
import com.catch_ya_group.catch_ya.modal.entity.Users;
import com.catch_ya_group.catch_ya.service.user.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/public/v1/auth/")
@RequiredArgsConstructor
@Tag(
        name = "User & Authentication",
        description = "Endpoints for user management and authentication, including registration, login, logout, and profile management."
)
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @Hidden
    public ResponseEntity<?> testingGetAllUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }

    @Operation(summary = "Register new user", description = "Registers a new user along with their profile and leaderboard data. Checks if the phone number already exists.")
    @PostMapping("/register")
    public ResponseEntity<?> newUserRegister(@RequestBody UserRegisterDTO dto){
        Users newUser = dto.getNewUser();
        UserInfos userInfos = dto.getUserInfos();
        Leaderboard leaderboard = dto.getLeaderboard();

       if (userService.checkUserAlreadyExits(newUser.getPhoneNo())){
            // And for the 403 Forbidden case
            Map<String, String> errorResponse = Collections.singletonMap("message", "Phone Number is already taken. Please choose a different Phone Number.");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN) // 403
                    .body(errorResponse);
        }
        else {
            // Success path
            Users registeredUser = userService.register(newUser, userInfos, leaderboard);
            return ResponseEntity.ok(registeredUser);
        }
    }

    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token along with a secure HTTP-only cookie.")
    //@CrossOrigin(origins = "https://catchya.online", allowCredentials = "true")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO user, HttpServletResponse response) {
        try {
            String token = userService.verify(user);
            Long userId = userService.findUserIdByPhoneNo(user.getPhoneNo());

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None") // REQUIRED for cross-domain cookies
                    .path("/")
                    .domain("catchya.online") // MUST be the root domain
                    .maxAge(Duration.ofHours(99999))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", token,
                    "userId", userId
            ));
        } catch (BadCredentialsException ex) {
            // Password incorrect or user not found
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Incorrect username or password"));
        } catch (Exception ex) {
            // General fallback
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Something went wrong"));
        }
    }

    @Operation(summary = "Logout user", description = "Logs out the user by clearing the authentication cookie.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true) // Match the original
                .sameSite("None") // Match the original
                .path("/") // Match the original
                .domain("catchya.online") // Match the original
                .maxAge(0) // Expire immediately
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Logged out successfully");
    }

    @Operation(summary = "Check unique username", description = "Checks if a unique username is already taken.")
    @PostMapping("/check-unique")
    public ResponseEntity<?> existsByUniqueName(@RequestBody UniqueNameRequest request) {
        String uniqueName = request.uniqueName();
        if (uniqueName == null || uniqueName.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "uniqueName is required"));
        }
        boolean exists = userService.existsByUniqueName(uniqueName);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @Operation(summary = "Change password", description = "Allows a user to change their password if the old password is correct.")
    @PutMapping("/change-pass")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request){
        if(userService.checkPasswordCorrect(request.userId(), request.oldPassword())){
            return ResponseEntity.ok(userService.changePassword(request.userId(), request.newPassword()));
        }
        else {
            return new ResponseEntity<>("Old password is incorrect.", HttpStatus.BAD_REQUEST);
        }
    }
}
