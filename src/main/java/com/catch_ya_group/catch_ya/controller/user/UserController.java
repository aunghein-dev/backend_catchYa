package com.catch_ya_group.catch_ya.controller.user;

import com.catch_ya_group.catch_ya.modal.entity.Users;
import com.catch_ya_group.catch_ya.repository.UserRepo;
import com.catch_ya_group.catch_ya.service.auth.JWTService;
import com.catch_ya_group.catch_ya.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JWTService jwtService;
    private final UserRepo userRepo;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> newUserRegister(@RequestPart("postingUser") Users newUser){

        if(userService.hasValueCode(newUser.getReferredCode())==0) {
            // This is the correct way to send a JSON error response
            Map<String, String> errorResponse = Collections.singletonMap("message", "Secret code is invalid.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND) // 404
                    .body(errorResponse); // This will be serialized to JSON: {"message": "..."}
        } else if(userService.checkIsUsedSecretCode(newUser.getReferredCode())) {
            // This is the correct way to send a JSON error response
            Map<String, String> errorResponse = Collections.singletonMap("message", "Secret code has already been used. Please try again.");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409
                    .body(errorResponse); // This will be serialized to JSON: {"message": "..."}
        } else if (userService.checkUserAlreadyExits(newUser.getUsername())){
            // And for the 403 Forbidden case
            Map<String, String> errorResponse = Collections.singletonMap("message", "Username is already taken. Please choose a different username.");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN) // 403
                    .body(errorResponse);
        }
        else {
            // Success path
            Users registeredUser = userService.userAndBusinessRegister(
                    newUser,
                    newBusiness,
                    newUser.getReferredCode());
            return ResponseEntity.ok(registeredUser);
        }
    }

    @CrossOrigin(origins = "https://app.openwaremyanmar.site", allowCredentials = "true")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user, HttpServletResponse response) {
        try {
            String token = service.verify(user);

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None") // REQUIRED for cross-domain cookies
                    .path("/")
                    .domain("openwaremyanmar.site") // MUST be the root domain
                    .maxAge(Duration.ofHours(24))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(Map.of("message", "Login successful"));
        } catch (BadCredentialsException ex) {
            // Password incorrect or user not found
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Incorrect username or password"));
        } catch (Exception ex) {
            // General fallback
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
                .domain("openwaremyanmar.site") // Match the original
                .maxAge(0) // Expire immediately
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Logged out successfully");
    }
}
