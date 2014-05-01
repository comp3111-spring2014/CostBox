package com.example.costbox.preference;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.costbox.CategoryDB;
import com.example.costbox.CategoryGridInfo;
import com.example.costbox.preference.CategoryEdit;
import com.example.costbox.R;

public class CategoryEditAdapter extends BaseAdapter {

	private Context context;
	private List<CategoryEditGridInfo> lstDate;
	//private TextView txtName;
	private ImageView cimage;
	private TextView cname;
	private CheckBox check;
	
	//Category db
	CategoryDB myCategoryDB;

	public static final int SIZE = 16;

	public CategoryEditAdapter(Context mContext, List<CategoryEditGridInfo> list, int page) {
		this.context = mContext;
		lstDate = new ArrayList<CategoryEditGridInfo>();
		int i = page * SIZE;
		int iEnd = i + SIZE;
		while ((i < list.size()) && (i < iEnd)) {
			lstDate.add(list.get(i));
			i++;
		}
	}

	@Override
	public int getCount() {
		return lstDate.size();
	}

	@Override
	public Object getItem(int position) {
		return lstDate.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final CategoryEditGridInfo user = lstDate.get(position);
		convertView = LayoutInflater.from(context).inflate(R.layout.precategoryitem, null);
		myCategoryDB = new CategoryDB(parent.getContext());

		cimage= (ImageView) convertView.findViewById(R.id.category_image);
		cname = (TextView) convertView.findViewById(R.id.category_name);
        check = (CheckBox) convertView.findViewById(R.id.check);
 

		cimage.setImageDrawable(user.getCategoryIcon());
		cname.setText(user.getCategoryName());
		check.setChecked(user.getCheck());
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			   @Override
			   public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			  if(arg1==false){ 
			       myCategoryDB.modify_visibility(user.getCategoryName(), 1);
			       //check.setChecked(false);
			    }else{
			    	myCategoryDB.modify_visibility(user.getCategoryName(), 0);
			    	//check.setChecked(true);
			    }
			   }
			  });

		return convertView;
	}

}
