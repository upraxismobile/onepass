package com.fgtit.onepass;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import com.fgtit.onepass.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener{

	public static final String KEY_TARGET_IP = "target_ip";
	public static final String KEY_TARGET_PORT = "target_port";
	public static final String KEY_WEB_ADDR = "web_addr";
	public static final String KEY_WEB_UPDATA = "web_update";
	public static final String KEY_DEV_SN = "device_sn";
	public static final String KEY_LOGIN_USER = "login_user";
	public static final String KEY_LOGIN_PASSWORD = "login_password";
	
	public static final String KEY_DOWN_TYPE = "down_type";
	public static final String KEY_STEP_TIME = "step_time";
	public static final String KEY_SET_TIME = "set_time";
	
	private EditTextPreference mServerIP;
	private EditTextPreference mServerPort;
	private EditTextPreference mWebAddr;
	private EditTextPreference mWebUpdate;
	private EditTextPreference mDevSN;
	private EditTextPreference mLoginUser;
	private EditTextPreference mLoginPassword;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.setserver);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setupViews();
	
		//AppList.addActivity(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
						
		displayPreference(mServerIP);
		displayPreference(mServerPort);
		displayPreference(mWebAddr);
		displayPreference(mWebUpdate);
		displayPreference(mDevSN);
		displayPreference(mLoginUser);
		displayPreference(mLoginPassword);
	}
	
	private void setupViews() {
		
		mServerIP = (EditTextPreference)findPreference(KEY_TARGET_IP);
		mServerPort = (EditTextPreference)findPreference(KEY_TARGET_PORT);
		mWebAddr = (EditTextPreference)findPreference(KEY_WEB_ADDR);
		mWebUpdate = (EditTextPreference)findPreference(KEY_WEB_UPDATA);
		mDevSN = (EditTextPreference)findPreference(KEY_DEV_SN);
		mLoginUser = (EditTextPreference)findPreference(KEY_LOGIN_USER);
		mLoginPassword = (EditTextPreference)findPreference(KEY_LOGIN_PASSWORD);
		
		mServerIP.setOnPreferenceChangeListener(this);
		mServerPort.setOnPreferenceChangeListener(this);
		mWebAddr.setOnPreferenceChangeListener(this);
		mWebUpdate.setOnPreferenceChangeListener(this);
		mDevSN.setOnPreferenceChangeListener(this);
		mLoginUser.setOnPreferenceChangeListener(this);
		mLoginPassword.setOnPreferenceChangeListener(this);
		
		mServerIP.setOnPreferenceClickListener(createOnPreferenceClickListener(mServerIP));
		mServerPort.setOnPreferenceClickListener(createOnPreferenceClickListener(mServerPort));
		mWebAddr.setOnPreferenceClickListener(createOnPreferenceClickListener(mWebAddr));
		mWebUpdate.setOnPreferenceClickListener(createOnPreferenceClickListener(mWebUpdate));
		mDevSN.setOnPreferenceClickListener(createOnPreferenceClickListener(mDevSN));
		mLoginUser.setOnPreferenceClickListener(createOnPreferenceClickListener(mLoginUser));
		mLoginPassword.setOnPreferenceClickListener(createOnPreferenceClickListener(mLoginPassword));
		
	}

	private OnPreferenceClickListener createOnPreferenceClickListener(
			final EditTextPreference editTextPreference) {
		
		return new EditTextPreference.OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				
				// TODO Auto-generated method stub
				final EditText editText = editTextPreference.getEditText();
				final AlertDialog dialog = (AlertDialog)editTextPreference.getDialog();
				editText.addTextChangedListener(new TextWatcher() {
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						/*
						try {
							int port = Integer.valueOf(editText.getText().toString());
							if (port>=0 && port<=100000000) {
								dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
							}else {
								dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
							}
						} catch (Exception e) {
							dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
						}
						*/
					}

					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						/*
						try {
							int port = Integer.valueOf(editText.getText().toString());
							if (port>=0 && port<=100000000) {
								dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
							}else {
								dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
							}
						} catch (Exception e) {
							dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
						}
						*/
					}

					public void afterTextChanged(Editable s) {
					}
				});
				
				return false;
			}
		};
	}

		
	private void displayPreference(final Preference preference) {
		if(preference==mServerIP)
			preference.setSummary(preference.getSharedPreferences().getString(preference.getKey(),"192.168.1.106"));
		if(preference==mServerPort)
			preference.setSummary(preference.getSharedPreferences().getString(preference.getKey(),"5002"));
		
		if(preference==mDevSN)
			preference.setSummary(preference.getSharedPreferences().getString(preference.getKey(),"00000000"));
		
		if(preference==mWebAddr)
			preference.setSummary(preference.getSharedPreferences().getString(preference.getKey(),"192.168.1.138"));
		if(preference==mWebUpdate)
			preference.setSummary(preference.getSharedPreferences().getString(preference.getKey(),"21"));
		
		if(preference==mLoginUser)
			preference.setSummary(preference.getSharedPreferences().getString(preference.getKey(),"xpb"));
		if(preference==mLoginPassword)
			preference.setSummary(preference.getSharedPreferences().getString(preference.getKey(),"1010"));
	}
	

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		if (preference == mServerIP ||preference == mServerPort 
				||preference == mDevSN 
				||preference == mWebAddr ||preference == mWebUpdate 
				||preference == mLoginUser ||preference == mLoginPassword){
			preference.setSummary(newValue.toString());
		}
		return true;
	}
		
	private void toast(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        //getMenuInflater().inflate(R.menu.settings, menu);
	        return true;
	    }
	    
	    @Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
				//case R.id.action_Cancel: 
				case android.R.id.home:
					this.finish();
					return true;		
				default:
					return super.onOptionsItemSelected(item);
			}
		}
}
