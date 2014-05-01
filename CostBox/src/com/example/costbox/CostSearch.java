package com.example.costbox; 

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.costbox.facebook.FBShareFragment;
import com.example.costbox.MyPreference;
import com.example.costbox.widget.KeyboardUtil;
import com.example.costbox.widget.KeyboardUtil.keyboardOnReturnListener;
import com.example.costbox.widget.pullToAddListView;
import com.example.costbox.widget.pullToAddListView.OnRefreshListener;
import com.facebook.Session;

@SuppressLint("NewApi")
public class CostSearch extends Activity 
{ 
  //initialization of variable
  public static CostDB myCostDB;
  private static Cursor myCursor;
  private static ListView Searchlist; 
  private static int _id;
  private static View _currentItemView;
  private TextView searchresult;
  int result_count;
  //contextual action mode init
  private ActionMode mActionMode;  
  //adapter of listview and database
  SimpleCursorAdapter adapter;
  
  
  /** Called when the activity is first created. */ 
  @Override 
  public void onCreate(Bundle savedInstanceState) 
  { 
    super.onCreate(savedInstanceState); 
    setContentView(R.layout.search);
	// action bar return back to the up layer
	ActionBar actionBar = getActionBar();  
	actionBar.setDisplayHomeAsUpEnabled(true); 
    //load the listview
    Searchlist = (ListView) this.findViewById(R.id.Searchlist); 
     String Query="";
     Intent i=getIntent();
     Bundle bunde=i.getExtras();
     Query=bunde.getString("Query");
    //load the database for today
    configure_database(Query); 
    //TextView
    searchresult = (TextView) findViewById(R.id.searchrusult);
    searchresult.setText("Find "+result_count+" totally");
  
    // go to the detail interface
    Searchlist.setOnItemClickListener(new AdapterView.OnItemClickListener()
    { 
      @Override
      public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) 
      { 
        myCursor.moveToPosition(arg2);//because of the header!!!
        _id = myCursor.getInt(0);  
        //give the control to CostDetail
        Intent intent=new Intent();
        intent.setClass(CostSearch.this, CostDetail.class);
        
        Bundle bundle = new Bundle();
        bundle.putString("category",myCursor.getString(3));
        bundle.putString("catemark",myCursor.getString(2));
        bundle.putString("comments",myCursor.getString(4));
        bundle.putDouble("cost",myCursor.getDouble(6));
        bundle.putString("picture", myCursor.getString(5));
        bundle.putInt("date",myCursor.getInt(1));
        bundle.putString("back_Identifier","false" ); 
        bundle.putString("addingtime", myCursor.getString(7));
        bundle.putString("addingorder",myCursor.getString(8));
        
        intent.putExtras(bundle);
        startActivityForResult(intent,0);
        overridePendingTransition(R.anim.rtocome, R.anim.rtoleft);				

        }
      }
    ); 
   

    
  }
  
   private void configure_database(String Query) {
	myCostDB = new CostDB(this); 
    myCursor = myCostDB.selectQuery(Query);
    result_count=myCursor.getCount();
    SimpleCursorAdapter.ViewBinder viewBinder = new SimpleCursorAdapter.ViewBinder() {
    	  @SuppressLint("NewApi")
		@Override
    	  public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
    	   if(view instanceof ImageView) {
    		   String cate_name=cursor.getString(2);
    		   
    		   try
    			{
    			   int temp_id = getResources().getIdentifier(cate_name, "drawable", "com.example.costbox");
        		   view.setBackground(getResources().getDrawable(temp_id));
        		   }
    			catch(Exception e){

    				view.setBackground(getResources().getDrawable(R.drawable.customcategory));
    			};

    	   }
    	   return false;
    	  }
    	 };
    
    adapter = 
      new SimpleCursorAdapter(this, R.layout.list, myCursor, new String[] { CostDB.CATEGORY,CostDB.FIELD_TEXT,CostDB.COMM, CostDB.COST_MONEY, CostDB.DATE}, new int[] { R.id.cate_image2,R.id.listTextView1,R.id.commentext,R.id.listCostView,R.id.addingtime});
    adapter.setViewBinder(viewBinder);
    Searchlist.setAdapter(adapter);
    
 
}
  
  /* The following method is used to set up the action mode callback
   * delete,edit,share
   */
  private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// Respond to clicks on the actions in the CAB
			switch (item.getItemId()) {
			case R.id.menu_delete:
				CostSearch.deleteTodo();
		        Toast.makeText( CostSearch.this, "deleted Successful", Toast.LENGTH_LONG).show(); 		
				result_count=myCursor.getCount();
			    searchresult.setText("Find "+result_count+" totally");
		        mode.finish(); 
				return true;

			case R.id.menu_edit:

				Intent intent=new Intent();
		        intent.setClass(CostSearch.this, CostDetail.class);
		        Bundle bundle = new Bundle();
		        bundle.putString("category",myCursor.getString(3));
		        bundle.putString("comments",myCursor.getString(4));
		        bundle.putDouble("cost",myCursor.getDouble(6));
		        bundle.putString("picture", myCursor.getString(5));
		        bundle.putInt("date",myCursor.getInt(1));
		        bundle.putString("back_Identifier","false" ); 
		        intent.putExtras(bundle);
		        startActivityForResult(intent,0);
		        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				mode.finish();
				return true;

			default:
				return false;
			
			}
  		
		}

  	public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			_currentItemView.setBackgroundResource(R.drawable.rouhe1);
			return true;
		}
  	    public void onDestroyActionMode(ActionMode mode) {
  		  _currentItemView.setBackgroundResource(R.drawable.itembg);  //return the color back
		}
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
	}; 
	// used for action bar implementation 
