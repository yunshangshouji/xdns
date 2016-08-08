package zhuboss.framework.rest.client;

import java.lang.reflect.Method;
import java.util.Map;

import org.jboss.resteasy.client.jaxrs.internal.proxy.ClientProxy;
import org.jboss.resteasy.client.jaxrs.internal.proxy.MethodInvoker;

public class MyClientProxy extends ClientProxy {
	public MyClientProxy(Map<Method, MethodInvoker> methodMap) {
		super(methodMap);
	}

	@Override
	public Object invoke(Object o, Method method, Object[] args)
			throws Throwable {
		try{
			Object result = super.invoke(o, method, args);
			return result;
		}catch(Exception e){
			throw new HttpSubRequestException("Remote HTTP Error,"+e.getMessage(),e);
		}
	}

}
