package com.kanav.familyshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserNameActivity extends Activity {
	
	private Button mButton;
	private EditText mEdit;
	
	public static String EXTRA_USER_NAME = "user_name";
	
	private TextView.OnEditorActionListener mWriteListener =
			new TextView.OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if(actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
						decideUserName();
					}
					return true;
				}
			};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_user_name);
		setResult(Activity.RESULT_CANCELED);
		mButton = (Button)findViewById(R.id.username_ok_button);
		mEdit = (EditText)findViewById(R.id.username_edittext);
		mEdit.setOnEditorActionListener(mWriteListener);
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				decideUserName();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_name, menu);
		return true;
	}
	
	public void decideUserName() {
		String name = mEdit.getText().toString();
		if(name.length() <= 0)
			return;
		Intent intent = new Intent();
		intent.putExtra(EXTRA_USER_NAME, name);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

}
