package com.openkg.openbase.common;

import java.util.UUID;

/**
 * Created by mi on 18-9-30.
 */
public class Uuid {

    public static String getUuid(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-","");
    }

    public static String getUuid(String str){
        UUID uuid = UUID.fromString(str);
        return  uuid.toString().replace("-","");
    }
}
