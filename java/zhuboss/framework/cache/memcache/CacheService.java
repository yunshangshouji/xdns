package zhuboss.framework.cache.memcache;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class CacheService {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String memcacheAddress;
	private MemcachedClient client;
	public CacheService(String memcacheAddress){
		this.memcacheAddress = memcacheAddress;
		MemcachedClientBuilder memcachedClientBuilder = new    XMemcachedClientBuilder(AddrUtil.getAddresses(this.memcacheAddress));
		try {
			client= memcachedClientBuilder.build();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("connect to memcache servers fail!", e);
		}
	}
	
	public boolean set(String key, int exp, Object object){
		try {
			GetsResponse<Object> prevValue = client.gets(key);
			if(prevValue == null){
				return client.set(key, exp, object);
			}else{
				return client.cas(key,exp,object, prevValue.getCas());
			}
		} catch (Exception e) {
			logger.error("set cache fail", e);
			throw new RuntimeException(e);
		} 
	}
	
	public Object get(String key){
		try {
			return client.get(key);
		} catch (Exception e) {
			logger.error("set cache fail", e);
			throw new RuntimeException(e);
		}
	}
	
	public void del(String key){
		try {
			client.delete(key);
		} catch (Exception e) {
			logger.error("del cache fail", e);
			throw new RuntimeException(e);
		} 
	}
}
