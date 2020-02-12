package com.openkg.openbase.constant;

public enum Msg {
    SUCCESS(0, "success"), FAILED(1, "failed"), EXCEPTION(-1, "exception") ,TIMEOUT(2,"timeout"),NOAUTH(3,"noauth");

    private int code;
    private String msg;

    Msg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {

        return code;
    }

    public String getMsg() {
        return msg;
    }
}
