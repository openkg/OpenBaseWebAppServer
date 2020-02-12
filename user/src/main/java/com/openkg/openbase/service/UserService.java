package com.openkg.openbase.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import com.openkg.openbase.common.AuthorizationOperation;
import com.openkg.openbase.common.Cache;
import com.openkg.openbase.common.Singleton;
import com.openkg.openbase.common.Uuid;
import com.openkg.openbase.model.*;


import org.apache.commons.codec.binary.Base64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sun.misc.BASE64Decoder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.concurrent.TimeUnit;

import org.apache.tika.Tika;

import static com.mongodb.client.model.Filters.eq;

@Service
public class UserService {

    private static final String TOKEN_TABLE = "openbase:uiservice:token";
    private static final String SMSCODE_TABLE = "openbase:uiservice:smscode";
    private static final String DateTimeFormatStr = "yyyy-MM-dd HH:mm:ss";
    private static int ValidSMSCodeTimeIntervalSeconds = 10 * 60;
    private static float applyReviewerCorrectRationTheshold = 0.9f;
    //private static float applyReviewerCorrectRationTheshold = 0.01f;
    private static final BASE64Decoder decoder = new BASE64Decoder();
    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final String fds_bucketName = "cloudfilesystembucket";
    private static final String fds_prefix = "http://cloudfilesystemendpoint";
    private static final String USERCOLLECTION = "user";
    private static final MongoCollection<Document> userCollection = Singleton.mongoDBUtil.getdb().getCollection(USERCOLLECTION);

    private Tika single_tika_instance;

    private Map<String, String> mimeType2FileExtMap;

    private ReviewQA reviewQuestionAnswer;

    public int GetValidSMSCodeTimeIntervalInMinutes() {
        return ValidSMSCodeTimeIntervalSeconds / 60;
    }

    private ReviewQA getReviewQuestionAnswer(){
        if (null == reviewQuestionAnswer){
            Gson gson = new Gson();
            try{
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("reviewerQA");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = reader.readLine();
                ReviewQA rQA = gson.fromJson(line, ReviewQA.class);
                reviewQuestionAnswer = rQA;
            }catch (IOException ex){
                ex.printStackTrace();
                System.err.println("[ERROR] [RESOURCES NOT FOUND]  can't read reviewerQA file content");
            }
        }

        return reviewQuestionAnswer;
    }

    private Map<String, String> getMimeType2FileExtHashMap(){
        if (null == mimeType2FileExtMap){
            //InputStream inputStream = ClassLoaderUtil.getResourceAsStream("MimeType2FileExt_config_daizhen", VerticalMain.class);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("MimeType2FileExt_config_daizhen");
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            //File file = new File(getClass().getClassLoader().getResource("MimeType2FileExt_config_daizhen").getFile());
            //String mimeType2Ext_Filepath = "conf/MimeType2FileExt_config_daizhen";
            //System.out.println(mimeType2Ext_Filepath);
            Map<String, String> mimetype2ext_hashmap = new HashMap<String, String>();
            try{
                //BufferedReader reader = new BufferedReader(new FileReader(file));
                BufferedReader reader = new BufferedReader(streamReader);
                String line = reader.readLine();
                while (line != null) {
                    //System.out.println(line);
                    String[] i_list =  line.trim().split("\\t");
                    //System.out.println(i_list[0]);
                    mimetype2ext_hashmap.put(i_list[0], i_list[1]);
                    // read next line
                    line = reader.readLine();
                }
                reader.close();
            }catch (IOException ex){
                ex.printStackTrace();
                System.err.println("[ERROR] [RESOURCES NOT FOUND]  can't read MimeType2FileExt_config_daizhen file content");
                //throw new IOException("Error reading MimeType2FileExt_config_daizhen file !!");
            }
            mimeType2FileExtMap = mimetype2ext_hashmap;
        }

        return mimeType2FileExtMap;
    }

    public Tika getSingleTikaInstance(){
        if (null == single_tika_instance){
            single_tika_instance = new Tika();
        }
        return single_tika_instance;
    }

