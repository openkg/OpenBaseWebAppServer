package com.openkg.openbase.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mi on 18-10-8.
 */
public class APIPermissions {
    public final static Integer USERLOGIN = 1;  //用户登录
    public final static Integer USERREGISTER = 2;  //用户注册

    public final static Integer USERUPDATE = 3;  //用户信息更新
    public final static Integer USERDETAIL = 4;  //获取用户信息
    public final static Integer USERGETQUESTION = 5; //获取题目
    public final static Integer USERCHECKANSWER = 6; //获取权限
    public final static Integer USERUPLOADPICTURE = 7; //上传用户头像
    public final static Integer USERLOGOUT = 8; //注销


    public final static Integer REVIEW = 11; //审核权限
    public final static Integer CHECK = 21; //验收权限
    public final static Integer VIEWKG =31; //浏览图谱

    public final static Integer UPDATEENTITY = 32; // 更新词条
    public final static Integer CREATENTITY = 33; // 新增词条
    public final static Integer DELETEENTITY = 34; // 删除词条

    public static Map<Integer, String> PermissionDescriptionMap;
    static {
        PermissionDescriptionMap = new HashMap<>();
        PermissionDescriptionMap.put(1, "帐号登录");
        PermissionDescriptionMap.put(2, "帐号注册");
        PermissionDescriptionMap.put(3, "更新帐号信息");
        PermissionDescriptionMap.put(4, "获取帐号信息");
        PermissionDescriptionMap.put(5, "审核专家答题");
        PermissionDescriptionMap.put(6, "答题结果评估");
        PermissionDescriptionMap.put(7, "上传帐号头像");
        PermissionDescriptionMap.put(8, "帐号登出");
        PermissionDescriptionMap.put(11, "审核");
        PermissionDescriptionMap.put(21, "验收");
        PermissionDescriptionMap.put(31, "浏览图谱");
        PermissionDescriptionMap.put(32, "更新词条");
        PermissionDescriptionMap.put(33, "新增词条");
        PermissionDescriptionMap.put(34, "删除词条");
    }

}
