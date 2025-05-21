package com.project.controller;


import com.project.dto.UpdateProfileRequest;
import com.project.dto.UserDto;
import com.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3004"})
@RequiredArgsConstructor
class ProfileController {

    private final UserService userService;

    /* JWT уже прошёл фильтр и валиден; claim sub == Keycloak userId */
    @GetMapping
    public UserDto me(@AuthenticationPrincipal Jwt jwt) {
        return userService.getById(jwt.getSubject());
    }

    @PutMapping
    public UserDto update(@AuthenticationPrincipal Jwt jwt,
                          @RequestBody UpdateProfileRequest req) {
        return userService.update(jwt.getSubject(), req);
    }

    @DeleteMapping
    public void delete(@AuthenticationPrincipal Jwt jwt) {
        userService.delete(jwt.getSubject());
    }
}
