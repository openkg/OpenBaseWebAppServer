package com.openkg.openbase.web;


import com.openkg.openbase.common.AuthorizationOperation;
import com.openkg.openbase.common.APIPermissions;
import com.openkg.openbase.constant.Msg;
import com.openkg.openbase.model.Res;
import com.openkg.openbase.model.Token;
import com.openkg.openbase.service.EditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public void setEditService(EditService editService) { this.editService = editService; }

    @ApiOperation(value = "updateEntity")
    @RequestMapping(value = "updateEntity", method = RequestMethod.POST)
    public Res submitUpdateEntity(@RequestBody Map<String,Object> map) {
        String token = (String) map.get("token");
        HashMap data = (HashMap<String, String>) map.get("updateEntity");
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
        if (editService.updateEntity(Token.fromCache(token).getUser_id(), data)) {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("编辑结果,保存成功");
            res.setToken(token);
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
