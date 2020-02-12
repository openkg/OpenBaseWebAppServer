package com.openkg.openbase.web;

import com.openkg.openbase.Manage.ReviewFactory;
import com.openkg.openbase.common.AuthorizationOperation;
import com.openkg.openbase.common.APIPermissions;
import com.openkg.openbase.constant.Msg;
import com.openkg.openbase.model.*;
import com.openkg.openbase.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(description = "openbase review api", tags = "review api",
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@RequestMapping(value = "review")
@CrossOrigin
public class ReviewController {
    private ReviewService reviewService;

    @Autowired
    public void setReviewService(ReviewService reviewService) { this.reviewService = reviewService; }

    /**
     * 领取审核任务
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
        res = AuthorizationOperation.checkAuthority(token, APIPermissions.REVIEW);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }

        //领取任务, 返回任务数据
        Map data = reviewService.getReivewJob(Token.fromCache(token).getUser_id(),source);
        if(data==null){
            res.setMsg("暂时没有可领取的任务");
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
     * 审核任务保存
     * */
    @ApiOperation(value = "saveTask")
    @RequestMapping(value = "saveTask", method = RequestMethod.POST)
    public Res saveTask(@RequestBody Map<String,Object> map) {
        String token = (String) map.get("token");
        String jobId = (String) map.get("jobId");
        Integer currentPage = (Integer) map.get("currentPage");
        Integer reviewSpan = (Integer) map.get("reviewSpan");
        List<HashMap> data = (List<HashMap>) map.get("data");

        Res res = new Res();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token,APIPermissions.REVIEW);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }

        if (reviewService.saveTask(Token.fromCache(token).getUser_id(), jobId, currentPage, reviewSpan, data)) {
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
     * 审核任务继续
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
        res = AuthorizationOperation.checkAuthority(token,APIPermissions.REVIEW);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }

        Map data = reviewService.continueTask(Token.fromCache(token).getUser_id(), jobId);
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
     * 审核任务提交
     * */
    @ApiOperation(value = "commitTask")
    @RequestMapping(value = "commitTask", method = RequestMethod.POST)
    public Res commitTask(@RequestBody Map<String,Object> map) {
        String token = (String) map.get("token");
        String jobId = (String) map.get("jobId");
        Integer currentPage = (Integer) map.get("currentPage");
        Integer reviewSpan = (Integer) map.get("reviewSpan");
        List<HashMap> data = (List<HashMap>) map.get("data");

        Res res = new Res();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token,APIPermissions.REVIEW);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }

        if (reviewService.commitTask(Token.fromCache(token).getUser_id(), jobId, currentPage, reviewSpan, data)) {
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
     * 审核任务记录与统计
     * */
    @ApiOperation(value = "getStats")
    @RequestMapping(value = "getStats", method = RequestMethod.GET)
    public Res getState(@RequestParam("token") String token,@RequestParam("source") String source) {
        Res res = new Res();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token,APIPermissions.REVIEW);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }

        Map map = reviewService.getState(Token.fromCache(token).getUser_id(), source);
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
    
    @RequestMapping(value = "updateAllReviewSeconds", method = RequestMethod.GET)
    public Res updateAllReviewSeconds(@RequestParam("token") String token,@RequestParam("source") String source) {
        Res res = new Res();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("请求参数错误");
            return res;
        }
        //权限验证
        res = AuthorizationOperation.checkAuthority(token,APIPermissions.REVIEW);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }
        
        int total = reviewService.updateUserReviewSeconds(source);
       
        res.setCode(Msg.SUCCESS.getCode());
        res.setMsg("更新成功");
        res.setToken(token);
        res.setData(total);
        return res;
    }
}
