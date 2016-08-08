package zhuboss.framework.util.auth.thiredparty;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import zhuboss.framework.util.CryptoUtils;

public class ThiredpartyAuth {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	private ICache cache;
	private Map<String,String> appKeyMap = new HashMap<String,String>();
	
	public ThiredpartyAuth(ICache cache){
		this.cache = cache;
	}
	
	public void setApp(String appid,String appkey){
		appKeyMap.put(appid, appkey);
	}
	
	public boolean validate(String appid,String timestamp,String uuid,String signature) {
		logger.debug("validte:appid["+appid+"],timestamp["+timestamp+"],uuid["+uuid+"],signature["+signature+"]");
		
		String appkey = appKeyMap.get(appid);
		if(appkey == null) {
			logger.error("appid not validate");
			return false;
		}
		if(!StringUtils.hasText(timestamp)) {
			logger.error("timestamp cannot be null");
			return false;
		}
		if(!StringUtils.hasText(uuid)) {
			logger.error("uuid cannot be null");
			return false;
		}
		if(!StringUtils.hasText(signature)) {
			logger.error("signature cannot be null");
			return false;
		}
		//TODO timestamp 有效期判断
		if(cache.exists(uuid)) {
			logger.error("uuid expired");
			return false;
		}
		String concatStr = appid+timestamp+uuid+appkey;
		try {
			String actualSignature = CryptoUtils.md5Digest(concatStr);
			if(actualSignature.equals(signature)){
				cache.add(uuid);
				return true;
			}else{
				logger.error("signature validate fail");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	
}