    public Boolean phoneNumberInDB(String phone) {
        Document match = new Document();
        match.put("phoneNumber",phone);
        long count  = userCollection.count(match);
        if(count <= 0){
            return false;
        }else{
            return true;
        }
    }

    public User getUserByPhoneNumber(String phone) {
        User userFound = null;
        Document match = new Document();
        match.put("phoneNumber",phone);

        MongoCursor<Document> cursor = userCollection.find(match).iterator();

        if (cursor.hasNext()) {
            Document one = cursor.next();
            userFound = User.fromDocument(one);
        }

        return userFound;
    }

    /**
     * 缓存用户验证短信提供的手机号和短信平台发送验证短信的时间戳
     */
    public void cachePhoneNumberAndTimeStampInRedis(String smsCode, String phoneNumber, Date smsSendingTime) {
        SimpleDateFormat df = new SimpleDateFormat(DateTimeFormatStr);//设置日期格式
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            JsonObject jObj = new JsonObject();
            jObj.addProperty("phoneNumber", phoneNumber);
            jObj.addProperty("timeStamp", df.format(smsSendingTime));
            Cache.getInstance().set(SMSCODE_TABLE, smsCode, jObj.toString(), Cache.TimeUnit.SECOND, ValidSMSCodeTimeIntervalSeconds);
        }
    }

    /**
     * 生成和当前缓存的有效验证码不相同的新的验证码, 现在的验证码是6位数字字符串
     */
    public String generateNewValidSMSCode() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        long startTime = System.currentTimeMillis();
        while (false || (System.currentTimeMillis() - startTime < 3000)) {
            String result = Cache.getInstance().get(SMSCODE_TABLE, String.format("%06d", number));
            if (result == null || result.isEmpty()) {
                break;
            } else {
                number = rnd.nextInt(999999);
            }
        }

        return String.format("%06d", number);
    }

    /*
     * 验证成功后需要调用次函数删除验证码
     */
    public void deleteSMSCode(String smsCode) {
        Cache.getInstance().del(SMSCODE_TABLE, smsCode);
    }


    /**
     * 检测用户提供的验证码和手机号在预先定好的时间范围内: 10分钟
     */
    public Boolean checkPhoneNumberAndSMSCodeTimeValid(String phoneNumber, String smsCode) {
        SimpleDateFormat df = new SimpleDateFormat(DateTimeFormatStr);//设置日期格式
        try {
            String jsonStr = Cache.getInstance().get(SMSCODE_TABLE, smsCode);
            if (jsonStr == null || jsonStr.isEmpty()) {
                return false;
            }
            JsonParser parser = new JsonParser();
            JsonObject jObj = (JsonObject) parser.parse(jsonStr);
            String target_phoneNumber = jObj.get("phoneNumber").getAsString();
            if (!phoneNumber.equals(target_phoneNumber)) {
                return false;
            }
            String timStamp = jObj.get("timeStamp").getAsString();
            Date timeStamp = df.parse(timStamp);
            Date now = new Date();
            long diffInMillies = Math.abs(now.getTime() - timeStamp.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.SECONDS);
            if (diff >= 0 && diff <= ValidSMSCodeTimeIntervalSeconds) {
                // 这个函数作用是检查，并不做删除这种有副作用的操作
                // 调用这个函数后，请自行删除 smsCode !!! by daizhen
                //deleteSMSCode(smsCode);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 用户注册
     */
    public String register(User user) {
        //是否有邀请码,否则默认为游客身份
        String invitationToke = user.getInvitation_token();
        if (invitationToke != null && !invitationToke.equals("")) {
            // 非空的邀请码
        } else {
            // 邀请码为空的情况
            // 所有刚注册成功的用户具有游客角色和审核者角色
            String my_role_list[] = new String[] {Integer.toString(UserRoleEnum.GUEST.getType()),Integer.toString(UserRoleEnum.REVIEWER.getType())};
            user.setUser_role_list(Arrays.asList(my_role_list));
        }
        user.setUser_id(UUID.randomUUID().toString().replace("-", ""));
        userCollection.insertOne(user.toDocument());

        String token = null;
        //生成Token和Token信息并缓存在redis中
        token = AuthorizationOperation.tokenGenerateAndSave(user);
        return token;
    }

    /**
     * 用户信息
     */
    public User getUserByID(String id) {
        User userFound = null;
        Document match = new Document();
        match.put("id",id);

        MongoCursor<Document> cursor = userCollection.find(match).iterator();

        if (cursor.hasNext()) {
            Document one = cursor.next();
            userFound = User.fromDocument(one);
        }

        return userFound;
    }

    /**
     * 用户登录
     */
    public String login(String userMobile, String password) {
        User storedUser = getUserByPhoneNumber(userMobile);
        if (storedUser == null){
            return null;
        }else{
            String pwd = storedUser.getUser_password();
            if(pwd.equals(password)){
                //生成Token和Token信息并缓存在redis中
                return AuthorizationOperation.tokenGenerateAndSave(storedUser);
            }else{
                return null;
            }
        }
    }


    public String uploadPicture(String content) {
        try {
            //content = URLDecoder.decode(content, "UTF-8").trim();
            if (Base64.isBase64(content)) {
                byte[] b = decoder.decodeBuffer(content);
                for (int i = 0; i < b.length; ++i) {
                    if (b[i] < 0) {//调整异常数据
                        b[i] += 256;
                    }
                }
                // 获取文件后缀名
                Tika tika = getSingleTikaInstance();

                String mimeType = tika.detect(b);
                Map<String, String> mime2extMap = getMimeType2FileExtHashMap();
                if(null == mime2extMap.get(mimeType)){
                    LOGGER.error("unkown picture type error", mime2extMap.get(mimeType));
                    return null;
                }
                String filetype = mime2extMap.get(mimeType);
                String fileName = Uuid.getUuid() + "." + filetype;
                String uri = fds_prefix + "/" + fds_bucketName + "/" + fileName;

                //int upload_result = upload image to cloud file system.
                int upload_result = 0;
                if(upload_result != 0){
                    return  null;
                }
                return uri;
            }
        } catch (Throwable e) {
            LOGGER.error("save picture error", e);
            return null;
        }
        return null;
    }


    /**
     * 更新用户信息
     */
    public boolean update(User user) {
        try{
            String userID = String.valueOf(user.getUser_id());
            User oldUser = getUserByID(userID);
            oldUser.setUser_fullname(user.getUser_fullname());
            oldUser.setUser_email(user.getUser_email());
            oldUser.setUser_organization(user.getUser_organization());
            oldUser.setUser_favourite_list(user.getUser_favourite_list());
            oldUser.setUser_photo(user.getUser_photo());
            Bson filter = eq("id", userID);
            Document toUpdateDoc = oldUser.toDocument();
            UpdateResult result = userCollection.replaceOne(filter, toUpdateDoc);
            if(result.getMatchedCount() == 1){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            LOGGER.error("修改用户User Collection失败", e);
            return false;
        }
    }

    /**
     * 注销用户
     */
    public boolean logout(String token) {
        try {
            Cache.getInstance().del(TOKEN_TABLE, token);
            return true;
        } catch (Exception e) {
            LOGGER.error("注销用户Session失败", e);
            return false;
        }
    }

    /**
     * 获取题目
     * by 李娟
     */
    // TODO: 18-9-29
    public List<Map<String, String>> getQuestion() {
        return getReviewQuestionAnswer().getQuestionList();
    }

    /**
     * 根据标准答案对用户提交的答案进行判断，返回和标准答案一致的答案数目
     * by daizhen
     */
    public int howManyAnswerIsCorrect(List<Map> answer) {
        int trueNum = 0;
        String[] ansArr = new String[20];
        if (Cache.getInstance().get("question", "answer") == null) {
            // 将正确答案放入redis
            List<Map<String, String>> trueAnswer = getReviewQuestionAnswer().getAnswerList();
            for (int i = 0; i < trueAnswer.size(); i++) {
                ansArr[i] = trueAnswer.get(i).get("answer");
            }
            Cache.getInstance().set("question", "answer", org.apache.commons.lang3.StringUtils.join(ansArr, ","));

        } else {
            //直接从redis读取答案
            ansArr = org.apache.commons.lang3.StringUtils.split(Cache.getInstance().get("question", "answer"), ",");
        }
        for (int i = 0; i < answer.size(); i++) {
            if (answer.get(i).get("result").toString().equals(ansArr[i])) {
                trueNum++;
            }
        }

        return trueNum;
    }

    /**
     * 根据用户的答案准确率，决定是否给予审核者的权限，并生成新的token,旧token会同步删除
     * by daizhen
     */
    public String isCheckPassed(String user_id, String oldToken, float correctRatio) {
        if (correctRatio >= applyReviewerCorrectRationTheshold) {
            // 增加审核者权限
            User storedUser = getUserByID(user_id);
            List<String> user_roles  = storedUser.getUser_role_list();
            boolean already_has_role = false;
            for (String role : user_roles){
                /*if(Integer.toString(UserRoleEnum.REVIEWER.getType()).equals(role)){
                    already_has_role = true;
                }*/
                if(Integer.toString(UserRoleEnum.CHECKER.getType()).equals(role)){
                    already_has_role = true;
                }
            }
            if (!already_has_role){
                //user_roles.add(Integer.toString(UserRoleEnum.REVIEWER.getType()));
                user_roles.add(Integer.toString(UserRoleEnum.CHECKER.getType()));
                update(storedUser);
            }
            //----------------------------------------------------------------------------
            String newToken = AuthorizationOperation.tokenGenerateAndSave(storedUser);
            Cache.getInstance().del("usertoken", oldToken);
            Cache.getInstance().setValueTime("usertoken", newToken, Cache.TimeUnit.SECOND, 300);
            return newToken;
        }

        return null;
    }

    @Transactional
    public boolean updateRole(String id, List<String> user_roles, String old_token) {
        User database_user = getUserByID(id);
        List<String> database_user_roles = database_user.getUser_role_list();
        if (database_user_roles == null || database_user_roles.isEmpty()) {
            LOGGER.error("user not exists, user_id:{}", id);
            return false;
        }
        List<String> to_add_roles = new ArrayList<>();
        List<String> to_delete_roles = new ArrayList<>();
        for (String role_new :user_roles){
            boolean found = false;
            for(String role_old:database_user_roles){
                if(role_new == role_old){
                    found = true;
                }
            }
            if(!found){
                to_add_roles.add(role_new);
            }
        }
        for (String role_old :database_user_roles){
            boolean found = false;
            for(String role_new:user_roles){
                if(role_new == role_old){
                    found = true;
                }
            }
            if(!found){
                to_delete_roles.add(role_old);
            }
        }
        boolean actually_updated = false;
        if(!to_add_roles.isEmpty()){
            for(String role : to_add_roles){
                database_user_roles.add(role);
                actually_updated = true;
            }
        }
        if(!to_delete_roles.isEmpty()){
            for(String role : to_delete_roles){
                database_user_roles.remove(role);
                actually_updated = true;
            }
        }
        if (actually_updated){
            update(database_user);
            String newToken = AuthorizationOperation.tokenGenerateAndSave(database_user);
            Cache.getInstance().del("usertoken", old_token);
            Cache.getInstance().setValueTime("usertoken", newToken, Cache.TimeUnit.SECOND, 300);
            database_user.setToken(newToken);
        }

        return true;
    }

    public PaginationResult<UserResponse> getUserList(int pageNumber, int pageSize) {
        long count = userCollection.count();
        Page page = new Page(pageSize, pageNumber, count);
        MongoCursor<Document> cursor = userCollection.find().skip(pageSize*(pageNumber-1)).limit(pageSize).iterator();
        List<User> userlist = new ArrayList();
        while (cursor.hasNext()){
            userlist.add(User.fromDocument(cursor.next()));
        }

        List<UserResponse> retUserList = new ArrayList<UserResponse>();
        for (User user : userlist) {
            UserResponse retUser = new UserResponse(user);
            retUserList.add(retUser);
        }
        return new PaginationResult<UserResponse>(retUserList, page);
    }
}
