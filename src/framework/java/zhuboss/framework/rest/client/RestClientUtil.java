package zhuboss.framework.rest.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.springframework.beans.factory.InitializingBean;

import zhuboss.framework.util.http.MyDefaultHttpRequestRetryHandler;

public class RestClientUtil implements InitializingBean{
	
	HttpClient httpClient;
	ResteasyWebTarget target;
	private String targetAddress ; 
	public RestClientUtil(){
		 try{
			    ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
				connectionManager.setMaxTotal(2000);
				connectionManager.setDefaultMaxPerRoute(200);
				
				this.httpClient = new DefaultHttpClient(connectionManager);
				//网络异常重连
				((DefaultHttpClient)this.httpClient).setHttpRequestRetryHandler(new MyDefaultHttpRequestRetryHandler());
				
		    }catch(Exception e){
		    	throw e;
		    }
		 //
		 
	}
	private Map<Class<?>,Object> proxyMap = new HashMap<Class<?>,Object>();
	
	public <T> T proxy(Class<T> cls){
		T obj = (T)proxyMap.get(cls);
		synchronized(this){
			if(obj == null){
				obj = (T)target.proxy(cls);
				proxyMap.put(cls, obj);
			}
		}
		return obj;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().httpEngine(new MyClientHttpEngine(httpClient)).build();
//		target = client.target(targetAddress);
		//使用扩展类实现对异常地捕获，重新定义异常，避免SubRequest的异常直接变为当前rest异常
		target = new MyClientWebTarget(client, targetAddress, (ClientConfiguration) client.getConfiguration() ); 
	}

	public String getTargetAddress() {
		return targetAddress;
	}

	public void setTargetAddress(String targetAddress) {
		this.targetAddress = targetAddress;
	}
	
}
