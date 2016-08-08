package zhuboss.framework.util;

import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;
import org.springframework.web.context.WebApplicationContext;

import zhuboss.framework.spring.CustomizedPropertyPlaceholderConfigurer;

public class EnviromentUtil {
	/**
	 * 获取Web环境中的Spring容器
	 * @return
	 */
	public static WebApplicationContext getWebApplicationContext(){
//		Request request = HttpChannel.getCurrentHttpChannel().getRequest();
		WebApplicationContext webApplicationContext = (WebApplicationContext)SpringInit.applicationContext;
		return webApplicationContext;
	}
	
	public static Object getProperty(String name){
		CustomizedPropertyPlaceholderConfigurer customizedPropertyPlaceholderConfigurer = (CustomizedPropertyPlaceholderConfigurer)getWebApplicationContext().getBean("propertyConfigurer") ;
		return customizedPropertyPlaceholderConfigurer.getContextProperty(name);
	}
	
	public static HttpSession getHttpSession(){
		Request request = HttpChannel.getCurrentHttpChannel().getRequest();
		return request.getSession();
	}
	
	public static Request getRequest(){
		Request request = HttpChannel.getCurrentHttpChannel().getRequest();
		return request;
	}
}
