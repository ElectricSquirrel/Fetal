package com.fetal.bean;

public class MemberBean {
	
	/**
	 * ID
	 */
	private int id;
	
	/**
	 * Զ���û�Id
	 */
	private int remote_id;
	
	/**
	 * �û��ǳ�
	 */
	private String nickname;
	
	/**
	 * �ֻ�����
	 */
	private String mobile;
	
	/**
	 * Ԥ����
	 */
	private long birthday;
	
	/**
	 * ͷ��
	 */
	private String thumbnail;
	
	/**
	 * ΢��
	 */
	private String weibo;
	
	/**
	 * QQ
	 */
	private String qq;
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setRemoteId(int remote_id){
		this.remote_id = remote_id;
	}
	
	public int getRemoteId(){
		return this.remote_id;
	}
	
	public void setNickname(String nickname){
		this.nickname = nickname;
	}
	
	public String getNickname(){
		return this.nickname;
	}
	
	public void setMobile(String mobile){
		this.mobile = mobile;
	}
	
	public String getMobile(){
		return this.mobile;
	}
	
	public void setBirthday(long day){
		this.birthday = day;
	}
	
	public long getBirthday(){
		return this.birthday;
	}
	
	public void setThumbnail(String thumbnail){
		this.thumbnail = thumbnail;
	}
	
	public String getThumbnail(){
		return this.thumbnail;
	}
	
	public void setWeibo(String weibo){
		this.weibo = weibo;
	}
	
	public String getWeibo(){
		return this.weibo;
	}
	
	public void setQQ(String qq){
		this.qq = qq;
	}
	
	public String getQQ(){
		return this.qq;
	}
}
