package com.example.costbox; 

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.costbox.facebook.MainFragment;
import com.facebook.Session;

@SuppressLint("NewApi")
public class CostBox extends FragmentActivity 
{ 
  public static CostDB myCostDB;
  private static Cursor myCursor;
  private static ListView myListView; 
  private static EditText myEditText; 
  private static EditText myCost;
  private static int _id;
  private static View _currentItemView;
  private Button add_b;
  private Button revise_b;
  private Button delete_b;
  //button for the data manipulation
  private Button sum_b;
  //button chosen
  private Button book_select;
  private Button travel_select;
  private Button food_select;
  private Button study_select;
  private Button enter_select;
  
  private Button testcate;//....................
  
  //contextual action mode 
  private ActionMode mActionMode;  
  //item 
  private int item_class;
  private float oldTouchValue;//text the position of finger
  public MainFragment mainFragment;
  /** Called when the activity is first created. */ 
  @Override 
  public void onCreate(Bundle savedInstanceState) 
  { 
    super.onCreate(savedInstanceState); 
    setContentView(R.layout.main);
    //get the fragement of FB share
    if (savedInstanceState == null) {
        // Add the fragment on initial activity setup
        mainFragment = new MainFragment();
        getSupportFragmentManager()
        .beginTransaction()
        .add(mainFragment, "FBS")
        .commit();
    } else {
        // Or set the fragment from restored state info
        mainFragment = (MainFragment) getSupportFragmentManager()
        .findFragmentByTag("FBS");
    }
    
    //end of share initialization
    myListView = (ListView) this.findViewById(R.id.myListView); 
    myEditText = (EditText) this.findViewById(R.id.myEditText); 
    myCost = (EditText) this.findViewById(R.id.myCost); 
    
    add_b = (Button) findViewById(R.id.add_button);
    
   
    book_select = (Button) findViewById(R.id.book_select); 
    travel_select = (Button) findViewById(R.id.travel_select); 
    study_select = (Button) findViewById(R.id.study_select); 
    food_select = (Button) findViewById(R.id.food_select); 
    enter_select = (Button) findViewById(R.id.enter_select); 
    
    //testcate = (Button) findViewById(R.id.testcate);
    /*
    testcate.setOnClickListener(new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//test_cate();
			SumOfCategory();
		}
	});
    */
    //avoid the soft keyboard
    getWindow().setSoftInputMode(
    		WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    


    myCostDB = new CostDB(this); 
    myCursor = myCostDB.select();
   
   
    SimpleCursorAdapter adapter = 
      new SimpleCursorAdapter(this, R.layout.list, myCursor, new String[] { CostDB.FIELD_TEXT,CostDB.COMM, CostDB.COST_MONEY}, new int[] { R.id.listTextView1,R.id.commentext,R.id.listCostView});
    myListView.setAdapter(adapter); 
   // go to the detail interface
    myListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    { 
      @Override
      public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) 
      { 
        myCursor.moveToPosition(arg2);
        _id = myCursor.getInt(0);  
        //give the control right to CostDetail
        Intent intent=new Intent();
        intent.setClass(CostBox.this, CostDetail.class);
        // need modification
        Bundle bundle = new Bundle();
        bundle.putString("category",myCursor.getString(3));
        bundle.putString("comments",myCursor.getString(4));
        bundle.putInt("cost",myCursor.getInt(6));
        bundle.putString("picture", myCursor.getString(5));
        bundle.putInt("date",myCursor.getInt(1));
        bundle.putString("back_Identifier","false" ); // used to identify between back key and delete button 's result_value
        
        intent.putExtras(bundle);
        startActivityForResult(intent,0);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);				
		
 
        }
      }
    ); 
    // go to the contextual mode
    //preserved for multiple choice mode
    //myListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL); //set multiple choice mode
    // when long clicked-->change to the multiple choice mode
