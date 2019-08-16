package com.xcy.pojo;

import java.io.InputStream;
import java.io.Serializable;

/*
 * 图片实体类
 */
public class CmPicture implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7927391987190134320L;
	private Integer id;//自增序列
	private String picid;// 图片唯一标识 记录了存入时的系统时间（毫秒）
	private String macid;// 设备id
	private InputStream context;// 图片内容

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPicid() {
		return picid;
	}

	public void setPicid(String picid) {
		this.picid = picid;
	}

	public String getMacid() {
		return macid;
	}

	public void setMacid(String macid) {
		this.macid = macid;
	}

	public InputStream getContext() {
		return context;
	}

	public void setContext(InputStream context) {
		this.context = context;
	}

}
