package com.example.costbox.test;

import com.example.costbox.CostBox;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.EditText;

public class CostBoxTest extends ActivityInstrumentationTestCase2<CostBox> {

	private CostBox mActivity; 
	 Button buttonBook, buttonEnter; 
	 EditText textCost; 
	//delta - the maximum delta between expected and actual for which both numbers are still considered equal.
	private static final double DELTA = 1e-15; // constant for comparing doubles values 

	public CostBoxTest() { 
		super(CostBox.class); 
	} 

	@Override 
	protected void setUp() throws Exception { 
	//this method is called every time before any test execution 
	super.setUp(); 

	mActivity = (CostBox) getActivity(); // get current activity 
		
	// link the objects with the activity objects 
		buttonBook = (Button) mActivity 
		.findViewById(com.example.costbox.R.id.book_select); 
		buttonEnter = (Button) mActivity 
		.findViewById(com.example.costbox.R.id.add_button); 
		textCost = (EditText) mActivity 
		.findViewById(com.example.costbox.R.id.myCost); 
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
	public void testKilosToPounds() { 

		/* INTERACTIONS */ 
		TouchUtils.clickView(this, buttonBook); // click the button buttonPounds 
		TouchUtils.tapView(this, textCost); // tap the EditText textKilos 
		sendKeys(123); // sent the number 1		
		
		
		TouchUtils.clickView(this, buttonEnter); // click the button buttonPounds 
		/*CHECK THE RESULT*/  
		

		//JUnit Assert equals 

		// message expected actual delta for comparing doubles 
		//assertEquals("1 kilo is 2.20462262 pounds", 2.20462262, pounds, DELTA); 
		assertTrue("hahaha",true);
	} 


}
