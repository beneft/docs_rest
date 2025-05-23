package com.project.templateservice.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.project.templateservice.dto.DocumentItem;
import com.project.templateservice.dto.FieldDto;
import com.project.templateservice.dto.TemplateDto;
import com.project.templateservice.feign.DocumentFeignClient;
import com.project.templateservice.model.Field;
import com.project.templateservice.model.Template;
import com.project.templateservice.pojo.pojo;
import com.project.templateservice.repository.TemplateRepository;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.document.SyntaxKind;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateService {
    @Autowired
    private DocumentFeignClient feignClient;
    private final TemplateRepository templateRepository;
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOps;

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

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

    public void fillTemplate(String id, Map<String, Object> fieldValues, String uploaderId) {
        Template template = templateRepository.findByFileId(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        GridFSFile file = gridFsTemplate.findOne(query(where("_id").is(template.getFileId())));
        if (file == null) throw new RuntimeException("File not found in GridFS");

        try (InputStream in = gridFsOps.getResource(file).getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Load the DOCX template into XDocReport
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

            // Create context and put fields from fieldValues map
            IContext context = report.createContext();
            fieldValues.forEach(context::put);

//            pojo pojo = new pojo(fieldValues.get("field_name").toString(), fieldValues.get("field_thing").toString(), fieldValues.get("field_price").toString(), fieldValues.get("field_qty").toString());
//
//            context.put("pojo", pojo);

            report.process(context, out);

            feignClient.sendFilledDocument(
                    template.getName(),
                    file.getMetadata().getString("_contentType"),
                    uploaderId,
                    out.toByteArray()
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to process template", e);
        } catch (XDocReportException e) {
            throw new RuntimeException(e);
        }
    }
}