/*
 *  The following method is used to set up the contextual mode (action mode)
 *  when the user LongClick the item, the action mode will be active.
 *  Users can delete, edit or share the information
 *  problem solved:mode call back
 *  problems not solved: the color and multiple choice (need another checklist to save the selected items)
 *                       choose inside the mode (now it is necessary to long click again which is not user-friendly
 *  log:2014-3-10 
*/
    myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
 		{
 			@Override
 			public boolean onItemLongClick(AdapterView arg0, View arg1, int arg2, long arg3) {
 				// TODO Auto-generated method stub
 				myCursor.moveToPosition(arg2);
 		        _id = myCursor.getInt(0);  
 		      //return to the original color
 		        if(_currentItemView!=null) 
 		       { _currentItemView.setBackgroundResource(R.drawable.greybg);}
 		        _currentItemView=arg1;
                //set new color
 		      _currentItemView.setBackgroundResource(R.drawable.rouhe1);

				mActionMode = CostBox.this
						.startActionMode(mActionModeCallback);
				//arg1.setSelected(true);
 		        return true;  
 			}
 			
 			
 			//contextual mode but not work utill now
 		
    });
    
    
 /*		
 	sum_b.setOnClickListener(new Button.OnClickListener() 
 	{
 		public void onClick(View v)
 		{
 			sumup();
 			//Toast.makeText(, 1, Toast.LENGTH_SHORT).show();
 		}
 	});
*/
    add_b.setOnClickListener(new Button.OnClickListener()
       {
         public void onClick(View v)
         {
           CostBox.addTodo();
           Toast.makeText( CostBox.this, "Added Successful", Toast.LENGTH_LONG).show(); 
           InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)); im.hideSoftInputFromWindow(myCost.getWindowToken(), 0);
           
         }
       });

    book_select.setOnClickListener(new Button.OnClickListener()
    {
      public void onClick(View v)
      {
    	  myEditText.setText("Book");
    	  myCost.setText("");
    	  
    	  getBook(book_select);
    	  backFood(food_select);
    	  backTravel(travel_select);
    	  backStudy(study_select);
    	  backEnter(enter_select);
    	 item_class=1;
    	  myCost.setFocusableInTouchMode(true);
    	  myCost.setFocusable(true);
    	  myCost.requestFocus(); 
    	  InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)); im.showSoftInput(myCost, 0);
    	  
      }
    });

    travel_select.setOnClickListener(new Button.OnClickListener()
    {
      public void onClick(View v)
      {
    	  myEditText.setText("Travelling");
    	  myCost.setText("");
    	  
    	  backBook(book_select);
    	  backFood(food_select);
    	  getTravel(travel_select);
    	  backStudy(study_select);
    	  backEnter(enter_select);
    	  item_class=2;
    	  myCost.setFocusableInTouchMode(true);
    	  myCost.setFocusable(true);
    	  myCost.requestFocus(); 
    	  InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)); im.showSoftInput(myCost, 0);
    	  
      }
    });
    
    study_select.setOnClickListener(new Button.OnClickListener()
    {
      public void onClick(View v)
      {
    	  myEditText.setText("Study tools");
    	  myCost.setText("");
    	  
    	  backBook(book_select);
    	  backFood(food_select);
    	  backTravel(travel_select);
    	  getStudy(study_select);
    	  backEnter(enter_select);
    	  item_class=3;
    	  myCost.setFocusableInTouchMode(true);
    	  myCost.setFocusable(true);
    	  myCost.requestFocus(); 
    	  InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)); im.showSoftInput(myCost, 0);
    	  
      }
    });
    
    food_select.setOnClickListener(new Button.OnClickListener()
    {
      public void onClick(View v)
      {
    	  myEditText.setText("Food");
    	  myCost.setText("");
    	  
    	  backBook(book_select);
    	  getFood(food_select);
    	  backTravel(travel_select);
    	  backStudy(study_select);
    	  backEnter(enter_select);
    	  item_class=4;
    	  
    	  myCost.setFocusableInTouchMode(true);
    	  myCost.setFocusable(true);
    	  myCost.requestFocus(); 
    	  InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)); im.showSoftInput(myCost, 0);
    	  
      }
    });
    
    enter_select.setOnClickListener(new Button.OnClickListener()
    {
      public void onClick(View v)
      {
    	  myEditText.setText("Entertainment");
    	  myCost.setText("");
    	  
    	  backBook(book_select);
    	  backFood(food_select);
    	  backTravel(travel_select);
    	  backStudy(study_select);
    	  getEnter(enter_select);
    	  item_class=5;
    	  
    	  myCost.setFocusableInTouchMode(true);
    	  myCost.setFocusable(true);
    	  myCost.requestFocus(); 
    	  InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)); im.showSoftInput(myCost, 0);
    	  
      }
    });
    
  }
  // The following method is used to set up the action mode callback
  private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// Respond to clicks on the actions in the CAB
			switch (item.getItemId()) {
			case R.id.menu_delete:
				CostBox.deleteTodo();
		        Toast.makeText( CostBox.this, "deleted Successful", Toast.LENGTH_LONG).show(); 		
				mode.finish(); 
				return true;

			case R.id.menu_edit:

				Intent intent=new Intent();
		        intent.setClass(CostBox.this, CostDetail.class);
		        
		        Bundle bundle = new Bundle();
		        bundle.putString("category",myCursor.getString(3));
		        bundle.putString("comments",myCursor.getString(4));
		        bundle.putInt("cost",myCursor.getInt(6));
		        bundle.putString("picture", myCursor.getString(5));
		        bundle.putInt("date",myCursor.getInt(1));
		        bundle.putString("back_Identifier","false" ); // used to identify between back key and delete button 's result_value
		        
		        intent.putExtras(bundle);
		        startActivityForResult(intent,0);
		        
				mode.finish();
				return true;
			case R.id.menu_share:
				 //share the dialog
                
                Bundle postParams=new Bundle();
                //dialog params which is not used now
				 /*
                 postParams.putString("name", myCursor.getString(2));
			     postParams.putString("caption",myCursor.getString(3));
			     postParams.putString("description", myCursor.getString(5));
			     postParams.putString("link", "http://weibo.com/jkbemr?topnav=1&wvr=5&topsug=1");
			     postParams.putString("picture","http://t1.baidu.com/it/u=4065605405,1069141967&fm=21&gp=0.jpg");
                 */
                //get the picture path and decode it to byte stream
                String picpath=myCursor.getString(5);
                byte[] data;
                if(picpath!="")
                { Bitmap image = BitmapFactory.decodeFile(picpath);
                  ByteArrayOutputStream bos = new ByteArrayOutputStream();  
                  image.compress(Bitmap.CompressFormat.JPEG, 100, bos);  
                  data = bos.toByteArray();
                }
                else 
                {  
                	Resources res=getResources(); 
                	Bitmap image = BitmapFactory.decodeResource(res, R.drawable.beauty);
                	ByteArrayOutputStream bos = new ByteArrayOutputStream();  
                    image.compress(Bitmap.CompressFormat.JPEG, 100, bos);  
                    data = bos.toByteArray();
                }
                
                //get the comment from the previous one
                 String comments=myCursor.getString(4);
                 if(comments.equals("No comments..."))
                 {
                	 comments="Let's use CostBox! So easy!";
                 }
			     //get the title
			     String title=myCursor.getString(3);
			     //get the cost
			     int get_cost=myCursor.getInt(6);
			    
			     String message_upload="I'm god damn rich!! I spent "+get_cost+" on "+title+" ! "+comments;
			     //data transfer
			     postParams.putByteArray("picture",data);
			     postParams.putString("message",message_upload);
			     Session session = mainFragment.getActiveS();
			     mainFragment.postParams=postParams;
			     //if logged in
                if(session.isOpened())
                {
			          mainFragment.publishStory();
                }
                //if not login
                else 
                {
               	  Toast.makeText(CostBox.this, "Please Login and Try Again",Toast.LENGTH_SHORT).show();
               	  mainFragment.onClickLogin();
                }
                mode.finish();
                
				return true;
			default:
				return false;
			
			}
  		
		}
  	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate the menu for the CAB
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu_list, menu);
		//to avoid the bug: color changing (solved after multi choise) still problem
			// comment it out
		/*
			myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
	 		{
	 			@Override
	 			public boolean onItemLongClick(AdapterView arg0, View arg1, int arg2, long arg3) {
	 				// TODO Auto-generated method stub
	 				myCursor.moveToPosition(arg2);
	 		        _id = myCursor.getInt(0);  
	 		      //return to the original color
	 		        if(_currentItemView!=null) 
	 		       { _currentItemView.setBackgroundResource(R.drawable.huopo1);}
	 		        _currentItemView=arg1;
	                //set new color
	 		      _currentItemView.setBackgroundResource(R.drawable.huopo3);
	 		        return true;  
	 			}
	 		//end of bug fixing	
	 			
	 			//contextual mode but not work utill now
	 		
	    });*/ 
			_currentItemView.setBackgroundResource(R.drawable.rouhe1);
			return true;
		}
  	    public void onDestroyActionMode(ActionMode mode) {
  		  _currentItemView.setBackgroundResource(R.drawable.greybg);  //return the color back
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		};  
  
  // used for action bar implementation 
