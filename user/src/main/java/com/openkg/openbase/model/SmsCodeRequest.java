package com.openkg.openbase.model;

import lombok.Data;

@Data
public class SmsCodeRequest {
    private String user_phone_number;
    private String random_string;
    private String ticket;

    public String getUser_phone_number() {
        return user_phone_number;
    }

    public void setUser_phone_number(String user_mobile) {
        this.user_phone_number = user_mobile;
    }

    public String getRandom_string() {
        return random_string;
    }

    public void setRandom_string(String randStr) {
        this.random_string = randStr;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String tick) {
        this.ticket = tick;
    }
}
