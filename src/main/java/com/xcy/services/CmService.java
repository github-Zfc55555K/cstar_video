package com.xcy.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.xcy.dao.CmDao;
import com.xcy.pojo.CmMac;
import com.xcy.pojo.CmPicture;
import com.xcy.pojo.CmUser;
import com.xcy.pojo.HeartBeat;
import com.xcy.pojo.Temperature;
import com.xcy.util.NumberTransform;
import com.xcy.util.ServerUtil;

import sun.misc.BASE64Encoder;

/*
 * 数据服务层
 */
@Service
public class CmService {

	@Autowired
	private CmDao cmdao;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	public int insertPicByMacId(String macId, String picId, InputStream context,Integer alarm) {
		return this.cmdao.insertPicByMacId(macId, picId, context,alarm);
	}

	public int insertHeartBeat(String macId, String heartId, String ip, String port) {
		return this.cmdao.insertHeartBeat(macId, heartId, ip, port);
	}

	/*
	 * 根据设备唯一标识 获取 ip 端口号
	 */
	public HeartBeat getIpPortByMacId(String macId) {
		return this.cmdao.getIpPortByMacId(macId);
	}

	@Cacheable(value = "TimeTo100S", keyGenerator = "keyGenerator")
	public List<String> getPicByMacId(String macId, Integer alarm) {
		List<String> data = new ArrayList<String>();
		List<CmPicture> list = this.cmdao.getPicByMacId(NumberTransform.convertASCLLToHex(macId), alarm);

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

	@Cacheable(value = "TimeTo100S", keyGenerator = "keyGenerator")
	public List<HeartBeat> getAllHeartBeatByMacId(String macId) {
		List<HeartBeat> list = this.cmdao.getAllHeartBeatByMacId(macId);
//		stringRedisTemplate.opsForList().leftPush("list", JSON.toJSONString(list));
//		stringRedisTemplate.opsForValue().set("heartBeat", JSON.toJSONString(list));
		return list;
	}

	@Cacheable(value = "TimeTo100S", keyGenerator = "keyGenerator")
	public int insertTemperaturecByMacId(String macId, String tId, String t) {
		return this.cmdao.insertTemperatureByMacId(macId, tId, t);
	}

	@Cacheable(value = "TimeTo100S", keyGenerator = "keyGenerator")
	public List<Temperature> getAllTemperatureByMacId(String macId) {
		return this.cmdao.getAllTemperatureByMacId(macId);
	}

	public CmPicture getMaxIndex(String macId) {
		return this.cmdao.getMaxIndex(macId);
	}

	public boolean getUserByUserName(String userName, String pswd) {
		CmUser u = this.cmdao.getUserByUserName(userName);
		if (u.getPassword() == null || u.getPassword() == "") {
			return false;
		}
		if (!ServerUtil.MD5(pswd).equals(u.getPassword())) {
			return false;
		}
		return true;
	}

	public int insertMac(String userId, String macId, String macJc, String roadId, String towerId) {
		return this.cmdao.insertMac(userId, macId, macJc, roadId, towerId);
	}

	public List<CmMac> getMacByUserName(String userId) {
		List<CmMac> list = this.cmdao.getMacByUserName(userId);
		for (CmMac c : list) {
			c.setMacId(NumberTransform.convertHexToASCII(c.getMacId()));
		}
		return list;
	}
}
