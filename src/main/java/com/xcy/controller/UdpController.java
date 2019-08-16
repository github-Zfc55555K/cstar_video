package com.xcy.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xcy.pojo.CmMac;
import com.xcy.pojo.CmPicture;
import com.xcy.pojo.HeartBeat;
import com.xcy.services.CmService;
import com.xcy.services.UdpService;
import com.xcy.util.Constant;
import com.xcy.util.NumberTransform;

import sun.misc.BASE64Encoder;

@Controller
public class UdpController {

	@Autowired
	private UdpService udpService;

	@Autowired
	private CmService cmService;

	private static Logger log = Logger.getLogger(UdpController.class);
	
	private Map<String, String> msg = null;

	/*
	 * 电池电量模块
	 */
	@ResponseBody
	@RequestMapping(value = "batteryCtrl", method = RequestMethod.GET)
	public String executeBattery(String macId, String box1, String box2, String box3) {
		return "ok";
	}

	/*
	 * 警报模块
	 */
	@ResponseBody
	@RequestMapping(value = "alarmCtrl", method = RequestMethod.GET)
	public String executeAlarm(String macId, String box1, String box2, String box3) {
//		System.out.println("测试"+box1 + box2 + box3);
		msg = new HashMap<String, String>();
		msg.put("macId", NumberTransform.convertASCLLToHex(macId));
		msg.put("alarm1", box1);
		msg.put("alarm2", box2);
		msg.put("alarm3", box3);
		int result = this.udpService.executeMessage(Constant.KZ_MSG_ALARM, msg);
		if(result > 0) {
			return "ok";
		}else {
			return "error";
		}
	}

	/*
	 * 手动拍摄模块
	 */
	@ResponseBody
	@RequestMapping(value = "photoCtrl", method = RequestMethod.GET)
	public String executePhoto(String macId, String box1, String box2, String box3) {
//		System.out.println(macId + " " + box1 + " " + box2 + " " + box3);
		msg = new HashMap<String, String>();
		if("1".equals(box1)) {
			msg.put("p", "1");
		}else if("1".equals(box2)) {
			msg.put("p", "2");
		}else if("1".equals(box3)) {
			msg.put("p", "3");
		}
		msg.put("macId", NumberTransform.convertASCLLToHex(macId));
		int result = this.udpService.executeMessage(Constant.KZ_MSG_PHOTO, msg);
		if(result > 0) {
			return "ok";
		}else {
			return "error";
		}
		
	}

	/*
	 * 像素设置模块
	 */
	@ResponseBody
	@RequestMapping(value = "pixelCtrl", method = RequestMethod.GET)
	public String executePixel(String macId, String pixel) {
		log.info("像素设置：macId:" + macId + "pixel:" + pixel);
		this.msg = new HashMap<String, String>();
		msg.put("macId", NumberTransform.convertASCLLToHex(macId));
		msg.put("pixel", pixel);
		int result = this.udpService.executeMessage(Constant.KZ_MSG_PIXEL, msg);
		if(result > 0) {
			return "ok";
		}else {
			return "error";
		}
	}

	/*
	 * 监测数据模块
	 */
	@ResponseBody
	@RequestMapping(value = "getPicture", method = RequestMethod.GET)
	public List<String> executePicture(String macId) throws IOException {
		List<String> data = new ArrayList<String>();
		List<CmPicture> list = this.cmService.getPicByMacId(NumberTransform.convertASCLLToHex(macId));

		byte[] b = null;
		for (CmPicture c : list) {
			String str = c.getPicid() + "|";
			b = NumberTransform.inputStreamToByte(c.getContext());
			BASE64Encoder encoder = new BASE64Encoder();
			String d = encoder.encode(b);
			str += d;
			data.add(str);
		}
		return data;
	}

	/*
	 * 显示心跳信息
	 */
	@ResponseBody
	@RequestMapping(value = "getHeartbeat", method = RequestMethod.GET)
	public List<HeartBeat> executeHeartbeat(String macId) {
		return this.cmService.getAllHeartBeatByMacId(NumberTransform.convertASCLLToHex(macId));
	}
	
	/*
	 * 复位
	 */
	@ResponseBody
	@RequestMapping(value = "reset", method = RequestMethod.GET)
	public String executeReset(String macId) {
		msg = new HashMap<String, String>();
		msg.put("macId", NumberTransform.convertASCLLToHex(macId));
		int  result = this.udpService.executeMessage(Constant.KZ_MSG_RESET, msg);
		if(result > 0) {
			return "ok";
		}else {
			return "error";
		}
	}
	
	/*
	 * 获得最新图片的索引  作为拍摄的照片
	 */
	@ResponseBody
	@RequestMapping(value = "getMaxIndexPic", method = RequestMethod.GET)
	public String getMaxIndex(String macId) {
		CmPicture pic = cmService.getMaxIndex(NumberTransform.convertASCLLToHex(macId));
		String data = pic.getId() + "|";
		byte[] b = NumberTransform.inputStreamToByte(pic.getContext());
		BASE64Encoder encoder = new BASE64Encoder();
		String d = encoder.encode(b);
		data += d;
		return data;
	}
	
	/*
	 * 登录
	 */
	@ResponseBody
	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String getUserByUserName(String userName,String pswd){
		if(this.cmService.getUserByUserName(userName,pswd)) {
			return "ok";
		}else {
			return "error";
		}
	}
	
	/*
	 * 添加设备
	 */
	@ResponseBody
	@RequestMapping(value = "insertmac", method = RequestMethod.GET)
	public String insertMac(String userName, String macId, String macJc, String roadId, String towerId){
		int i = this.cmService.insertMac(userName, NumberTransform.convertASCLLToHex(macId), macJc, roadId, towerId);;
		if(i > 0) {
			return "ok";
		}else {
			return "error";
		}
	}
	@ResponseBody
	@RequestMapping(value = "getAllMacByUserName", method = RequestMethod.GET)
	public List<CmMac> getMacByUserName(String userName){
		return this.cmService.getMacByUserName(userName);
	}
}
