package com.kanav.familyshare;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class MainActivity extends Activity implements ActionBar.OnNavigationListener {
	
	private MenuItem menuItem;
	ShareActionProvider shareActionProv;
	private static final String STATE_NAV_ITEM = "selected navigation item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
       actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        /*actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        final String[] values = getResources().getStringArray(R.array.drop_down);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(actionBar.getThemedContext(), android.R.layout.simple_spinner_item, 
        			android.R.id.text1, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(adapter, this);*/
        
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        
        Tab tab = actionBar.newTab()
        		.setText(R.string.chat)
        		.setTabListener(new TabListener<ChatFragment>(this, "Chat", ChatFragment.class));
        
        actionBar.addTab(tab);
        
        tab = actionBar.newTab()
        			.setText(R.string.update_status)
        			.setTabListener(new TabListener<StatusFragment>(
        					this, "Update status", StatusFragment.class));
        actionBar.addTab(tab);
        
        tab = actionBar.newTab()
    			.setText(R.string.upload_pics)
    			.setTabListener(new TabListener<PicsFragment>(
    					this, "Upload pics", PicsFragment.class));
        actionBar.addTab(tab);
        setContentView(R.layout.activity_main);
    }

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if(savedInstanceState.containsKey(STATE_NAV_ITEM)){
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_NAV_ITEM));
		}
	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_NAV_ITEM, getActionBar().getSelectedNavigationIndex());
	}



	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    
    public void doShare(){
    	Intent intent  = new Intent(Intent.ACTION_SEND);
    	intent.setType("text/plain");
    	intent.putExtra(Intent.EXTRA_TEXT, "Howdy soldier");
    	shareActionProv.setShareIntent(intent);
    }


	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		Fragment frag = new StatusFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(StatusFragment.text, arg0 + 1);
		frag.setArguments(bundle);
		getFragmentManager().beginTransaction().replace(android.R.id.content, frag).commit();
		return true;
	}
}
