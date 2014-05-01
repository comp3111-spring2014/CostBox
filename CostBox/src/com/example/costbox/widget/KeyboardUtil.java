package com.example.costbox.widget;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.costbox.CostBox;
import com.example.costbox.InputKeyFragment;
import com.example.costbox.R;


public class KeyboardUtil {
	private Context ctx;
	private Activity act;
	private KeyboardView keyboardView;
	private Keyboard k1;
	private Keyboard k2;
	public boolean isnun = false;
	public boolean isupper = false;
	private EditText ed;
	keyboardOnReturnListener keyboard_callback;

	
	//interface return to the fragment
	public interface keyboardOnReturnListener {
        public void onReturnFromKeyboard(int state);
    }
	
	
  /*
   * type: 0 for char key
   *       1 for number key
   */
	
	public KeyboardUtil(Activity act, Context ctx, EditText edit, int type,keyboardOnReturnListener callback) {
		keyboard_callback=callback;
		this.act = act;
		this.ctx = ctx;
		this.ed = edit;
		k1 = new Keyboard(ctx, R.xml.char_key);
		k2 = new Keyboard(ctx, R.xml.number_key);
		keyboardView = (KeyboardView) act.findViewById(R.id.keyboard_view);
		if(type==0)
		{keyboardView.setKeyboard(k1);}   //char key
		else if(type==1)
		{keyboardView.setKeyboard(k2);}   // number key
		
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(true);
		keyboardView.setOnKeyboardActionListener(listener);
	}

	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			Editable editable = ed.getText();
			int start = ed.getSelectionStart();
			if (primaryCode == Keyboard.KEYCODE_CANCEL) {
				
				hideKeyboard();
				
			} else if (primaryCode == Keyboard.KEYCODE_DELETE) {
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			} else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
				changeKey();
				keyboardView.setKeyboard(k1);

			} else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
				if (isnun) {
					isnun = false;
					keyboardView.setKeyboard(k1);
				} else {
					isnun = true;
					keyboardView.setKeyboard(k2);
				}
			} else if (primaryCode == 57419) { // go left
				if (start > 0) {
					ed.setSelection(start - 1);
				}
			} else if (primaryCode == 57421) { // go right
				if (start < ed.length()) {
					ed.setSelection(start + 1);
				}
			} 
			//save
			else if(primaryCode==57420)
			{    
				  try{
				  keyboard_callback.onReturnFromKeyboard(1);
				  }
				  catch(Exception e)
				  {
					  Toast.makeText(act, "Sorry, your input is invalid.", 100).show();
				  }
			}
			else if(primaryCode==51222)
			{     //next step and change focus, keyboard
				  keyboard_callback.onReturnFromKeyboard(3);
			}
			else if(primaryCode==57500)
			{
				  keyboard_callback.onReturnFromKeyboard(2);
			}
			
			else {
				editable.insert(start, Character.toString((char) primaryCode));
			}
		}
	};
	

	private void changeKey() {
		List<Key> keylist = k1.getKeys();
		if (isupper) {
			isupper = false;
			for(Key key:keylist){
				if (key.label!=null && isword(key.label.toString())) {
					key.label = key.label.toString().toLowerCase();
					key.codes[0] = key.codes[0]+32;
				}
			}
		} else {
			isupper = true;
			for(Key key:keylist){
				if (key.label!=null && isword(key.label.toString())) {
					key.label = key.label.toString().toUpperCase();
					key.codes[0] = key.codes[0]-32;
				}
			}
		}
	}

    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }
    
    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.INVISIBLE);
        }
    }
    
    private boolean isword(String str){
    	String wordstr = "abcdefghijklmnopqrstuvwxyz";
    	if (wordstr.indexOf(str.toLowerCase())>-1) {
			return true;
		}
    	return false;
    }

}
