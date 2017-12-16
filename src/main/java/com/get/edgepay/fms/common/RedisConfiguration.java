package com.get.edgepay.fms.common;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@ComponentScan
@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisConfiguration extends CachingConfigurerSupport {

	@Autowired
	private RedisProperties redisProperties;

	/*
	 * @Bean(name="redissonClient", destroyMethod="shutdown") RedissonClient
	 * redisson() throws IOException { Config config = new Config();
	 * SingleServerConfig singleServerConfig = config.useSingleServer();
	 * 
	 * singleServerConfig.setAddress(redisProperties.getUrl()); singleServerConfig
	 * .setConnectionPoolSize(redisProperties.getPool().getMaxActive());
	 * singleServerConfig
	 * .setConnectionMinimumIdleSize(redisProperties.getPool().getMaxIdle());
	 * singleServerConfig.setDatabase(redisProperties.getDatabase());
	 * singleServerConfig.setConnectTimeout(redisProperties.getTimeout());
	 * 
	 * if (redisProperties.getPassword() != null) {
	 * singleServerConfig.setPassword(redisProperties.getPassword()); } return
	 * Redisson.create(config); }
	 */

	@Bean(name = "redissonClient", destroyMethod = "shutdown")
	RedissonClient redisson() throws IOException {
		Config config = new Config();
		if (redisProperties.getCluster() != null && redisProperties.getCluster().getNodes().size() > 1) {
			ClusterServersConfig clusterServer = config.useClusterServers();

			clusterServer.setScanInterval(2000); // cluster state scan interval
													// in milliseconds

			clusterServer.addNodeAddress(redisProperties.getCluster().getNodes()
					.toArray(new String[redisProperties.getCluster().getNodes().size()]));

			clusterServer.setMasterConnectionPoolSize(redisProperties.getPool().getMaxActive());
			clusterServer.setMasterConnectionMinimumIdleSize(redisProperties.getPool().getMaxIdle());
			clusterServer.setConnectTimeout(redisProperties.getTimeout());

			RedissonClient redisson = Redisson.create(config);
			return redisson;
		} else {
			SingleServerConfig singleServerConfig = config.useSingleServer();

			singleServerConfig.setAddress(redisProperties.getUrl());
			singleServerConfig.setConnectionPoolSize(redisProperties.getPool().getMaxActive());
			singleServerConfig.setConnectionMinimumIdleSize(redisProperties.getPool().getMaxIdle());
			// singleServerConfig.setDatabase(redisProperties.getDatabase());
			singleServerConfig.setConnectTimeout(redisProperties.getTimeout());

			if (redisProperties.getPassword() != null) {
				singleServerConfig.setPassword(redisProperties.getPassword());
			}
			return Redisson.create(config);
		}
	}
}
