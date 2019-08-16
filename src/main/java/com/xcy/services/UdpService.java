package com.xcy.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xcy.pojo.ControlMsg;
import com.xcy.pojo.HeartBeat;
import com.xcy.pojo.Message;
import com.xcy.server.UdpServer;
import com.xcy.util.Constant;
import com.xcy.util.NumberTransform;

@Component
public class UdpService {

	private static Logger log = Logger.getLogger(UdpService.class);

	@Autowired
	private CmService cmService;

	private static UdpService udpService;

	@PostConstruct
	public void init() {
		udpService = this;
		udpService.cmService = this.cmService;
	}

	/*
	 * 处理控制层传过来的 参数 拼接成 报文 并发送报文
	 */
	public int executeMessage(int type, Map<String, String> msg) {
		// 从心跳中获取 ip 和 port
		HeartBeat hb = this.cmService.getIpPortByMacId(msg.get("macId"));
		if (hb == null) {
			return -1;
		}
		ControlMsg controlMsg;
		if (type == Constant.KZ_MSG_PIXEL) {
			// 设置像素
			String content = "";
			if (Constant.PIXEL_1.equals(msg.get("pixel"))) {
				content = "0001" + Constant.KZ_KEY_SET_PIXEL + Constant.KZ_VALUE_PIXEL_1;
			} else if (Constant.PIXEL_2.equals(msg.get("pixel"))) {
				content = "0001" + Constant.KZ_KEY_SET_PIXEL + Constant.KZ_VALUE_PIXEL_2;
			} else if (Constant.PIXEL_3.equals(msg.get("pixel"))) {
				content = "0001" + Constant.KZ_KEY_SET_PIXEL + Constant.KZ_VALUE_PIXEL_3;
			} else {
				return -1;
			}
			controlMsg = new ControlMsg("0102", hb.getMacId(), Constant.KZ_SERVER_TO_TERMINAL,
					Constant.KZ_TYPE_PARAM_SET, "0008", "0001", "0001", content);
			this.sendMessage(hb.getMacIp(), Integer.parseInt(hb.getMacPort()), controlMsg.toString());
			log.info("发送------<控制报文 像素设置>------" + controlMsg.toString());
		} else if (type == Constant.KZ_MSG_ALARM) {
			// 报警设置
			String content = "0003" + Constant.KZ_KEY_TOTEL_TIME
					+ NumberTransform.numToHex32(Integer.parseInt(msg.get("alarm1"))) + Constant.KZ_KEY_INTERVAL_TIME
					+ NumberTransform.numToHex32(Integer.parseInt(msg.get("alarm2"))) + Constant.KZ_KEY_CONTINUED_TIME
					+ NumberTransform.numToHex32(Integer.parseInt(msg.get("alarm3")));
			controlMsg = new ControlMsg("0102", hb.getMacId(), Constant.KZ_SERVER_TO_TERMINAL,
					Constant.KZ_TYPE_PARAM_SET, "0014", "0001", "0001", content);
			this.sendMessage(hb.getMacIp(), Integer.parseInt(hb.getMacPort()), controlMsg.toString());
			log.info("发送------<控制报文 报警设置>------" + controlMsg.toString());
		} else if (type == Constant.KZ_MSG_RESET) {
			// 复位
			String content = Constant.ZW_TWO_BYTE;
			controlMsg = new ControlMsg("0102", hb.getMacId(), Constant.KZ_SERVER_TO_TERMINAL, Constant.KZ_TYPE_RESET,
					"0002", "0001", "0001", content);
			this.sendMessage(hb.getMacIp(), Integer.parseInt(hb.getMacPort()), controlMsg.toString());
			log.info("发送------<控制报文 复位>------" + controlMsg.toString());
		} else if (type == Constant.KZ_MSG_PHOTO) {
			// 手动拍摄
			if ("1".equals(msg.get("p"))) {
				String content = "0001" + Constant.KZ_KEY_SET_LENS_1 + Constant.KZ_VALUE_ON;
				controlMsg = new ControlMsg("0102", hb.getMacId(), Constant.KZ_SERVER_TO_TERMINAL,
						Constant.KZ_TYPE_PARAM_SET, "0008", "0001", "0001", content);
				this.sendMessage(hb.getMacIp(), Integer.parseInt(hb.getMacPort()), controlMsg.toString());
				log.info("发送------<控制报文 手动拍摄>------" + controlMsg.toString());
			} else if ("2".equals(msg.get("p"))) {
				String content = "0001" + Constant.KZ_KEY_SET_LENS_2 + Constant.KZ_VALUE_ON;
				controlMsg = new ControlMsg("0102", hb.getMacId(), Constant.KZ_SERVER_TO_TERMINAL,
						Constant.KZ_TYPE_PARAM_SET, "0008", "0001", "0001", content);
				this.sendMessage(hb.getMacIp(), Integer.parseInt(hb.getMacPort()), controlMsg.toString());
				log.info("发送------<控制报文 手动拍摄>------" + controlMsg.toString());
			} else if ("3".equals(msg.get("p"))) {
				String content = "0001" + Constant.KZ_KEY_SET_LENS_3 + Constant.KZ_VALUE_ON;
				controlMsg = new ControlMsg("0102", hb.getMacId(), Constant.KZ_SERVER_TO_TERMINAL,
						Constant.KZ_TYPE_PARAM_SET, "0008", "0001", "0001", content);
				this.sendMessage(hb.getMacIp(), Integer.parseInt(hb.getMacPort()), controlMsg.toString());
				log.info("发送------<控制报文 手动拍摄>------" + controlMsg.toString());
			}
		}
		return 1;
	}

