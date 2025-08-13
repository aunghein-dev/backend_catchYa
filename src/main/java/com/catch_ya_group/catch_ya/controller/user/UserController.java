package com.catch_ya_group.catch_ya.controller.user;

import com.catch_ya_group.catch_ya.modal.dto.UserLoginDTO;
import com.catch_ya_group.catch_ya.modal.dto.UserRegisterDTO;
import com.catch_ya_group.catch_ya.modal.entity.Leaderboard;
import com.catch_ya_group.catch_ya.modal.entity.UserInfos;
import com.catch_ya_group.catch_ya.modal.entity.Users;
import com.catch_ya_group.catch_ya.service.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/public/v1/auth/")
@RequiredArgsConstructor
@Tag(name = "User & Authentication", description = "AIPs which can be provided by user login, logout, register")
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> testingGetAllUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }

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

    //@CrossOrigin(origins = "https://domain.com", allowCredentials = "true")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO user, HttpServletResponse response) {
        try {
            String token = userService.verify(user);

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(false)
                    .secure(false)
                    .sameSite("Lax") // REQUIRED for cross-domain cookies
                    .path("/")
                    //.domain("domain.com") // MUST be the root domain
                    .maxAge(Duration.ofHours(99999))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", token
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true) // Match the original
                .sameSite("None") // Match the original
                .path("/") // Match the original
                //.domain("domain.com") // Match the original
                .maxAge(0) // Expire immediately
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/check-unique")
        public ResponseEntity<?> existsByUniqueName(@RequestBody Map<String, String> payload) {
        String uniqueName = payload.get("uniqueName");
        if (uniqueName == null || uniqueName.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "uniqueName is required"));
        }
        boolean exists = userService.existsByUniqueName(uniqueName);
        return ResponseEntity.ok(Map.of("exists", exists));
    }


}
