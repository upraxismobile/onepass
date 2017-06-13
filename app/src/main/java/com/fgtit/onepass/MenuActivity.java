package com.fgtit.onepass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fgtit.onepass.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.os.Build;
import android.provider.Settings;

public class MenuActivity extends Activity{

	private ListView listView;
	private List<Map<String, Object>> mData;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		
		listView=(ListView) findViewById(R.id.listView1);	
		SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.listview_menuitem,
				new String[]{"title","info","img"},
				new int[]{R.id.title,R.id.info,R.id.img});
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new ListView.OnItemClickListener(){
			@Override  
			public void onItemClick(AdapterView<?> parent, View view, int pos,long id) {  
				//Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);  
				switch(pos){
				case 0:{
						Intent intent = new Intent(MenuActivity.this, RecordsActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
					break;
				case 1:{
						Intent intent = new Intent(MenuActivity.this, EmployeesActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
					break;
				case 2:{
						Intent intent = new Intent(MenuActivity.this, UtilitiesActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
					break;
				case 3:{
						Intent intent = new Intent(MenuActivity.this, SystemActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
					break;
				case 4:{
						Intent intent = new Intent(MenuActivity.this, AboutActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
					break;
				}
			}             
		});
        
	}
	
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();

		map = new HashMap<String, Object>();
		map.put("title", getString(R.string.txt_title_01));
		map.put("info", getString(R.string.txt_info_01));
		map.put("img", R.drawable.view_details);
		list.add(map);
				
		map = new HashMap<String, Object>();
		map.put("title", getString(R.string.txt_title_02));
		map.put("info", getString(R.string.txt_info_02));
		map.put("img", R.drawable.group);
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", getString(R.string.txt_title_03));
		map.put("info", getString(R.string.txt_info_03));
		map.put("img", R.drawable.reload);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", getString(R.string.txt_title_04));
		map.put("info", getString(R.string.txt_info_04));
		map.put("img", R.drawable.engineering);
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", getString(R.string.txt_title_05));
		map.put("info", getString(R.string.txt_info_05));
		map.put("img", R.drawable.about);
		list.add(map);
				
		mData=list;		
		return list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.system, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		case android.R.id.home:
			this.setResult(1);
			this.finish();
			return true;
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    @Override  
 	public boolean onKeyDown(int keyCode, KeyEvent event) {  
 	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
 			this.setResult(1);
 			this.finish();
 	    	return true;  
 	    }
 	    return super.onKeyDown(keyCode, event);  
 	}
}
