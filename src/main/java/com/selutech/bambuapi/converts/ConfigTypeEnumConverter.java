package com.selutech.bambuapi.converts;

import com.selutech.bambuapi.data.ConfigType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Component;

@Component
public class ConfigTypeEnumConverter implements Converter<String,ConfigType> {

    @Override
    public ConfigType convert(String source) {
        if (source == null || source.isEmpty()) {
            return null; // or throw an exception if you prefer
        }

        try {
            return ConfigType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle the case where the string does not match any enum constant
            throw new IllegalArgumentException("Invalid ConfigType: " + source, e);
        }
    }
}
