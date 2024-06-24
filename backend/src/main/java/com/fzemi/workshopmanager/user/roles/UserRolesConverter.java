package com.fzemi.workshopmanager.user.roles;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class UserRolesConverter implements AttributeConverter<Set<UserRoles>, String> {

    private final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(Set<UserRoles> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
                .map(UserRoles::name)
                .collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public Set<UserRoles> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Set.of();
        }
        return Arrays.stream(dbData.split(SEPARATOR))
                .map(UserRoles::valueOf)
                .collect(Collectors.toSet());
    }
}
