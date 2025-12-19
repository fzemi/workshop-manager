package com.fzemi.workshopmanager.file.service.impl;

import com.fzemi.workshopmanager.file.dto.TemplateInfoDTO;
import com.fzemi.workshopmanager.file.exception.FileUploadException;
import com.fzemi.workshopmanager.file.service.DocumentTemplateService;
import com.fzemi.workshopmanager.handler.ErrorCodes;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Implementation of DocumentTemplateService.
 * Manages document templates stored in classpath resources.
 */
@Service
public class DocumentTemplateServiceImpl implements DocumentTemplateService {

    private static final String TEMPLATES_PATH = "templates/documents/";
    
    /**
     * Map of available templates with their metadata.
     * Key: template file name (without .html), Value: TemplateInfoDTO
     */
    private static final Map<String, TemplateInfoDTO> TEMPLATES = Map.of(
            "RepairOrder", TemplateInfoDTO.builder()
                    .name("RepairOrder")
                    .displayName("Zlecenie naprawy")
                    .description("Dokument zlecenia naprawy pojazdu")
                    .build(),
            "VatDeclaration", TemplateInfoDTO.builder()
                    .name("VatDeclaration")
                    .displayName("Oświadczenie VAT")
                    .description("Oświadczenie o statusie płatnika VAT")
                    .build()
    );

    @Override
    public List<TemplateInfoDTO> getAvailableTemplates() {
        return List.copyOf(TEMPLATES.values());
    }

    @Override
    public String getTemplateContent(String templateName) {
        if (!TEMPLATES.containsKey(templateName)) {
            throw new FileUploadException(
                    "Template not found: " + templateName,
                    ErrorCodes.FILE_NOT_FOUND
            );
        }
        
        String resourcePath = TEMPLATES_PATH + templateName + ".html";
        
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileUploadException(
                    "Failed to read template: " + templateName,
                    ErrorCodes.NO_FILE_ACCESS,
                    e
            );
        }
    }

    @Override
    public String getTemplateContentWithEmbeddedImages(String templateName) {
        String htmlContent = getTemplateContent(templateName);
        return embedImagesAsBase64(htmlContent);
    }
    
    /**
     * Replace image src attributes with base64 data URLs.
     */
    private String embedImagesAsBase64(String html) {
        String result = html;
        
        // Embed header.png
        try {
            byte[] headerBytes = getTemplateImage("header.png");
            String headerBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(headerBytes);
            result = result.replace("src=\"header.png\"", "src=\"" + headerBase64 + "\"");
        } catch (Exception e) {
            // Ignore if header image not found
        }
        
        // Embed footer.png
        try {
            byte[] footerBytes = getTemplateImage("footer.png");
            String footerBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(footerBytes);
            result = result.replace("src=\"footer.png\"", "src=\"" + footerBase64 + "\"");
        } catch (Exception e) {
            // Ignore if footer image not found
        }
        
        return result;
    }

    @Override
    public byte[] getTemplateImage(String imageName) {
        // Validate image name to prevent path traversal
        if (imageName.contains("..") || imageName.contains("/") || imageName.contains("\\")) {
            throw new FileUploadException(
                    "Invalid image name: " + imageName,
                    ErrorCodes.NO_FILE_ACCESS
            );
        }
        
        // Only allow specific image files
        if (!imageName.equals("header.png") && !imageName.equals("footer.png")) {
            throw new FileUploadException(
                    "Image not found: " + imageName,
                    ErrorCodes.FILE_NOT_FOUND
            );
        }
        
        String resourcePath = TEMPLATES_PATH + imageName;
        
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            return resource.getContentAsByteArray();
        } catch (IOException e) {
            throw new FileUploadException(
                    "Failed to read image: " + imageName,
                    ErrorCodes.NO_FILE_ACCESS,
                    e
            );
        }
    }
}
