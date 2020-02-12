package com.openkg.openbase.model;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.openkg.openbase.common.AuthorizationOperation;
import com.openkg.openbase.common.Singleton;
import lombok.Data;
import lombok.Getter;
import org.bson.Document;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * user model
 * Created by nickyyyyy on 18-8-3
 */
@Data
public class User {

    private String invitation_token;
    private String token;
    private String user_id;  // 注册时必须有值
    private String user_fullname; // 注册时候必须有值
    private String user_email;
    private String user_mobile;  // 注册时候必须有值
    private String user_password; // 注册时候必须有值
    private String user_photo;
    private String user_organization;
    private List<String> user_favourite_list;
    private String user_smsverificationcode; // 注册时候必须有值
    @Getter(onMethod_ = { @JsonGetter("role")})
    private List<String> user_role_list; // 注册时候必须有值, 默认是游客: 4


    public void setUser_smsverificationcode(String smsverificationcode){this.user_smsverificationcode = smsverificationcode;}
    public String getUser_smsverificationcode(){return user_smsverificationcode;}


    public String getInvitation_token() {
		return invitation_token;
	}

	public void setInvitation_token(String invitation_token) {
		this.invitation_token = invitation_token;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getInvitationToken(){return invitation_token;}
    public void setInvitationToken(String invitation_token){this.invitation_token = invitation_token;}

    public String getToken(){return token;}

    public void setToken(String token){this.token = token;}

    public String getId() {
        return user_id;
    }

    public void setId(String id) {
        this.user_id = id;
    }


    public String getUser_fullname() {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname) {
        this.user_fullname = user_fullname;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public void setUser_organization(String user_organization) {
        this.user_organization = user_organization;
    }

    public String getUser_organization() {
        return user_organization;
    }

    public List<String> getUser_favourite() {
        return user_favourite_list;
    }

    public void setUser_favourite(List<String> user_favourites) {
        this.user_favourite_list = user_favourites;
    }


    public String toString() {
        return Singleton.GSON.toJson(this);
    }

    public static User fromJson(String jsonStr) {
        return Singleton.GSON.fromJson(jsonStr, User.class);
    }

    public static User fromDocument(Document oneDoc){
        User res_user = new User();
        //--------------------------------------------------------------------
        // required
        res_user.user_id = oneDoc.getString("id");
        res_user.user_fullname = oneDoc.getString("name");
        res_user.user_role_list = (List<String>)oneDoc.get("roleList");
        res_user.user_password = oneDoc.getString("password");
        res_user.user_mobile = oneDoc.getString("phoneNumber");
        //------------------------------------------------------------------------------
        // optional
        if(oneDoc.containsKey("email")){
            res_user.user_email = oneDoc.getString("email");
        }
        if(oneDoc.containsKey("favoriteList")){
            res_user.user_favourite_list = (List<String>)oneDoc.get("favoriteList");
        }
        if(oneDoc.containsKey("organization")){
            res_user.user_organization = oneDoc.getString("organization");
        }
        if(oneDoc.containsKey("photo")){
            res_user.user_photo = oneDoc.getString("photo");
        }

        return res_user;
    }

    public Document toDocument(){
        Document res_doc = new Document();
        //--------------------------------------------------------------
        // 这5个是必须有的
        res_doc.put("id", this.user_id);
        res_doc.put("name", this.user_fullname);
        res_doc.put("password", this.user_password);
        res_doc.put("phoneNumber", this.user_mobile);
        res_doc.put("roleList", this.user_role_list);
        //-------------------------------------------------------------
        // 后面这些是可选的
        if(this.user_organization != null && !this.user_organization.isEmpty()){
            res_doc.put("organization", this.user_organization);
        }
        if(this.user_photo != null && !this.user_photo.isEmpty()){
            res_doc.put("photo", this.user_photo);
        }
        if(this.user_favourite_list != null && !CollectionUtils.isEmpty(this.user_favourite_list)){
            res_doc.put("favoriteList", this.user_favourite_list);
        }
        if(this.user_email != null && !this.user_email.isEmpty()){
            res_doc.put("email", this.user_email);
        }

        return res_doc;
    }

	public JsonObject toJsonObject(){
        String jsonStr = this.toString();
        return Singleton.GSON.fromJson(jsonStr, JsonObject.class);
    }

}
