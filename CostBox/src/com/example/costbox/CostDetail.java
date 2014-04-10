package com.example.costbox;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class CostDetail extends Activity {
	
	Bundle bunde;
	Intent intent;
	private Button returnbt1, trashbt1;
	private TextView C1;
	private TextView D1;
	private TextView comments;
	private ImageView detailimage;
	String ImageAddress;
	String category;
	String comment_text;
	String cost;
	String picture_address;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		// action bar return back to the up layer
		ActionBar actionBar = getActionBar();  
		actionBar.setDisplayHomeAsUpEnabled(true); 
		//used to restore a imagepath to fix the bug of rotation
		if(savedInstanceState!=null)
		{
			ImageAddress=savedInstanceState.getString("imagepath"); 
		
		}//added
		
		C1 = (TextView) findViewById(R.id.Category);
		D1 = (TextView) findViewById(R.id.detailcost);
		comments = (TextView) findViewById(R.id.comments);
		returnbt1=(Button) findViewById(R.id.returnbt);
		
		trashbt1=(Button) findViewById(R.id.trashbt);
		
	    detailimage=(ImageView) findViewById(R.id.detailimage);
	    
		intent=this.getIntent();
		bunde=intent.getExtras();
		
		category=bunde.getString("category");
		cost=bunde.getInt("cost")+"";
		comment_text=bunde.getString("comments");
		picture_address=bunde.getString("picture");
		
	    //show multiple information including the picture
		C1.setText(category);
	    D1.setText(cost);
	    comments.setText(comment_text);
	    if(picture_address.equals("None"))
	    {
	    	//using the default pic
	    }
	    else 
	    {
	    	 ImageView imageView = (ImageView) findViewById(R.id.detailimage);
	         imageView.setImageBitmap(BitmapFactory.decodeFile(picture_address));
	    }
	    C1.setClickable(true);
	    C1.setFocusable(true);
	    D1.setClickable(true);
	    D1.setFocusable(true);
	    comments.setClickable(true);
	    comments.setFocusable(true);
	    
		
		returnbt1.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{   
				intent.putExtras(bunde);
				CostDetail.this.setResult(RESULT_OK,intent);
				CostDetail.this.finish();
			}
		});
		

		trashbt1.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				//CostDetail.this.setResult(RESULT_CANCELED,intent);
				//CostDetail.this.finish();
				dialog();
			}
		});
		
		

		detailimage.setOnLongClickListener(new ImageView.OnLongClickListener()
		{
			public boolean onLongClick(View v)
			{
				new AlertDialog.Builder(CostDetail.this)
				.setItems(new String[] {"From album","Take photos"}, 
						new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichone)
					{

						if(whichone==0)
						{
							 Intent i = new Intent(
				             Intent.ACTION_PICK,
				             android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, 1);
                            //overridePendingTransition(R.anim.in_from_button, R.anim.out_to_top);
						}

						else if(whichone==1)
						{   File fos=null;

						    String filename=category+cost+".jpg";// modified 4.3 
						    ImageAddress=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+filename;
						    fos=new File(ImageAddress);  
						    Uri u=Uri.fromFile(fos);
							Intent i= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					        i.putExtra(MediaStore.EXTRA_OUTPUT,u);
							startActivityForResult(i,2);
							//overridePendingTransition(R.anim.in_from_button, R.anim.out_to_top);
						}
					}
				})
				.show();
				return true;
			}
		});
		

	       C1.setOnClickListener(new TextView.OnClickListener() {
	    	   public void onClick(View v){
	    		   final EditText temp=new EditText(CostDetail.this);
	    		   temp.setText(C1.getText());
	    		   new AlertDialog.Builder(CostDetail.this)
	    		   .setView(temp)
	    		   .setPositiveButton("save", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog,int which)
						{
							C1.setText(temp.getText());
							bunde.putString("category", temp.getText().toString());
						}
					}
	               )
	    		   .setNegativeButton("cancel", null)
	    		   .show();
	    		     
	    		   
	    	   }
	    	   });


	       D1.setOnClickListener(new TextView.OnClickListener() {
	    	   public void onClick(View v){
	    		   final EditText temp=new EditText(CostDetail.this);
	    		   temp.setText(D1.getText());
	    		   new AlertDialog.Builder(CostDetail.this)
	    		   .setView(temp)
	    		   .setPositiveButton("save", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog,int which)
						{
							D1.setText(temp.getText());
							//change string to int
							String cost_string_temp = temp.getText().toString(); 
							    int cost_temp=0;
							    try{
							    cost_temp = Integer.parseInt(cost_string_temp);
							    }
							    catch(NumberFormatException nfe){	
							    }
						 	bunde.putInt("cost", cost_temp);
						 	
						}
					}
	               )
	    		   .setNegativeButton("cancel", null)
	    		   .show();
	    		     
	    		   
	    	   }
	    	   });


       comments.setOnClickListener(new TextView.OnClickListener() {
    	   public void onClick(View v){
    		   final EditText temp=new EditText(CostDetail.this);
    		   temp.setText(comments.getText());
    		   new AlertDialog.Builder(CostDetail.this)
    		   .setView(temp)
    		   .setPositiveButton("save", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog,int which)
					{
						comments.setText(temp.getText());
						bunde.putString("comments", temp.getText().toString());
					}
				}
               )
    		   .setNegativeButton("cancel", null)
    		   .show();
    		     
    		   
    	   }
    	   });

 

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("imagepath",ImageAddress); //added
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, CostBox.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	protected void dialog() {
		AlertDialog.Builder dg = new Builder(this);
				dg.setMessage("Are you sure to delete?");
				dg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// TODO Auto-generated method stub
						CostDetail.this.setResult(RESULT_CANCELED,intent);
						CostDetail.this.finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// TODO Auto-generated method stub
					}
				})
				.show();
	}	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event){
	//to judge whether it is the back key.
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			bunde.putString("back_Identifier", "true"); 
			// set true means that it is a back key currently so do not delete the item.
			intent.putExtras(bunde);
			CostDetail.this.setResult(RESULT_CANCELED,intent);
			CostDetail.this.finish();
			return true;
		}
		else return super.onKeyDown(keyCode, event);
	}
	 @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	 
	        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
	            Uri selectedImage = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	 
	            Cursor cursor = getContentResolver().query(selectedImage,
	                    filePathColumn, null, null, null);
	            cursor.moveToFirst();
	 
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String picturePath = cursor.getString(columnIndex);
	            cursor.close();
	 
	            ImageView imageView = (ImageView) findViewById(R.id.detailimage);
	            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
	            bunde.putString("picture", picturePath);
	 
	        }
	        else if (requestCode == 2 && resultCode == RESULT_OK ) {
                ImageView imageView = (ImageView) findViewById(R.id.detailimage);
	            imageView.setImageBitmap(BitmapFactory.decodeFile(ImageAddress));
	            bunde.putString("picture", ImageAddress);
	        }
	 
	    }

}
