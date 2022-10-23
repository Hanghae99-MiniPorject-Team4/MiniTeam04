package com.example.advanced.domain;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
@Slf4j
public class CategoryConverter implements AttributeConverter<Category, String> {


    @Override
    public String convertToDatabaseColumn(Category category) {
        if (category == null)
            return null;
        return category.getValue();
    }

    @Override
    public Category convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;

        try {
            return Category.fromCode(dbData);
        } catch (IllegalArgumentException e) {
            log.error("failure to convert cause unexpected code [{}]", dbData, e);
            throw e;
        }
    }
}