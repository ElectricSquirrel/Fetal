package com.fetal.service;

import java.io.File;

import android.content.Intent;

import com.fetal.base.ServiceBase;
import com.fetal.util.MovieMaker;

public class MovieService extends ServiceBase{
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MovieMaker mmaker = new MovieMaker();
		mmaker.toMovie(intent.getStringExtra("tape"));
		mmaker = null;
		clearTmpDir();
		stopSelf();
		android.os.Process.killProcess(android.os.Process.myPid());
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 清空临时文件夹
	 */
	private void clearTmpDir(){
		String path = "/sdcard/Fetal/tmp/";
		File file = new File(path);
	    if (!file.exists()) {
	        return;
		}
		if (!file.isDirectory()) {
		        return;
		}
		String[] list = file.list();
		int length = list.length;
		for (int i = 0; i < length; i++) {
				File temp = new File(path + list[i]);
		        if (temp.isFile()) {
		        	temp.delete();
		        }
		        temp = null;
		}
		file = null;
	}
}
