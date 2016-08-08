package zhuboss.framework.rest.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.ProcessingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.jboss.resteasy.client.core.SelfExpandingBufferredInputStream;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zhuboss.framework.util.JavaUtil;
import zhuboss.framework.util.serialize.JsonUtil;

public class MyClientHttpEngine extends ApacheHttpClient4Engine {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public MyClientHttpEngine(HttpClient httpClient){
		super(httpClient);
	}

	@Override
	public ClientResponse invoke(ClientInvocation request) {
		String uri = request.getUri().toString();
		String requestContent = null;
	      final HttpRequestBase httpMethod = createHttpMethod(uri, request.getMethod());
	      final HttpResponse res;
	      try
	      {
	         loadHttpMethod(request, httpMethod);
	         /**
	          * Add by ZhuZhengquan
	          */
	         if(httpMethod instanceof HttpEntityEnclosingRequestBase){
	        	 if(((HttpEntityEnclosingRequestBase)httpMethod).getEntity()!=null){
	        		 requestContent = JavaUtil.InputStreamTOString(((HttpEntityEnclosingRequestBase)httpMethod).getEntity().getContent(),"UTF-8");
	        	 }
	         }
	         
	         
	         res = httpClient.execute(httpMethod, httpContext);
	      }
	      catch (Exception e)
	      {
	         throw new ProcessingException("Unable to invoke request", e);
	      }
	      
	      /**
	       * add by zhuzhengquan拦截内容，并关闭连接 is.close();
	       */
	      InputStream is = null;
	      byte[] bytes;
			try {
				is = res.getEntity().getContent();
				bytes = JavaUtil.InputStreamToBytes(is);
				is.close();
				String t = new String(bytes, "UTF-8");
				logger.info("\nRequest Line:"+httpMethod.getRequestLine()
						+"\nRequestContent:"+requestContent
						+"\nResponse Status Line:"+res.getStatusLine()
						+"\nResponse Entity:"+t);
				
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RuntimeException(e);
		}finally{
			if(is !=null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
   
//    stream = new SelfExpandingBufferredInputStream(hc4Stream);
	      
		 final InputStream hc4Stream = new ByteArrayInputStream(bytes);
	      ClientResponse response = new ClientResponse(request.getClientConfiguration())
	      {

	         @Override
	         protected void setInputStream(InputStream is)
	         {
//	            stream = is;
	         }

	         public InputStream getInputStream()
	         {
	            
	            return hc4Stream;
	         }

	         public void releaseConnection()
	         {
	            isClosed = true;
	            // Apache Client 4 is stupid,  You have to get the InputStream and close it if there is an entity
	            // otherwise the connection is never released.  There is, of course, no close() method on response
	            // to make this easier.
	            try {
					hc4Stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	         }
	      };
	      response.setProperties(request.getMutableProperties());
	      response.setStatus(res.getStatusLine().getStatusCode());
	      response.setHeaders(extractHeaders(res));
	      response.setClientConfiguration(request.getClientConfiguration());
	      return response;
	      
//		logger.info("request uri:"+request.getUri().toString());
//		Object requestEntity = request.getEntity();
//		if(requestEntity!=null){
//			logger.info("request entity:"+JsonUtil.serializeToJson(requestEntity));
//		}
//		ClientResponse response = super.invoke(request);
//		logger.info("status line:"+response.getStatus()+" "+response.getStatusInfo());
//		return response;
	}

	
}
