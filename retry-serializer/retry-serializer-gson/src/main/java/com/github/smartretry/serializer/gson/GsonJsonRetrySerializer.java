package com.github.smartretry.serializer.gson;

import com.github.smartretry.core.RetrySerializer;
import com.google.gson.Gson;
import org.kohsuke.MetaInfServices;

/**
 * @author yuni[mn960mn@163.com]
 */
@MetaInfServices(RetrySerializer.class)
public class GsonJsonRetrySerializer implements RetrySerializer {

    private Gson gson = new Gson();

    @Override
    public String serialize(Object object) {
        return gson.toJson(object);
    }

    @Override
    public Object deserialize(String value, Class<?> clazz) {
        return gson.fromJson(value, clazz);
    }
}
