package com.project.controller;

import com.project.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3004"})
public class TagController {

    private final TagService tagService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<String>> getTags(@PathVariable String userId) {
        return ResponseEntity.ok(tagService.getTags(userId));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Void> addTag(@PathVariable String userId, @RequestBody String tag) {
        tag = tag.replaceAll("^\"|\"$", "");
        tagService.addTag(userId, tag);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeTag(@PathVariable String userId, @RequestBody String tag) {
        tag = tag.replaceAll("^\"|\"$", "");
        tagService.removeTag(userId, tag);
        return ResponseEntity.ok().build();
    }
}
