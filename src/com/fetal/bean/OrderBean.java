package com.fetal.bean;

public class OrderBean {
	
	//����ID
	private int id;
	
	//�������û�ID
	private int member;
	
	//��ƷID
	private int product;
	
	//�ջ���
	private String name;
	
	//�ֻ�
	private String mobile;
	
	//��ַ
	private String addr;
	
	//״̬
	private int status;
	
	//�µ�ʱ��
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
