package com.openkg.openbase.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openkg.openbase.common.mongo.MongoDBUtil;

import java.lang.reflect.Type;
import java.util.Map;


/**
 * Created by mi on 18-9-29.
 */

public class Singleton {
    public static final Gson GSON = new Gson();
    public static final Type GSON_REPRESENTATIVE_MAP = new TypeToken<Map<String, Object>>() {}.getType();
    public static final MongoDBUtil mongoDBUtil= new MongoDBUtil();
}

