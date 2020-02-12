package com.openkg.openbase.web;

import com.openkg.openbase.Manage.CheckFactory;
import com.openkg.openbase.Manage.ReviewFactory;
import com.openkg.openbase.common.AuthorizationOperation;
import com.openkg.openbase.common.APIPermissions;
import com.openkg.openbase.constant.Msg;
import com.openkg.openbase.model.Res;
import com.openkg.openbase.model.Subject;
import com.openkg.openbase.model.Token;
import com.openkg.openbase.service.CheckService;
import com.openkg.openbase.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(description = "openbase review api", tags = "acceptance api",
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@RequestMapping(value = "acceptance")
@CrossOrigin
public class CheckController {
    private CheckService checkService;

    @Autowired
    public void setCheckService(CheckService checkService) { this.checkService = checkService; }

    /**
     * 领取验收任务
     * */
    @ApiOperation(value = "getTask")
    @RequestMapping(value = "getTask", method = RequestMethod.GET)
    public Res getTask(@RequestParam("token") String token, @RequestParam("source") String source) {
        Res res = new Res();
        if (token == null || token.equals("") || source == null || source.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token, APIPermissions.CHECK);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }

        //领取任务, 返回任务数据
        Map data = checkService.getJobData(Token.fromCache(token).getUser_id(),source);
        if(data==null){
            res.setMsg("没有可领取的任务，请检查审核进度");
            res.setCode(Msg.FAILED.getCode());
            return res;
        }
        if (data.size() == 0){
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("任务出错");
            res.setToken(token);
        } else {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("领取成功");
            res.setToken(token);
            res.setData(data);
        }
        return res;
    }


    /**
     * 验收任务保存
     * */
    @ApiOperation(value = "saveTask")
    @RequestMapping(value = "saveTask", method = RequestMethod.POST)
    public Res saveTask(@RequestBody Map<String,Object> map) {
        String token = (String) map.get("token");
        String jobId = (String) map.get("jobId");
        Integer currentPage = (Integer) map.get("currentPage");
        Integer reviewSpan = (Integer) map.get("acceptanceSpan");
        List<HashMap> data = (List<HashMap>) map.get("data");

        Res res = new Res();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token,APIPermissions.CHECK);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }

        if (checkService.saveTask(Token.fromCache(token).getUser_id(), jobId, currentPage, reviewSpan, data)) {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("保存成功");
            res.setToken(token);
        } else {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("保存失败");
            res.setToken(token);
        }
        return res;
    }

    /**
     * 验收任务继续
     * */
    @ApiOperation(value = "continueTask")
    @RequestMapping(value = "continueTask", method = RequestMethod.GET)
    public Res continueTask(@RequestParam("token") String token, @RequestParam("jobId") String jobId) {
        Res res = new Res();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token,APIPermissions.CHECK);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }

        Map data = checkService.continueTask(Token.fromCache(token).getUser_id(), jobId);
        if (data == null){
            res.setMsg("获取该审核任务发生异常，请联系管理员！");
            res.setCode(Msg.FAILED.getCode());
            return res;
        }
        if (data.size() == 0){
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("任务出错");
            res.setToken(token);
        } else {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("领取成功");
            res.setToken(token);
            res.setData(data);
        }
        return res;
    }

    /**
     * 验收任务提交
     * */
    @ApiOperation(value = "commitTask")
    @RequestMapping(value = "commitTask", method = RequestMethod.POST)
    public Res commitTask(@RequestBody Map<String,Object> map) {
        String token = (String) map.get("token");
        String jobId = (String) map.get("jobId");
        Integer currentPage = (Integer) map.get("currentPage");
        Integer reviewSpan = (Integer) map.get("acceptanceSpan");
        List<HashMap> data = (List<HashMap>) map.get("data");
        Res res = new Res();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token,APIPermissions.CHECK);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }

        if (checkService.commitTask(Token.fromCache(token).getUser_id(), jobId, currentPage, reviewSpan, data)) {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("保存成功");
            res.setToken(token);
        } else {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("保存失败");
            res.setToken(token);
        }
        return res;
    }

    /**
     * 验收任务记录与统计
     * */
    @ApiOperation(value = "getStats")
    @RequestMapping(value = "getStats", method = RequestMethod.GET)
    public Res getState(@RequestParam("token") String token, @RequestParam("source") String source) {
        Res res = new Res();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token,APIPermissions.CHECK);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }

        Map map = checkService.getState(Token.fromCache(token).getUser_id(), source);
        if (map != null) {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("任务列表,统计获取成功");
            res.setToken(token);
            res.setData(map);
        } else {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("获取失败");
            res.setToken(token);
        }
        return res;
    }
}
