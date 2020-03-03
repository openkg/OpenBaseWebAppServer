package com.openkg.openbase.web;


import com.openkg.openbase.common.AuthorizationOperation;
import com.openkg.openbase.common.APIPermissions;
import com.openkg.openbase.constant.Msg;
import com.openkg.openbase.model.Res;
import com.openkg.openbase.model.Token;
import com.openkg.openbase.service.EditService;
import com.openkg.openbase.service.HttpClientService;
import com.openkg.openbase.service.MongoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "openbase edit api", tags = "edit api",
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@RequestMapping(value = "edit")
@CrossOrigin
public class EditController {
    private EditService editService;
    private HttpClientService httpClient;
    private MongoService mongoService;

    @Autowired
    public void setEditService(EditService editService) { this.editService = editService; }

    @Autowired
    public void setHttpClient(HttpClientService httpClient) {
        this.httpClient = httpClient;
    }

    @Autowired
    public void setMongoService(MongoService mongoService){this.mongoService = mongoService;}

    @ApiOperation(value = "updateEntity")
    @RequestMapping(value = "updateEntity", method = RequestMethod.POST)
    public Res submitUpdateEntity(@RequestBody Map<String,Object> map) {
        String token = (String) map.get("token");
        HashMap data = (HashMap<String, String>) map.get("updateEntity");
        Res res = new Res();

        System.out.println();
        Token tokenvalue = Token.fromCache(token);
        String user_id = tokenvalue.getUser_id();
        String data_id = (String)data.get("@id");
        int data_size = data.size();
        // 需要写一个查询old_version的接口
        String old_version = mongoService.getEntityHistoryByID(data_id, "update");

        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token, APIPermissions.UPDATEENTITY);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }
        // 此处修改 时间戳不能够有中文字符
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (editService.updateEntity(Token.fromCache(token).getUser_id(), data, timeStamp)) {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("编辑结果,保存成功");
            res.setToken(token);

            try {
                System.out.println("submitUpdateEntity: user_id = " + user_id);
                System.out.println("data_id = " + data_id);
                System.out.println("old version: " + old_version);
                System.out.println("new version: " + timeStamp);
                System.out.println("data_size = " + data_size);

                Map<String, Object> parameter = new HashMap<String, Object>();
                // put更新实例
                parameter.put("dataId", data_id);
                parameter.put("newVersion", timeStamp);
                parameter.put("oldVersion", old_version);
                // 目前时间戳这个参数没法测试，只能空值
                parameter.put("userId", user_id);
//                HttpClientService.HttpResponse response_http = httpClient.doPut("http://113.31.104.113:8080/api/v1/ont-id/data", parameter);
//                System.out.println("post response = " + response_http.getBody());
                // get请求得到荣誉值
//                String get_url = "http://113.31.104.113:8080/api/v1/honor-point?userId=" + user_id;
//                String response_str = httpClient.doGet(get_url);
//                System.out.println("get response = " + response_str);
            }
            catch (Exception e){
                e.printStackTrace();
            }


        } else {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("编辑结果,保存失败");
            res.setToken(token);
        }
        return res;
    }

    @ApiOperation(value = "createEntity")
    @RequestMapping(value = "createEntity", method = RequestMethod.POST)
    public Res submitCreateEntity(@RequestBody Map<String,Object> map) {
        String token = (String) map.get("token");
        HashMap data = (HashMap) map.get("createEntity");
        Res res = new Res();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token, APIPermissions.CREATENTITY);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }
        if (editService.createEntity(Token.fromCache(token).getUser_id(), data)) {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("新增结果,保存成功");
            res.setData(data);
            res.setToken(token);



        } else {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("新增结果,保存失败");
            res.setToken(token);
        }
        return res;
    }

    @ApiOperation(value = "deleteEntity")
    @RequestMapping(value = "deleteEntity", method = RequestMethod.POST)
    public Res submitDeleteEntity(@RequestBody Map<String,Object> map) {
        String token = (String) map.get("token");
        HashMap data = (HashMap) map.get("deleteEntity");
        Res res = new Res();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token, APIPermissions.DELETEENTITY);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }

        if (editService.deleteEntity(Token.fromCache(token).getUser_id(), data)) {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("实体删除成功");
            res.setData(data);
            res.setToken(token);
        } else {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("实体删除失败");
            res.setToken(token);
        }
        return res;
    }

    @ApiOperation(value = "searchPropertyNameList")
    @RequestMapping(value = "searchPropertyNameList", method = RequestMethod.POST)
    public Res searchPropertyNameList(@RequestBody Map<String,Object> map) {
        String token = (String) map.get("token");
        String key = (String) map.get("searchKey");
        Res res = new Res();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token, APIPermissions.UPDATEENTITY);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }
        
        HashMap resultNameList = editService.searchPropertyNameList(key);
        if (null == resultNameList) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("属性列表获取失败");
            res.setToken(token);
        } else {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("属性列表获取成功");
            res.setData(resultNameList);
            res.setToken(token);
        }
        return res;
    }
}
