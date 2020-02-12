package com.openkg.openbase.model;

public enum UserRoleEnum {
	SUPERUSER(1, "superuser"), REVIEWER(2, "reviewer"), CHECKER(3, "checker"), GUEST(4, "guest");
	
	private int type;
	private String name;
	
	private UserRoleEnum(int type, String name) {
		this.type = type;
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public static UserRoleEnum getByType(int type) {
		for (UserRoleEnum val: values()) {
			if (val.getType() == type) {
				return val;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
