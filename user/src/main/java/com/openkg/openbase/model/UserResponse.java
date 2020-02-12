package com.openkg.openbase.model;

import lombok.Data;


import java.util.List;

/**
 * user model
 * Created by nickyyyyy on 18-8-3
 */
@Data
public class UserResponse {


    private String user_id;
    private String user_fullname;
    private String user_email;
    private String user_mobile;
    private String user_photo;
    private String user_organization;
    private List<String> user_favourite;
    private List<String> user_roles;    // 这个是从user_role的表里面取出的
    
    public UserResponse() {
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
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

	public String getUser_photo() {
		return user_photo;
	}

	public void setUser_photo(String user_photo) {
		this.user_photo = user_photo;
	}

	public String getUser_organization() {
		return user_organization;
	}

	public void setUser_organization(String user_organization) {
		this.user_organization = user_organization;
	}

	public List<String> getUser_favourite() {
		return user_favourite;
	}

	public void setUser_favourite(List<String> user_favourite) {
		this.user_favourite = user_favourite;
	}

	public List<String> getUser_roles() {
		return user_roles;
	}

	public void setUser_roles(List<String> user_roles) {
		this.user_roles = user_roles;
	}

	public UserResponse(User user) {
		user_id = user.getUser_id();
		user_fullname = user.getUser_fullname();
		user_email = user.getUser_email();
		user_mobile = user.getUser_mobile();
		user_photo = user.getUser_photo();
		user_organization = user.getUser_organization();
		user_favourite = user.getUser_favourite();
		user_roles = user.getUser_role_list();
	}

}
