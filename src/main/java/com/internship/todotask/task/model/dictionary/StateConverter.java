package com.internship.todotask.task.model.dictionary;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;

@Converter
public class StateConverter implements AttributeConverter<State, String> {

    @Override
    public String convertToDatabaseColumn(State state) {
        return state.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public State convertToEntityAttribute(String s) {
        return State.valueOf(s.toUpperCase(Locale.ROOT));
    }

}
