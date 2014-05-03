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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.annotation.SuppressLint;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.View.OnClickListener;
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
import android.widget.DatePicker;
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
public class CostBox extends FragmentActivity 
implements CategoryFragment.OnReturnListener,
          InputKeyFragment.OnReturnListener2
{ 
  //initialization of variable
  public static CostDB myCostDB;
  private static Cursor myCursor;
  private static pullToAddListView myListView; 
  private static int _id;
  private static View _currentItemView;
  //contextual action mode init
  private ActionMode mActionMode;  
  //FB share fragments ( run in background )
  public FBShareFragment fbShareFragment;
  // two squeeze fragments
  private CategoryFragment category_list_fragment;
  private InputKeyFragment inputKey_fragment;
  //Two TextView shown on the top
  public static TextView today_date;
  public static TextView today_total;
  //Temp comments and picpath
  private static String comments_temp="No comments...";
  private static String picpath_temp="None";
  //Adding time
  static String adding_time="";
  static int select_date;
  //flip variable
  private float oldTouchValue;//text the position of finger
  //Check if touch event should be interrupted
  Boolean is_adding;
  //adapter of listview and database
  SimpleCursorAdapter adapter;
  /*
   * the following is about the cartview
   */
  final String[] quickChoice = new String[]{"Today","Past 3 Days","Past Week","Past Month"};
	final String[] monthChoice = new String[]{"Janary", "February", "March", "April", 
			"May", "June", "July", "August", "September", "October", "November", "December"};
	private int choice = 0;
	private int day_diff = 0;
	private String start_date = new String();
	private String end_date = new String();
	private String today_date2 = new String();
	private String line_start = new String();
	private String line_end = new String();
	private int today_year, today_monthOfYear, today_dayOfMonth;
	private int start_year, start_monthOfYear, start_dayOfMonth;
	private int end_year, end_monthOfYear, end_dayOfMonth;
	
  //initialization of pie and dou	
	String[] categories;
	double[] values;
	final int[] COLORS = new int[] {0xFFECD078,0xFFD95B43,0xFFC02942,0xFF542437,0xFF53777A};
	

	//initialization of line
	String[] titles = new String[] {"Daily Cost"};
	List<double[]> xvalue = new ArrayList<double[]>();
	List<double[]> yvalue = new ArrayList<double[]>();
	
	public void getPieValue(String startdate,  String enddate){
		int start = Integer.parseInt(startdate);
		int end = Integer.parseInt(enddate);
		Map<String, Double> result = new HashMap<String, Double>(200);
		result = CostBox.myCostDB.sum_Of_catogory_Of_days(start, end);
		
		categories = new String[result.size()];
		values = new double[result.size()];
		
		Iterator iter = result.entrySet().iterator();
		int temp = 0;
		while(iter.hasNext())
		{
			Map.Entry entry =(Map.Entry)iter.next();
			categories[temp] = (String) entry.getKey();
			values[temp] = (Double) Double.parseDouble(entry.getValue().toString());
			temp++;
		}
	}
	
	public void getLineValue(String startdate,  String enddate){
		int start = Integer.parseInt(startdate);
		int end = Integer.parseInt(enddate);
		Map<Integer, Double> result = new HashMap<Integer, Double>(200);
		result = CostBox.myCostDB.sum_up_Of_days(start, end);
		
		Object[] key =  result.keySet().toArray();    
		Arrays.sort(key);
		
		double x_tmp[] = new double[result.size()];
		double y_tmp[] = new double[result.size()];

		int temp = 0;
		for(int i = 0; i<key.length; i++){
			x_tmp[temp] = (Double) Double.parseDouble(key[i].toString()) % 100;
			y_tmp[temp] = (Double) Double.parseDouble(result.get(key[i]).toString());
			temp++;
		}
		for(int i=0;i<x_tmp.length;i++)
			Log.v("aaa", "aaa "+ x_tmp[i] + " aaa " + y_tmp[i]);
		
		xvalue.clear();
		yvalue.clear();
		xvalue.add(x_tmp);
		yvalue.add(y_tmp);
	}
	PointStyle[] styles = new PointStyle[] { PointStyle.DIAMOND};
	int[] colors = new int[] { Color.BLUE };
	
	// others
	private TextView startDate, endDate;
	
	private Button startchoose, endchoose, piechart, doughnutchart, linechart, quickchoice, monthchoice;
	private GraphicalView myChartView;
	
  /** Called when the activity is first created. */ 
	
  @Override 
  public void onCreate(Bundle savedInstanceState) 
  { 
    
   
    if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
    { 
   super.onCreate(savedInstanceState); 
    setContentView(R.layout.main);
    is_adding=false;
    select_date=Date_int_transfer();

    //load all the fragments
    //get the fragement of FB share
    if (savedInstanceState == null) {
        fbShareFragment = new FBShareFragment();
        getSupportFragmentManager().beginTransaction().add(fbShareFragment, "FBS")
        .commit();
        /*
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();  
		ft.add(R.id.container, category_list_fragment);
		ft.add(R.id.container2, inputKey_fragment);
		ft.addToBackStack(null); 
		ft.commit();
		*/
    } else {
        fbShareFragment = (FBShareFragment) getSupportFragmentManager()
        .findFragmentByTag("FBS");
    }
    
    
    category_list_fragment = new CategoryFragment();
    inputKey_fragment = new InputKeyFragment();
    //load the listview
    myListView = (pullToAddListView) this.findViewById(R.id.myListView); 
    //init refresh of list
    myListView.setonRefreshListener(new OnRefreshListener() {
    	@Override
    	public void onRefresh() 
    	{       
    		    //wait for a while
                sleepfor1s(); 
                //lock the buttom layer ( the listview )
                lockButtomLayer();
                myListView.onRefreshComplete();
                //show the fragment from both sides
                showCIfragment();
    	}

		private void showCIfragment() {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();  
			ft.setCustomAnimations(R.anim.fragment_move_in2, R.anim.fragment_move_out2);
			ft.add(R.id.container, category_list_fragment);
			ft.setCustomAnimations(R.anim.fragment_move_in, R.anim.fragment_move_out);
			ft.add(R.id.container2, inputKey_fragment);
			//ft.addToBackStack(null); 
			ft.commit();
		} 

		private void lockButtomLayer() {
			View currentLayout=(View)findViewById(R.id.myListView);
			currentLayout.setOnTouchListener(new View.OnTouchListener(){
			       @Override
			       public boolean onTouch(View v, MotionEvent event) {
			        // TODO Auto-generated method stub
			        return true;
			       } 
			 });
			is_adding=true;
		}

		
    });
    
    //load the database for today
    configure_database(Date_int_transfer()+""); 
  
    // go to the detail interface
    myListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    { 
      @Override
      public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) 
      { 
        myCursor.moveToPosition(arg2-1);//because of the header!!!
        _id = myCursor.getInt(0);  
        //give the control to CostDetail
        Intent intent=new Intent();
        intent.setClass(CostBox.this, CostDetail.class);
        
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
    myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
 		{
 			@Override
 			public boolean onItemLongClick(AdapterView arg0, View arg1, int arg2, long arg3) {
 				// TODO Auto-generated method stub
 				myCursor.moveToPosition(arg2-1);//because of the header
 		        _id = myCursor.getInt(0);  
 		       //return to the original color
 		        if(_currentItemView!=null) 
 		       { _currentItemView.setBackgroundResource(R.drawable.itembg);}
 		        _currentItemView=arg1;
                //set new color
 		        _currentItemView.setBackgroundResource(R.drawable.rouhe1);
				mActionMode = CostBox.this
						.startActionMode(mActionModeCallback);
 		        return true;  
 			}
    });
    
   //configure Textview
    today_date=(TextView)findViewById(R.id.todaydate);
    today_total=(TextView)findViewById(R.id.today_total);
    today_date.setText(Date_format_transfer(select_date));
    today_total.setText(SumUp());
    }
    else if((this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE))
    {     
    	super.onCreate(savedInstanceState); 
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        WindowManager.LayoutParams.FLAG_FULLSCREEN); 
    	setContentView(R.layout.summary_layout);
        is_adding=true;
		
		// 将日期选择的 end 定位到今天，start 根据 Preference 来做
		startDate = (TextView) findViewById(R.id.startdate);
		endDate = (TextView) findViewById(R.id.enddate);
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
		String source = settings.getString("summary_view", "");  
		if(source.equals("004")) day_diff = 0;
		else if(source.equals("005")) day_diff = -2;
		else if(source.equals("006")) day_diff = -6;
		else if(source.equals("007")) day_diff = -29;
		
		
		Calendar calendar = Calendar.getInstance();		
		today_year = calendar.get(Calendar.YEAR);
		today_monthOfYear = calendar.get(Calendar.MONTH);
		today_dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int today_month = today_monthOfYear + 1;
	    String today_formattedMonth = "" + today_month;
	    String today_formattedDayOfMonth = "" + today_dayOfMonth;
	    if(today_month < 10) today_formattedMonth = "0" + today_month;
	    if(today_dayOfMonth < 10) today_formattedDayOfMonth = "0" + today_dayOfMonth;
		today_date2 = "" + today_year + today_formattedMonth + today_formattedDayOfMonth;

		start_date = com.example.costbox.CostBox.getDateStr(today_date2, day_diff);
		start_year = Integer.parseInt(start_date.substring(0,4));
		start_monthOfYear = Integer.parseInt(start_date.substring(4,6))-1;
		start_dayOfMonth = Integer.parseInt(start_date.substring(6));
		startDate.setText(start_date.substring(0,4) + "-" +start_date.substring(4,6) + "-" + start_date.substring(6));
		
		end_date = com.example.costbox.CostBox.getDateStr(today_date2, 0);
		end_year = Integer.parseInt(end_date.substring(0,4));
		end_monthOfYear = Integer.parseInt(end_date.substring(4,6))-1;
		end_dayOfMonth = Integer.parseInt(end_date.substring(6));
		endDate.setText(end_date.substring(0,4) + "-" +end_date.substring(4,6) + "-" + end_date.substring(6));
		
		getPieValue(start_date,end_date);
		showPieChart(categories, values);
		
		// Click the start date button, and choose the date, then update the pie chart
		startchoose = (Button) findViewById(R.id.start);
		startchoose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(CostBox.this, DatePickerDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						start_year = year; start_monthOfYear = monthOfYear; start_dayOfMonth = dayOfMonth;
						int start_month = monthOfYear + 1;
					    String start_formattedMonth = "" + start_month;
					    String start_formattedDayOfMonth = "" + dayOfMonth;
					    if(start_month < 10) start_formattedMonth = "0" + start_month;
					    if(dayOfMonth < 10) start_formattedDayOfMonth = "0" + dayOfMonth;
					    
					    startDate.setText( year + "-" + start_formattedMonth + "-" + start_formattedDayOfMonth);
					    start_date = "" + year + start_formattedMonth + start_formattedDayOfMonth;
						
						getPieValue(start_date, end_date);
						showPieChart(categories, values);
						
					}
				} , start_year, start_monthOfYear, start_dayOfMonth);
				
				datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_NEGATIVE){
							
						}
					}
				});
				
				datePickerDialog.show();
			}
			
		});
		
		// Click the end date button, and choose the date, then update the pie chart
		endchoose = (Button) findViewById(R.id.end);
		endchoose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(CostBox.this, DatePickerDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						end_year = year; end_monthOfYear = monthOfYear; end_dayOfMonth = dayOfMonth;
						int end_month = monthOfYear + 1;
					    String end_formattedMonth = "" + end_month;
					    String end_formattedDayOfMonth = "" + dayOfMonth;
					    if(end_month < 10) end_formattedMonth = "0" + end_month;
					    if(dayOfMonth < 10) end_formattedDayOfMonth = "0" + dayOfMonth;
					    
						endDate.setText( year + "-" + end_formattedMonth + "-" + end_formattedDayOfMonth);
						end_date = "" + year + end_formattedMonth + end_formattedDayOfMonth;
						
						getPieValue(start_date, end_date);
						showPieChart(categories, values);
						
					}
				}, end_year, end_monthOfYear, end_dayOfMonth);
				
				datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_NEGATIVE){
							
						}
					}
				});
				
				datePickerDialog.show();
			}
		});
		
		
		piechart = (Button) findViewById(R.id.piechart);
		piechart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPieChart(categories, values);
			}
		});
		
		doughnutchart = (Button) findViewById(R.id.doughnut);
		doughnutchart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDoughnutChart(categories, values);
				
			}
		});
		
		// Click the quick choice button, and get the corresponding start date and end date, then update the chart
		quickchoice = (Button) findViewById(R.id.quickchoice);
		quickchoice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder quick_choice = new AlertDialog.Builder(CostBox.this, AlertDialog.THEME_HOLO_LIGHT);
				quick_choice.setTitle("Quick Choice for Pie Chart");
				quick_choice.setSingleChoiceItems(quickChoice, choice, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						choice = which;
					}
				});
				quick_choice.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.v("hhh","hhh123" + quickChoice[choice]);
							// 这里需要添加和之前类似的代码，重新 getPieValue
							if(choice == 0) day_diff = 0;
							else if(choice == 1) day_diff = -2;
							else if(choice == 2) day_diff = -6;
							else if(choice == 3) day_diff = -29;
							start_date = com.example.costbox.CostBox.getDateStr(today_date2, day_diff);
							startDate.setText(start_date.substring(0,4) + "-" +start_date.substring(4,6) + "-" + start_date.substring(6));
							end_date = com.example.costbox.CostBox.getDateStr(today_date2, 0);
							endDate.setText(end_date.substring(0,4) + "-" +end_date.substring(4,6) + "-" + end_date.substring(6));
							getPieValue(start_date, end_date);
							showPieChart(categories, values);
						}
					});
				quick_choice.setNegativeButton("Cancel", null);
				quick_choice.show();
			}
		});
		
		// Click the line button, choose the month, and display the line chart of daily cost of corresponding month
		linechart = (Button) findViewById(R.id.linechart);
		linechart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder month_choice = new AlertDialog.Builder(CostBox.this, AlertDialog.THEME_HOLO_LIGHT);
				month_choice.setTitle("Choose Month");
				month_choice.setSingleChoiceItems(monthChoice, choice, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						choice = which;						
					}
				});
				month_choice.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int month = choice + 1;
					    String formattedMonth = "" + month;
					    if(month < 10) formattedMonth = "0" + month;
					    line_start = "2014" + formattedMonth + "01";
						line_end = "2014" + formattedMonth + "31";
						
						getLineValue(line_start, line_end);
						showLineChart(titles,xvalue, yvalue);
					}
				});
				month_choice.setNegativeButton("Cancel", null);
				month_choice.show();
			}
		});

    }
  }
  
   @Override
