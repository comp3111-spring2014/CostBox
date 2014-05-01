package com.example.costbox;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryGridAdapter extends BaseAdapter {

	private Context context;
	private List<CategoryGridInfo> lstDate;
	//private TextView txtName;
	private ImageView cimage;
	private TextView cname;

	public static final int SIZE = 10;

	public CategoryGridAdapter(Context mContext, List<CategoryGridInfo> list, int page) {
		this.context = mContext;
		lstDate = new ArrayList<CategoryGridInfo>();
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
		CategoryGridInfo user = lstDate.get(position);
		convertView = LayoutInflater.from(context).inflate(R.layout.categoryitem, null);


		cimage= (ImageView) convertView.findViewById(R.id.category_image);
		cname = (TextView) convertView.findViewById(R.id.category_name);

 

		cimage.setImageDrawable(user.getCategoryIcon());
		cname.setText(user.getCategoryName());


		return convertView;
	}

}
