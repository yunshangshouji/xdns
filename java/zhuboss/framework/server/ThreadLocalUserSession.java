package zhuboss.framework.server;

import java.io.Serializable;
import java.util.Map;


public class ThreadLocalUserSession implements Serializable{
	
	private static ThreadLocal<Object> threadLocalUserSession = new ThreadLocal<Object>() ;
	
	private static ThreadLocal<Map<String,Object>> vars = new ThreadLocal<Map<String,Object>>() ;
	
	public static void set(Object object){
		threadLocalUserSession.set(object);
	}
	public static Object get(){
		Object object = threadLocalUserSession.get();
		return object;
	}
	
	public static void setVars(Map<String,Object> vars){
		ThreadLocalUserSession.vars.set(vars);
	}
	public static Map<String,Object> getVars(){
		Map<String,Object> map = vars.get();
		return map;
	}
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 257055888354465147L;
	

}