protected void onSaveInstanceState(Bundle outState) {
	// TODO Auto-generated method stub
	super.onSaveInstanceState(outState);
}

@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
}

@Override
protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
}

private void configure_database(String Date) {
	myCostDB = new CostDB(this); 
    myCursor = myCostDB.select(Date); 
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

    				//int temp_id = getResources().getIdentifier("book", "drawable", "com.example.costbox"); 
    				view.setBackground(getResources().getDrawable(R.drawable.customcategory));
    			};
    		   
    		   
    	   }
    	   return false;
    	  }
    	 };
    
    adapter = 
      new SimpleCursorAdapter(this, R.layout.list, myCursor, new String[] { CostDB.CATEGORY,CostDB.FIELD_TEXT,CostDB.COMM, CostDB.COST_MONEY, CostDB.TIME}, new int[] { R.id.cate_image2,R.id.listTextView1,R.id.commentext,R.id.listCostView,R.id.addingtime});
    adapter.setViewBinder(viewBinder);
    myListView.setAdapter(adapter);
    

}
  
  /* The following method is used to set up the action mode callback
   * delete,edit,share
   */
  private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// Respond to clicks on the actions in the CAB
			switch (item.getItemId()) {
			case R.id.menu_delete:
				CostBox.deleteTodo();
		        Toast.makeText( CostBox.this, "Deleted", Toast.LENGTH_LONG).show(); 		
				mode.finish(); 
				return true;

			
			case R.id.menu_share:
                if(isNetworkConnected(CostBox.this)==false)
                {
                	Toast.makeText(CostBox.this, "Please check your network status.", 100).show();
                	return true;
                }
                Bundle postParams=new Bundle();
                //get the picture path and decode it to byte stream
                String picpath=myCursor.getString(5);
                byte[] data;
               
                //default pic
                if(picpath.equals("None"))
                { 
                    Resources res=getResources(); 
            	    Bitmap image = BitmapFactory.decodeResource(res, R.drawable.beauty);
            	    ByteArrayOutputStream bos = new ByteArrayOutputStream();  
                    image.compress(Bitmap.CompressFormat.JPEG, 100, bos);  
                    data = bos.toByteArray(); 
                }
                else 
                {  
                	Bitmap image = BitmapFactory.decodeFile(picpath);
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
			     double get_cost=myCursor.getDouble(6);
			    
			     String message_upload="I'm soooo rich!! I spent "+get_cost+" on "+title+" ! "+comments;
			     
			     //data transfer
			     postParams.putByteArray("picture",data);
			     postParams.putString("message",message_upload);
			     Session session = fbShareFragment.getActiveS();
			     fbShareFragment.postParams=postParams;
			     //if logged in
                if(session.isOpened())
                {
			          fbShareFragment.publishStory();
                }
                  //if not login
                else 
                {
               	      Toast.makeText(CostBox.this, "Please Login and Try Again",Toast.LENGTH_SHORT).show();
               	      facebookLogin();
               	      //delay the apperance of the alert
               	      //should be a better way to cope with this problem
               	      /*
               	      for(int i=0;i<20;i++)
               	      { sleepfor1s();}
               	      */
               	  // show the share result
                   /*
               	   AlertDialog.Builder dg = new Builder(CostBox.this);
   				   dg.setMessage("Do you want to share it now?");
   				   dg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
   					 @Override
   					public void onClick(DialogInterface dialog, int whichButton) {
   						fbShareFragment.publishStory();
   					}
   				    })
   				   .setNegativeButton("No", new DialogInterface.OnClickListener() {
   				 	@Override
   					public void onClick(DialogInterface dialog, int whichButton) {
   						// TODO Auto-generated method stub
   					}
   				    })
   				    .show();
   				    */
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
	public void facebookLogin() {
		fbShareFragment.onClickLogin();
	}
  
    // used for action bar implementation 
@Override
  public boolean onCreateOptionsMenu(final Menu menu) {
	 super.onCreateOptionsMenu(menu); 
	 getMenuInflater().inflate(R.menu.main, menu);
	 SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();  
	 SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() 
	    {
	        public boolean onQueryTextChange(String newText) 
	        {
	        	//adapter.getFilter().filter(newText.toString());
	        	//myListView.setAdapter(adapter);
	        	//adapter.notifyDataSetChanged();
	            return true;
	        }

	        public boolean onQueryTextSubmit(String query) 
	        {   //adapter.getFilter().filter(query.toString());
	        	Intent i=new Intent();
	        	i.setClass(CostBox.this, CostSearch.class);
	            Bundle bundle = new Bundle();
	            bundle.putString("Query",query);
	            i.putExtras(bundle);
	            startActivity(i);
	            overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
	        	menu.findItem(R.id.menu_search).collapseActionView();
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
		this.finish();
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		break;
	 default:break;
	}
	return super.onOptionsItemSelected(item);
}


@Override
protected void onStop() {
	// TODO Auto-generated method stub
	super.onStop();

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
		int date=bunde.getInt("date");
		myCostDB.update(_id,catemark,category,Cost,comments,Pic_Addr,date,adding_time_temp,adding_order_temp);
		
		    myCursor.requery(); 
		    myListView.invalidateViews();
		    Toast.makeText(this, "Updates Saved", Toast.LENGTH_LONG).show();
		}
		else if(requestCode==2)
		{
			Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
			//do nothing...
		}
		
    	today_date.setText(Date_format_transfer(select_date));
        today_total.setText(SumUp());

		break;
	case RESULT_CANCELED:
		String identifier = bunde.getString("back_Identifier");
		if(identifier!=null)
		{
			if (identifier.equals("false"))
		  {
			myCostDB.delete(_id);
			Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
			myCursor.requery();
			myListView.invalidateViews();
		  }
		  else Toast.makeText(this, "Edition Canceled", Toast.LENGTH_SHORT).show(); 
		}
    	today_date.setText(Date_format_transfer(select_date));
        today_total.setText(SumUp());
		break;
	case 999:
		break;
    	
	default:
		today_date.setText(Date_format_transfer(select_date));
        today_total.setText(SumUp());
		break;
	}
	fbShareFragment.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
	
}

@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
}

@Override
protected void onResumeFragments() {
	// TODO Auto-generated method stub
	super.onResumeFragments();
}

/* the first return callback from the category fragment
 * (non-Javadoc)
 * @see com.example.costbox.CategoryFragment.OnReturnListener#onReturnFromFragment1(int, java.lang.String)
 */
public void onReturnFromFragment1(int state,String reserve) {

	  //save it
	  if(state==1) 
	  {
		 InputKeyFragment inputkey_fragment=(InputKeyFragment)
	         getSupportFragmentManager().findFragmentById(R.id.container2);
		 CategoryFragment category_fragment=(CategoryFragment)
	         getSupportFragmentManager().findFragmentById(R.id.container);
		 //fetch the info
		 String cost_temp=category_fragment.Money_total.getText().toString();
		 if(check_number(cost_temp)==0)
		 {
			 Toast.makeText(this, "Sorry, your input is invalid.", 100).show();
		     
		 }
		 else{
		 String title_temp=inputkey_fragment.editTT.getText().toString();
		 String catemark=category_fragment.chosen_cate;
		 comments_temp=inputkey_fragment.comments;
		 picpath_temp=inputkey_fragment.ImageAddress;
		 
		 // back to initial
		 category_fragment.Money_total.setText("");
		 inputkey_fragment.editTT.setText("");
		 inputkey_fragment.comments="No comments...";
		 inputkey_fragment.ImageAddress="None";
		 category_fragment.chosen_cate="others";
		 
		 // if have something to record
		 if(!(cost_temp.equals("")&&title_temp.equals(""))){
		 CostBox.addTodo2(cost_temp,title_temp,catemark);
		 Toast.makeText( CostBox.this, "Added Successful", Toast.LENGTH_LONG).show(); 
		 }

		 //return to clickable interface
		 View currentLayout=(View)findViewById(R.id.myListView);
		 currentLayout.setOnTouchListener(new View.OnTouchListener(){
		       @Override
		       public boolean onTouch(View v, MotionEvent event) {
		        // TODO Auto-generated method stub
		        return false;
		       } 
		 });
		 RemoveFragmentFromActivity();
		 }
		 is_adding=false;
	  }
	  //cancel it
	  else if(state==2)
	  {     //clear the TextView
		    InputKeyFragment inputkey_fragment=(InputKeyFragment)
             getSupportFragmentManager().findFragmentById(R.id.container2);
             CategoryFragment category_fragment=(CategoryFragment)
             getSupportFragmentManager().findFragmentById(R.id.container);
             
             category_fragment.Money_total.setText("");
		     inputkey_fragment.editTT.setText("");
		     //return to clickable interface
			 ReturnClickableOfButtomLayer();
			 //remove the fragment
		     RemoveFragmentFromActivity();
		     is_adding=false;
	  }
	  else if(state==4)   // when choosing the category from the gridview --> tell the inputkey
	  {
		  InputKeyFragment inputkey_fragment=(InputKeyFragment)
	                getSupportFragmentManager().findFragmentById(R.id.container2);

		  CategoryFragment category_fragment=(CategoryFragment)
	                getSupportFragmentManager().findFragmentById(R.id.container);
		  
		  inputkey_fragment.editTT.setText(reserve);
		  inputkey_fragment.editTT.setSelection(inputkey_fragment.editTT.getText().length());
		  EditText Money_total=category_fragment.Money_total;
		  Money_total.setText("");
		  ShowTheKeyboard(inputkey_fragment, Money_total);	

	  }
		 //get information from Category fragment
    }
/*
 * the second return callback from the inputKey fragment
 * (non-Javadoc)
 * @see com.example.costbox.InputKeyFragment.OnReturnListener2#onReturnFromFragment2(int)
 */
public void onReturnFromFragment2(int state) {
      //get information from inputKeyFragment
	  /*
	   * the info. is asked to be saved
	   */
	  if(state==1) 
	  {
		 
		  InputKeyFragment inputkey_fragment=(InputKeyFragment)
	         getSupportFragmentManager().findFragmentById(R.id.container2);
		 CategoryFragment category_fragment=(CategoryFragment)
	         getSupportFragmentManager().findFragmentById(R.id.container);
		 //fetch the info
		 String cost_temp=category_fragment.Money_total.getText().toString();
		 if(check_number(cost_temp)==0)
		 {
			 Toast.makeText(this, "Sorry, your input is invalid.", 100).show();
		     
		 }
		 else{
			 
		 
		 String title_temp=inputkey_fragment.editTT.getText().toString();
		 String catemark=category_fragment.chosen_cate;
		 comments_temp=inputkey_fragment.comments;
		 picpath_temp=inputkey_fragment.ImageAddress;
		 
		 // back to initial
		 category_fragment.Money_total.setText("");
		 inputkey_fragment.editTT.setText("");
		 inputkey_fragment.comments="No comments...";
		 inputkey_fragment.ImageAddress="None";
		 category_fragment.chosen_cate="others";
		 
		 // if have something to record
		 if(!(cost_temp.equals("")&&title_temp.equals(""))){
		 CostBox.addTodo2(cost_temp,title_temp,catemark);
		 Toast.makeText( CostBox.this, "Added Successful", Toast.LENGTH_LONG).show(); 
		 }

		 //return to clickable interface
		 View currentLayout=(View)findViewById(R.id.myListView);
		 currentLayout.setOnTouchListener(new View.OnTouchListener(){
		       @Override
		       public boolean onTouch(View v, MotionEvent event) {
		        // TODO Auto-generated method stub
		        return false;
		       } 
		 });
		 RemoveFragmentFromActivity();
		 
		 }
		 is_adding=false;
	  }
	  /*
	   * the info is asked to be canceled
	   */
	  else if(state==2)
	  {     InputKeyFragment inputkey_fragment=(InputKeyFragment)
            getSupportFragmentManager().findFragmentById(R.id.container2);
            CategoryFragment category_fragment=(CategoryFragment)
            getSupportFragmentManager().findFragmentById(R.id.container);
            category_fragment.Money_total.setText("");
   		    inputkey_fragment.editTT.setText("");
		    //return to clickable interface
			 View currentLayout=(View)findViewById(R.id.myListView);
			 currentLayout.setOnTouchListener(new View.OnTouchListener(){
			       @Override
			       public boolean onTouch(View v, MotionEvent event) {
			        // TODO Auto-generated method stub
			        return false;
			       } 
			 });
			 RemoveFragmentFromActivity();
			 is_adding=false;
	  }
	  /*
	   * when tap the arrow on keyboard , it allows the user to input cost
	   */
	  else if(state==3)
	  {
		  
			  InputKeyFragment inputkey_fragment=(InputKeyFragment)
		                getSupportFragmentManager().findFragmentById(R.id.container2);
			  CategoryFragment category_fragment=(CategoryFragment)
		                getSupportFragmentManager().findFragmentById(R.id.container);
			  
			  EditText Money_total=category_fragment.Money_total;
			  Money_total.setSelection(Money_total.getText().length());

			  //show the keyboard
			  ShowTheKeyboard(inputkey_fragment, Money_total);	

		  
	  }
	  else if(state==4)
	  {
		  
	  }
    }

/*
 * used to swipt function: left to get the next day & right to get the previous day
 * (non-Javadoc)
 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
 */

@Override
public boolean dispatchTouchEvent(MotionEvent event) {
  
	switch (event.getAction())
    {
    
      case MotionEvent.ACTION_DOWN:
        oldTouchValue = event.getX();
        break;
      case MotionEvent.ACTION_UP:
        // set leaving animation
    	  
    	    
    	float currentX = event.getX();
        if (currentX-oldTouchValue>150.0f&&is_adding==false)  
        {  
      	    //animation
            AddListViewAnimation();
            
        	String temp=getDateStr(select_date+"",-1);
        	select_date=Integer.parseInt(temp);
        	configure_database(temp);	
        	today_date.setText(Date_format_transfer(select_date));
            today_total.setText(SumUp());
          
  
        }
        if (oldTouchValue -currentX > 150.0f && is_adding==false) 
        {   

        	int saved_date=select_date;
        	String temp=getDateStr(select_date+"",1);
        	select_date=Integer.parseInt(temp);
        	if(select_date<=Date_int_transfer())
        	{   //animation
                AddListViewAnimation();
                
        		configure_database(temp);	
            	today_date.setText(Date_format_transfer(select_date));
                today_total.setText(SumUp());
              
        	}
        	else if(is_adding==false){
        		select_date=saved_date;
        		Toast.makeText( CostBox.this, "Tomorrow hasn't come!", Toast.LENGTH_SHORT).show(); 	
        	}
        	
        }
        break;
      case MotionEvent.ACTION_MOVE:
        // TODO: Some code to make the ViewFlipper
        // act like the home screen.
    	  
        break;
     }
    
return super.dispatchTouchEvent(event);


}

public void AddListViewAnimation() {
	Animation animation2=AnimationUtils.loadAnimation(this, R.anim.slideright);
	LayoutAnimationController lac2=new LayoutAnimationController(animation2);
	lac2.setOrder(LayoutAnimationController.ORDER_NORMAL);
	lac2.setDelay(0.5f);
	myListView.setLayoutAnimation(lac2);
}

public boolean onKeyDown(int keyCode, KeyEvent event) {
	  
    if (keyCode == KeyEvent.KEYCODE_BACK
              && event.getRepeatCount() == 0 && this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT) {
    	      
    	        InputKeyFragment inputkey_fragment=(InputKeyFragment)
                getSupportFragmentManager().findFragmentById(R.id.container2);
                CategoryFragment category_fragment=(CategoryFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);
                if(category_fragment!=null&&inputkey_fragment!=null)
                {
                category_fragment.Money_total.setText("");
       		    inputkey_fragment.editTT.setText("");
    		    //return to clickable interface
    			 View currentLayout=(View)findViewById(R.id.myListView);
    			 currentLayout.setOnTouchListener(new View.OnTouchListener(){
    			       @Override
    			       public boolean onTouch(View v, MotionEvent event) {
    			        // TODO Auto-generated method stub
    			        return false;
    			       } 
    			 });
    			 RemoveFragmentFromActivity();
    			 is_adding=false;
                }
                Log.d("button","This is return1");
          return true;
      }
    else if(keyCode == KeyEvent.KEYCODE_BACK
            && event.getRepeatCount() == 0 && this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)
    {
    	//do nothing
    	Log.d("button","This is return");
        return true;
    }
    else 
    {
    	//do nothing
    }
    Log.d("button","This is return2");
      //return super.onKeyDown(keyCode, event);
     return true;
}



// the following is the tool functions
public static int check_number(String input_text)
{
	String pattern = "[0]?[0-9]+?(.[0-9]+)?" ; 
	Pattern p = Pattern.compile(pattern);    
	Matcher m = p.matcher(input_text);
	boolean result = m.matches();
	if(result == true) return 1 ;
	else return 0; 
	// 1 represent that the input from the user is a legal number. 
	// 0 represent that the input is illegal.
}
private static String SumUp()
{
	 double result = myCostDB.sum_up(select_date+"");
	 return result+"";
}
// update adding action
private static void addTodo2(String cost, String title ,String catemark)
{
	  String cost_string_temp = cost; 
	  for (int i =0 ; i < cost.length(); i++)
	  {
		  int index = cost.lastIndexOf(".");
		  if (index >= 0) 
			  cost_string_temp = cost.substring(0,index+2);
		  
	  }
	    double cost_temp=0;
	    try{
	    cost_temp = Double.parseDouble(cost_string_temp);
	    }
	    catch(NumberFormatException nfe){
	    	
	    }
	    // strech the cost number which is a int type data. 
	    
	    
	    String adding_order=""; 
		 Time t=new Time();
		 t.setToNow();
		 int mHour=t.hour;
		 int mMinuts=t.minute;
		 int mSec=t.second;
		 if(mHour<10) { adding_time="0"+mHour+"";adding_order="0"+mHour+"";}
		 else {adding_time=mHour+"";adding_order=mHour+"";}
		 adding_time+=":";
		 if(mMinuts<10) {adding_time+="0"+mMinuts;adding_order+="0"+mMinuts;} 
		 else { adding_time+=mMinuts+"";adding_order+=mMinuts+"";}

		 if(mSec<10) adding_order+="0"+mSec;else adding_order+=mSec;
		if(select_date==Date_int_transfer()) 
		{ 
			myCostDB.insert(catemark,title,cost_temp,comments_temp,picpath_temp,select_date,adding_time,adding_order);
		}
		else
		{
			myCostDB.insert(catemark,title,cost_temp,comments_temp,picpath_temp,select_date,"additional","235959");
		}
	    myCursor.requery(); 
	    myListView.invalidateViews();
	    _id = 0; 
	    today_total.setText(SumUp());
	    comments_temp="No comments...";
	    picpath_temp="None";
	    adding_time="";
}

private static void deleteTodo()
{
  if(_id == 0)
  return;
  myCostDB.delete(_id); 
  myCursor.requery();
  myListView.invalidateViews(); 

  _currentItemView.setBackgroundResource(R.drawable.itembg);
  _id = 0; 
  today_total.setText(SumUp());
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
 * used to show the custom keyboard
 */
public void ShowTheKeyboard(InputKeyFragment inputkey_fragment,
		EditText Money_total) {
	Context ctx;
	  Activity act;
	  ctx = this;
	  act = this;
	  int inputback = Money_total.getInputType();
	  Money_total.setInputType(InputType.TYPE_NULL);   // to avoid the default keyboard
	  Money_total.requestFocus();
	  new KeyboardUtil(act, ctx, Money_total,1,inputkey_fragment).showKeyboard();
	  Money_total.setInputType(inputback);
	  Money_total.setSelection(Money_total.getText().length());
	  
}
/*
 * used to return the clickable buttom layer so that we can use the listview
 */
public void ReturnClickableOfButtomLayer() {
	View currentLayout=(View)findViewById(R.id.myListView);
	 currentLayout.setOnTouchListener(new View.OnTouchListener(){
	       @Override
	       public boolean onTouch(View v, MotionEvent event) {
	        // TODO Auto-generated method stub
	        return false;
	       } 
	 }); 
}
/*
 * used to remove two fragments from the main view
 */
public void RemoveFragmentFromActivity() {
	InputKeyFragment inputkey_fragment=(InputKeyFragment)
	         getSupportFragmentManager().findFragmentById(R.id.container2);
		 CategoryFragment category_fragment=(CategoryFragment)
	         getSupportFragmentManager().findFragmentById(R.id.container);
	FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
	 ft2.setCustomAnimations(R.anim.fragment_move_in2, R.anim.fragment_move_out2);
	 ft2.remove(category_fragment);
	 ft2.setCustomAnimations(R.anim.fragment_move_in, R.anim.fragment_move_out);
	 ft2.remove(inputkey_fragment); 
	 //ft2.addToBackStack(null); 
	 ft2.commit();
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
/*
 * check the status of network
 */
public static boolean isNetworkConnected(Context context) { 
    
	if (context != null) { 
              ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
              .getSystemService(Context.CONNECTIVITY_SERVICE); 
               NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
         if (mNetworkInfo != null) { 
                   return mNetworkInfo.isAvailable(); 
              } 
      } 
            return false; 
} 
 /*
  * used to sleep for 1s	
  */
private void sleepfor1s() {
		try {  Thread.sleep(200);   } 
		catch (InterruptedException e) {  e.printStackTrace();  }
	}
/*
 * the following is about chartview
 */

public void showPieChart(String[] categoryString, double[] valueDouble){
	LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
	layout.removeAllViews();
	
	
	final CategorySeries mySeries = buildCategoryDataset("", categoryString, valueDouble);
	
	int[] colorTmp = new int[categoryString.length];
	for(int i=0;i<categoryString.length;i++){
		colorTmp[i] = COLORS[i%5];
	}
	final DefaultRenderer myRenderer = buildCategoryRenderer(colorTmp);
	
	myRenderer.setChartTitle("Category Sum of Cost from " + start_date.substring(0,4) + "-" 
			+ start_date.substring(4,6) + "-" + start_date.substring(6) + " to " 
			+ end_date.substring(0,4) + "-" + end_date.substring(4,6) + "-" + end_date.substring(6));
	myRenderer.setChartTitleTextSize(50);
	myRenderer.setLabelsTextSize(30);
	myRenderer.setLegendTextSize(30);
	myRenderer.setLabelsColor(Color.BLACK);
	myRenderer.setZoomEnabled(false);
	myRenderer.setShowLabels(true);
	myRenderer.setDisplayValues(true);
	myRenderer.setPanEnabled(false);
	myRenderer.setShowLegend(true);
	
	myChartView = ChartFactory.getPieChartView(this, mySeries, myRenderer);
	myChartView.repaint();
	layout.addView(myChartView);
	
	myRenderer.setClickEnabled(true);
	myChartView.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			SeriesSelection mySeriesSelection = myChartView.getCurrentSeriesAndPoint();
			if (mySeriesSelection == null) {
				Toast.makeText(CostBox.this, "No item is selected", Toast.LENGTH_SHORT).show();
			} else {
				for (int i=0;i<mySeries.getItemCount();i++){
					myRenderer.getSeriesRendererAt(i).setHighlighted(i == mySeriesSelection.getPointIndex());
				}
				myChartView.repaint();
				Toast.makeText(CostBox.this, "The cost of " 
						+ mySeries.getCategory(mySeriesSelection.getPointIndex()) 
						+ " is " + mySeriesSelection.getValue() + ".", Toast.LENGTH_SHORT).show();;
			}
		}
	});
}

