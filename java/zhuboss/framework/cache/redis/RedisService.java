package zhuboss.framework.cache.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import zhuboss.framework.util.JavaUtil;


public class RedisService implements InitializingBean {
	Logger logger = LoggerFactory.getLogger(RedisService.class);
	
	JedisPool jedisPool ;
	private String address;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		jedisPool = new JedisPool(new JedisPoolConfig(),address.split(":")[0],Integer.parseInt(address.split(":")[1]));
		
	}

	public boolean exists(final String key){
		return (Boolean)this.execute(new ExecuteTemplate() {
			@Override
			public Object execute(Jedis jedis) {
				return jedis.exists(JavaUtil.objectToBytes(key));
			}
			});
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> hgetAllObjects(final String key){
		return (Map<String, Object> )this.execute(new ExecuteTemplate() {
			@Override
			public Object execute(Jedis jedis) {
                    byte[] bkey = JavaUtil.objectToBytes(key);
                    Map<byte[], byte[]> all = jedis.hgetAll(bkey);
                    Map<String, Object> allObjs = new HashMap<String, Object>();
                    logger.debug("get all the objects with key : " + key);
                    for (Entry<byte[], byte[]> item : all.entrySet()) {
                        if (item.getKey() != null && item.getValue() != null && item != null) {
                            String _key = (String) JavaUtil.bytesToObject(item.getKey());
                            Object _value = JavaUtil.bytesToObject(item.getValue());
                            allObjs.put(_key, _value);
                        } else {
                            logger.debug(item.getKey() + ":" + item.getValue());
                        }
                    }
                    return allObjs;
            }
		});
	}
	
	public void hdelObject(final String key, final String field) {
		this.execute(new ExecuteTemplate() {
			@Override
			public Object execute(Jedis jedis) {
                    byte[] bkey = JavaUtil.objectToBytes(key);
                    byte[] bfield = JavaUtil.objectToBytes(field);
                    logger.debug("remove the object from redis with key " + key + ":" + field);
                    return jedis.hdel(bkey, bfield);
				
			}
		});
	}
	public Long hsetObject(final String key, final String field, final Object value){
		return (Long)this.execute(new ExecuteTemplate() {
			@Override
			public Object execute(Jedis jedis) {
				   byte[] bkey = JavaUtil.objectToBytes(key);
                   byte[] bfield = JavaUtil.objectToBytes(field); 
                   byte[] bvalue = JavaUtil.objectToBytes(value);
                   logger.debug(key + ":" + field + " put in the redis");
                   return jedis.hset(bkey, bfield, bvalue);
				
			}
		});
	}
	
	public Long expire(final String key, final int seconds){
		return (Long)this.execute(new ExecuteTemplate() {
			@Override
			public Object execute(Jedis jedis) {
				 byte[] bkey = JavaUtil.objectToBytes(key);
                 logger.debug("object with key " + key + " expired after " + seconds + " seconds");
                 return jedis.expire(bkey, seconds);
				
			}
		});
	}
	 public Object hgetObject(final String key, final String field) {
		 return this.execute(new ExecuteTemplate() {
			@Override
			public Object execute(Jedis jedis) {
				byte[] bytes  = jedis.hget(JavaUtil.objectToBytes(key), JavaUtil.objectToBytes(field));
				return JavaUtil.bytesToObject(bytes);
				
			}
		});
	   }
	 
	public void removeObject(final String id){
		ExecuteTemplate et = new ExecuteTemplate() {
			
			@Override
			public Object execute(Jedis jedis) {
				return jedis.del(id);
				
			}
		};
		this.execute(et);
	}
	
	public Object execute(ExecuteTemplate executeTemplate){
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			return executeTemplate.execute(jedis);
		}finally{
			if(jedis!=null){
				jedisPool.returnResourceObject(jedis);
			}
		}
	}
	
	public interface ExecuteTemplate{
		Object execute(Jedis jedis);
	}

	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 向缓存中放入数据
	 * @param key
	 * @param value
	 */
	public void set(final String key, final Object value){
		this.execute(new ExecuteTemplate() {
			
			@Override
			public Object execute(Jedis jedis) {
				 byte[] bkey = JavaUtil.objectToBytes(key);
                 byte[] bvalue = JavaUtil.objectToBytes(value);
				jedis.set(bkey, bvalue);
				
				return null;
			}
		});
	}
	
	/**
	 * 从缓存中获取数据
	 * @param key
	 * @return
	 */
	public Object get(final String key){
		return this.execute(new ExecuteTemplate() {
			
			@Override
			public Object execute(Jedis jedis) {
				byte[] bkey = JavaUtil.objectToBytes(key);
				byte[] objectByte = jedis.get(bkey);
				if(objectByte != null && objectByte.length > 0){
					return JavaUtil.bytesToObject(objectByte);
				}else{
					return null;
				}
			}
		});
	}
}
