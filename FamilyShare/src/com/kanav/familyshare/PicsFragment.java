package com.kanav.familyshare;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PicsFragment extends Fragment{
	public static final String text ="Upload Pics";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView tv = new TextView(getActivity());
		tv.setGravity(Gravity.CENTER);
		//tv.setText(Integer.toString(getArguments().getInt(text)));
		tv.setText(text);
		return tv;
	}
	
	

}
