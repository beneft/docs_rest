package com.project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Document Service API", version = "1.0", description = "Upload and get documents")
)
@Configuration
public class SwaggerConfig { }
