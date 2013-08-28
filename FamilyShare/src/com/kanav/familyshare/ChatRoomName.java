package com.kanav.familyshare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChatRoomName extends DialogFragment{
	
	public static final String ROOM_NAME = "ROOM_NAME";
	
	public static ChatRoomName newInstance() {
		ChatRoomName dialog = new ChatRoomName();
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.chat_room, null);
		final EditText value = (EditText) view.findViewById(R.id.chat_room_name);
		builder.setView(view);
		builder.setTitle(R.string.chat_room_title);
		builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(value.getText().toString().length() != 0) {
				Intent result = new Intent();
				result.putExtra(ROOM_NAME, value.getText().toString());
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
				dismiss();
				}
				else {
					Toast.makeText(getActivity(), "Please enter a name, or hit cancel",Toast.LENGTH_SHORT).show();
				}
			}
		});
		return dialog;
		
	}
	
}
