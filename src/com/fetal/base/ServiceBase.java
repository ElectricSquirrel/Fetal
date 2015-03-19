package com.fetal.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServiceBase extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public boolean onUnbind(Intent intent){
		return super.onUnbind(intent);
	}
}