@Override
  public boolean onCreateOptionsMenu(Menu menu) {
	 super.onCreateOptionsMenu(menu); 
	 getMenuInflater().inflate(R.menu.main, menu);
	 SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();  
     return true; 
  }
  @Override
public boolean onOptionsItemSelected(MenuItem item) {
	switch(item.getItemId())
	{
	case R.id.menu_about:
		AlertDialog.Builder dg = new Builder(this);
		dg.setTitle("About");
		dg.setMessage("This is CostBox version 1.0");
		dg.setPositiveButton("OK",null);
		dg.show();
		break;
	case R.id.menu_setting:
		//Log.d("setting", "enter in switch!");
		Intent mIntent = new Intent();
		mIntent.setClass(CostBox.this, MyPreference.class);
		startActivity(mIntent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		break;
	
	 default:break;
	
	}
	return super.onOptionsItemSelected(item);
}

  private void test_cate()
  {
	  String temps = myCostDB.date_display(20130201,20140530)+"";  // the date_int required other data. Like 2014-April-1st, it becomes 20140301
	  											// You can use Date_int_tranfer function to achive it. 
	  Toast.makeText(this, temps, Toast.LENGTH_SHORT).show();
  }
  
  
  private void SumUp()
  {
	 int result = myCostDB.sum_up();
	 String temps = result +"." ; 
	 Toast.makeText(this, temps, Toast.LENGTH_SHORT).show();
	  
  }
 
  //The following part is to test the category sum function. 
  
// for traversing the map, the reference : http://ludaojuan21.iteye.com/blog/243475  
// and 									   http://blog.csdn.net/wzb56_earl/article/details/7864911 
// but why there's yellow underline. 
private void SumOfCategory()
  {
	/*
	  Map<String,Integer> result = new HashMap<String,Integer>(200);
	  
	  
	  Integer t1,t2; 
	  t1 = Integer.valueOf(1);
	  t2 = Integer.valueOf(2);
	  result.put("first", t1);
	  result.put("second",t2);
	  
	  */
	  Map<String,Integer> result = new HashMap<String,Integer>(200);
	  result = myCostDB.sum_of_catogory();
	  
	  Iterator iter = result.entrySet().iterator();
	  String temps ="";
	  while(iter.hasNext())
	  {
		  Map.Entry entry =(Map.Entry)iter.next();
		  temps = temps + "key: "+ entry.getKey() +"\n";
		  temps = temps + "value:" + entry.getValue() + "\n" ; 
	  }
	  
	  Toast.makeText(this, temps, Toast.LENGTH_LONG).show();
  }


  private static void addTodo() 
  {
    if (myEditText.getText().toString().equals("")) return;

    String cost_string_temp = myCost.getText().toString(); 
    int cost_temp=0;
    try{
    cost_temp = Integer.parseInt(cost_string_temp);
    }
    catch(NumberFormatException nfe){
    	
    }
    // strech the cost number which is a int type data. 
    
    String temp = "";
	final Calendar c = Calendar.getInstance();
	int date=(c.get(Calendar.YEAR)) * 10000; 
	  
	if (c.get(Calendar.MONTH) < 10 ) temp = temp + "0"+c.get(Calendar.MONTH) ;
	else temp = temp +c.get(Calendar.MONTH); 
	if (c.get(Calendar.DATE) < 10 ) temp = temp + "0"+c.get(Calendar.DATE) ;
	else temp = temp +c.get(Calendar.DATE);
	date += Integer.parseInt(temp);
	
	myCostDB.insert(myEditText.getText().toString(),cost_temp,"No comments...","None",date);
    myCursor.requery(); 
    myListView.invalidateViews();
    myEditText.setText("");
    myCost.setText("");
    _id = 0; 
    } 
  private static void editTodo()
  { 

    if(_currentItemView!=null) 
     { _currentItemView.setBackgroundResource(R.drawable.warm);}
    _id = 0; 
    } 
  private static void deleteTodo()
  {
    if(_id == 0)
    return;
    myCostDB.delete(_id); 
    myCursor.requery();
    myListView.invalidateViews(); 
    myEditText.setText("");
    myCost.setText("");
  
    _currentItemView.setBackgroundResource(R.drawable.greybg);
    _id = 0; 
  }

  public void getBook(Button button)
  {
	  Resources resources = getBaseContext().getResources(); 
	  Drawable btnDrawable = resources.getDrawable(R.drawable.book2);
	  button.setBackgroundDrawable(btnDrawable);
  }
  public void getTravel(Button button)
  {
	  Resources resources = getBaseContext().getResources(); 
	  Drawable btnDrawable = resources.getDrawable(R.drawable.travel2);
	  button.setBackgroundDrawable(btnDrawable);
  }
  public void getFood(Button button)
  {
	  Resources resources = getBaseContext().getResources(); 
	  Drawable btnDrawable = resources.getDrawable(R.drawable.food2);
	  button.setBackgroundDrawable(btnDrawable);
  }
  public void getStudy(Button button)
  {
	  Resources resources = getBaseContext().getResources(); 
	  Drawable btnDrawable = resources.getDrawable(R.drawable.study2);
	  button.setBackgroundDrawable(btnDrawable);
  }
  public void getEnter(Button button)
  {
	  Resources resources = getBaseContext().getResources(); 
	  Drawable btnDrawable = resources.getDrawable(R.drawable.enter2);
	  button.setBackgroundDrawable(btnDrawable);
  }

  public void backBook(Button button)
  {
	  Resources resources = getBaseContext().getResources(); 
	  Drawable btnDrawable = resources.getDrawable(R.drawable.book);
	  button.setBackgroundDrawable(btnDrawable);
  }
  public void backTravel(Button button)
  {
	  Resources resources = getBaseContext().getResources(); 
	  Drawable btnDrawable = resources.getDrawable(R.drawable.travel1);
	  button.setBackgroundDrawable(btnDrawable);
  }
  public void backFood(Button button)
  {
	  Resources resources = getBaseContext().getResources(); 
	  Drawable btnDrawable = resources.getDrawable(R.drawable.food1);
	  button.setBackgroundDrawable(btnDrawable);
  }
  public void backStudy(Button button)
  {
	  Resources resources = getBaseContext().getResources(); 
	  Drawable btnDrawable = resources.getDrawable(R.drawable.study1);
	  button.setBackgroundDrawable(btnDrawable);
  }
  public void backEnter(Button button)
  {
	  Resources resources = getBaseContext().getResources(); 
	  Drawable btnDrawable = resources.getDrawable(R.drawable.enter1);
	  button.setBackgroundDrawable(btnDrawable);
  }
  
  

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {

    switch (event.getAction())
    {
      case MotionEvent.ACTION_DOWN:
        oldTouchValue = event.getX();
        break;
      case MotionEvent.ACTION_UP:
        float currentX = event.getX();
        if (oldTouchValue < currentX)  
        {
        	/*
        	String currentStr="1";
        	myCostDB = new CostDB(this,currentStr); 
            myCursor = myCostDB.select();
            */
  
        }
        if (oldTouchValue > currentX) 
        {
          
        }
        break;
      case MotionEvent.ACTION_MOVE:
        // TODO: Some code to make the ViewFlipper
        // act like the home screen.
        break;
      
    }
    return super.onTouchEvent(event);
  }
  
  //orientation change function
  @Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		if(this.getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_LANDSCAPE){
			//setContentView(R.layout.summary);
			//setContentView(R.layout.transist);
			Intent intent = new Intent();
			intent.setClass(CostBox.this,com.example.costbox.chartview.Summary.class);
			
			
			
			
			
			startActivityForResult(intent,1);
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			//this.finish();
			
		} else if(this.getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_PORTRAIT){
			//Toast.makeText(getApplicationContext(), "PORTRAIT", Toast.LENGTH_SHORT).show();
		}
		
	}
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) 
{
	// TODO Auto-generated method stub
	Bundle bunde = data.getExtras();
	switch(resultCode)
	{
	case RESULT_OK:
		if(requestCode==0)
		{//Bundle bunde=data.getExtras();
		String category= bunde.getString("category");
		String comments= bunde.getString("comments");
		int Cost= bunde.getInt("cost");
		String Pic_Addr=bunde.getString("picture");
		int date=Date_int_tranfer();
		myCostDB.update(_id,category,Cost,comments,Pic_Addr,date);
		
		    myCursor.requery(); 
		    myListView.invalidateViews();
		    Toast.makeText(this, "Updates Saved", Toast.LENGTH_LONG).show();
		}
		else if(requestCode==1)
		{
			//setContentView(R.layout.main);
		}
		break;
	case RESULT_CANCELED:
		String identifier = bunde.getString("back_Identifier");
		if (identifier.equals("false"))
		{
			myCostDB.delete(_id);
			Toast.makeText(this, "Deleted Already", Toast.LENGTH_SHORT).show();
			myCursor.requery();
			myListView.invalidateViews();
		}
		else Toast.makeText(this, "Edition Canceled", Toast.LENGTH_SHORT).show(); 
		break;
	default:
		break;
	}
	mainFragment.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
	
}
  public int Date_int_tranfer()
  {
	  	String temp = "";
		final Calendar c = Calendar.getInstance();
		int date=(c.get(Calendar.YEAR)) * 10000; 
		  
		if (c.get(Calendar.MONTH) < 10 ) temp = temp + "0"+c.get(Calendar.MONTH) ;
		else temp = temp +c.get(Calendar.MONTH); 
		if (c.get(Calendar.DATE) < 10 ) temp = temp + "0"+c.get(Calendar.DATE) ;
		else temp = temp +c.get(Calendar.DATE);
		date += Integer.parseInt(temp);
		return date;
  }

}