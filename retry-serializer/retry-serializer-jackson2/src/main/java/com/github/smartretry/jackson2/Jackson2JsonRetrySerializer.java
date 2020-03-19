package com.github.smartretry.jackson2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.smartretry.core.RetrySerializer;
import org.kohsuke.MetaInfServices;

import java.io.IOException;

/**
 * @author yuni[mn960mn@163.com]
 */
@MetaInfServices(RetrySerializer.class)
public class Jackson2JsonRetrySerializer implements RetrySerializer {

    private static final ObjectMapper INSTANCE = newObjectMapper(JsonInclude.Include.NON_NULL);

    public static ObjectMapper newObjectMapper(JsonInclude.Include include) {
        ObjectMapper mapper = new ObjectMapper();
        // 设置输出时包含属性的风格
        if (include != null) {
            mapper.setSerializationInclusion(include);
        }
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    @Override
    public String serialize(Object object) {
        try {
            return INSTANCE.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(String value, Class<?> clazz) {
        try {
            return INSTANCE.readValue(value, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}