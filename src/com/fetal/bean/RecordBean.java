package com.fetal.bean;

public class RecordBean {
	
	/**
	 * ID
	 */
	private int id;
	
	/**
	 * �û�ID
	 */
	private int mid;
	
	/**
	 * Զ���û�ID
	 */
	private int remote_mid;
	
	/**
	 * ��¼����
	 */
	private int pregnancy;
	
	/**
	 * ��¼ʱ��
	 */
	private long date;
	
	/**
	 * ��¼ʱ��
	 */
	private int time;
	
	/**
	 * ���̥����
	 */
	private int min;
	
	/**
	 * ���̥����
	 */
	private int max;
	
	/**
	 * ƽ��̥����
	 */
	private int average;
	
	/**
	 * �໤����
	 */
	private int report;
	
	/**
	 * ̥��Ƶ��ͼ
	 */
	private String chart;
	
	/**
	 * ¼����
	 */
	private String tape;
	
	/**
	 * Ƶ�ʵ�
	 */
	private String point;
	
	/**
	 * ��Ƶ�ļ�
	 */
	private String sound;
	
	/**
	 * �Ƿ����
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
