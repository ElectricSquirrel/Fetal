package com.fetal.bean;

public class RecordBean {
	
	/**
	 * ID
	 */
	private int id;
	
	/**
	 * 用户ID
	 */
	private int mid;
	
	/**
	 * 远程用户ID
	 */
	private int remote_mid;
	
	/**
	 * 记录孕期
	 */
	private int pregnancy;
	
	/**
	 * 记录时间
	 */
	private long date;
	
	/**
	 * 记录时长
	 */
	private int time;
	
	/**
	 * 最低胎心率
	 */
	private int min;
	
	/**
	 * 最高胎心率
	 */
	private int max;
	
	/**
	 * 平均胎心率
	 */
	private int average;
	
	/**
	 * 监护报告
	 */
	private int report;
	
	/**
	 * 胎心频率图
	 */
	private String chart;
	
	/**
	 * 录音带
	 */
	private String tape;
	
	/**
	 * 频率点
	 */
	private String point;
	
	/**
	 * 音频文件
	 */
	private String sound;
	
	/**
	 * 是否分享
	 */
	private int share;
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setMid(int mid){
		this.mid = mid;
	}
	
	public int getMid(){
		return this.mid;
	}
	
	public void setRemoteMid(int remote_mid){
		this.remote_mid = remote_mid;
	}
	
	public int getRemoteMid(){
		return this.remote_mid;
	}
	
	public void setPregnancy(int pregnancy){
		this.pregnancy = pregnancy;
	}
	
	public int getPregnancy(){
		return this.pregnancy;
	}
	
	public void setDate(long date){
		this.date = date;
	}
	
	public long getDate(){
		return this.date;
	}
	
	public void setTime(int time){
		this.time = time;
	}
	
	public int getTime(){
		return this.time;
	}
	
	public void setMinBeat(int min){
		this.min = min;
	}
	
	public int getMinBeat(){
		return this.min;
	}
	
	public void setMaxBeat(int max){
		this.max = max;
	}
	
	public int getMaxBeat(){
		return this.max;
	}
	
	public void setAverageBeat(int average){
		this.average = average;
	}
	
	public int getAverageBeat(){
		return this.average;
	}
	
	public void setReport(int report){
		this.report = report;
	}
	
	public int getReport(){
		return this.report;
	}
	
	public void setChart(String chart){
		this.chart = chart;
	}
	
	public String getChart(){
		return this.chart;
	}
	
	public void setTape(String tape){
		this.tape = tape;
	}
	
	public String getTape(){
		return this.tape;
	}
	
	public void setPoint(String point){
		this.point = point;
	}
	
	public String getPoint(){
		return this.point;
	}
	
	public void setSound(String sound){
		this.sound = sound;
	}
	
	public String getSound(){
		return this.sound;
	}
	
	public void setShare(int share) {
		this.share = share;
	}
	
	public int getShare() {
		return this.share;
	}
}
