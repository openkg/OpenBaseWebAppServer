package com.openkg.openbase.model;

import com.google.gson.JsonObject;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class Res {

    private int Code;
    private String Msg;
    private String Token;
    private Object Data;
    private Page page;

    public Res(){}

    public Res(int code,String msg,String token,JsonObject data){
        Code = code;
        Msg = msg;
        Token = token;
        Data = data;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        this.Code = code;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        this.Msg = msg;
    }

    public Object getData() {
        return Data;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public void setData(Object data) {
        this.Data = data;
    }

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
