package com.xcy.server;

import java.util.HashMap;

public interface IUdpServer {

	void forwardMsg();//转发消息
	String calChxNum(String context);//计算校验位
}
