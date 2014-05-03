package com.example.costbox.test;

import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import java.util.ArrayList;

import com.example.costbox.preference.CategoryEdit;
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

	public float location_cast(int x)
	{
		float result = (float)(x/1.5);
		return result;
	}

	public void test() throws Exception
	{

		View view=solo.getView(com.example.costbox.R.id.myListView);
		int location[]=new int[2];
		view.getLocationOnScreen(location);
		solo.drag(location[0]+10,location[0]+10,location[1],location[1]+view.getHeight(),3);
		// solo.enterText(1,"book");
		solo.clickOnScreen(87, 241);
		// solo.clickOnScreen(450, 1136);
		solo.enterText(0, "9");
		solo.clickOnScreen(584, 1138);

		// the following is for details, the number above has been changed!

		solo.clickInList(0);
		if(solo.waitForActivity(CostDetail.class)){
			solo.assertCurrentActivity("Detail", CostDetail.class);

			// For the picture
			solo.clickOnView(solo.getView(com.example.costbox.R.id.detailimage));
			solo.goBack();
			// For the category text
			solo.clickOnText("food");
			solo.enterText(0, "test");
			solo.clickOnButton("save");

			solo.clickOnText("9.0");
			solo.enterText(0, "100");
			solo.clickOnButton("save");// save

			solo.clickOnText("No comments...");
			solo.enterText(0, "test");
			solo.clickOnView(solo.getView(com.example.costbox.R.id.returnbt));
			
			// for the returning home button on the left above. 
			solo.clickInList(0);
			if(solo.waitForActivity(CostDetail.class)){
				solo.assertCurrentActivity("Detail", CostDetail.class);
				solo.clickOnView(solo.getView(android.R.id.home));
			}

			// delete item
			solo.clickInList(0);
			solo.clickOnView(solo.getView(com.example.costbox.R.id.trashbt));
			solo.clickOnButton("Yes");
		}

		//test_ScrollLayout
		solo.drag(location[0]+10,location[0]+10,location[1],location[1]+view.getHeight(),3);
		solo.drag(location[0]+300,location[0]+10,location[1]+10,location[1]+10,3);  // move to the right page
		solo.clickOnScreen(location_cast(131), location_cast(362)); // click on the category
		solo.clickOnScreen(location_cast(214), location_cast(1136)); // click the edittext to input some words for the coverage of keyboard
		// solo.clickOnScreen(location_cast(92), location_cast(1377)); // input a word for the category
		solo.enterText(1, "q");
		solo.clickOnScreen(location_cast(876), location_cast(914)); // switch to the digit input place
		// solo.clickOnScreen(location_cast(670), location_cast(1707)); // input some thing
		solo.enterText(0, "9");
		solo.clickOnView(solo.getView(com.example.costbox.R.id.add_comments));
		solo.enterText(0, "testing");
		solo.clickOnButton("save");
		solo.clickOnScreen(584, 1138); // click on OK for quit the current fragment.

		solo.clickInList(0);
		solo.goBack();

		solo.clickLongInList(0);
		solo.clickOnView(solo.getView(com.example.costbox.R.id.menu_delete));

		solo.drag(location[0]+10,location[0]+10,location[1],location[1]+view.getHeight(),3);
		solo.goBack();

	}

	public void testslide() throws Exception
	{
		View view=solo.getView(com.example.costbox.R.id.myListView);
		int location[]=new int[2];
		view.getLocationOnScreen(location);
		solo.drag(location[0]+300,location[0]+10,location[1]+200,location[1]+200,3);
		solo.sleep(1000);
		solo.drag(location[0]+10,location[0]+300,location[1]+200,location[1]+200,3);
		solo.sleep(1000);
		solo.drag(location[0]+10,location[0]+300,location[1]+200,location[1]+200,3);
		solo.sleep(1000);
		solo.drag(location[0]+300,location[0]+10,location[1]+200,location[1]+200,3);
	}

	public void testVPreference() throws Exception
	{
		// Preference
		solo.clickOnView(solo.getView(com.example.costbox.R.id.file));
		solo.sleep(1000);
		solo.clickOnScreen(location_cast(749), location_cast(308));
		solo.sleep(1000);
		// using coordinates.
		solo.clickOnScreen(location_cast(551), location_cast(418));
		if(solo.waitForActivity(CategoryEdit.class)) {

			 solo.assertCurrentActivity("Edit", CategoryEdit.class);
			 solo.clickOnView(solo.getView(com.example.costbox.R.id.category_adding));
			 solo.enterText(0, "test"); // create a new category for test
			 solo.clickOnButton("Yes");

			 // solo.clickOnScreen(location_cast(603), location_cast(1105)); // click on the checkbox of new-added cate
			 // solo.clickOnScreen(location_cast(603), location_cast(1105)); // bring it back to be visible.


			 solo.clickLongOnScreen(468,800); // long click on the new-added cate.
			 // ArrayList<View> gg = solo.getCurrentViews();
			 // solo.sleep(1000);

			 // View gview = gg.get(105);
			 // solo.clickLongOnView(gview);
			 View gview=solo.getView(com.example.costbox.R.id.scr);
			 // int glocation[]=new int[2];
			 // gview.getLocationOnScreen(glocation);
			 // solo.clickLongOnScreen((float)(gview.getWidth()/2+20),(float)(gview.getHeight()/2));
			 solo.clickLongOnScreen(450,816);
			 solo.clickOnView(solo.getView(com.example.costbox.R.id.category_delete));
			 solo.sleep(1000);

			 solo.clickOnView(solo.getView(android.R.id.home));

			 solo.clickOnScreen(location_cast(551), location_cast(418));
			 solo.goBack();

			 solo.clickOnView(solo.getView(android.R.id.home));
		}
	}

}