	/*
	 * 处理检查报文 终端到服务器
	 */
	public void executeZJTerminalToServerMessage(Message message, long time) {
		if (Constant.JC_TYPE_PHOTO.equals(message.getMsgType())) {
			// 如果是摄像
			String context = message.getMsgContent();
			log.info("<监测报文>-------报文：" + context);
			if (!context.startsWith("FFD8") || !context.endsWith("FFD9")) {
				log.info("<监测报文>-----内容 :不完整（不是以FFD8开头 & FFD9结尾） 舍弃 !!");
				return;
			}
			long t2 = System.currentTimeMillis();
			log.info("<监测报文>-----本次接收时间" + (t2 - time) + "毫秒");
			byte[] b = NumberTransform.hexToBytes(context);
			InputStream is = NumberTransform.byteToInputStream(b);
			UdpService.udpService.cmService.insertPicByMacId(message.getMacId(), NumberTransform.getData(), is);

		} else if (Constant.JC_TYPE_TEMPERATURE.equals(message.getMsgType())) {
			String context = message.getMsgContent();
			log.info("<监测报文>------温度：" + context);
			String temperature = NumberTransform.signedHexToInt(context);
			UdpService.udpService.cmService.insertTemperaturecByMacId(message.getMacId(), NumberTransform.getData(),
					temperature);
		}

	}

	/*
	 * 处理控制报文 终端到服务器
	 */
	public void executeKZTerminalToServerMessage(Message message) {

	}

	/*
	 * 处理状态报文 终端到服务器
	 */
	public void executeZTerminalToServerTMessage(String ip, String port, Message message) {
		// 上传心跳
		UdpService.udpService.cmService.insertHeartBeat(message.getMacId(), NumberTransform.getData(), ip.substring(1),
				port);
	}

	/*
	 * 负责发送报文
	 */
	public void sendMessage(String ip, int port, String msg) {
		InetAddress inet = null;
		try {
			inet = InetAddress.getByName(ip);
			byte[] bytes = NumberTransform.hexToBytes(msg);
			DatagramPacket fromPacket = new DatagramPacket(bytes, bytes.length, inet, port);
			UdpServer.socket.send(fromPacket);
			log.info("发送------<控制报文>------：" + msg + "<ip>+" + ip + "<端口号>:" + port);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
