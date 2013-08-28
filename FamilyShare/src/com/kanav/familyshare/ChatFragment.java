package com.kanav.familyshare;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

// Some code adapted from http://www.ece.ncsu.edu/wireless/MadeInWALAN/AndroidTutorial/
public class ChatFragment extends Fragment{
	
	public static final int MSG_READ = 1;
	public static final int MSG_WRITE = 2;
	public static final int MSG_TOAST = 3;
	private static String TOAST = "toast";
	public static String TAG = "ChatFrag";
	
	private ListView mList;
	private EditText mEdit;
	private Button mButton; 

	private ArrayAdapter<String> mAdapter;
	private StringBuffer mOutString;
	private ChatService mService = null;
	private String mUserName;
	private String mChatRoomName;
	
	public static final String TYPE_SEND_MSG = "1";
	public static final String DELIMITER = "-";
	
	// Intent codes
	private final int INTENT_GET_USERNAME = 1;	
	private static final int GET_ROOM_NAME = 2;
	
	private TextView.OnEditorActionListener mWriteListener	=
		new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if(actionId == EditorInfo.IME_NULL && 
						event.getAction() == KeyEvent.ACTION_UP) {
					String message = v.getText().toString();
					sendMessage(message);
				}
				return false;
			}
		
	};
	
	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			MessageUnPacker unpacked;
			switch(msg.what) {
				case MSG_WRITE:
					byte[] writeBuf = (byte[]) msg.obj;
					String writeMsg = new String(writeBuf);
					unpacked = new MessageUnPacker(writeMsg);
					mAdapter.add(unpacked.getMsg());
					break;

				case MSG_READ:
					String readBuf = (String) msg.obj;
					unpacked = new MessageUnPacker(readBuf);
					Log.e(TAG, "incoming message. type " + unpacked.getMsgType());
					if(unpacked.getMsgType().equals(TYPE_SEND_MSG)) {
						Log.e(TAG,"msg read " + unpacked.getMsg() + ", room: " + unpacked.getRoom());
						if(mChatRoomName != null && mChatRoomName.equals(unpacked.getRoom())) {
							mAdapter.add(unpacked.getMsg());
						}
					}
					
					break;
					
				case MSG_TOAST:
					Toast.makeText(getActivity(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
					break;
			}
		}
		
	};
	
	public void setUserName(String name) {
		mUserName = name;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mOutString = new StringBuffer("");
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.activity_chat,
		        container, false);
		return view;
		//getActivity().requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		//setContentView(R.layout.activity_chat);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
	}

	protected void sendMessage(String message) {
		if(message.length() > 0) {
			if(mUserName == null)
				mUserName = "User";
			if(mChatRoomName == null)
				mChatRoomName = "Default Room";
			message = mUserName + ":" + message;
			MessagePacker finalMsg = new MessagePacker(TYPE_SEND_MSG, mChatRoomName, message);
			message = finalMsg.getFinalMsg();

			byte[] send = message.getBytes(); 
			mService.write(send);

			mOutString.setLength(0);
			mEdit.setText(mOutString);
		}
		else {
			Toast.makeText(getActivity(), "Please enter a message", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mService != null) {
			mService.stop();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mService != null) {
			mService.stop();
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mService.start();
	}

	@Override
	public void onStart() {
		super.onStart();
		setUpChat();
	}

	private void setUpChat() {
		mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);
		mList = (ListView)getActivity().findViewById(R.id.chatList);
		mList.setAdapter(mAdapter);

		mEdit = (EditText)getActivity().findViewById(R.id.editChat);
		mEdit.setOnEditorActionListener(mWriteListener);
		
		mButton = (Button)getActivity().findViewById(R.id.sendChat);
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView view = (TextView) getActivity().findViewById(R.id.editChat);
                String message = view.getText().toString();
                sendMessage(message);
				
			}
		});
		if(mOutString == null) 
			mOutString = new StringBuffer("");
		if(mService == null)
			mService = new ChatService(getActivity(), mHandler);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mService != null) {
			mService.stop();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case INTENT_GET_USERNAME:
			if(resultCode == Activity.RESULT_OK) {
				mUserName = data.getExtras().getString(UserNameActivity.EXTRA_USER_NAME);
				Toast.makeText(getActivity(), "Username changed to: " + mUserName, Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(getActivity(), R.string.username_error, Toast.LENGTH_SHORT).show();
			}
		case GET_ROOM_NAME:
			if(resultCode == Activity.RESULT_OK) {
				mChatRoomName = data.getExtras().getString(ChatRoomName.ROOM_NAME);
				Toast.makeText(getActivity(), "new room :" +mChatRoomName, Toast.LENGTH_SHORT).show();
			}
		}
	}

	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.username:
			Intent userNameIntent = new Intent(getActivity(), UserNameActivity.class);
			startActivityForResult(userNameIntent, INTENT_GET_USERNAME);
			return true;
		case R.id.chat_room:
			/*Intent chatListIntent = new Intent(getActivity(), ChatList.class);
			startActivity(chatListIntent);
			return true;*/
			ChatRoomName dialog = ChatRoomName.newInstance();
			dialog.setTargetFragment(this, GET_ROOM_NAME);
			dialog.show(getFragmentManager().beginTransaction(), "dialog");
		}
		return false;
	}
	
	private class MessagePacker {
		private String finalMsg;
		public MessagePacker(String msgType, String room, String msg) {
			if(msgType == null || room == null || msg == null) {
				throw new IllegalArgumentException("Null parameters in MessageCreator not allowed");
			}
			else {
				finalMsg = TYPE_SEND_MSG + DELIMITER + room + DELIMITER + msg;
			}
		}
		
		public String getFinalMsg() {
			return finalMsg;
		}
	}
	
	private class MessageUnPacker {
		private String room;
		private String msg;
		private String msgType;
		public MessageUnPacker(String input) {
			if(input.contains(DELIMITER)) {
				String[] parts = input.split(DELIMITER);
				this.msgType = parts[0];
				this.room = parts[1];
				this.msg = parts[2];
			}
			else
				throw new IllegalArgumentException("Message doesnt contain delimiter");
			
		}
		public String getRoom() {
			return room;
		}
		public String getMsg() {
			return msg;
		}
		public String getMsgType() {
			return msgType;
		}
		
	}
	
}
