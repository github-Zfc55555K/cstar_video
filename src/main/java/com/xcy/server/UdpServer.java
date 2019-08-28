package com.xcy.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.xcy.pojo.Message;
import com.xcy.pojo.ReplyMsg;
import com.xcy.services.UdpService;
import com.xcy.util.Constant;
import com.xcy.util.NumberTransform;
import com.xcy.util.ServerUtil;

/**
 * UDP 服务器 只负责 ： 0 解析配置文件 加载 服务器端口号 以及 最大 接收字节数组 1 监听端口 ，收消息 2 计算校验和 3 发送 应答报文 4
 * 将报文字符串封装为 message 5 将 message 交给 service
 */
public class UdpServer implements IUdpServer {

	private Properties config;

	private ConcurrentHashMap<String, Message> messages;

	private UdpService udpService;

	public static DatagramSocket socket = null;

	private static Logger log = Logger.getLogger(UdpServer.class);

	private static long t1 = 0;

	public UdpServer() {
		log.info("UDP服务器启动,读取配置文件.....................");
		parseConfig("config/server.properties");
		this.messages = new ConcurrentHashMap<>();
		this.udpService = new UdpService();
	}

	@Override
	public void forwardMsg() {
		int port = Integer.parseInt(this.config.getProperty("server.port"));
		int size = Integer.parseInt(this.config.getProperty("server.rec_size"));
		log.info("UDP服务器端口号:" + port);
		byte[] msg = new byte[size];
		try {
			// 创建socket
			UdpServer.socket = new DatagramSocket(port);
			// 声明接收数据的包
			DatagramPacket fromPacket = new DatagramPacket(msg, msg.length);
			boolean flag = true;
			while (flag) {
				// 循环接收包
				UdpServer.socket.receive(fromPacket);
				// 发送方地址
				InetAddress fromInetAddress = fromPacket.getAddress();
				// 发送方端口号
				int formPort = fromPacket.getPort();
				log.info("源地址:" + fromInetAddress.toString() + " 源端口号:" + formPort);
				// 发送方消息 转字符串
//				byte[] b = fromPacket.getData();
				System.out.println(fromPacket.getLength());
				// 去掉空白字符
				byte[] b = Arrays.copyOf(msg, fromPacket.getLength());
				// String fromStr = new String(fromPacket.getData(), 0,
				// fromPacket.getLength());
				String fromStr = NumberTransform.bytesToHex(b);
				if (fromStr.length() < 60) {
					log.info("源地址:" + fromInetAddress.toString() + " 源端口号:" + formPort + "消息长度小于30字节，无效报文，舍弃");
					continue;
				}
				String msgHeader = fromStr.substring(0, 4); // 报文头
				String macId = fromStr.substring(4, 38);
				String frameType = fromStr.substring(38, 40); // 帧类型
				String msgType = fromStr.substring(40, 42);// 报文类型
				String msglength = fromStr.substring(42, 46); // 报文内容长度
				String currentPkg = fromStr.substring(46, 50);
				String totalPkg = fromStr.substring(50, 54);
				String msgContent = fromStr.substring(54, fromStr.length() - 4);
				// -------------计算校验和------------
				String msgBody = fromStr.substring(0, fromStr.length() - 4); // 不包含校验和的内容
				String msgChx = fromStr.substring(fromStr.length() - 2, fromStr.length()).toUpperCase();
				String chx = this.calChxNum(msgBody).toUpperCase();
				// -------------应答报文-------------
				ReplyMsg rm = null;
				Message message = null;
				byte[] sendBytes = null;
				if (!msgChx.equals(chx)) {
					// 发送失败标志
					if (Constant.JC_TERMINAL_TO_SERVER.equals(frameType)) {
						rm = new ReplyMsg(msgHeader, macId, fromInetAddress.toString(), formPort + "",
								Constant.JC_SERVER_TO_TERMINAL, msgType, msglength, Constant.ZW_TWO_BYTE,
								Constant.ZW_TWO_BYTE, Constant.ERROR);
					} else if (Constant.KZ_TERMINAL_TO_SERVER.equals(frameType)) {
						rm = new ReplyMsg(msgHeader, macId, fromInetAddress.toString(), formPort + "",
								Constant.KZ_SERVER_TO_TERMINAL, msgType, msglength, Constant.ZW_TWO_BYTE,
								Constant.ZW_TWO_BYTE, Constant.ERROR);
					} else if (Constant.ZT_TERMINAL_TO_SERVER.equals(frameType)) {
						rm = new ReplyMsg(msgHeader, macId, fromInetAddress.toString(), formPort + "",
								Constant.ZT_SERVER_TO_TERMINAL, msgType, msglength, Constant.ZW_TWO_BYTE,
								Constant.ZW_TWO_BYTE, Constant.ERROR);
					} else {
//						rm = new ReplyMsg();
						log.info("<ERROR>不存在的报文类型--------舍弃");
						continue;
					}
					sendBytes = NumberTransform.hexToBytes(rm.toString());
					DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, fromInetAddress,
							formPort);
					UdpServer.socket.send(sendPacket);
					log.info("接收------计算校验和<失败>,报文内容为:" + fromStr.substring(0, fromStr.length() - 4) + "校验和为：" + msgChx
							+ "服务器计算的校验和为:" + chx);
					log.info("发送-------应答报文:" + rm.toString());
					continue;
				} else {
					// 发送成功标志
					if (Constant.JC_TERMINAL_TO_SERVER.equals(frameType)) {
						// 监测报文
						rm = new ReplyMsg(msgHeader, macId, fromInetAddress.toString(), formPort + "",
								Constant.JC_SERVER_TO_TERMINAL, msgType, msglength, Constant.ZW_TWO_BYTE,
								Constant.ZW_TWO_BYTE, Constant.TURE);
						// 查看包完整性 等待偏移量=最大包数 目前只存在视频图像传输用到多个包发送数据 这里还是统一处理 在service 中再判断是哪种检测
						message = messages.get(macId);
						if (message == null) {
							// 第一个包 ，在map中无记录
							message = new Message(msgHeader, macId, fromInetAddress.toString(), formPort + "",
									frameType, msgType, msglength, currentPkg, totalPkg, msgContent, chx);
							if (fromStr.substring(46, 50).equals(fromStr.substring(50, 54))) {
								log.info("总包数：" + fromStr.substring(50, 54) + "---------当前包数:"
										+ fromStr.substring(46, 50));
								if ("0001".equals(fromStr.substring(46, 50))) {
									t1 = System.currentTimeMillis();
								}
								// 已经达到 最大偏移量 直接进行处理 不存入Map中
								// 对message进行处理
								this.udpService.executeZJTerminalToServerMessage(message, t1);
							} else {
								log.info("总包数：" + fromStr.substring(50, 54) + "---------当前包数:"
										+ fromStr.substring(46, 50));
								if ("0001".equals(fromStr.substring(46, 50))) {
									t1 = System.currentTimeMillis();
								}
								// 没有达到最大 偏移量
								// 存入map中 等待下此继续获取数据
								messages.put(macId, message);
							}

						} else {
							// 在 map 中有记录 判断是否 达到最大偏移量
							if (fromStr.substring(46, 50).equals(fromStr.substring(50, 54))) {
								log.info("总包数：" + fromStr.substring(50, 54) + "---------当前包数:"
										+ fromStr.substring(46, 50));
								if ("0001".equals(fromStr.substring(46, 50))) {
									t1 = System.currentTimeMillis();
								}
								// 达到最大偏移量
								message.setMsgContent(message.getMsgContent() + msgContent);
								// 清空 Map 中的报文缓存
								messages.remove(macId);
								if (messages.get(macId) != null) {
									// 防止缓存
									messages.remove(macId);
								}
								// 对massage进行处理
								this.udpService.executeZJTerminalToServerMessage(message, t1);
							} else {
								// 未达到最大偏移量
								log.info("总包数：" + fromStr.substring(50, 54) + "---------当前包数:"
										+ fromStr.substring(46, 50));
								if ("0001".equals(fromStr.substring(46, 50))) {
									t1 = System.currentTimeMillis();
								}
								message.setMsgContent(message.getMsgContent() + msgContent);
								messages.put(macId, message);
							}
						}
						sendBytes = NumberTransform.hexToBytes(rm.toString());
						DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, fromInetAddress,
								formPort);
						socket.send(sendPacket);
//						log.info("接收------<监测报文>------计算校验和成功,报文内容为:" + fromStr.substring(0, fromStr.length() - 4)
//								+ "校验和为：" + msgChx + "服务器计算的校验和为:" + chx);
						log.info("接收------<监测报文>------计算校验和成功");
						log.info("发送------应答报文:" + rm.toString());
					} else if (Constant.KZ_TERMINAL_TO_SERVER.equals(frameType)) {
						// 控制报文
						rm = new ReplyMsg(msgHeader, macId, fromInetAddress.toString(), formPort + "",
								Constant.KZ_SERVER_TO_TERMINAL, msgType, msglength, Constant.ZW_TWO_BYTE,
								Constant.ZW_TWO_BYTE, Constant.TURE);
						message = new Message(msgHeader, macId, fromInetAddress.toString(), formPort + "", frameType,
								msgType, msglength, currentPkg, totalPkg, msgContent, chx);
						this.udpService.executeKZTerminalToServerMessage(message);
						log.info("接收------<控制报文>------计算校验和成功,报文内容为:" + fromStr.substring(0, fromStr.length() - 4)
								+ "校验和为：" + msgChx + "服务器计算的校验和为:" + chx);
					} else if (Constant.ZT_TERMINAL_TO_SERVER.equals(frameType)) {
						// 状态报文 包括 心跳 和 报警时的图片（这里由于硬件那边设计放在状态里面 所以将监测状态报文的直接copy过来）
						rm = new ReplyMsg(msgHeader, macId, fromInetAddress.toString(), formPort + "",
								Constant.ZT_TERMINAL_TO_SERVER, msgType, msglength, Constant.ZW_TWO_BYTE,
								Constant.ZW_TWO_BYTE, Constant.TURE);
						// 查看包完整性 等待偏移量=最大包数 目前只存在视频图像传输用到多个包发送数据 这里还是统一处理 在service 中再判断是哪种检测
						message = messages.get(macId);
						if (message == null) {
							// 第一个包 ，在map中无记录
							message = new Message(msgHeader, macId, fromInetAddress.toString(), formPort + "",
									frameType, msgType, msglength, currentPkg, totalPkg, msgContent, chx);
							if (fromStr.substring(46, 50).equals(fromStr.substring(50, 54))) {
								log.info("总包数：" + fromStr.substring(50, 54) + "---------当前包数:"
										+ fromStr.substring(46, 50));
								if ("0001".equals(fromStr.substring(46, 50))) {
									t1 = System.currentTimeMillis();
								}
								// 已经达到 最大偏移量 直接进行处理 不存入Map中
								// 对message进行处理
								this.udpService.executeZTerminalToServerTMessage(message, t1,
										fromInetAddress.toString(), formPort + "");
							} else {
								log.info("总包数：" + fromStr.substring(50, 54) + "---------当前包数:"
										+ fromStr.substring(46, 50));
								if ("0001".equals(fromStr.substring(46, 50))) {
									t1 = System.currentTimeMillis();
								}
								// 没有达到最大 偏移量
								// 存入map中 等待下此继续获取数据
								messages.put(macId, message);
							}

						} else {
							// 在 map 中有记录 判断是否 达到最大偏移量
							if (fromStr.substring(46, 50).equals(fromStr.substring(50, 54))) {
								log.info("总包数：" + fromStr.substring(50, 54) + "---------当前包数:"
										+ fromStr.substring(46, 50));
								if ("0001".equals(fromStr.substring(46, 50))) {
									t1 = System.currentTimeMillis();
								}
								// 达到最大偏移量
								message.setMsgContent(message.getMsgContent() + msgContent);
								// 清空 Map 中的报文缓存
								messages.remove(macId);
								if (messages.get(macId) != null) {
									// 防止缓存
									messages.remove(macId);
								}
								// 对massage进行处理
								this.udpService.executeZTerminalToServerTMessage(message, t1,
										fromInetAddress.toString(), formPort + "");
							} else {
								// 未达到最大偏移量
								log.info("总包数：" + fromStr.substring(50, 54) + "---------当前包数:"
										+ fromStr.substring(46, 50));
								if ("0001".equals(fromStr.substring(46, 50))) {
									t1 = System.currentTimeMillis();
								}
								message.setMsgContent(message.getMsgContent() + msgContent);
								messages.put(macId, message);
							}
						}
						sendBytes = NumberTransform.hexToBytes(rm.toString());
						DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, fromInetAddress,
								formPort);
						socket.send(sendPacket);
//						log.info("接收------<状态报文>------计算校验和成功,报文内容为:" + fromStr.substring(0, fromStr.length() - 4)
//								+ "校验和为：" + msgChx + "服务器计算的校验和为:" + chx);
						log.info("接收------<状态报文>------计算校验和成功");
						log.info("发送------应答报文:" + rm.toString());
					}
				}

				fromPacket.setLength(size);

			}
			socket.close();
		} catch (

		SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parseConfig(String path) {
		this.config = ServerUtil.getConfig(path);

	}

	@Override
	public String calChxNum(String context) {
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
		int mod = total % 256;
		String hex = Integer.toHexString(mod);
		len = hex.length();
		// 如果不够校验位的长度，补0,这里用的是两位校验
		if (len < 2) {
			hex = "0" + hex;
		}
		return hex;
	}

}
