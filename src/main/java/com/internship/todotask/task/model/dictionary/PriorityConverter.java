package com.internship.todotask.task.model.dictionary;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;

@Converter
public class PriorityConverter implements AttributeConverter<Priority, String> {

    @Override
    public String convertToDatabaseColumn(Priority priority) {
        return priority.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public Priority convertToEntityAttribute(String s) {
        return Priority.valueOf(s.toUpperCase(Locale.ROOT));
    }

}
