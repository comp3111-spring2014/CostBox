package com.example.costbox;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.costbox.widget.KeyboardUtil;
import com.example.costbox.widget.ScrollLayout;

public class CategoryFragment extends Fragment 
implements com.example.costbox.widget.KeyboardUtil.keyboardOnReturnListener{
	OnReturnListener mcallback;
	private Context ctx;
	private Activity act;
	private View view;
	EditText Money_total;
	//grid
	private int PageCount;
	private int PageCurrent;
	private int gridID = -1;
	private static final float PAGE_SIZE = 10.0f;
	private GridView gridView;
	private ScrollLayout curPage;
	private LinearLayout layoutBottom;
	private List<CategoryGridInfo> lstDate = new ArrayList<CategoryGridInfo>();  //important
	private CategoryGridInfo info; 
	private ImageView imgCur;
	public static String chosen_cate="others";
	CategoryDB myCategoryDB;
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public interface OnReturnListener {
        public void onReturnFromFragment1(int state,String reserve);
    }

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_category, container, false);
		
		myCategoryDB = new CategoryDB(getActivity());
		Money_total = (EditText) view.findViewById(R.id.Money_total);
		ctx = this.getActivity();
		act = this.getActivity();
		Money_total.setOnTouchListener(new EditText.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				int inputback = Money_total.getInputType();
				Money_total.setInputType(InputType.TYPE_NULL);
				new KeyboardUtil(act, ctx, Money_total,1,CategoryFragment.this).showKeyboard();
				Money_total.setInputType(inputback);
				Money_total.setSelection(Money_total.getText().length());// cursor to the end
				
				return false;
				
			}
		});
		//grid
		
		findViews();
		setGrid();
		setCurPage(0);
		curPage.setPageListener(new ScrollLayout.PageListener() {

			@Override
			public void page(int page) {
				setCurPage(page);
			}
		});
		
		return view;
	}
/*
 * (non-Javadoc)
 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
 */
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mcallback = (OnReturnListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CategoryFragment.OnReturnListener");
        }
    }
	@Override
	public void onReturnFromKeyboard(int state) {
		if(state==4)
		{
			mcallback.onReturnFromFragment1(4,null);
		}
		else if(state==1)
		{   
			mcallback.onReturnFromFragment1(1, null);
		  
		}
		else if(state==2)//delete it
		{
			mcallback.onReturnFromFragment1(2,null);
		}
		
		
	}
	/*
	 * find the view of the grid and buttom layout
	 * adding the item of each gridblock 
	 * communicate with category database in the future
	 * load the icon and name
	 */
	//change the height but didn't test pay attention!
	private void findViews() {
		curPage = (ScrollLayout) view.findViewById (R.id.scr);
		layoutBottom = (LinearLayout) view.findViewById(R.id.layout_scr_bottom);
		curPage.getLayoutParams().height = getActivity().getWindowManager().getDefaultDisplay().getHeight() * 1 / 4;
		if(lstDate.isEmpty())
		{ testAdding();}

	}
/*
 * setting the grid frame and structure
 * column = 5
 * use lstDate as the adapter ( info --> 1stDate<arraylist of info> --> grid block)
 */
	public void add_by_db(String cate_name)
	{
		// This function requires that the picture name in R.drawable stay the same with 
		info = new CategoryGridInfo();
		
		try
		{
			int temp_id = getResources().getIdentifier(cate_name, "drawable", "com.example.costbox"); 
			info.setCategoryIcon(getResources().getDrawable(temp_id));
		}
		catch(Exception e){

			//int temp_id = getResources().getIdentifier("book", "drawable", "com.example.costbox"); 
			info.setCategoryIcon(getResources().getDrawable(R.drawable.customcategory));
		};
		
		//String cap_cate_name = cate_name.toUpperCase();
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
				{   if(cursor.getString(1).equals("0"))  add_by_db(cursor.getString(0));
					cursor.moveToNext();
				}
		
	}

	private void setGrid() {

		PageCount = (int) Math.ceil(lstDate.size() / PAGE_SIZE);
		if (gridView != null) {
			curPage.removeAllViews();
		}
		for (int i = 0; i < PageCount; i++) {
			gridView = new GridView(getActivity());
			gridView.setAdapter(new CategoryGridAdapter(getActivity(), lstDate, i));
			gridView.setNumColumns(5);
			gridView.setHorizontalSpacing(8);
			gridView.setVerticalSpacing(8);
			gridView.setSelector(R.drawable.commentbg);
			gridView.setOnItemClickListener(gridListener);
			curPage.addView(gridView);
		}
	}
/*
 * arg0  identical adapter
 * arg2  position on the page
 * Retrieve the information and return the value to callback
 */
	public OnItemClickListener gridListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
			PageCurrent = curPage.getCurScreen();
			gridID = arg2 + PageCurrent * 10;

			if (((GridView) arg0).getTag() != null) {
				((View) ((GridView) arg0).getTag()).setBackgroundResource(R.drawable.alphe);
			}
			((GridView) arg0).setTag(arg1);
			arg1.setBackgroundResource(R.drawable.commentbg);
			
			String cate_name=lstDate.get(gridID).getCategoryName();
			chosen_cate=cate_name; //mark category
			mcallback.onReturnFromFragment1(4,cate_name);
		}
	};
/*
 * change page
 * change the buttom layout style
 * I can change the image and do not change others
 * WHY it isn't shown on the buttom/ try to solve  maybe it is related to the layout
 */

	public void setCurPage(int page) {
		layoutBottom.removeAllViews();
		for (int i = 0; i < PageCount; i++) {
			imgCur = new ImageView(getActivity());
			imgCur.setBackgroundResource(R.drawable.bg_img_item);
			imgCur.setId(i);		
			if (imgCur.getId() == page) {
				imgCur.setBackgroundResource(R.drawable.bg_img_item_true);
			}
			layoutBottom.addView(imgCur);
		}
	}
	

}
