package com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model;

public class QueryRepairResModel {
	/**
	 * id
	 **/
	private int f_id;
	/**
	 * 账户
	 **/
	private String f_accounts;
	/**
	 * 报修单号
	 **/
	private String f_repairNo;
	/**
	 * 建立日期
	 **/
	private String f_date;
	/**
	 * 更新日期
	 **/
	private String f_time;
	/**
	 * 处理状态
	 **/
	private int f_state;
	/**
	 * 手机号
	 **/
	private String f_phone;
	/**
	 * 处理者
	 **/
	private String f_admin;
	/**
	 * 回复者
	 **/
	private String f_replyAdmin;
	/**
	 * 问题类型
	 **/
	private int f_type;
	/**
	 * 内容
	 **/
	private String f_content;
	/**
	 * 回复内容
	 **/
	private String f_replyContent;
	/**
	 * 网址
	 **/
	private String f_webSite;
	/**
	 * 添加报修单的IP地址
	 **/
	private String f_ip;

	public int getF_id() {
		return f_id;
	}

	public void setF_id(int f_id) {
		this.f_id = f_id;
	}

	public String getF_accounts() {
		return f_accounts;
	}

	public void setF_accounts(String f_accounts) {
		this.f_accounts = f_accounts;
	}

	public String getF_repairNo() {
		return f_repairNo;
	}

	public void setF_repairNo(String f_repairNo) {
		this.f_repairNo = f_repairNo;
	}

	public String getF_date() {
		return f_date;
	}

	public void setF_date(String f_date) {
		this.f_date = f_date;
	}

	public String getF_time() {
		return f_time;
	}

	public void setF_time(String f_time) {
		this.f_time = f_time;
	}

	public int getF_state() {
		return f_state;
	}

	public void setF_state(int f_state) {
		this.f_state = f_state;
	}

	public String getF_phone() {
		return f_phone;
	}

	public void setF_phone(String f_phone) {
		this.f_phone = f_phone;
	}

	public String getF_admin() {
		return f_admin;
	}

	public void setF_admin(String f_admin) {
		this.f_admin = f_admin;
	}

	public String getF_replyAdmin() {
		return f_replyAdmin;
	}

	public void setF_replyAdmin(String f_replyAdmin) {
		this.f_replyAdmin = f_replyAdmin;
	}

	public int getF_type() {
		return f_type;
	}

	public void setF_type(int f_type) {
		this.f_type = f_type;
	}

	public String getF_content() {
		return f_content;
	}

	public void setF_content(String f_content) {
		this.f_content = f_content;
	}

	public String getF_replyContent() {
		return f_replyContent;
	}

	public void setF_replyContent(String f_replyContent) {
		this.f_replyContent = f_replyContent;
	}

	public String getF_webSite() {
		return f_webSite;
	}

	public void setF_webSite(String f_webSite) {
		this.f_webSite = f_webSite;
	}

	public String getF_ip() {
		return f_ip;
	}

	public void setF_ip(String f_ip) {
		this.f_ip = f_ip;
	}

	@Override
	public String toString() {
		return "QueryRepairResModel{" +
				"f_id=" + f_id +
				", f_accounts='" + f_accounts + '\'' +
				", f_repairNo='" + f_repairNo + '\'' +
				", f_date='" + f_date + '\'' +
				", f_time='" + f_time + '\'' +
				", f_state=" + f_state +
				", f_phone='" + f_phone + '\'' +
				", f_admin='" + f_admin + '\'' +
				", f_replyAdmin='" + f_replyAdmin + '\'' +
				", f_type=" + f_type +
				", f_content='" + f_content + '\'' +
				", f_replyContent='" + f_replyContent + '\'' +
				", f_webSite='" + f_webSite + '\'' +
				", f_ip='" + f_ip + '\'' +
				'}';
	}
}