public void showDoughnutChart(String[] category, double[] value){
	LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
	layout.removeAllViews();
	
	List<String[]> categories = new ArrayList<String[]>();
	List<double[]> values = new ArrayList<double[]>();
	categories.add(category);
	values.add(value);
	
	final MultipleCategorySeries mySeries = buildMultipleCategoryDataset("", categories, values);
	
	int[] colorTmp = new int[category.length];
	for(int i=0;i<category.length;i++){
		colorTmp[i] = COLORS[i%5];
	}
	final DefaultRenderer myRenderer = buildCategoryRenderer(colorTmp);
	
	myRenderer.setChartTitle("Category Sum of Cost from " + start_date.substring(0,4) + "-" 
			+ start_date.substring(4,6) + "-" + start_date.substring(6) + " to " 
			+ end_date.substring(0,4) + "-" + end_date.substring(4,6) + "-" + end_date.substring(6));
	myRenderer.setChartTitleTextSize(50);
	myRenderer.setLabelsTextSize(30);
	myRenderer.setLegendTextSize(30);
	myRenderer.setLabelsColor(Color.BLACK);
	myRenderer.setZoomEnabled(false);
	myRenderer.setShowLabels(true);
	myRenderer.setDisplayValues(true);
	myRenderer.setPanEnabled(false);
	myRenderer.setShowLegend(false);
	
	myChartView = ChartFactory.getDoughnutChartView(this, mySeries, myRenderer);
	myChartView.repaint();
	layout.addView(myChartView);
}


