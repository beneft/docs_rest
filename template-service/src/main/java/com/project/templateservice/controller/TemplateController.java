package com.project.templateservice.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.project.templateservice.dto.DocumentItem;
import com.project.templateservice.dto.FieldDto;
import com.project.templateservice.dto.TemplateDto;
import com.project.templateservice.model.Template;
import com.project.templateservice.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TemplateController {

    private final TemplateService templateService;
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOps;

    @PostMapping
    public ResponseEntity<Template> createTemplate(
            @RequestPart("template") TemplateDto dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {
        Template saved = templateService.saveTemplate(dto, file);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Template>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @GetMapping("/{id}/metadata")
    public ResponseEntity<Template> getTemplate(@PathVariable String id) {
        return ResponseEntity.ok(templateService.getTemplate(id));
    }

    @GetMapping("/metadata")
    public ResponseEntity<List<DocumentItem>> getMetadata() {
        return ResponseEntity.ok(templateService.getMetadataList());
    }

    @GetMapping("/{id}/fields")
    public ResponseEntity<List<FieldDto>> getFields(@PathVariable String id) {
        return ResponseEntity.ok(templateService.getFields(id));
    }


    // TODO: ДОБАВИТЬ UPLOADER ID
    @PostMapping("/{id}/fill")
    public ResponseEntity<Void> fillTemplate(@PathVariable String id, @RequestBody Map<String, Object> fieldValues,@RequestParam(required = true) String uploaderId) {
        templateService.fillTemplate(id, fieldValues, uploaderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFile(@PathVariable String id) throws IOException {
        Template template = templateService.getTemplateByFileId(id);
        if (template.getFileId() == null) return ResponseEntity.notFound().build();

        GridFSFile file = gridFsTemplate.findOne(query(where("_id").is(template.getFileId())));
        if (file == null) return ResponseEntity.notFound().build();

        var resource = gridFsOps.getResource(file);
        return ResponseEntity.ok()
                .header("Content-Type", file.getMetadata().getString("_contentType"))
                .header("Content-Disposition", "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
