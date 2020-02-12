package com.openkg.openbase.common;

import com.openkg.openbase.web.UserController;
import org.apache.jena.base.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mi on 18-10-10.
 */
public class ParamCheck {
    private final static Logger LOGGER = LoggerFactory.getLogger(ParamCheck.class);

    private static final Pattern pPhone = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern pChineseWord = Pattern.compile("[\\u4e00-\\u9fa5]");

    /*
    **手机号合法检测
     */
    public static boolean isMobiPhoneNum(String telNum) {
        Matcher m = pPhone.matcher(telNum);
        return m.matches();
    }


    /*
    **密码合法性检测
     */
    public static boolean isPassword(String password) {
        if (password.length() <6) {
            return false;
        }
        //判断是否有空格字符串
        if (password.contains(" ")){
            return false;
        }
        //判断是否有汉字
        Matcher m = pChineseWord.matcher(password);
        if (m.find()) {
            return false;
        }
        //判断是否是字母和数字
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

}
