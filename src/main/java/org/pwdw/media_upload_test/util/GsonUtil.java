package org.pwdw.media_upload_test.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Component;

@Component
public class GsonUtil {

    private final Gson gson;

    public GsonUtil() {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        this.gson = gsonBuilder.create();
    }

    public String print(Object object) {
        return gson.toJson(object);
    }

}
