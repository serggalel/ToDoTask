package com.internship.todotask.user.model.dictionary;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;

@Converter
public class ConverterEnum implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        return role.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public Role convertToEntityAttribute(String s) {
        return Role.valueOf(s.toUpperCase(Locale.ROOT));
    }
}
