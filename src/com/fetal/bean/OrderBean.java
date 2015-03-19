package com.fetal.bean;

public class OrderBean {
	
	//订单ID
	private int id;
	
	//服务器用户ID
	private int member;
	
	//产品ID
	private int product;
	
	//收货人
	private String name;
	
	//手机
	private String mobile;
	
	//地址
	private String addr;
	
	//状态
	private int status;
	
	//下单时间
	private long date;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setMember(int member) {
		this.member = member;
	}
	
	public int getMember() {
		return this.member;
	}
	
	public void setProduct(int product) {
		this.product = product;
	}
	
	public int getProduct() {
		return this.product;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getMobile() {
		return this.mobile;
	}
	
	public void setAddr(String addr) {
		this.addr = addr;
	}
	
	public String getAddr() {
		return this.addr;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return this.status;
	}
	
	public void setDate(Long date) {
		this.date = date;
	}
	
	public long getDate() {
		return this.date;
	}
}
