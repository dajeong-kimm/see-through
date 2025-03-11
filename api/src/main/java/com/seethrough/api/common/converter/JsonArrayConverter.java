package com.seethrough.api.common.converter;

import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JsonArrayConverter implements AttributeConverter<Set<String>, String> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Set<String> attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (Exception e) {
			throw new RuntimeException("JSON writing error", e);
		}
	}

	@Override
	public Set<String> convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData, new TypeReference<Set<String>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException("JSON reading error", e);
		}
	}
}
