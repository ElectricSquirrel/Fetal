package com.fetal.service;

import android.content.Intent;

import com.fetal.base.ServiceBase;
import com.fetal.util.MusicMixer;

public class MixService extends ServiceBase{
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MusicMixer mixer = new MusicMixer();
		mixer.makeMusic(intent.getStringExtra("input1"), intent.getStringExtra("input2"), intent.getStringExtra("output"));
		mixer = null;
		stopSelf();
		android.os.Process.killProcess(android.os.Process.myPid());
		return super.onStartCommand(intent, flags, startId);
	}
}
