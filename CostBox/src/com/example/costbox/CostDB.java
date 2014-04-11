package com.example.costbox; 
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CostDB extends SQLiteOpenHelper
{
  private final static String DATABASE_NAME = "Cost_db12";
  private final static int DATABASE_VERSION = 3;
  public static String TABLE_NAME = "cost_table"; 
  public final static String FIELD_id = "_id";
  public final static String PIC_ADDR="pic_address";
  public final static String CATEGORY="cost_category";
  public final static String FIELD_TEXT = "item_text"; 
  public final static String COST_MONEY = "cost_text";
  public final static String COMM= "comment_text";
  public final static String DATE = "created_date";
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
		      +COST_MONEY+" integer not null default '0')";   // then here the cost money attributes becomes integer type. 
    db.execSQL(sql); 
    } 
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  { 
    String sql = "DROP TABLE IF EXISTS " 
      + TABLE_NAME; db.execSQL(sql);
      onCreate(db); 
   }

  public Cursor select() 
  { 
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
    return cursor;
    }
  public long insert(String text1, int money,String text3,String text4 , int text5 )
  { 
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put(FIELD_TEXT, text1);
    cv.put(COST_MONEY, money);
    cv.put(COMM, text3);
    cv.put(PIC_ADDR, text4);
    cv.put(DATE, text5);
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
  public void update(int id, String text1, int money,String text3,String text4, int text5) 
  { 
    SQLiteDatabase db = this.getWritableDatabase(); 
    String where = FIELD_id + " = ?";
    String[] whereValue = { Integer.toString(id) }; 
    
    ContentValues cv = new ContentValues(); 
    cv.put(FIELD_TEXT, text1); 
    cv.put(COST_MONEY, money);
    cv.put(COMM, text3);
    cv.put(PIC_ADDR,text4);
    cv.put(DATE, text5); 
    db.update(TABLE_NAME, cv, where, whereValue); 
   }
  
  
  // newly added function for the  test function of sum and category_sum. 
  
  public int sum_up()
  {
	  SQLiteDatabase db = this.getReadableDatabase();
	  String command = "SELECT SUM("+ COST_MONEY +") FROM " + TABLE_NAME ;
	  Cursor cursor  = db.rawQuery(command,null);
	  cursor.moveToFirst();  // to move the cursor to the top to access the sum. 
	  return cursor.getInt(0);  // I suppose it return the sum result, but I don't know the position of the _id 
  }
 
  // This method is just for the testing
  public int date_display(int start_date, int end_date)
  {
	  SQLiteDatabase db = this.getReadableDatabase(); 
	  String command = "SELECT SUM("+ COST_MONEY +") FROM " + TABLE_NAME + " WHERE " +DATE + ">"+ start_date+" AND " +DATE +"<" + end_date ;  
	  Cursor cursor = db.rawQuery(command, null); 
	  cursor.moveToFirst();
	  int answer = cursor.getInt(0);
	  cursor.close();
	  return answer ; 
  }
  
  
  public Map<String, Integer> sum_of_catogory()
  {
	  Map<String,Integer> cate_sum = new HashMap< String,Integer >(200);
	 
	  SQLiteDatabase db = this.getReadableDatabase(); 
	  String command = "SELECT " + FIELD_TEXT + " , SUM("+ COST_MONEY + ") FROM " + TABLE_NAME + " GROUP BY " + FIELD_TEXT; 
	  Cursor cursor = db.rawQuery(command, null);
	  cursor.moveToFirst();
	  
	  while(!cursor.isAfterLast()) 
	  {
		  Integer buffer; 
		  buffer = Integer.valueOf(cursor.getInt(1));// the following line is not usable. This line recommended by Eclipse
		  //buffer = new Integer(cursor.getInt(1));  // The incentive to introduce buffer is that, 
		  										   // Java's Hashmap don't support original type(like int). So we need to 
		  										   // Encapsulate what we got from the cursor as a int type to be Integer.
		  cate_sum.put(cursor.getString(0), buffer);
		  cursor.moveToNext();
	  }
	  
	  return cate_sum; 
  } 
  
}




// Supplement : 

//1. what is the category's mean ? In my implemented method, the category refers to "type of shopping", 
// but in this database definition the fidld_text represent the same meaning.

//2.Attention about the DATABASE_VERSION, since I create a new column in the database, but the original will not be updated even you delete
// all the tuples, since the table itself existed there. The only strategy is to delete the original one and create a new one.
//Here's the tricky part: Before my debugging, the constant, database_version is 1. I have to change it to 2, or the Onupgrade function will 
// distinguish it between the "1" version, so the new database will not be created in that case.