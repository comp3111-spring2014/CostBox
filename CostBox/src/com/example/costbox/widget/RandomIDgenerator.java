package com.example.costbox.widget;

import java.util.Random;
/*
 * This class is used to generate an ID which is identical 
 */
public class RandomIDgenerator {

	private String s = ""; 
	public String getRandomID()
	{
		
	     Random ran =new Random(System.currentTimeMillis()); 
	for (int i = 0; i < 4; i++) { 
	       s =s + ran.nextInt(100); 
	} 
	return s;
	}
}
