package com.example.costbox;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.costbox.CategoryFragment.OnReturnListener;
import com.example.costbox.widget.KeyboardUtil;
import com.example.costbox.widget.RandomIDgenerator;

public class InputKeyFragment extends Fragment 
implements com.example.costbox.widget.KeyboardUtil.keyboardOnReturnListener{
	private Context ctx;
	private Activity act;
	EditText editTT;
    OnReturnListener2 mcallback;
    Button choose_pic;
    Button add_pic;
    Button add_comments;
    static String comments="No comments...";
    static String ImageAddress="None";

	
	public interface OnReturnListener2 {
        public void onReturnFromFragment2(int state);
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_inputkey, container, false);
		editTT = (EditText) view.findViewById(R.id.editTT);
		ctx = this.getActivity();
		act = this.getActivity();
		choose_pic=(Button) view.findViewById(R.id.choose_pic);
		add_pic=(Button)view.findViewById(R.id.take_pic);
		add_comments=(Button)view.findViewById(R.id.add_comments);
		choose_pic.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{   
				Intent i = new Intent(
			    Intent.ACTION_PICK,
			    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1);
			}
		});
		
		add_pic.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{   
				File fos=null;
			    RandomIDgenerator generate_id=new RandomIDgenerator();
			    String filename_first=generate_id.getRandomID(); //generate a file name
			    String filename=filename_first+".jpg";// combine a file name
			    ImageAddress=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+filename;
			    fos=new File(ImageAddress);  
			    Uri u=Uri.fromFile(fos);
				Intent i= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		        i.putExtra(MediaStore.EXTRA_OUTPUT,u);
		        startActivity(i);  // bug cannot return!!
			}
		});
		
		add_comments.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{   final EditText temp=new EditText(getActivity());
				new AlertDialog.Builder(getActivity())
	    		   .setView(temp)
	    		   .setPositiveButton("save", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog,int which)
						{
							comments=temp.getText().toString();
							Toast.makeText(getActivity(),"Saved",Toast.LENGTH_SHORT).show();
						}
					}
	               )
	    		   .setNegativeButton("cancel", null)
	    		   .show();
			}
		});
		
		editTT.setOnTouchListener(new EditText.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				// show the keyboard
				int inputback = editTT.getInputType();
				editTT.setInputType(InputType.TYPE_NULL);  //to avoid the keyboard appearing
				new KeyboardUtil(act, ctx, editTT,0,InputKeyFragment.this).showKeyboard();
				editTT.setInputType(inputback);
				editTT.setSelection(editTT.getText().length());
				return false;
			}
		});
		editTT.requestFocus();
	
		
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		    if (requestCode == 1 && resultCode == -1 && null != data) {
	            Uri selectedImage = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	 
	            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
	                    filePathColumn, null, null, null);
	            cursor.moveToFirst();
	 
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String picturePath = cursor.getString(columnIndex);
	            cursor.close();
	            Log.d("return choose",requestCode+"");
	            ImageAddress=picturePath;//save the picture path
	            Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
	 
	        }
	        else if (requestCode == 2 && resultCode == -1 ) {
                 //do nothing
	        	Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
	        }
	        else{
	        	
	        	Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
	        
	        }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
	}
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mcallback = (OnReturnListener2) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CategoryFragment.OnReturnListener");
        }
    }

	@Override
	public void onReturnFromKeyboard(int state) {
		// TODO Auto-generated method stub
		if(state==1)//save it
		{
			mcallback.onReturnFromFragment2(1);
		}
		else if(state==2)//delete it
		{
			mcallback.onReturnFromFragment2(2);
		}
		else if(state==3)
		{   
			//editTT.setInputType(InputType.TYPE_NULL);
			mcallback.onReturnFromFragment2(3);
		}
	}
	
}
