package com.example.costbox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CategoryDB extends SQLiteOpenHelper{
	private final static int DB_VERSION = 1;
	private final static String DBNAME = "category_database";
	private final static String TABLE_NAME = "category_table";
	private final static String FIELD_id = "_id";
	private final static String CATEGORY_TITLE = "title";
	private final static String CHOSEN = "flag_chosen";
	
	private static Context mcontext; 
	public CategoryDB(Context context)
	{ 
		super(context,DBNAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " 
			      + TABLE_NAME  + " ("+ FIELD_id +
			      " INTEGER primary key autoincrement, " + CATEGORY_TITLE + " text, "
			      +CHOSEN+" integer not null default '0' )";  
	    db.execSQL(sql);
	    // If the value of the chosen is 0, that represent that the picture is shown in the Gridview to user.
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub   
		String sql = "DROP TABLE IF EXISTS " 
			      + TABLE_NAME; db.execSQL(sql);
			      onCreate(db); 
	}
	
	public Cursor select()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select "+CATEGORY_TITLE + " , "+ CHOSEN +" from "+TABLE_NAME, null);
		return cursor;
	}
	public int count()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select *" +" from "+TABLE_NAME, null);
		int c = cursor.getCount();
		return c;
	}
	
	public long insert(String text)
	{
		// adding the default type of categories in.
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues row_value = new ContentValues();
		row_value.put(CATEGORY_TITLE, text); 
		row_value.put(CHOSEN, 0); 
		long row = db.insert(TABLE_NAME, null, row_value);
		return row;
	}
	
	public void set_Up()
	{
		String[] cate = new String[]{"food","drink","book","print","trans","cloths","play",
				"house","cure","phone","barber","date","gift","others"};
		// All the default category name
		for (int i =0;i<cate.length; i++)
		{
			insert(cate[i]);
		}
	}
	
	
	
	public void modify_visibility(String cate_name, int visibility)
	{
		// This is open for the user to decide whether to
		// let the icon appear in the above fragment.
		// if visibility = 0 ,it is then visible. 
		// equals 1, it is then invisible.
		
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues(); 
		args.put(CHOSEN, visibility);
		db.update(TABLE_NAME, args, "title= \"" + cate_name +"\"", null);

	}public int check_visibility(String cate_name)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String command = "SELECT "+ CHOSEN + " FROM " + TABLE_NAME + 
				" WHERE " + CATEGORY_TITLE + " = \"" + cate_name + "\""; 
		Cursor cursor = db.rawQuery(command,null);
		cursor.moveToFirst();
		int result = cursor.getInt(0);
		return result;
	}
	public void add_new_cate(String newCate_name)
	{
		// the new inserted item is set default to be visible.
		insert(newCate_name);
		modify_visibility(newCate_name, 0);
	}
	public void delete_cate(String cate_name)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, "title= \""+cate_name +"\"", null);
	}
	public int check_category(String cate_name)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String command = "SELECT *" + " FROM " + TABLE_NAME + 
				" WHERE " + CATEGORY_TITLE + " = \"" + cate_name + "\"";
		Cursor cursor = db.rawQuery(command, null);
		cursor.moveToFirst();
		int count = cursor.getCount();
		if(count > 0) return 1 ;
		else return 0;
	}
	
}
