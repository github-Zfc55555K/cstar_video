package com.xcy.pojo;

/*
 * 报文实体类
 */
public class Message {

	private String msgHeader;// 报文头  0-3
	private String macId;// 设备id号 4-37
	private String sourceIp;// 源ip 地址 --- 
	private String sourcePort;// 源 端口号 ---
	private String frameType;// 帧类型 38-39
	private String msgType;// 报文类型 40-41
	private String msglength;// 报文长度 42-45
	private String currentPkg;// 当前包段 46-49
	private String totalPkg;// 总包数 50-53
	private String msgContent;// 报文内容 54 - (length - 4)
	private String chx;// 校验和  最后4个字符

	
	public Message(String msgHeader, String macId, String sourceIp, String sourcePort, String frameType, String msgType,
			String msglength, String currentPkg, String totalPkg, String msgContent, String chx) {
		this.msgHeader = msgHeader;
		this.macId = macId;
		this.sourceIp = sourceIp;
		this.sourcePort = sourcePort;
		this.frameType = frameType;
		this.msgType = msgType;
		this.msglength = msglength;
		this.currentPkg = currentPkg;
		this.totalPkg = totalPkg;
		this.msgContent = msgContent;
		this.chx = chx;
	}


	public String getMsgHeader() {
		return msgHeader;
	}


	public void setMsgHeader(String msgHeader) {
		this.msgHeader = msgHeader;
	}


	public String getMacId() {
		return macId;
	}


	public void setMacId(String macId) {
		this.macId = macId;
	}


	public String getSourceIp() {
		return sourceIp;
	}


	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}


	public String getSourcePort() {
		return sourcePort;
	}


	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}


	public String getFrameType() {
		return frameType;
	}


	public void setFrameType(String frameType) {
		this.frameType = frameType;
	}


	public String getMsgType() {
		return msgType;
	}


	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}


	public String getMsglength() {
		return msglength;
	}


	public void setMsglength(String msglength) {
		this.msglength = msglength;
	}


	public String getCurrentPkg() {
		return currentPkg;
	}


	public void setCurrentPkg(String currentPkg) {
		this.currentPkg = currentPkg;
	}


	public String getTotalPkg() {
		return totalPkg;
	}


	public void setTotalPkg(String totalPkg) {
		this.totalPkg = totalPkg;
	}


	public String getMsgContent() {
		return msgContent;
	}


	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}


	public String getChx() {
		return chx;
	}


	public void setChx(String chx) {
		this.chx = chx;
	}


}