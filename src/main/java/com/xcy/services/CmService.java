package com.xcy.services;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.xcy.dao.CmDao;
import com.xcy.pojo.CmMac;
import com.xcy.pojo.CmPicture;
import com.xcy.pojo.CmUser;
import com.xcy.pojo.HeartBeat;
import com.xcy.pojo.Temperature;
import com.xcy.util.NumberTransform;
import com.xcy.util.ServerUtil;

/*
 * 数据服务层
 */
@Service
public class CmService {

	@Autowired
	private CmDao cmdao;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	public int insertPicByMacId(String macId,String picId,InputStream context){
		return this.cmdao.insertPicByMacId(macId, picId, context);
	}
	
	public int insertHeartBeat(String macId,String heartId,String ip,String port){
		return this.cmdao.insertHeartBeat(macId, heartId, ip, port);
	}
	
	/*
	 * 根据设备唯一标识 获取 ip 端口号
	 */
	public HeartBeat getIpPortByMacId(String macId){
		return this.cmdao.getIpPortByMacId(macId);
	}
	
	public List<CmPicture> getPicByMacId(String macId){
		return this.cmdao.getPicByMacId(macId);
	}
	
	@Cacheable(value = "heartBeatList", keyGenerator = "keyGenerator")
	public List<HeartBeat> getAllHeartBeatByMacId(String macId){
		List<HeartBeat> list = this.cmdao.getAllHeartBeatByMacId(macId);
//		stringRedisTemplate.opsForList().leftPush("list", JSON.toJSONString(list));
//		stringRedisTemplate.opsForValue().set("heartBeat", JSON.toJSONString(list));
		return list;
	}
	
	public int insertTemperaturecByMacId(String macId,String tId,String t) {
		return this.cmdao.insertTemperatureByMacId(macId, tId, t);
	}
	
	public List<Temperature> getAllTemperatureByMacId(String macId){
		return this.cmdao.getAllTemperatureByMacId(macId);
	}
	
	public CmPicture getMaxIndex(String macId) {
		return this.cmdao.getMaxIndex(macId);
	}
	
	public boolean getUserByUserName(String userName,String pswd) {
		CmUser u =  this.cmdao.getUserByUserName(userName);
		if(u.getPassword() == null || u.getPassword() == "") {
			return false;
		}
		if(!ServerUtil.MD5(pswd).equals(u.getPassword())) {
			return false;
		}
		return true;
	}

	public int insertMac(String userId, String macId, String macJc, String roadId, String towerId) {
		return this.cmdao.insertMac(userId, macId, macJc, roadId, towerId);
	}
	
	public List<CmMac> getMacByUserName(String userId){
		List<CmMac> list = this.cmdao.getMacByUserName(userId);
		for(CmMac c : list) {
			c.setMacId(NumberTransform.convertHexToASCII(c.getMacId()));
		}
		return list;
	}
}
