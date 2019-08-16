package com.xcy.util;

/**
 * 定义常量
 */
public interface Constant {

	/*
	 * JC KZ ZT 分别代表 检测 控制 状态三种类型的报文
	 * TERMINAL 代表机器
	 * SERVER 代表本服务器
	 * TO 代表数据流向
	 * 实际报文中 全部 都是16进制数 这里省略了 0x 
	 */
	String JC_TERMINAL_TO_SERVER = "01";
	String JC_SERVER_TO_TERMINAL = "02";
	String KZ_SERVER_TO_TERMINAL = "03";
	String KZ_TERMINAL_TO_SERVER = "04";
	String ZT_TERMINAL_TO_SERVER = "05";
	String ZT_SERVER_TO_TERMINAL = "06";
	
	/**
	 * 检测 报文中 分为 温度 摄像检测
	 * TEMPERATURE 为温度
	 * PHOTO 为摄像
	 */
	String JC_TYPE_TEMPERATURE = "01";
	String JC_TYPE_PHOTO = "03";
	int KZ_MSG_PIXEL = 1; //设置像素
	int KZ_MSG_ALARM = 2; //警报设置
	int KZ_MSG_RESET = 3;//复位
	int KZ_MSG_PHOTO = 4;//手动拍摄
	
	/*
	 * 字符填充 确保占位 不为空
	 */
	String ZW_TWO_BYTE = "0001";
	String ZW_ONE_BYTE = "01";
	
	/*
	 * 报文应答 true false
	 */
	String TURE = "00";
	String ERROR = "FF";
	/*
	 *  控制
	 *  报文类型  TYPE 
	 *  KEY 参数键
	 *  VALUE 参数值
	 */
	String KZ_TYPE_RESET = "01";//复位
	String KZ_TYPE_PARAM_SET = "03";//参数设置
	String KZ_TYPE_PARAM_READ = "05";//参数读取
	String KZ_KEY_MORE_30 = "0001";//电池电量 
	String KZ_KEY_LESS_30 = "0002";
	String KZ_KEY_LESS_10 = "0003";
	String KZ_KEY_SHUTDOWN_BUZZER = "0004"; // 关闭蜂鸣器
	String KZ_KEY_TOTEL_TIME = "0005"; //蜂鸣器 响的总时间
	String KZ_KEY_INTERVAL_TIME = "0006";//蜂鸣器 间隔时间
	String KZ_KEY_CONTINUED_TIME = "0007";//蜂鸣器 持续时间
	String KZ_KEY_SET_LENS_1 = "0008";//设置镜头开关
	String KZ_KEY_SET_LENS_2 = "0009";
	String KZ_KEY_SET_LENS_3 = "000A";
	String KZ_KEY_SET_PIXEL = "000B";//设置分辨率
	
	String KZ_VALUE_ON = "00000001";// 开
	String KZ_VALUE_OFF = "00000000";// 关
	
	String KZ_VALUE_PIXEL_1 = "00000000";//分辨率 160*120
	String KZ_VALUE_PIXEL_2 = "00000001";//分辨率 320*240
	String KZ_VALUE_PIXEL_3 = "00000002";//分辨率 640*480
	
	String PIXEL_1 = "160*120";
	String PIXEL_2 = "320*240";
	String PIXEL_3 = "640*480";
}