@Override
  public boolean onCreateOptionsMenu(final Menu menu) {
	 super.onCreateOptionsMenu(menu); 
	 getMenuInflater().inflate(R.menu.search, menu);
	 SearchView searchView = (SearchView) menu.findItem(R.id.search_search).getActionView();  
	 SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() 
	    {
	        public boolean onQueryTextChange(String newText) 
	        {
	            return true;
	        }

	        public boolean onQueryTextSubmit(String query) 
	        {   configure_database(query);
	            Searchlist.invalidateViews();
	        	menu.findItem(R.id.search_search).collapseActionView();
	            searchresult.setText("Find "+result_count+" totally");
	            return true;
	        }
	    };
	    searchView.setOnQueryTextListener(queryTextListener);
	 return true; 
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
	switch(item.getItemId())
	{

	case android.R.id.home:
        // app icon in action bar clicked; go home
        Intent intent = new Intent(this, CostBox.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
        return true;
	 default:break;
	}
	return super.onOptionsItemSelected(item);
}

  //orientation change function
public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		if(this.getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_LANDSCAPE){
			//setContentView(R.layout.summary);
			//setContentView(R.layout.transist);
			Intent intent = new Intent();
			intent.setClass(CostSearch.this,com.example.costbox.chartview.Summary.class);
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
		{
		String category= bunde.getString("category");
		String catemark=bunde.getString("catemark");
		String comments= bunde.getString("comments");
		String adding_time_temp=bunde.getString("addingtime");
		String adding_order_temp=bunde.getString("addingorder");
		double Cost= bunde.getDouble("cost");
		String Pic_Addr=bunde.getString("picture");
		int date=Date_int_transfer();
		myCostDB.update(_id,catemark,category,Cost,comments,Pic_Addr,date,adding_time_temp,adding_order_temp);
		
		    myCursor.requery(); 
		    Searchlist.invalidateViews();
		    Toast.makeText(this, "Updates Saved", Toast.LENGTH_LONG).show();
		}

		break;
	case RESULT_CANCELED:
		String identifier = bunde.getString("back_Identifier");
		if(identifier!=null)
		{
			if (identifier.equals("false"))
		  {
			myCostDB.delete(_id);
			Toast.makeText(this, "Deleted Already", Toast.LENGTH_SHORT).show();
			myCursor.requery();
			Searchlist.invalidateViews();
			result_count=myCursor.getCount();
		    searchresult.setText("Find "+result_count+" totally");
		  }
		  else Toast.makeText(this, "Edition Canceled", Toast.LENGTH_SHORT).show(); 
		}

		break;
		
    	
	default:

		break;
	}

    super.onActivityResult(requestCode, resultCode, data);
	
}


private static void deleteTodo()
{
  if(_id == 0)
  return;
  myCostDB.delete(_id); 
  myCursor.requery();
  Searchlist.invalidateViews(); 

  _currentItemView.setBackgroundResource(R.drawable.itembg);
  _id = 0; 

}

/*
* make the date into String int the form of YYYY-MM-DD
*/
public String Date_format_transfer(int date)
{     
	    String Date=date+"";
	    String temp="";
	    for(int i=6;i<8;i++)
	    	temp+=Date.charAt(i);
	    temp+=" ";
	    //DD 
	    int Month=0;
	    Month=(int)(Date.charAt(4)-'0')*10+(int)(Date.charAt(5)-'0');
	    //Month=4;
	    switch (Month)
	    {
	    case 1:
	    	temp+="January ";break;
	    case 2:
	    	temp+="Feburary ";break;
	    case 3:
	    	temp+="March ";break;
	    case 4:
	    	temp+="April ";break;
	    case 5:
	    	temp+="May ";break;
	    case 6:
	    	temp+="June ";break;
	    case 7:
	    	temp+="July ";break;
	    case 8:
	    	temp+="August ";break;
	    case 9:
	    	temp+="September ";break;
	    case 10:
	    	temp+="October ";break;
	    case 11:
	    	temp+="November ";break;
	    case 12:
	    	temp+="December ";break;
	    default:
	    	temp+="May" ;break;
	    };
	    for(int i=0;i<4;i++)
	    	temp+=Date.charAt(i);

	    return temp;
}
/*
* make the date into int in the form of YYYYMMDD
*/
public static int Date_int_transfer()
{
	  	String temp = "";
	  	Time t=new Time();
	    t.setToNow();
	    int mYear=t.year;
	    int mMonth=t.month+1;
	    int mDay=t.monthDay;
	    
		int date=mYear* 10000; 
		  
		if (mMonth< 10 ) temp = temp + "0"+mMonth;
		else temp = temp +mMonth; 
		if (mDay < 10 ) temp = temp + "0"+mDay ;
		else temp = temp +mDay;
		date += Integer.parseInt(temp);
		return date;
}

/*
 * used to get the previous or the next day of the current day
 * return String
 * input currentday(String) & dayAddNum(Integer) +/- number
 */
public static String getDateStr(String day,long dayAddNum)  {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date nowDate = null;
		
			try {
				nowDate = df.parse(day);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		Date newDate2 = new Date(nowDate.getTime() + dayAddNum * 24 * 60 * 60 * 1000);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String dateOk = simpleDateFormat.format(newDate2);
		return dateOk;
	}
  
}