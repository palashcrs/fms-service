package com.get.edgepay.fms.util;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utility class for updating and reading from cache
 *
 */
@Component
public class CacheUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheUtil.class);

	@Autowired
	RedissonClient redissonClient;

	public <T, V> void addToCache(String cacheName, T key, V value) {
		RMap<T, V> cache = redissonClient.getMap(cacheName);
		cache.fastPut(key, value);
	}

	public <T, V> V getFromCache(String cacheName, T key, Class<V> valueType) {
		RMap<T, V> cache = redissonClient.getMap(cacheName);
		// Get Value from cache with key
		return cache.get(key);
	}

	public <T, V> Set<Entry<T, V>> getFromCache(String cacheName, Class<V> valueType) {
		RMap<T, V> cache = redissonClient.getMap(cacheName);
		// Get Value from cache with key
		return cache.entrySet();
	}

	public <T, V> void addToCache(String cacheName, T key, V value, long keyTimeOut) {
		RMap<T, V> cache = redissonClient.getMap(cacheName);
		cache.expire(keyTimeOut, TimeUnit.MILLISECONDS);
		cache.fastPut(key, value);
	}

	public <T, V> boolean removeFromCache(String cacheName, T key, Class<V> valueType) {
		RMap<T, V> cache = redissonClient.getMap(cacheName);
		return cache.delete();
	}

	public <T, V> boolean removeFromCache(String cacheName, Class<V> valueType) {
		RMap<T, V> cache = redissonClient.getMap(cacheName);
		return cache.delete();
	}

	public <T, V> boolean getAndUpdateCacheWithLock(String cacheName, T key, V value, long keyTimeOut) {
		RLock lock = redissonClient.getLock(cacheName);
		LOGGER.info("Checking Lock Status for {} status {}", cacheName, lock.isLocked());
		if (lock.isLocked())
			return true;
		lock.lock();
		LOGGER.info("Locked {} status {}", cacheName, lock.isLocked());
		RMap<T, V> cache = redissonClient.getMap(cacheName);
		cache.expire(keyTimeOut, TimeUnit.MILLISECONDS);
		cache.fastPut(key, value);

		return false;
	}

	public void unlockScheduler(String cacheName) {
		RLock lock = redissonClient.getLock(cacheName);
		LOGGER.info("Is scheduler locked? {} Releasing lock", lock.isLocked());
		lock.unlock();
	}
}
