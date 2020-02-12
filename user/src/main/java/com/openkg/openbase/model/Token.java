package com.openkg.openbase.model;

import com.openkg.openbase.common.Cache;
import com.openkg.openbase.common.Singleton;

import java.util.List;
import java.util.Set;

/**
 * Created by mi on 18-9-29.
 */
public class Token {
    private static final String TOKEN_TABLE = "openbase:uiservice:token";

    private String user_id;
    private List<String> roles;

    public Token(String user_id, List<String> roles) {
        this.user_id = user_id;
        this.roles = roles;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String toString() {
        return Singleton.GSON.toJson(this);
    }

    public static Token fromJson(String jsonStr) {
        return Singleton.GSON.fromJson(jsonStr, Token.class);
    }

    public static Token fromCache(String token) {
        try {
            String tokenValueStr = Cache.getInstance().get(TOKEN_TABLE, token);
            return Token.fromJson(tokenValueStr);
        } catch (Exception e) {
            return null;
        }
    }
}
