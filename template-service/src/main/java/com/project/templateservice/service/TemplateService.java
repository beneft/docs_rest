package com.project.templateservice.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.project.templateservice.dto.DocumentItem;
import com.project.templateservice.dto.FieldDto;
import com.project.templateservice.dto.TemplateDto;
import com.project.templateservice.model.Field;
import com.project.templateservice.model.Template;
import com.project.templateservice.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOps;

    public Template saveTemplate(TemplateDto dto, MultipartFile file) throws IOException {
        String fileId = null;
        if (file != null && !file.isEmpty()) {
            fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType()).toString();
        }

        List<Field> fields = dto.getFields().stream()
                .map(f -> Field.builder()
                        .name(f.getName())
                        .type(f.getType())
                        .required(f.isRequired())
                        .build())
                .collect(Collectors.toList());

        Template template = Template.builder()
                .name(dto.getName())
                .fields(fields)
                .fileId(fileId)
                .build();

        return templateRepository.save(template);
    }

    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    public Template getTemplate(String id) {
        return templateRepository.findById(id).orElseThrow();
    }

    public Template getTemplateByFileId(String fileId) {
        return templateRepository.findByFileId(fileId).orElseThrow();
    }

    public GridFSFile getFile(String fileId) {
        return gridFsTemplate.findOne(query(where("_id").is(new ObjectId(fileId))));
    }

    public List<DocumentItem> getMetadataList() {
        return templateRepository.findAll().stream()
                .map(template -> {
                    String contentType = null;
                    if (template.getFileId() != null) {
                        GridFSFile file = gridFsTemplate.findOne(
                                Query.query(where("_id").is(template.getFileId())));
                        if (file != null && file.getMetadata() != null) {
                            contentType = file.getMetadata().getString("_contentType");
                        }
                    }

                    return DocumentItem.builder()
                            .id(template.getFileId())
                            .name(template.getName())
                            .contentType(contentType)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<FieldDto> getFields(String id) {
        Template template = getTemplateByFileId(id);
        return template.getFields().stream()
                .map(f -> new FieldDto(f.getName(), f.getType(), f.isRequired()))
                .collect(Collectors.toList());
    }

    public void fillTemplate(String id, Map<String, Object> fieldValues) {
        Template template = templateRepository.findByFileId(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        GridFSFile file = gridFsTemplate.findOne(query(where("_id").is(template.getFileId())));
        if (file == null) throw new RuntimeException("File not found in GridFS");

        try (InputStream in = gridFsOps.getResource(file).getInputStream();
             XWPFDocument doc = new XWPFDocument(in);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            replacePlaceholders(doc, fieldValues);
            doc.write(out);

//            feignClient.sendFilledDocument(
//                    template.getName(),
//                    out.toByteArray(),
//                    file.getMetadata().getString("_contentType")
//            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to process template", e);
        }
    }

    private void replacePlaceholders(XWPFDocument doc, Map<String, Object> values) {
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    for (Map.Entry<String, Object> entry : values.entrySet()) {
                        text = text.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
                    }
                    run.setText(text, 0);
                }
            }
        }

        for (XWPFTable table : doc.getTables()) {
            table.getRows().forEach(row ->
                    row.getTableCells().forEach(cell ->
                            replacePlaceholders(cell.getParagraphs(), values)
                    )
            );
        }
    }

    private void replacePlaceholders(List<XWPFParagraph> paragraphs, Map<String, Object> values) {
        for (XWPFParagraph paragraph : paragraphs) {
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    for (Map.Entry<String, Object> entry : values.entrySet()) {
                        text = text.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
                    }
                    run.setText(text, 0);
                }
            }
        }
    }
}
