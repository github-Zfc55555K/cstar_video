package com.xcy.pojo;

/**
 * 应答报文
 *
 */
public class ReplyMsg {

	private String msgHeader;// 报文头 0-3
	private String macId;// 设备id号 4-37
	private String sourceIp;// 源ip 地址 ---
	private String sourcePort;// 源 端口号 ---
	private String frameType;// 帧类型 38-39
	private String msgType;// 报文类型 40-41
	private String msglength;// 报文长度 42-45
	private String currentPkg;// 当前包段 46-49
	private String totalPkg;// 总包数 50-53
	private String msgStatus;// 报文状态 54 - (length - 4)
	private String chx;// 校验和 最后4个字符

	public ReplyMsg(){
		
	}
	
	public ReplyMsg(String msgHeader, String macId, String sourceIp, String sourcePort, String frameType,
			String msgType, String msglength, String currentPkg, String totalPkg, String msgStatus) {
		this.msgHeader = msgHeader;
		this.macId = macId;
		this.sourceIp = sourceIp;
		this.sourcePort = sourcePort;
		this.frameType = frameType;
		this.msgType = msgType;
		this.msglength = msglength;
		this.currentPkg = currentPkg;
		this.totalPkg = totalPkg;
		this.msgStatus = msgStatus;
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

	public String getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(String msgStatus) {
		this.msgStatus = msgStatus;
	}

	public String getChx() {
		String context = this.msgHeader + this.macId + this.frameType + this.msgType + this.msglength + this.currentPkg
				+ this.totalPkg + this.msgStatus;
		// 判断为空
		if (context == null || context.equals("")) {
			return "0";
		}
		// 包含 非十六进制数 防止乱码 造成程序终止
		String regex = "^[A-Fa-f0-9]+$";
		if (!context.matches(regex)) {
			return "0";
		}
		int total = 0;
		int len = context.length();
		int num = 0;
		while (num < len) {
			String s = context.substring(num, num + 2);
			total += Integer.parseInt(s, 16);
			num = num + 2;
		}
		/**
		 * 用256求余最大是255，即16进制的FF
		 */
		int mod = total % 65536;
		String hex = Integer.toHexString(mod);

		len = hex.length();
		// 如果不够校验位的长度，补0
		if (len == 1) {
			hex = "000" + hex;
		} else if (len == 2) {
			hex = "00" + hex;
		} else if (len == 3) {
			hex = "0" + hex;
		}
		return hex.toUpperCase();
	}

	@Override
	public String toString() {
		return this.msgHeader + this.macId + this.frameType + this.msgType + this.msglength + this.currentPkg
				+ this.totalPkg + this.msgStatus + this.getChx();
	}

}
