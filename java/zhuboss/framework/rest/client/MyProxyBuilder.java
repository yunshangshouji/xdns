package zhuboss.framework.rest.client;

import org.jboss.resteasy.client.jaxrs.ProxyConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvoker;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ClientProxy;
import org.jboss.resteasy.client.jaxrs.internal.proxy.MethodInvoker;
import org.jboss.resteasy.client.jaxrs.internal.proxy.SubResourceInvoker;
import org.jboss.resteasy.util.IsHttpMethod;

import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Set;

public class MyProxyBuilder<T>
{
	private final Class<T> iface;
	private final ResteasyWebTarget webTarget;
	private ClassLoader loader = Thread.currentThread().getContextClassLoader();
	private MediaType serverConsumes;
	private MediaType serverProduces;

   public static <T> MyProxyBuilder<T> builder(Class<T> iface, WebTarget webTarget)
   {
      return new MyProxyBuilder<T>(iface, (ResteasyWebTarget)webTarget);
   }

	@SuppressWarnings("unchecked")
	public static <T> T proxy(final Class<T> iface, WebTarget base, final ProxyConfig config)
	{
      if (iface.isAnnotationPresent(Path.class))
      {
         Path path = iface.getAnnotation(Path.class);
         if (!path.value().equals("") && !path.value().equals("/"))
         {
            base = base.path(path.value());
         }
      }
		HashMap<Method, MethodInvoker> methodMap = new HashMap<Method, MethodInvoker>();
		for (Method method : iface.getMethods())
		{
         MethodInvoker invoker;
         Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
         if ((httpMethods == null || httpMethods.size() == 0) && method.isAnnotationPresent(Path.class) && method.getReturnType().isInterface())
         {
            invoker = new SubResourceInvoker((ResteasyWebTarget)base, method, config);
         }
         else
         {
            invoker = createClientInvoker(iface, method, (ResteasyWebTarget)base, config);
         }
         methodMap.put(method, invoker);
		}

		Class<?>[] intfs =
		{
				iface
		};

		/**
		 * modified by ZhuZhengquan
		 */
		MyClientProxy clientProxy = new MyClientProxy(methodMap);
		//ClientProxy clientProxy = new ClientProxy(methodMap);
		/**
		 * end by ZhuZhengquan
		 */
		
		// this is done so that equals and hashCode work ok. Adding the proxy to a
		// Collection will cause equals and hashCode to be invoked. The Spring
		// infrastructure had some problems without this.
		clientProxy.setClazz(iface);

		return (T) Proxy.newProxyInstance(config.getLoader(), intfs, clientProxy);
	}

   private static <T> ClientInvoker createClientInvoker(Class<T> clazz, Method method, ResteasyWebTarget base, ProxyConfig config)
   {
      Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
      if (httpMethods == null || httpMethods.size() != 1)
      {
         throw new RuntimeException("You must use at least one, but no more than one http method annotation on: " + method.toString());
      }
      ClientInvoker invoker = new ClientInvoker(base, clazz, method, config);
      invoker.setHttpMethod(httpMethods.iterator().next());
      return invoker;
   }

   private MyProxyBuilder(Class<T> iface, ResteasyWebTarget webTarget)
   {
      this.iface = iface;
      this.webTarget = webTarget;
   }

   public MyProxyBuilder<T> classloader(ClassLoader cl)
	{
		this.loader = cl;
		return this;
	}

	public MyProxyBuilder<T> defaultProduces(MediaType type)
	{
		this.serverProduces = type;
		return this;
	}

	public MyProxyBuilder<T> defaultConsumes(MediaType type)
	{
		this.serverConsumes = type;
		return this;
	}

   public MyProxyBuilder<T> defaultProduces(String type)
   {
      this.serverProduces = MediaType.valueOf(type);
      return this;
   }

   public MyProxyBuilder<T> defaultConsumes(String type)
   {
      this.serverConsumes = MediaType.valueOf(type);
      return this;
   }
	public T build()
	{
      return proxy(iface, webTarget, new ProxyConfig(loader, serverConsumes, serverProduces));
	}



}
