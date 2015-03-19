package com.fetal.base;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

public class ApplicationBase extends Application{
	
	public List<String> taskQueue;
	
	@Override
	public void onCreate(){
		super.onCreate();
		taskQueue = new ArrayList<String>();
	}
	
	@Override
	public void onLowMemory(){
		super.onLowMemory();
	}
	
	@Override
	public void onTerminate(){
		super.onTerminate();
	}
	
	/**
	 * 获取任务列队
	 * @return
	 */
	public List<String> getTaskQueue() {
		return this.taskQueue;
	}
	
	/**
	 * 设置任务列队
	 * @param queue
	 */
	public void setTaskQueue(List<String> queue) {
		this.taskQueue = queue;
	}
}
