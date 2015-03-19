package com.fetal.broadcaster;

import com.fetal.activity.MonitorActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadsetBroadcaster extends BroadcastReceiver{
	
	private int state = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (state == 0) {
			state = 1;
		} else {
			Intent i = new Intent(context, MonitorActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);
		}
	}
}