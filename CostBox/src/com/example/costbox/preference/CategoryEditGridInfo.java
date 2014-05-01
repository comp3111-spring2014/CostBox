package com.example.costbox.preference;

import android.graphics.drawable.Drawable;

public class CategoryEditGridInfo {

	//private String category_icon;
	private Drawable category_image;
	private String category_name;
	private Boolean check;
	
	public Boolean getCheck() {
		return check;
	}


	public void setCheck(Boolean check) {
		this.check = check;
	}


	public void setCategoryIcon(Drawable arg) {
		this.category_image = arg;
	}
	

	public void setCategoryName(String arg) {
		this.category_name = arg;
	}

	public Drawable getCategoryIcon() {
		return category_image;
	}

	public String getCategoryName() {
		return category_name;
	}




}
