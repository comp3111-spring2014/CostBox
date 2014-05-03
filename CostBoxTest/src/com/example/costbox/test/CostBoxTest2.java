package com.example.costbox.test;

import com.example.costbox.CostBox;
import com.example.costbox.CostDetail;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

public class CostBoxTest2 extends ActivityInstrumentationTestCase2<CostBox>{

	private Solo solo;

	public CostBoxTest2() { 
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
	
	public void test() throws Exception{
		View view=solo.getView(com.example.costbox.R.id.myListView);
		int location[]=new int[2];
		view.getLocationOnScreen(location);
		solo.drag(location[0]+10,location[0]+10,location[1],location[1]+view.getHeight(),3);
		solo.clickOnScreen(80,233);
		solo.enterText(0, "9");
		solo.clickOnScreen(613,1180);
		
		// this part is for testing the search function.
		solo.clickOnView(solo.getView(com.example.costbox.R.id.menu_search));
		solo.enterText(0, "food");
		solo.sendKey(Solo.ENTER);
		solo.sleep(1000);
		solo.clickInList(0);
		if(solo.waitForActivity(CostDetail.class)){
			solo.assertCurrentActivity("Detail", CostDetail.class);
			solo.clickOnText("food");
			solo.enterText(0, "test");
			solo.clickOnButton("save");
			solo.clickOnView(solo.getView(com.example.costbox.R.id.returnbt));
		}
		solo.clickOnView(solo.getView(android.R.id.home));
		
		solo.clickOnView(solo.getView(com.example.costbox.R.id.file));
		solo.sleep(1000);
		solo.clickOnScreen(500, 300);
		solo.sleep(1000);
		solo.clickOnView(solo.getView(com.example.costbox.R.id.file));
		solo.sleep(1000);
		solo.clickOnScreen(500, 400);
		solo.clickOnButton("OK");
		
	}
	
	public void testRotate() throws Exception
	{
		solo.setActivityOrientation(0);
		solo.sleep(1000);
		solo.clickOnView(solo.getView(com.example.costbox.R.id.piechart));
		solo.clickOnView(solo.getView(com.example.costbox.R.id.doughnut));
		solo.clickOnView(solo.getView(com.example.costbox.R.id.linechart));
		solo.clickOnButton("Yes");
		
		solo.clickOnView(solo.getView(com.example.costbox.R.id.start));
		solo.clickOnButton("Cancel");

		solo.clickOnView(solo.getView(com.example.costbox.R.id.end));
		solo.clickOnButton("Cancel");

		solo.clickOnView(solo.getView(com.example.costbox.R.id.quickchoice));
		solo.clickOnButton("Yes");

		solo.setActivityOrientation(1);
		solo.sleep(1000);
	}
}