public void showLineChart(final String[] titles, final List<double[]> xvalue, final List<double[]> yvalue){
	LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
	layout.removeAllViews();
	
	
	XYMultipleSeriesDataset myDataset = buildDataset(titles, xvalue, yvalue);
	XYMultipleSeriesRenderer myRenderer = buildRenderer(colors, styles);

//	double[] x_tmp = xvalue.get(0);
//	double x_min=999999999, x_max = -999999999;
//	for(int i=0;i<x_tmp.length;i++){
//		if(x_tmp[i]<x_min) x_min=x_tmp[i];
//		if(x_tmp[i]>x_max) x_max=x_tmp[i];
//	}
	
	double[] y_tmp = yvalue.get(0);
	double y_min=999999999, y_max = -999999999;
	for(int i=0;i<y_tmp.length;i++){
		if(y_tmp[i]<y_min) y_min=y_tmp[i];
		if(y_tmp[i]>y_max) y_max=y_tmp[i];
	}
	
	myRenderer.setChartTitle("Sum of daily cost in " + monthChoice[choice]);
	myRenderer.setChartTitleTextSize(50);
	myRenderer.setLabelsTextSize(30);
	myRenderer.setLegendTextSize(30);
	myRenderer.setPointSize(5);
	myRenderer.setShowLegend(false);

	for (int i = 0; i < 31; i++) {
		myRenderer.addXTextLabel(i+1, ""+(i+1));
	}
	
	myRenderer.setXLabels(0);
	myRenderer.setXAxisMin(0);
	myRenderer.setXAxisMax(32);

	myRenderer.setYAxisMin(y_min*0.8);
	myRenderer.setYAxisMax(y_max*1.2);
	
	myRenderer.setShowGrid(true);
	myRenderer.setZoomEnabled(false);
	myRenderer.setXLabelsAlign(Align.RIGHT);
	myRenderer.setYLabelsAlign(Align.RIGHT);
	myRenderer.setZoomButtonsVisible(false);
	myRenderer.setPanEnabled(false);
	
	myRenderer.setAxesColor(Color.BLACK);
	myRenderer.setLabelsColor(Color.BLACK);
	myRenderer.setBackgroundColor(Color.WHITE);
	myRenderer.setApplyBackgroundColor(true);
	myRenderer.setMarginsColor(Color.WHITE);
	
	myRenderer.setMargins(new int[] {30,200,30,30});
	
	myChartView = ChartFactory.getLineChartView(this, myDataset, myRenderer);
	myChartView.repaint();

	layout.addView(myChartView);

	
	myRenderer.setClickEnabled(true);
	myRenderer.setSelectableBuffer(100);
	myChartView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			SeriesSelection mySeriesSelection = myChartView.getCurrentSeriesAndPoint();
			if (mySeriesSelection == null) {
				Toast.makeText(CostBox.this, "No chart element", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(CostBox.this, "The total cost of day " + mySeriesSelection.getXValue()
						+ " in " +  monthChoice[choice] + " is " + mySeriesSelection.getValue() + ".",
						Toast.LENGTH_SHORT).show();;
			}
		}
	});
}

