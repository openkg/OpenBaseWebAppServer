package com.openkg.openbase.common;

import com.openkg.openbase.constant.Msg;
import com.openkg.openbase.model.Res;
import com.openkg.openbase.model.Token;
import com.openkg.openbase.model.User;
import com.openkg.openbase.service.UserService;
import org.apache.jena.atlas.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mi on 18-10-8.
 */
public class AuthorizationOperation {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthorizationOperation.class);

    private static final String TOKEN_TABLE = "openbase:uiservice:token";

    private final static int[] admin_permissions = {1, 2, 3, 4, 7, 11, 21, 31, 32, 33, 34};
    private final static int[] checker_permissions = {1, 2, 3, 4, 7, 21, 31, 32, 33, 34};
    private final static int[] reviewer_permissions = {1, 2, 3, 4, 5,6,7, 11, 31, 32, 33, 34};
    private final static int[] visitor_permissions = {1, 2, 3, 4, 5, 6, 7, 31, 32, 33, 34};

    private static Map<String, int[]> rolePermissionMap;
    static {
        rolePermissionMap = new HashMap<>();
        rolePermissionMap.put("1", admin_permissions);
        rolePermissionMap.put("2", reviewer_permissions);
        rolePermissionMap.put("3", checker_permissions);
        rolePermissionMap.put("4", visitor_permissions);
    }

    public static Res checkAuthority(String tokenStr, Integer permission) {
        Res response = new Res();
        Token token = Token.fromCache(tokenStr);
        if (token == null) {
            response.setCode(Msg.TIMEOUT.getCode());
            response.setMsg("登录Token超时,请重新登录！");
            return response;
        }
        //刷新token存在时间
        Cache.getInstance().setValueTime(TOKEN_TABLE, tokenStr, Cache.TimeUnit.SECOND, 60*60);
        List<Integer> authorities = allPermissionOfRoles(token.getRoles());
        if (authorities.contains(permission) || permission == -1) {
            response.setCode(Msg.SUCCESS.getCode());
        } else {
            response.setCode(Msg.FAILED.getCode());
            response.setMsg(String.format("对不起，您没有[%s]权限执行该操作！", APIPermissions.PermissionDescriptionMap.get(permission)));
            response.setToken(tokenStr);
        }
        return response;
    }

    public static Boolean hasAdminAuthority(Token tk, String tkStr){
        //刷新token存在时间
        Cache.getInstance().setValueTime(TOKEN_TABLE, tkStr, Cache.TimeUnit.SECOND, 60*60);
        return tk.getRoles().contains("1");
    }

    /*
    *实现生成token和保存
    */
    public static String tokenGenerateAndSave(User user) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String t = MD5(user.getId() + df.format(new Date()));

        //获取用户所有角色
        List<String> roles = user.getUser_role_list();

        LOGGER.info(String.format("roles : %s" ,roles.toString()));
        LOGGER.info(String.format("authorities : ", allPermissionOfRoles(roles).toString()));
        Token token = new Token(user.getUser_id(), roles);
        Cache.getInstance().set(TOKEN_TABLE, t, token.toString(), Cache.TimeUnit.SECOND, 60*60);
        return t;
    }

    private static List<Integer> allPermissionOfRoles(List<String> roleList){
        HashSet<Integer> ret = new HashSet<Integer>();
        for(String role : roleList){
            if(rolePermissionMap.containsKey(role)){
                for(Integer p : rolePermissionMap.get(role)){
                    ret.add(p);
                }
            }
        }
        List<Integer> retList = new ArrayList<Integer>(ret);
        return retList;
    }


    private static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
}
