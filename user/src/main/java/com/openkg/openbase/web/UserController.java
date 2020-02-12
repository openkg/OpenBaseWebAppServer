package com.openkg.openbase.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.openkg.openbase.common.*;
import com.openkg.openbase.model.*;
import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultRequest;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openkg.openbase.constant.Msg;
import com.openkg.openbase.service.UserService;

//----------------------------------------------------------------------------
// dependency for tencent sms code by daizhen
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
//------------------------------------------------------------------------------

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;

@Api(description = "openbase user api", tags = "user api",
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@RequestMapping(value = "user")
@CrossOrigin
public class UserController {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    // ---------------------------------------------------------------------------------------
    // 接入腾讯的sms服务
    // 短信应用SDK AppID
    //int appid = 1400XXXXXX; // 1400开头
    @Value("${sms.tencent.appID}")
    private int appid;
    // 短信应用SDK AppKey
    @Value("${sms.tencent.appKey}")
    private String appkey;
    // 短信模板ID，需要在短信应用中申请
    //int templateId = XXXXXX; // NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请
    @Value("${sms.tencent.templateID}")
    private int templateId;

    //-----------------------------------------------------------------------------------------
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     */
    @ApiOperation(value = "registe")
    @RequestMapping(value = "registe", method = RequestMethod.POST)
    public Res register(@RequestBody User user) {
        LOGGER.info(String.format("param is :%s", user.toString()));

        Res response = new Res();
        //手机号码、密码合法性验证
        // TODO: 18-9-29  
        String phone = user.getUser_mobile();
        String password = user.getUser_password();
        String smsCode = user.getUser_smsverificationcode();
        if (!ParamCheck.isMobiPhoneNum(phone)) {
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("手机号码无效");
            return response;
        }
        if (!ParamCheck.isPassword(password)) {
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("密码不合法");
            return response;
        }
        if (userService.phoneNumberInDB(user.getUser_mobile())) {
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("该手机号码已经注册");
            return response;
        }
        if (!(smsCode != null && !smsCode.isEmpty())) {
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("输入的验证码不能为空");
            return response;
        }

        //--------------------------------------------------------------------------------------------------------
        //---------------------------------------------------------------------------------------------
        //---------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------
        // 检查验证码是否有效
        if (userService.checkPhoneNumberAndSMSCodeTimeValid(phone, smsCode)) {
            // 验证码验证成功
            userService.deleteSMSCode(smsCode);
            String token = userService.register(user);
            response.setCode(Msg.SUCCESS.getCode());
            response.setMsg(Msg.SUCCESS.getMsg());
            if (token != null) {
                response.setToken(token);
            }
            return response;
        } else {
            //System.out.println("验证码验证失败");
            // 验证码验证失败
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("验证码验证失败");
            return response;
        }

    }


    /**
     * 用户登录
     */
    @ApiOperation(value = "login")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Res login(@RequestBody User user) {
        LOGGER.info(String.format("param is :%s", user.toString()));
        String userMobile = user.getUser_mobile();
        if (!userService.phoneNumberInDB(userMobile)) {
            Res response = new Res(Msg.FAILED.getCode(), "手机号码未注册", null, null);
            return response;
        }
        String password = user.getUser_password();
        String smsCode = user.getUser_smsverificationcode();
        if (password != null && !password.isEmpty()) {
            // 手机号和密码登录方式
            String token = userService.login(userMobile, password);
            if (token != null) {
                Res response = new Res(Msg.SUCCESS.getCode(), "登录成功", token, null);
                return response;
            } else {
                Res response = new Res(Msg.FAILED.getCode(), "密码错误", null, null);
                return response;
            }
        } else if (smsCode != null && !smsCode.isEmpty()) {
            // 手机号和验证码登录方式
            //------------------------------------------------------------------------------------------
            // 检查验证码是否有效
            if (userService.checkPhoneNumberAndSMSCodeTimeValid(userMobile, smsCode)) {
                // 验证码验证成功
                userService.deleteSMSCode(smsCode);
                User userFound = userService.getUserByPhoneNumber(userMobile);
                String token = userService.login(userFound.getUser_mobile(), userFound.getUser_password());
                Res response = new Res(Msg.SUCCESS.getCode(), "登录成功", token, null);
                return response;
            } else {
                //System.out.println("验证码验证失败");
                // 验证码验证失败
                Res response = new Res(Msg.FAILED.getCode(), "验证码验证失败", null, null);
                return response;
            }
        }

        Res response = new Res(Msg.FAILED.getCode(), "参数错误:手机号+密码/验证码二选一", null, null);
        return response;

    }

    /**
     * 获取用户信息
     */
    @ApiOperation(value = "get user detail")
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public Res findById(@RequestParam("token") String token) {
        Res res = new Res();
        //检查登录状态和权限
        res = AuthorizationOperation.checkAuthority(token, APIPermissions.USERDETAIL);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }

        //获取缓存Token值
        Token tokenValue = Token.fromCache(token);
        String id = tokenValue.getUser_id();

        User user = userService.getUserByID(id);
        if (user != null) {
            List<String> roles = tokenValue.getRoles();
            user.setUser_role_list(roles);
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("获取用户数据成功");
            res.setToken(token);
            user.setUser_password("");
            res.setData(user);
        } else {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("获取用户数据失败");
            res.setToken(token);
        }
        return res;
    }

    /**
     * 用户信息更新
     */
    @ApiOperation("detailUpdate")
    @RequestMapping(value = "detailUpdate", method = RequestMethod.POST)
    public Res detailUpdate(@RequestBody User user) {
        Res res = new Res();

        String token = user.getToken();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("登录超时或未登录");
            return res;
        }
        //检查登录状态和权限
        res = AuthorizationOperation.checkAuthority(token, APIPermissions.USERUPDATE);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }
        //从缓存中获取用户id
        Token tokenValue = Token.fromCache(token);
        if (tokenValue == null) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("登录超时或未登录");
            return res;
        }
        String user_id = tokenValue.getUser_id();

        user.setUser_id(user_id);

        boolean result = userService.update(user);
        if (result) {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("更新成功");
            res.setToken(token);
        } else {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("更新失败");
        }
        return res;
    }


    /**
     * 用户信息更新
     */
    @ApiOperation("updateRole")
    @RequestMapping(value = "updateRole", method = RequestMethod.POST)
    public Res updateRole(@RequestBody User user) {
        Res res = new Res();

        String token = user.getToken();
        if (token == null || token.equals("")) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("登录超时或未登录");
            return res;
        }

        //从缓存中获取用户id
        Token tokenObj = Token.fromCache(token);
        if (tokenObj == null) {
            res.setCode(Msg.TIMEOUT.getCode());
            res.setMsg("登录Token超时或未登录,请重新登录！");
            return res;
        }
        //权限验证
        if(!AuthorizationOperation.hasAdminAuthority(tokenObj, token)){
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("Get UserList 需要有超级管理员的权限");
            return res;
        }
        // 可能根据 phone 修改会更靠谱
        if (Strings.isNullOrEmpty(user.getUser_id().trim())) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("userid参数为空");
            return res;
        }
        List<String> user_roles = user.getUser_role_list();
        if (user_roles == null || user_roles.isEmpty()) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("user_role参数为空");
            return res;
        }
        for(String role : user_roles){
            UserRoleEnum roleEnum = UserRoleEnum.getByType(Integer.parseInt(role));
            if (roleEnum == null) {
                res.setCode(Msg.FAILED.getCode());
                res.setMsg("user_role参数错误");
                return res;
            }
        }

        User saved = userService.getUserByID(user.getUser_id());
        if (saved == null) {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("用户不存在");
            return res;
        }
        // user.setId(user_id);

        // 这个函数内部会修改user类里面的token属性，因此，后面的res.setToken需要用user.getToken()替代。
        boolean result = userService.updateRole(user.getUser_id(), user_roles, token);
        if (result) {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("更新成功");
            res.setToken(user.getToken());
        } else {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("更新失败");
        }
        return res;
    }


    /**
     * 上传用户头像
     */
    @ApiOperation("uploadPicture")
    @RequestMapping(value = "uploadPicture", method = RequestMethod.POST)
    public Res uploadPicture(@RequestBody UploadPictureRequest uploadPictureRequest) {
        Res res = new Res();
        String token = uploadPictureRequest.getToken();

        //权限验证
        res = AuthorizationOperation.checkAuthority(token, APIPermissions.USERUPLOADPICTURE);
        if (res.getCode() != Msg.SUCCESS.getCode()) {
            return res;
        }
        //String filetype = uploadPictureRequest.getFiletype();
        String content = uploadPictureRequest.getFilecontent();
        String uri = userService.uploadPicture(content);
        if (uri != null) {
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg("上传头像成功");
            res.setToken(token);
            Map<String, String> jsonObject = new HashMap<>();
            jsonObject.put("uri", uri);
            res.setData(jsonObject);
        } else {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg("上传头像失败");
            res.setToken(token);
        }
        return res;
    }

    /**
     * 用户退出登录
     */
    @ApiOperation(value = "logout")
    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public Res logout(@RequestParam("token") String token) {

        Res response = new Res();
        if (userService.logout(token)) {
            response.setCode(Msg.SUCCESS.getCode());
            response.setMsg("注销成功");
        } else {
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("注销失败");
        }
        return response;

    }

    /**
     * 获取申请审核者的测试题目, 目前数据库中一共20个题目
     * by daizhen
     */
    @ApiOperation("getQuestion")
    @RequestMapping(value = "getQuestion", method = RequestMethod.GET)
    public Res getQuestion(@RequestParam("token") String token) {
        Res response = new Res();
        if (token == null || token.equals("")) {
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("请求参数错误");
            return response;
        }
        //权限验证
        response = AuthorizationOperation.checkAuthority(token, APIPermissions.USERGETQUESTION);
        if (response.getCode() != Msg.SUCCESS.getCode()) {
            return response;
        }
        List<Map<String, String>> Data = userService.getQuestion();
        Cache.getInstance().setValueTime("usertoken", token, Cache.TimeUnit.SECOND, 300);
        response.setToken(token);
        response.setData(Data);
        response.setMsg("请求成功");
        response.setCode(Msg.SUCCESS.getCode());
        return response;

    }


    /**
     * 检查用户上传的答案和标准答案的一致性，并依次判断是否给予审核者权限
     * by 李娟
     * 注意，权限更改后刷新redis，否则需要重新登录后才能获取对应权限
     */
    @ApiOperation("check")
    @RequestMapping(value = "check", method = RequestMethod.POST)
    public Res check(@RequestBody QuestionCheckRequest questionCheckRequest) {
        String oldToken = questionCheckRequest.getToken();
        List<Map> answer = questionCheckRequest.getAnswer();
        Res response = new Res();
        if (oldToken == null || oldToken.equals("")) {
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("请求参数错误");
            return response;
        }

        //权限验证
        response = AuthorizationOperation.checkAuthority(oldToken, APIPermissions.USERCHECKANSWER);
        if (response.getCode() != Msg.SUCCESS.getCode()) {
            return response;
        }
        Token tokenValue = Token.fromCache(oldToken);
        String user_id = tokenValue.getUser_id();

        int correctCount = userService.howManyAnswerIsCorrect(answer);
        Map<String, Integer> responseData = new HashMap<>();
        responseData.put("rightCount", correctCount);
        responseData.put("count", answer.size());
        response.setData(responseData);
        String newToken = userService.isCheckPassed(user_id, oldToken, (float)correctCount / answer.size());
        if (newToken != null) {
            response.setToken(newToken);
            response.setMsg("申请通过");
            response.setCode(Msg.SUCCESS.getCode());
        } else {
            response.setMsg("申请不通过");
            response.setCode(Msg.FAILED.getCode());
            response.setToken(oldToken);
        }

        return response;

    }

    @ApiOperation("smscode")
    @RequestMapping(value = "smscode", method = RequestMethod.POST)
    public Res smscode(@RequestBody SmsCodeRequest smsCodeRequest, HttpServletRequest request) {

        String remoteAddress = request.getRemoteAddr(); // IP/Host of remote user
        LOGGER.info(String.format("param is :%s", smsCodeRequest.getUser_phone_number()));

        Res response = new Res();

        //手机号码、密码合法性验证
        // TODO: 18-9-29
        String phone = smsCodeRequest.getUser_phone_number();
        if (!ParamCheck.isMobiPhoneNum(phone)) {
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("手机号码无效");
            return response;
        }

        try{
            Credential cred = new Credential("secretid", "secretkey");

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("captcha.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            CaptchaClient client = new CaptchaClient(cred, "", clientProfile);
            CaptchaParams captchaParams= new CaptchaParams();
            captchaParams.setUserIp(remoteAddress);
            captchaParams.setTicket(smsCodeRequest.getTicket());
            captchaParams.setRandomStr(smsCodeRequest.getRandom_string());
            String params = Singleton.GSON.toJson(captchaParams);
            DescribeCaptchaResultRequest req = DescribeCaptchaResultRequest.fromJsonString(params, DescribeCaptchaResultRequest.class);

            DescribeCaptchaResultResponse resp = client.DescribeCaptchaResult(req);

            if(resp.getCaptchaCode() == 1){
                // okey
                // nothing to do, just go through this block.
            }else {
                // wrong
                response.setCode(Msg.FAILED.getCode());
                response.setMsg(resp.getCaptchaMsg());
                return response;
            }
        }catch (TencentCloudSDKException e){
            response.setCode(Msg.FAILED.getCode());
            response.setMsg(e.toString());
            e.printStackTrace();
            return response;
        }


        try {
            String new_smsCode = userService.generateNewValidSMSCode();
            int validSmsCodeInterval = userService.GetValidSMSCodeTimeIntervalInMinutes();
            String[] params = {new_smsCode, Integer.toString(validSmsCodeInterval)};//数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.sendWithParam("86", phone,
                    templateId, params, "", "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            if (result.result == 0) {
                // 成功发送短信
                response.setCode(Msg.SUCCESS.getCode());
                response.setMsg(Msg.SUCCESS.getMsg());
                userService.cachePhoneNumberAndTimeStampInRedis(new_smsCode, phone, new Date());
                return response;
            } else {
                response.setCode(Msg.FAILED.getCode());
                response.setMsg(result.errMsg);
                return response;
            }
            //System.out.println(result);

        } catch (HTTPException e) {
            // HTTP响应码错误
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("HTTP响应码错误");
            e.printStackTrace();
            return response;
        } catch (JSONException e) {
            // json解析错误
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("json解析错误");
            e.printStackTrace();
            return response;
        } catch (IOException e) {
            // 网络IO错误
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("网络IO错误");
            e.printStackTrace();
            return response;
        }

    }

    /**
     * 检查题目，更改权限
     * by 李娟
     * 注意，权限更改后刷新redis，否则需要重新登录后才能获取对应权限
     */
    @ApiOperation("userList")
    @RequestMapping(value = "userList", method = RequestMethod.GET)
    public Res userList(String token, Integer pageSize, Integer pageNumber) {

        Res response = new Res();
        if (token == null || token.equals("")) {
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("请求参数错误");
            return response;
        }

        if (pageSize == null || pageSize <= 0) {
            pageSize = 20;
        }
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        Token tokenObj = Token.fromCache(token);
        if (tokenObj == null) {
            response.setCode(Msg.TIMEOUT.getCode());
            response.setMsg("登录Token超时,请重新登录！");
            return response;
        }
        //权限验证
        if(!AuthorizationOperation.hasAdminAuthority(tokenObj, token)){
            response.setCode(Msg.FAILED.getCode());
            response.setMsg("Get UserList 需要有超级管理员的权限");
            return response;
        }

        PaginationResult<UserResponse> listWithPage = userService.getUserList(pageNumber, pageSize);
        response.setData(listWithPage.getList());
        response.setPage(listWithPage.getPage());
        response.setToken(token);
        response.setMsg(Msg.SUCCESS.getMsg());
        return response;
    }

}

