package com.fzemi.workshopmanager.file.entity;

import com.fzemi.workshopmanager.file.config.FileTags;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class FileTagsConverter implements AttributeConverter<List<FileTags>, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<FileTags> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
                .map(FileTags::name)
                .collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public List<FileTags> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(dbData.split(SEPARATOR))
                .map(FileTags::valueOf)
                .collect(Collectors.toList());
    }
}
