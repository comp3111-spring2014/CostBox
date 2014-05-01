package com.example.costbox.preference;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.costbox.CategoryDB;
import com.example.costbox.CategoryGridAdapter;
import com.example.costbox.CategoryGridInfo;
import com.example.costbox.CostBox;
import com.example.costbox.CostDB;
import com.example.costbox.CostDetail;
import com.example.costbox.MyPreference;
import com.example.costbox.R;
import com.example.costbox.widget.ScrollLayout;

public class CategoryEdit extends Activity {
	
	private static GridView gridView;
	private static ImageView plusView;
	private LinearLayout layoutBottom;
	private ScrollLayout curPage;
	private int PageCount;
	private int PageCurrent;
	private int gridID = -1;
	private static final float PAGE_SIZE = 16.0f;
	private List<CategoryEditGridInfo> lstDate = new ArrayList<CategoryEditGridInfo>();  //important
	private CategoryEditGridInfo info; 
	private ImageView imgCur;
	CategoryEditAdapter newadapter;
	
	
	CategoryDB myCategoryDB;
	CostDB myCostDB;
	private static String cate_name;
	
	private static View currentItemView;
	private ActionMode mActionMode;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categoryedit);
		ActionBar actionBar = getActionBar();  
		actionBar.setDisplayHomeAsUpEnabled(true); 
		
		myCategoryDB = new CategoryDB(getBaseContext());
		myCostDB= new CostDB(getBaseContext());

		
		findViews();
		setGrid();
		setCurPage(0);
		curPage.setPageListener(new ScrollLayout.PageListener() {

			@Override
			public void page(int page) {
				setCurPage(page);
			}
		});
	}
	private void findViews() {
		curPage = (ScrollLayout)this.findViewById (R.id.scr);
		layoutBottom = (LinearLayout)this.findViewById(R.id.layout_scr_bottom);
		curPage.getLayoutParams().height = this.getWindowManager().getDefaultDisplay().getHeight() *4/5  ;
		if(lstDate.isEmpty())
		{ testAdding();}   
	}
	
	public void add_by_db(String cate_name)
	{
		// This function requires that the picture name in R.drawable stay the same with 
		info = new CategoryEditGridInfo();
		try
		{
			int temp_id = getResources().getIdentifier(cate_name, "drawable", "com.example.costbox"); 
			info.setCategoryIcon(getResources().getDrawable(temp_id));
		}
		catch(Exception e){

			info.setCategoryIcon(getResources().getDrawable(R.drawable.customcategory));
		};
		int vis=myCategoryDB.check_visibility(cate_name);
		if(vis==0)info.setCheck(true);
		else info.setCheck(false);
		info.setCategoryName(cate_name);
		lstDate.add(info);
	}
	
	public void testAdding() {
		//testing adding
		
				
				// maybe remains problem 
				Cursor cursor = myCategoryDB.select();
				//only set_Up for the first time
				if(myCategoryDB.count()==0)
					myCategoryDB.set_Up();

				cursor.moveToFirst();
				while(!cursor.isAfterLast()  )
				{
					add_by_db(cursor.getString(0));
					cursor.moveToNext();
				}
		
	}
	private void setGrid() {

		PageCount = (int) Math.ceil(lstDate.size() / PAGE_SIZE) ;
		if (gridView != null) {
			curPage.removeAllViews();
		}
		for (int i = 0; i < PageCount; i++) {
			gridView = new GridView(this);
			newadapter= new CategoryEditAdapter(this, lstDate, i);
			gridView.setAdapter(newadapter);
			gridView.setNumColumns(4);
			gridView.setHorizontalSpacing(8);
			gridView.setVerticalSpacing(8);
			gridView.setSelector(R.drawable.alphe);
			gridView.setOnItemLongClickListener(gridListener);
		    
			curPage.addView(gridView);
		}
	}
	public void setCurPage(int page) {
		layoutBottom.removeAllViews();
		for (int i = 0; i < PageCount; i++) {
			imgCur = new ImageView(this);
			imgCur.setBackgroundResource(R.drawable.bg_img_item);
			imgCur.setId(i);		
			if (imgCur.getId() == page) {
				imgCur.setBackgroundResource(R.drawable.bg_img_item_true);
			}
			layoutBottom.addView(imgCur);
		}
	}
	

	
	public OnItemLongClickListener gridListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			PageCurrent = curPage.getCurScreen();
			gridID = position + PageCurrent * 16;
			cate_name=lstDate.get(gridID).getCategoryName();
			
			if(currentItemView!=null) 
		       { currentItemView.setBackgroundResource(R.drawable.alphe);}
		        currentItemView=view;
             //set new color
		        currentItemView.setBackgroundResource(R.drawable.commentbg);
				mActionMode = CategoryEdit.this
						.startActionMode(mActionModeCallback);
			// TODO Auto-generated method stub
			return true;
		}

	};
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// Respond to clicks on the actions in the CAB
			switch (item.getItemId()) {
			case R.id.category_delete:
			
			String[] cate = new String[]{"food","drink","book","print","trans","cloths","play",
				"house","cure","phone","barber","date","gift","others"};
			int k=0;
			for(int i=0;i<cate.length;i++){
				if(cate_name.equals(cate[i]))
				{
					AlertDialog.Builder dg = new Builder(CategoryEdit.this);
					dg.setMessage("Sorry, "+cate_name+" is a default item.");
					dg.setPositiveButton("Alright", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int whichButton) {
							// TODO Auto-generated method stub
						}
					})
					.show();
					k=1;
					break;
				}
				
				
			}
			if(k==0)
			{
				if (myCostDB.detect_existence(cate_name) == 1)    //bug in function
				{
					AlertDialog.Builder dg = new Builder(CategoryEdit.this);
				dg.setMessage("You have some records of "+cate_name+", continue to delete them all?");
				dg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// TODO Auto-generated method stub
						myCostDB.delete_item(cate_name);
						myCategoryDB.delete_cate(cate_name);
						lstDate.clear();
						testAdding();
						setGrid();
						setCurPage(0);
						 Toast.makeText( CategoryEdit.this, "Category deleted", Toast.LENGTH_LONG).show(); 		
					}
				})
				.setNegativeButton("No",new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// TODO Auto-generated method stub

					}
				})
				.show();
				}
				else 
				{
					myCostDB.delete_item(cate_name);
					myCategoryDB.delete_cate(cate_name);
					lstDate.clear();
					testAdding();
					setGrid();
					setCurPage(0);
					 Toast.makeText( CategoryEdit.this, "Category deleted", Toast.LENGTH_LONG).show(); 		
				}

			  }
				//Toast.makeText(CategoryEdit.this, cursor.getCount()+"", Toast.LENGTH_LONG).show();
				
				newadapter.notifyDataSetChanged();
				// need to delete the corresponding real items in costbox database
				// you can first alert the user
				//adapter to finish the refresh work.
		       
				mode.finish(); 
				return true;

								
			default:
				return false;
			}
		}
	  	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.categoryedit_menu_list, menu);
				currentItemView.setBackgroundResource(R.drawable.commentbg);
				return true;
			}
	  	    public void onDestroyActionMode(ActionMode mode) {
	  		  currentItemView.setBackgroundResource(R.drawable.alphe);  //return the color back
			}
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
		};  
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		        case android.R.id.home:
		            // app icon in action bar clicked; go home
		            Intent intent =this.getIntent();
		            CategoryEdit.this.setResult(RESULT_OK, intent);
		            this.finish();
		            overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
		            return true;
		        case R.id.category_adding:
					final EditText cate_name_text = new EditText(CategoryEdit.this);
		        	AlertDialog.Builder builder= new AlertDialog.Builder(CategoryEdit.this);
		        	builder.setTitle("Category Name").setView(cate_name_text);
		        	
		        	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		            {
		                @Override
		                public void onClick(DialogInterface dialog, int which)
		                {
		                	String cate_name = cate_name_text.getText().toString();
		                	if(myCategoryDB.check_category(cate_name)==1)
		                	{
		                		//preserve
		                	}
		                	else{
		                	myCategoryDB.add_new_cate(cate_name);
		                	add_by_db(cate_name);
		                	setGrid();
		            		setCurPage(0);
		                	//testAdding();
		                	//newadapter.notifyDataSetChanged();
		                	Toast.makeText(CategoryEdit.this, "New Category Added.", Toast.LENGTH_SHORT).show();
		                	}
		                	} 
		            });
		        	
		        	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		                {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which)
		                    {
		                    	Toast.makeText(CategoryEdit.this, "Add Request Canceled.", Toast.LENGTH_SHORT).show();
		                    }
		                });
		        	
		        	builder.show();
		        	return true;
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		}
		
	    // used for action bar implementation 
		@Override
		  public boolean onCreateOptionsMenu(Menu menu) {
			 super.onCreateOptionsMenu(menu); 
			 getMenuInflater().inflate(R.menu.categoryedit, menu);
		     return true; 
		  }

		 
		
}
