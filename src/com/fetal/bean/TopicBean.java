package com.fetal.bean;

import org.json.JSONArray;

public class TopicBean {
	
	//主题ID
	private int id;
	
	//用户昵称
	private String nickname;
	
	//头像
	private String thumbnail;
	
	//孕周
	private int pregnancy;
	
	//分享时间
	private long date;
	
	//时长
	private int time;
	
	//缩略图
	private String chart;
	
	//视频
	private String tape;
	
	//音频
	private String sound;
	
	//标记点
	private String point;
	
	//报告
	private int report;
	
	//评论
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
