package com.fetal.base;

import java.util.List;

import com.fetal.R;
import com.fetal.activity.CommunicateActivity;
import com.fetal.activity.LoginActivity;
import com.fetal.activity.MonitorActivity;
import com.fetal.activity.RecordsetActivity;
import com.fetal.activity.SettingActivity;
import com.fetal.activity.ShopActivity;
import com.fetal.base.ApplicationBase;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityBase extends Activity implements OnClickListener{
	
	protected ImageView menu_recordset, menu_communicate, menu_monitor, menu_shop, menu_setting, top_left, top_right;
	protected TextView top_title;
	protected ActivityManager activityManager;
	protected boolean isExit = false;
	protected ApplicationBase app;
	protected RunningTaskInfo info;
	protected String className;
	protected List<String> taskQueue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		app = (ApplicationBase) getApplication();
		info = activityManager.getRunningTasks(1).get(0);
		className = info.topActivity.getClassName();
		taskQueue = app.getTaskQueue();
		if (!taskQueue.contains(className)) {
			taskQueue.add(className);
			app.setTaskQueue(taskQueue);
		}
		SharedPreferences shared = getSharedPreferences("user", MODE_PRIVATE);
		if (
				!shared.contains("nickname") && 
				("com.fetal.activity.RecordsetActivity".equals(className) || 
				"com.fetal.activity.CommunicateActivity".equals(className) || 
				"com.fetal.activity.MonitorActivity".equals(className) || 
				"com.fetal.activity.SettingActivity".equals(className))
			) {
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}
	}
	
	/**
	 * 初始化公共头部
	 */
	protected void initTopUI() {
		top_title = (TextView) findViewById(R.id.top_title);
		top_left = (ImageView) findViewById(R.id.top_left);
		top_right = (ImageView) findViewById(R.id.top_right);
	}
	
	/**
	 * 初始化公共脚部
	 */
	protected void initBottomUI() {
		menu_recordset = (ImageView) findViewById(R.id.menu_recordset);
		if ("com.fetal.activity.RecordsetActivity".equals(className)) {
			menu_recordset.setImageResource(R.drawable.btn_recordset_on);
		} else {
			menu_recordset.setImageResource(R.drawable.btn_recordset_off);
		}
		menu_recordset.setOnClickListener(this);
		menu_communicate = (ImageView) findViewById(R.id.menu_communicate);
		if ("com.fetal.activity.CommunicateActivity".equals(className)) {
			menu_communicate.setImageResource(R.drawable.btn_communicate_on);
		} else {
			menu_communicate.setImageResource(R.drawable.btn_communicate_off);
		}
		menu_communicate.setOnClickListener(this);
		menu_monitor = (ImageView) findViewById(R.id.menu_monitor);
		if ("com.fetal.activity.MonitorActivity".equals(className)) {
			menu_monitor.setImageResource(R.drawable.btn_monitor_on);
		} else {
			menu_monitor.setImageResource(R.drawable.btn_monitor_off);
		}
		menu_monitor.setOnClickListener(this);
		menu_shop = (ImageView) findViewById(R.id.menu_shop);
		if ("com.fetal.activity.ShopActivity".equals(className)) {
			menu_shop.setImageResource(R.drawable.btn_shop_on);
		} else {
			menu_shop.setImageResource(R.drawable.btn_shop_off);
		}
		menu_shop.setOnClickListener(this);
		menu_setting = (ImageView) findViewById(R.id.menu_setting);
		if ("com.fetal.activity.SettingActivity".equals(className)) {
			menu_setting.setImageResource(R.drawable.btn_setting_on);
		} else {
			menu_setting.setImageResource(R.drawable.btn_setting_off);
		}
		menu_setting.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.menu_recordset:
				startActivity(new Intent(this, RecordsetActivity.class));
				finish();
				break;
			case R.id.menu_communicate:
				startActivity(new Intent(this, CommunicateActivity.class));
				finish();
				break;
			case R.id.menu_monitor:
				startActivity(new Intent(this, MonitorActivity.class));
				finish();
				break;
			case R.id.menu_shop:
				startActivity(new Intent(this, ShopActivity.class));
				finish();
				break;
			case R.id.menu_setting:
				startActivity(new Intent(this, SettingActivity.class));
				finish();
				break;
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		taskQueue = app.getTaskQueue();
		taskQueue.remove(className);
		app.setTaskQueue(taskQueue);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			int qlength = taskQueue.size();
			if (qlength <= 1) {
				if (isExit) {
					System.exit(0);
				} else {
					isExit = true;
					Toast.makeText(this, "再按一次后退键退出应用程序", Toast.LENGTH_SHORT).show();
				}
			} else {
				finish();
			}
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
