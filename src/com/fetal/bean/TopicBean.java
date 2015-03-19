package com.fetal.bean;

import org.json.JSONArray;

public class TopicBean {
	
	//����ID
	private int id;
	
	//�û��ǳ�
	private String nickname;
	
	//ͷ��
	private String thumbnail;
	
	//����
	private int pregnancy;
	
	//����ʱ��
	private long date;
	
	//ʱ��
	private int time;
	
	//����ͼ
	private String chart;
	
	//��Ƶ
	private String tape;
	
	//��Ƶ
	private String sound;
	
	//��ǵ�
	private String point;
	
	//����
	private int report;
	
	//����
	private JSONArray comment;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getNickname() {
		return this.nickname;
	}
	
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	public String getThumbnail() {
		return this.thumbnail;
	}
	
	public void setPregnancy(int pregnancy) {
		this.pregnancy = pregnancy;
	}
	
	public int getPregnancy() {
		return this.pregnancy;
	}
	
	public void setDate(long date) {
		this.date = date;
	}
	
	public long getDate() {
		return this.date;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public int getTime() {
		return this.time;
	}
	
	public void setChart(String chart) {
		this.chart = chart;
	}
	
	public String getChart() {
		return this.chart;
	}
	
	public void setTape(String tape) {
		this.tape = tape;
	}
	
	public String getTape() {
		return this.tape;
	}
	
	public void setSound(String sound) {
		this.sound = sound;
	}
	
	public String getSound() {
		return this.sound;
	}
	
	public void setPoint(String point) {
		this.point = point;
	}
	
	public String getPoint() {
		return this.point;
	}
	
	public void setReport(int report) {
		this.report = report;
	}
	
	public int getReport() {
		return this.report;
	}
	
	public void setComment(JSONArray comment) {
		this.comment = comment;
	}
	
	public JSONArray getComment() {
		return this.comment;
	}
}
