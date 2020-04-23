package com.jd.bluedragon.test;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

public class BaseMockTest {

    public static Gson gson;

    static {

        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        gson = builder.create();

    }

}
