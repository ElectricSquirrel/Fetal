package com.fetal.service;

import android.content.Intent;

import com.fetal.base.ServiceBase;
import com.fetal.util.MusicMixer;

public class RepeatService extends ServiceBase{
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MusicMixer mixer = new MusicMixer();
		mixer.combiteMusic(intent.getIntExtra("number", 1), intent.getStringExtra("input"), intent.getStringExtra("output"));
		mixer = null;
		stopSelf();
		android.os.Process.killProcess(android.os.Process.myPid());
		return super.onStartCommand(intent, flags, startId);
	}
}
