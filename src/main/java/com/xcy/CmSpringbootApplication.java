package com.xcy;

import java.io.UnsupportedEncodingException;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xcy.server.UdpServer;

@SpringBootApplication
@MapperScan("com.xcy.dao")
public class CmSpringbootApplication {

	public static void main(String[] args) throws UnsupportedEncodingException {
		SpringApplication.run(CmSpringbootApplication.class, args);
		new UdpServer().forwardMsg();
	}
}
