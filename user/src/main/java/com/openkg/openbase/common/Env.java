package com.openkg.openbase.common;

/**
 * Created by mi on 18-9-27.
 */
public enum Env {
    DEFAULT("default"),DEV("dev"),PROD("PROD");
    public  String fullName;

    Env(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return toString().toLowerCase();
    }

    public String getFullName() {
        return fullName;
    }

    public static Env getEnum(String fullName) {
        for (Env v : values()) {
            if (v.getFullName().equalsIgnoreCase(fullName)) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }
}
