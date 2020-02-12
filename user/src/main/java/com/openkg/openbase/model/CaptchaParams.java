package com.openkg.openbase.model;

public class CaptchaParams {
    private String CaptchaType = "9";
    private String UserIp;
    private String Randstr;
    private String CaptchaAppId = "2069503143";
    private String AppSecretKey = "0fR8G0p1LaAxj6aDiAH0e4g**";
    private String Ticket;

    public String getCaptchaType() {
        return CaptchaType;
    }

    public String getRandomStr() {
        return Randstr;
    }

    public void setRandomStr(String randStr) {
        this.Randstr = randStr;
    }

    public String getTicket() {
        return Ticket;
    }

    public void setTicket(String tick) {
        this.Ticket = tick;
    }

    public String getUserIp() {
        return UserIp;
    }

    public void setUserIp(String uIp) {
        this.UserIp = uIp;
    }

    public String getCaptchaAppId() {
        return CaptchaAppId;
    }

    public void setCaptchaAppId(String appId) {
        this.CaptchaAppId = appId;
    }

    public String getAppSecretKey() {
        return AppSecretKey;
    }

}
