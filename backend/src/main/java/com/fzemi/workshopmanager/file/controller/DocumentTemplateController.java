package com.fzemi.workshopmanager.file.controller;

import com.fzemi.workshopmanager.file.dto.TemplateInfoDTO;
import com.fzemi.workshopmanager.file.service.DocumentTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for document template operations.
 */
@RestController
@RequestMapping("/api/v1/documents/templates")
public class DocumentTemplateController {

    private final DocumentTemplateService documentTemplateService;

    @Autowired
    public DocumentTemplateController(DocumentTemplateService documentTemplateService) {
        this.documentTemplateService = documentTemplateService;
    }

    /**
     * Get list of available document templates.
     * @return list of template info
     */
    @GetMapping
    public ResponseEntity<List<TemplateInfoDTO>> getAvailableTemplates() {
        List<TemplateInfoDTO> templates = documentTemplateService.getAvailableTemplates();
        return ResponseEntity.ok(templates);
    }

    /**
     * Get template HTML content by name with images embedded as base64.
     * @param templateName the template name (without .html extension)
     * @return HTML content with embedded images
     */
    @GetMapping(value = "/{templateName}", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getTemplate(@PathVariable String templateName) {
        String content = documentTemplateService.getTemplateContentWithEmbeddedImages(templateName);
        return ResponseEntity.ok(content);
    }

    /**
     * Get template image (header/footer).
     * @param imageName the image file name (e.g., header.png, footer.png)
     * @return image bytes
     */
    @GetMapping(value = "/images/{imageName}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getTemplateImage(@PathVariable String imageName) {
        byte[] imageBytes = documentTemplateService.getTemplateImage(imageName);
        return ResponseEntity.ok(imageBytes);
    }
}
