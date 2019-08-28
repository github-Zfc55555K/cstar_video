package com.xcy.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.xcy.server.UdpServer;

public class NumberTransform {

	private static Logger log = Logger.getLogger(NumberTransform.class);
	
	/*
	 * 从UDP 中接收到的为 byte数组转16进制
	 */
	public static String bytesToHex(byte[] bytes) throws UnsupportedEncodingException {
		String HEXES = "0123456789ABCDEF";
		if (bytes == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * bytes.length);
		for (final byte b : bytes) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	/*
	 * 将16进制的字符串转成字节数组
	 */
	public static byte[] hexToBytes(String str) {
		//检测是否包含不为16进制的数字
		String regex = "^[A-Fa-f0-9]+$";
		if (!str.matches(regex)) {
			log.info("<ERROR>16进账转字节数组：包含非16进制字符");
			return null;
		}
		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < str.length() / 2; i++) {
			String subStr = str.substring(i * 2, i * 2 + 2);
			bytes[i] = (byte) Integer.parseInt(subStr, 16);
		}
		return bytes;
	}

	/*
	 * 取设备的唯一标识 将 ID 中的 十六进制转为 ASCII
	 */
	public static String convertHexToASCII(String hex) {
		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < hex.length() - 1; i += 2) {
			String output = hex.substring(i, (i + 2));
			int decimal = Integer.parseInt(output, 16);
			sb.append((char) decimal);
			temp.append(decimal);
		}
		return sb.toString();
	}

	/*
	 * 取温度 将带符号的十六进制数 转为10进制
	 */
	public static String signedHexToInt(String hex) {
		return String.valueOf((Integer.valueOf(hex, 16).shortValue()));
	}

	/*
	 * 无符号 十六进制转 10 进制
	 */
	public static Integer unsignedHexToInt(String hex) {
		return Integer.parseInt(hex, 16);
	}

	/*
	 * 十进制 转 无符号 十六进制
	 */
	public static String intToUnsignedHex(int i) {
		return Integer.toHexString(i).toUpperCase();
	}

	/*
	 * 字节数组转为 inputStream 方便存入数据库中
	 */
	public static InputStream byteToInputStream(byte[] b) {
		return new ByteArrayInputStream(b);
	}

	public static String getData() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	/*
	 * inputStream转为 字节数组
	 */
	public static byte[] inputStreamToByte(InputStream is) {
		try {
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
		}
		return null;
	}

	/*
	 * 数字转16进制不足位补零
	 */
	public static String numToHex32(int b) {
		return String.format("%08x", b).toUpperCase();
	}

	/*
	 * ASCLL 码 转 16进制
	 */
	public static String convertASCLLToHex(String str) {
		char[] chars = str.toCharArray();
		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int) chars[i]));
		}
		return hex.toString();
	}

}
