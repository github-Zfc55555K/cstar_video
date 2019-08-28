package com.xcy.dao;

import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xcy.pojo.CmMac;
import com.xcy.pojo.CmPicture;
import com.xcy.pojo.CmUser;
import com.xcy.pojo.HeartBeat;
import com.xcy.pojo.Temperature;

/*
 * 数据层
 */
@Mapper
public interface CmDao {

	int insertPicByMacId(@Param("macId") String macId, @Param("picId") String picId,
			@Param("context") InputStream context,@Param("alarm")Integer alarm);

	int insertHeartBeat(@Param("macId") String macId, @Param("heartId") String heartId, @Param("ip") String ip,
			@Param("port") String port);

	HeartBeat getIpPortByMacId(@Param("macId") String macId);

	List<CmPicture> getPicByMacId(@Param("macId") String macId, @Param("alarm")Integer alarm);

	List<HeartBeat> getAllHeartBeatByMacId(@Param("macId") String macId);

	int insertTemperatureByMacId(@Param("macId") String macId, @Param("tId") String tId,
			@Param("temperature") String temperature);

	List<Temperature> getAllTemperatureByMacId(@Param("macId") String macId);

	CmPicture getMaxIndex(@Param("macId") String macId);

	CmUser getUserByUserName(@Param("userName") String userName);

	int insertMac(@Param("userId") String userId, @Param("macId") String macId, @Param("macJc") String macJc,
			@Param("roadId") String roadId, @Param("towerId") String towerId);
	
	List<CmMac> getMacByUserName(@Param("userId")String userId);
}
