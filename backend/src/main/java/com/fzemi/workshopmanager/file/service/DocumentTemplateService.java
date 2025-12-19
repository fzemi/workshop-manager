package com.fzemi.workshopmanager.file.service;

import com.fzemi.workshopmanager.file.dto.TemplateInfoDTO;

import java.util.List;

/**
 * Service for managing document templates.
 */
public interface DocumentTemplateService {
    
    /**
     * Get list of available document templates.
     * @return list of template info DTOs
     */
    List<TemplateInfoDTO> getAvailableTemplates();
    
    /**
     * Get template HTML content by name.
     * @param templateName the template name (without .html extension)
     * @return HTML content of the template
     */
    String getTemplateContent(String templateName);
    
    /**
     * Get template HTML content with images embedded as base64 data URLs.
     * @param templateName the template name (without .html extension)
     * @return HTML content with embedded images
     */
    String getTemplateContentWithEmbeddedImages(String templateName);
    
    /**
     * Get template image (header/footer) as byte array.
     * @param imageName the image file name (e.g., header.png)
     * @return image bytes
     */
    byte[] getTemplateImage(String imageName);
}
