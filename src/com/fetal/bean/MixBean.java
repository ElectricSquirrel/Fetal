package com.fetal.bean;

public class MixBean {
	//混音ID
	private int id;
	//用户ID
	private int mid;
	//远程用户ID
	private int remote_mid;
	//分享时间
	private long date;
	//时长
	private int time;
	//胎心文件
	private String rfile;
	//胎心标题
	private String record;
	//音乐文件
	private String mfile;
	//音乐标题
	private String music;
	//混音文件路径
	private String sound;
	//分享状态
	private int share;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setMid(int mid) {
		this.mid = mid;
	}
	
	public int getMid() {
		return this.mid;
	}
	
	public void setRemoteMid(int remote_mid) {
		this.remote_mid = remote_mid;
	}
	
	public int getRemoteMid() {
		return this.remote_mid;
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
	
	public void setRecord(String record) {
		this.record = record;
	}
	
	public String getRecord() {
		return this.record;
	}
	
	public void setRfile(String rfile) {
		this.rfile = rfile;
	}
	
	public String getRfile() {
		return this.rfile;
	}
	
	public void setMusic(String music) {
		this.music = music;
	}
	
	public String getMusic() {
		return this.music;
	}
	
	public void setMfile(String mfile) {
		this.mfile = mfile;
	}
	
	public String getMfile() {
		return this.mfile;
	}
	
	public void setSound(String sound) {
		this.sound = sound;
	}
	
	public String getSound() {
		return this.sound;
	}
	
	public void setShare(int share) {
		this.share = share;
	}
	
	public int getShare() {
		return this.share;
	}
}
