package com.openkg.openbase.model;


import com.google.gson.JsonObject;
import com.openkg.openbase.common.Singleton;
import lombok.Data;

import java.util.List;

/**
 * user model
 * Created by nickyyyyy on 18-8-3
 */
@Data
public class UserRequest {


    private String user_id;
    private String user_fullname;
    private String user_email;
    private String user_mobile;
    private String user_photo;
    private String user_organization;
    private List<String> user_favourite;
    private Integer user_role;

    public UserRequest() {
	}

	public UserRequest(User user) {
		user_id = user.getUser_id();
		user_fullname = user.getUser_fullname();
		user_email = user.getUser_email();
		user_mobile = user.getUser_mobile();
		user_photo = user.getUser_photo();
		user_organization = user.getUser_organization();
		user_favourite = user.getUser_favourite_list();
	}

}
