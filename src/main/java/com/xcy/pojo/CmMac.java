package com.xcy.pojo;

/*
 * 设备
 */
public class CmMac {

	private String userId;
	private String macId;
	private String macJc;
	private String roadId;
	private String towerId;
	private String gpsjd;
	private String gpswd;
	private Integer msgStatus;

	public Integer getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(Integer msgStatus) {
		this.msgStatus = msgStatus;
	}

	public String getGpsjd() {
		return gpsjd;
	}

	public void setGpsjd(String gpsjd) {
		this.gpsjd = gpsjd;
	}

	public String getGpswd() {
		return gpswd;
	}

	public void setGpswd(String gpswd) {
		this.gpswd = gpswd;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMacId() {
		return macId;
	}

	public void setMacId(String macId) {
		this.macId = macId;
	}

	public String getMacJc() {
		return macJc;
	}

	public void setMacJc(String macJc) {
		this.macJc = macJc;
	}

	public String getRoadId() {
		return roadId;
	}

	public void setRoadId(String roadId) {
		this.roadId = roadId;
	}

	public String getTowerId() {
		return towerId;
	}

	public void setTowerId(String towerId) {
		this.towerId = towerId;
	}

}
