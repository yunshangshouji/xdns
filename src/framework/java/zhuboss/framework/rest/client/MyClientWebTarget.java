package zhuboss.framework.rest.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;

public class MyClientWebTarget extends ClientWebTarget {

	public MyClientWebTarget(ResteasyClient client, String uri, ClientConfiguration configuration) throws IllegalArgumentException, NullPointerException
	   {
	      super(client, uri,configuration);
	   }

	@Override
	public <T> T proxy(Class<T> proxyInterface) {
		return MyProxyBuilder.builder(proxyInterface, this).build();
	}

	
}
