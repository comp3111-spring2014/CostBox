package com.example.costbox;

import android.graphics.drawable.Drawable;

public class CategoryGridInfo {

	//private String category_icon;
	private Drawable category_image;
	private String category_name;
	
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
