package com.xcy.pojo;

public class Temperature {

	private String macId;
	private String tId;// 温度唯一标识 一般为时间
	private String temperature;// 温度

	public String getMacId() {
		return macId;
	}

	public void setMacId(String macId) {
		this.macId = macId;
	}

	public String gettId() {
		return tId;
	}

	public void settId(String tId) {
		this.tId = tId;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

}
