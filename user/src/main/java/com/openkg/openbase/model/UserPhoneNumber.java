package com.openkg.openbase.model;

import lombok.Data;

@Data
public class UserPhoneNumber {
    private String user_mobile;

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }
}
