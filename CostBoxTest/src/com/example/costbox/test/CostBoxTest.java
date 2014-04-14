package com.example.costbox.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.costbox.CostBox;
import com.example.costbox.CostDetail;
import com.example.costbox.chartview.*;
import com.robotium.solo.Solo;

public class CostBoxTest extends ActivityInstrumentationTestCase2<CostBox> {


	private Solo solo;

	public CostBoxTest() { 
		super(CostBox.class); 
	} 

	@Override 
	protected void setUp() throws Exception { 
	//this method is called every time before any test execution 
		solo = new Solo(getInstrumentation(), getActivity());
	} 


	@Override 
	protected void tearDown() throws Exception { 
	//this method is called every time after any test execution 
	// we want to clean the texts 
	solo.finishOpenedActivities();
	} 
	
	public void testView(){
		assertNotNull(getActivity());
	}
	
	public void testBook()
	{
		solo.clickOnView(solo.getView(com.example.costbox.R.id.book_select));
		solo.enterText(1, "100");
		solo.clickOnView(solo.getView(com.example.costbox.R.id.add_button));
	}
	public void testFood()
	{
		solo.clickOnView(solo.getView(com.example.costbox.R.id.food_select));
		solo.enterText(1, "100");
		solo.clickOnView(solo.getView(com.example.costbox.R.id.add_button));
	}
	public void testEnter()
	{
		solo.clickOnView(solo.getView(com.example.costbox.R.id.enter_select));
		solo.enterText(1, "100");
		solo.clickOnView(solo.getView(com.example.costbox.R.id.add_button));
	}
	public void testTravel()
	{
		solo.clickOnView(solo.getView(com.example.costbox.R.id.travel_select));
		solo.enterText(1, "100");
		solo.clickOnView(solo.getView(com.example.costbox.R.id.add_button));
	}
	public void testStudy()
	{
		solo.clickOnView(solo.getView(com.example.costbox.R.id.study_select));
		solo.enterText(1, "100");
		solo.clickOnView(solo.getView(com.example.costbox.R.id.add_button));
	}
	
	
	public void testZSummary(){
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.clickOnView(solo.getView(com.example.costbox.R.id.piechart));
		solo.clickOnView(solo.getView(com.example.costbox.R.id.doughnut));
		solo.clickOnView(solo.getView(com.example.costbox.R.id.barchart));
		solo.clickOnView(solo.getView(com.example.costbox.R.id.linechart));
	}
	
	
	public void testYDetail(){
		solo.assertCurrentActivity("Wrong activity", CostBox.class);
		solo.clickInList(0);     // select on the first item of the list view 
		if(solo.waitForActivity(CostDetail.class)){
			solo.assertCurrentActivity("Detail", CostDetail.class);
			
			TextView tmp = (TextView) solo.getView(com.example.costbox.R.id.comments);
			solo.clickOnText("No comments...");
			solo.enterText(0, "testing");
			solo.clickOnButton("save");

//			solo.typeText(com.example.costbox.R.id.comments, "test");

			solo.clickLongOnView(solo.getView(com.example.costbox.R.id.detailimage));
			solo.goBack();
			
			solo.clickOnText("Book");
			solo.clickOnButton("save");
			
			solo.clickOnText("0");
			solo.clickOnButton("save");
			
			solo.clickOnView(solo.getView(com.example.costbox.R.id.returnbt));
			
//			 solo.goBack(); //  Simulates pressing the hardware back key. Do nothing, finally just return;
		}
		
			solo.clickInList(0);
			if(solo.waitForActivity(CostDetail.class)){
				solo.clickOnView(solo.getView(com.example.costbox.R.id.trashbt));
				solo.clickOnButton("Yes");
			}
		
		solo.clickLongInList(0);
		
		
	}
	
	
	
}
