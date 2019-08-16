package com.xcy.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableCaching
public class RedisConfiguration extends CachingConfigurerSupport {

	private static Logger lg = Logger.getLogger(RedisConfiguration.class);
	@Autowired
	private JedisConnectionFactory jedisConnectionFactory;

	/*
	 * 使用注解时自动生成 Key 
	 */
	@Bean
	@Override
	public KeyGenerator keyGenerator() {
		return (o, method, objects) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(o.getClass().getSimpleName());
            stringBuilder.append(".");
            stringBuilder.append(method.getName());
            stringBuilder.append("(");
            for (Object obj : objects) {
                stringBuilder.append(obj.toString());
            }
            stringBuilder.append(")");
            lg.info("缓存键值 -> [" + stringBuilder.toString() + "]");
            return stringBuilder.toString();
        };
	}

	@Bean
	@Override
	public CacheManager cacheManager() {
		lg.info("初始化缓存服务器管理器，设置默认缓存过期时间600s");
		return new RedisCacheManager(
	            RedisCacheWriter.nonLockingRedisCacheWriter(jedisConnectionFactory),
	            this.getRedisCacheConfigurationWithTtl(600), // 默认策略，未配置的 key 会使用这个
	            this.getRedisCacheConfigurationMap() // 指定 key 策略
	        );
	}

	/*
	 * 指定 自定义的缓存注解 并设置时间
	 * 默认使用   @Cacheable(value = "DefaultKeyTest", keyGenerator = "keyGenerator")
	 */
	private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        redisCacheConfigurationMap.put("heartBeatList", this.getRedisCacheConfigurationWithTtl(100));
        return redisCacheConfigurationMap;
    }
	
	/*
	 * 设置 TTL 
	 */
	private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
            RedisSerializationContext
                .SerializationPair
                .fromSerializer(jackson2JsonRedisSerializer)
        ).entryTtl(Duration.ofSeconds(seconds));

        return redisCacheConfiguration;
    }
	@Bean
	public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		// 设置序列化
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		// 配置redisTemplate
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		RedisSerializer stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer); // key序列化
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer); // value序列化
		redisTemplate.setHashKeySerializer(stringSerializer); // Hash key序列化
		redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer); // Hash value序列化
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Override
	@Bean
	public CacheErrorHandler errorHandler() {
		// 异常处理，当Redis发生异常时，打印日志，但是程序正常走
		CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {
			@Override
			public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
			}

			@Override
			public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
			}

			@Override
			public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
			}

			@Override
			public void handleCacheClearError(RuntimeException e, Cache cache) {
				lg.error("Redis occur handleCacheClearError：", e);
			}
		};
		return cacheErrorHandler;
	}

	/**
	 * 此内部类就是把yml的配置数据，进行读取，创建JedisConnectionFactory和JedisPool，以供外部类初始化缓存管理器使用
	 */
	@ConfigurationProperties
	class DataJedisProperties {
		@Value("${spring.redis.host}")
		private String host;
		@Value("${spring.redis.password}")
		private String password;
		@Value("${spring.redis.port}")
		private int port;
		@Value("${spring.redis.timeout}")
		private int timeout;
		@Value("${spring.redis.jedis.pool.max-idle}")
		private int maxIdle;
		@Value("${spring.redis.jedis.pool.max-wait}")
		private long maxWaitMillis;

		@Bean
		JedisConnectionFactory jedisConnectionFactory() {
			lg.info("缓存服务器启动成功，嘻嘻~");
			JedisConnectionFactory factory = new JedisConnectionFactory();
			factory.setHostName(host);
			factory.setPort(port);
			factory.setTimeout(timeout);
			factory.setPassword(password);
			return factory;
		}

		@Bean
		public JedisPool redisPoolFactory() {
			lg.info("缓存池初始化成功，连接IP：" + host + "端口号：" + port + "");
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxIdle(maxIdle);
			jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
			JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
			return jedisPool;
		}
	}

}
