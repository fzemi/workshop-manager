package com.fzemi.workshopmanager.file.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "file.local")
public class LocalFileStorageConfig {

    private String location = "upload-dir";

    private String baseUrl = "http://localhost:8080/";
}
