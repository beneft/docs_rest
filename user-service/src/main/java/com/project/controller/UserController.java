package com.project.controller;

import com.project.dto.SignRequest;
import com.project.model.User;
import com.project.repository.UserRepository;
import com.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.getUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    @PostMapping("/signers")
    public ResponseEntity<String> addSigner(@RequestBody SignRequest request) {
        userService.addSignerToDocument(request);
        return ResponseEntity.ok("User added as signer to document");
    }

    @PostMapping("/sign")
    public ResponseEntity<String> signDocument(@RequestBody SignRequest request) {
        userService.signDocument(request);
        return ResponseEntity.ok("User signed the document");
    }
}

