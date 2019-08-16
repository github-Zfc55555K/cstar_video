package com.xcy.pojo;

/*
 * 心跳
 */
public class HeartBeat {

	private String macId;
	private String heartId;
	private String macIp;
	private String macPort;

	public String getMacId() {
		return macId;
	}

	public void setMacId(String macId) {
		this.macId = macId;
	}

	public String getHeartId() {
		return heartId;
	}

	public void setHeartId(String heartId) {
		this.heartId = heartId;
	}

	public String getMacIp() {
		return macIp;
	}

	public void setMacIp(String macIp) {
		this.macIp = macIp;
	}

	public String getMacPort() {
		return macPort;
	}

	public void setMacPort(String macPort) {
		this.macPort = macPort;
	}

}