/*
 * The following methods are used to build the chart more quickly and conveniently.
 */

/*
 * The following three methods are used for pie chart.
 */
protected CategorySeries buildCategoryDataset(String title, String[] categories, double[] values) {
	CategorySeries series = new CategorySeries(title);
	int length = categories.length;
	for (int i = 0; i < length; i++){
		series.add(categories[i], values[i]);
	}
	return series;
}

protected MultipleCategorySeries buildMultipleCategoryDataset(String title,
		List<String[]> categories, List<double[]> values) {
	MultipleCategorySeries series = new MultipleCategorySeries(title);
	int k=0;
	for (double[] value : values) {
		series.add(categories.get(k), value);
		k++;
	}
	return series;
}

protected DefaultRenderer buildCategoryRenderer(int[] colors) {
	DefaultRenderer renderer = new DefaultRenderer();
	for (int color : colors) {
		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		r.setColor(color);
		renderer.addSeriesRenderer(r);
	}
	return renderer;
}


/*
 * The following methods are used for line charts
 */

protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues, List<double[]> yValues) {
	XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	addXYSeries(dataset, titles, xValues, yValues, 0);
	return dataset;
}

public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,List<double[]> yValues, int scale) {
	int length = titles.length;
	for (int i = 0; i < length; i++) {
		XYSeries series = new XYSeries(titles[i], scale);
		double[] xV = xValues.get(i);
		double[] yV = yValues.get(i);
		int seriesLength = xV.length;
		for (int k = 0; k < seriesLength; k++) {
			series.add(xV[k], yV[k]);
		}
		dataset.addSeries(series);
	}
}

protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
	XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	setRenderer(renderer, colors, styles);
	return renderer;
}

protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
	int length = colors.length;
	for (int i = 0; i < length; i++) {
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(colors[i]);
		r.setPointStyle(styles[i]);
		r.setFillPoints(true);
		r.setDisplayChartValues(true);
		r.setDisplayChartValuesDistance(40);
		r.setChartValuesTextSize(30);
		renderer.addSeriesRenderer(r);
	}
}
}
/*
 * the following is about charview
*/
