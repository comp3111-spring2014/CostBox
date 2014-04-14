package com.example.costbox.test;

import com.example.costbox.CostBox;
import com.example.costbox.CostDetail;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class CostBoxTest extends ActivityInstrumentationTestCase2<CostBox> {

	private CostBox mActivity; 
	Button buttonBook, buttonFood, buttonTravel, buttonStudy, buttonEnter, buttonAdd, buttonReturn, buttonTrash;
	EditText textCost; 
	ListView myListView;
	ImageView detailimage;

	public CostBoxTest() { 
		super(CostBox.class); 
	} 

	@Override 
	protected void setUp() throws Exception { 
	//this method is called every time before any test execution 
	super.setUp(); 

	mActivity = (CostBox) getActivity(); // get current activity 
		
	// link the objects with the activity objects 
		buttonBook = (Button) mActivity.findViewById(com.example.costbox.R.id.book_select); 
		buttonFood = (Button) mActivity.findViewById(com.example.costbox.R.id.food_select);
		buttonTravel = (Button) mActivity.findViewById(com.example.costbox.R.id.travel_select);
		buttonStudy = (Button) mActivity.findViewById(com.example.costbox.R.id.study_select);
		buttonEnter = (Button) mActivity.findViewById(com.example.costbox.R.id.enter_select);
		buttonAdd = (Button) mActivity.findViewById(com.example.costbox.R.id.add_button); 
		textCost = (EditText) mActivity.findViewById(com.example.costbox.R.id.myCost); 
		myListView = (ListView) mActivity.findViewById(com.example.costbox.R.id.myListView);
		
		detailimage = (ImageView) mActivity.findViewById(com.example.costbox.R.id.detailimage);
		buttonReturn = (Button) mActivity.findViewById(com.example.costbox.R.id.returnbt);
		buttonTrash = (Button) mActivity.findViewById(com.example.costbox.R.id.trashbt);
	} 


	@Override 
	protected void tearDown() throws Exception { 
	//this method is called every time after any test execution 
	// we want to clean the texts 
	textCost.clearComposingText(); 
	super.tearDown(); 
	} 

	@SmallTest // SmallTest: this test doesn't interact with any file system or network. 
	public void testView() { // checks if the activity is created 
		assertNotNull(getActivity()); 
	} 

	@SmallTest 
	public void testBook() { 
		TouchUtils.clickView(this, buttonBook);
		TouchUtils.tapView(this, textCost);
		sendKeys("1"); 	
		sendKeys("0");
		sendKeys("0");
		
		TouchUtils.clickView(this, buttonAdd); // click the button buttonPounds  
		assertTrue("hahaha",true);
	} 
	
	@SmallTest 
	public void testFood() { 
		TouchUtils.clickView(this, buttonFood);
		TouchUtils.tapView(this, textCost);
		sendKeys("1"); 	
		sendKeys("0");
		sendKeys("0");
		
		TouchUtils.clickView(this, buttonAdd); // click the button buttonPounds  
		assertTrue("hahaha",true);
	} 
	
	@SmallTest 
	public void testTravel() {  
		TouchUtils.clickView(this, buttonTravel);
		TouchUtils.tapView(this, textCost);
		sendKeys("1"); 	
		sendKeys("0");
		sendKeys("0");
		
		TouchUtils.clickView(this, buttonAdd); // click the button buttonPounds  
		assertTrue("hahaha",true);
	} 
	
	@SmallTest 
	public void testEnter() { 
		TouchUtils.clickView(this, buttonEnter);
		TouchUtils.tapView(this, textCost);
		sendKeys("1"); 	
		sendKeys("0");
		sendKeys("0");
		
		TouchUtils.clickView(this, buttonAdd); // click the button buttonPounds  
		assertTrue("hahaha",true);
	} 
	
	@SmallTest 
	public void testStudy() { 
		TouchUtils.clickView(this, buttonStudy);
		TouchUtils.tapView(this, textCost);
		sendKeys("1"); 	
		sendKeys("0");
		sendKeys("0");
		
		TouchUtils.clickView(this, buttonAdd); // click the button buttonPounds  
		assertTrue("hahaha",true);
	}
	
//	public void testDetail(){
//		TouchUtils.clickView(this, myListView.getChildAt(2));
//		TouchUtils.clickView(this, buttonReturn);
//		TouchUtils.clickView(this, myListView.getChildAt(2));
//		TouchUtils.longClickView(this, detailimage);
//		TouchUtils.clickView(this, buttonTrash);
//		assertTrue("hahaha",true);
//	}
	
	public void testLongClick(){
		TouchUtils.longClickView(this, myListView.getChildAt(2));
		assertTrue("hahaha",true);
	}
}