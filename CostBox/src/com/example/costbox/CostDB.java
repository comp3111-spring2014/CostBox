package com.example.costbox; 
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class CostDB extends SQLiteOpenHelper
{
  private final static String DATABASE_NAME = "Cost_db13";
  private final static int DATABASE_VERSION = 4;
  public static String TABLE_NAME = "cost_table"; 
  public final static String FIELD_id = "_id";
  public final static String PIC_ADDR="pic_address";
  public final static String CATEGORY="cost_category";
  public final static String FIELD_TEXT = "item_text"; 
  public final static String COST_MONEY = "cost_text";
  public final static String COMM= "comment_text";
  public final static String DATE = "created_date";
  public final static String TIME = "adding_time";
  public final static String TIME_TO_ORDER= "adding_order";
  /*
  public CostDB(Context context, String Date) 
  { 
    super(context, DATABASE_NAME, null, DATABASE_VERSION); 
    TABLE_NAME=Date;  
  }
  */
  public CostDB(Context context) 
  { 
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
  
  @Override 
  /*item list sorting
  _id + category + title + comment + picture address + cost_money
  */
  public void onCreate(SQLiteDatabase db)
  {
   /* String sql = "CREATE TABLE " 
      + TABLE_NAME + " (" + FIELD_id +
      " INTEGER primary key autoincrement, "
      + " " + CATEGORY+" text, "+FIELD_TEXT +" text, "+COMM+" text, "+PIC_ADDR +" text, "+COST_MONEY+" text)";*/
	  String sql = "CREATE TABLE " 
		      + TABLE_NAME + " (" + FIELD_id +
		      " INTEGER primary key autoincrement, " + DATE + " integer, "
		      + " " + CATEGORY+" text, "+FIELD_TEXT +" text, "+COMM+" text, "+PIC_ADDR +" text, "
		      +COST_MONEY+" REAL not null default '0.0', "+TIME+" text, "+TIME_TO_ORDER+" text)";   // then here the cost money attributes becomes integer type. 
    db.execSQL(sql); 
    } 
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  { 
    String sql = "DROP TABLE IF EXISTS " 
      + TABLE_NAME; db.execSQL(sql);
      onCreate(db); 
   }

  public Cursor select(String Date) 
  { 
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.query(TABLE_NAME, null,DATE+"='"+Date+"'", null, null, null, TIME_TO_ORDER+" DESC");
    cursor.moveToFirst();
    return cursor;
   }
  
  public Cursor selectQuery(String Query) 
  { 
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.query(TABLE_NAME, null,CATEGORY+"='"+Query+"'", null, null, null, DATE+" DESC");
    return cursor;
   }
  
  public long insert(String catemark,String text1, double money,String text3,String text4 , int text5,String text6,String text7)
  { 
    SQLiteDatabase db = this.getWritableDatabase();
    
    ContentValues cv = new ContentValues();
    cv.put(CATEGORY, catemark);
    cv.put(FIELD_TEXT, text1);
    cv.put(COST_MONEY, money);
    cv.put(COMM, text3);
    cv.put(PIC_ADDR, text4);
    cv.put(DATE, text5);
    cv.put(TIME, text6);
    cv.put(TIME_TO_ORDER, text7);
    long row = db.insert(TABLE_NAME, null, cv);  // insert some value into the database, but require a contentValues type to achieve.
    return row; 
    }
  public void delete(int id) 
  { 
    SQLiteDatabase db = this.getWritableDatabase();
    String where = FIELD_id + " = ?"; 
    String[] whereValue = { Integer.toString(id) };
    db.delete(TABLE_NAME, where, whereValue); 
    } 
  public void update(int id,String catemark, String text1, double money,String text3,String text4, int text5,String text6,String text7) 
  { 
    SQLiteDatabase db = this.getWritableDatabase(); 
    String where = FIELD_id + " = ?";
    String[] whereValue = { Integer.toString(id) }; 
    
    ContentValues cv = new ContentValues(); 
    cv.put(FIELD_TEXT, text1); 
    cv.put(CATEGORY, catemark);
    cv.put(COST_MONEY, money);
    cv.put(COMM, text3);
    cv.put(PIC_ADDR,text4);
    cv.put(DATE, text5); 
    cv.put(TIME, text6);
    cv.put(TIME_TO_ORDER, text7);
    db.update(TABLE_NAME, cv, where, whereValue); 
   }
  
  
  // newly added function for the  test function of sum and category_sum. 
  

  public double sum_up(String Date)
  {
	  SQLiteDatabase db = this.getReadableDatabase();
	  String command = "SELECT SUM("+ COST_MONEY +") FROM " + TABLE_NAME+" WHERE "+DATE+"='"+Date+"'";
	  Cursor cursor  = db.rawQuery(command,null);
	  cursor.moveToFirst();  // to move the cursor to the top to access the sum. 
	  String temp_result = cursor.getDouble(0) + "";  // I suppose it return the sum result, but I don't know the position of the _id 
	  int index = temp_result.lastIndexOf("."); 
	  if (index >= 0)
	  	temp_result = temp_result.substring(0,index+2); 
	  return Double.parseDouble( temp_result) ; 
  }
 
  // This method is just for the testing
  public double date_display(int start_date, int end_date)
  {
	  SQLiteDatabase db = this.getReadableDatabase(); 
	  String command = "SELECT SUM("+ COST_MONEY +") FROM " + TABLE_NAME + " WHERE " +DATE + ">"+ start_date+" AND " +DATE +"<" + end_date ;  
	  Cursor cursor = db.rawQuery(command, null); 
	  cursor.moveToFirst();
	  double answer = cursor.getDouble(0);
	  cursor.close();
	  return answer ; 
  }
  
  public int detect_existence(String cate_name)
  {
	  SQLiteDatabase db = this.getReadableDatabase();
	  String command = "SELECT *"+ " FROM " + TABLE_NAME + " WHERE " + CATEGORY + " = \"" +cate_name + "\"";
	  Cursor cursor = db.rawQuery(command,null);
	  cursor.moveToFirst();
	  int count = cursor.getCount();
		if(count > 0) return 1 ;
		else return 2;

  }
  
  public void delete_item(String cate_name)
  {
	  SQLiteDatabase db = this.getReadableDatabase(); 
	  db.delete(TABLE_NAME, "cost_category= \""+cate_name +"\"", null);
  }
  
  
  public Map<String, Double> sum_Of_catogory_Of_days(int start_date, int end_date)
  {
	  Map<String,Double> cate_sum = new HashMap< String,Double >(200);
	 
	  SQLiteDatabase db = this.getReadableDatabase(); 
	  String command = "SELECT " + CATEGORY + " , SUM("+ COST_MONEY + ") FROM " + TABLE_NAME + " WHERE "+DATE + " >= "+ start_date+" AND " +DATE +" <= " + end_date
			  + " GROUP BY " + CATEGORY ;
	  Cursor cursor = db.rawQuery(command, null);
	  cursor.moveToFirst();
	  
	  while(!cursor.isAfterLast()) 
	  {
		  Double buffer; 
		  buffer = Double.valueOf(cursor.getDouble(1));// the following line is not usable. This line recommended by Eclipse
		  //buffer = new Integer(cursor.getInt(1));  // The incentive to introduce buffer is that, 
		  										   // Java's Hashmap don't support original type(like int). So we need to 
		  										   // Encapsulate what we got from the cursor as a int type to be Integer.
		  cate_sum.put(cursor.getString(0), buffer);
		  cursor.moveToNext();
	  }
	  
	  return cate_sum; 
  } 
  
  
  public Map<Integer, Double> sum_up_Of_day(int date)
  {
	  Map<Integer,Double> sum_of_a_day = new HashMap< Integer,Double >(200);
	  
	  SQLiteDatabase db = this.getReadableDatabase();
	  String command = "SELECT SUM("+ COST_MONEY +") FROM " + TABLE_NAME  + " WHERE " +DATE + " = \"" + date + "\"" ;
	  Cursor cursor  = db.rawQuery(command,null);
	  cursor.moveToFirst();  // to move the cursor to the top to access the sum. 
	  
	  while(!cursor.isAfterLast()) 
	  {
		  Double buffer; 
		  buffer = Double.valueOf(cursor.getDouble(1));// the following line is not usable. This line recommended by Eclipse
		  //buffer = new Integer(cursor.getInt(1));  // The incentive to introduce buffer is that, 
		  										   // Java's Hashmap don't support original type(like int). So we need to 
		  										   // Encapsulate what we got from the cursor as a int type to be Integer.
		  sum_of_a_day.put(date, buffer);
		  cursor.moveToNext();
	  }
	  return sum_of_a_day;
  }
  
  public Map<Integer, Double> sum_up_Of_days(int start_date, int end_date )
  {
    Map<Integer,Double> sum_of_single_day = new HashMap< Integer,Double >(200);
    
    SQLiteDatabase db = this.getReadableDatabase();
    String command = "SELECT " + DATE + " , SUM("+ COST_MONEY + ") FROM " + TABLE_NAME + " WHERE "+DATE + " >= "+ start_date+" AND " 
    		+DATE +" <= " + end_date + " GROUP BY " + DATE ;
    
    Cursor cursor = db.rawQuery(command, null);
    cursor.moveToFirst();
    
    while(!cursor.isAfterLast()) 
    {
      Double buffer; 
      buffer = Double.valueOf(cursor.getDouble(1));// the following line is not usable. This line recommended by Eclipse
      //buffer = new Integer(cursor.getInt(1));  // The incentive to introduce buffer is that, 
                             // Java's Hashmap don't support original type(like int). So we need to 
                             // Encapsulate what we got from the cursor as a int type to be Integer.
      sum_of_single_day.put(cursor.getInt(0), buffer);
      cursor.moveToNext();
    }
    return sum_of_single_day;
  }

}
