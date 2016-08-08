package zhuboss.framework.util.http;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import zhuboss.framework.util.serialize.JsonUtil;


public class RestInvoker implements IServiceInvoker{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String CONTENT_TYPE = "application/json";
	
	private String baseURL;
	
	HttpClient httpClient;
	public RestInvoker(){
		
	    try{
	    	
		    ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
			connectionManager.setMaxTotal(2000);
			connectionManager.setDefaultMaxPerRoute(200);
			
			this.httpClient = new NoSSLValidateHttpClient(connectionManager);
			//网络异常重连
			((DefaultHttpClient)this.httpClient).setHttpRequestRetryHandler(new MyDefaultHttpRequestRetryHandler());
			
	    }catch(Exception e){
	    	logger.error(e.getMessage(), e);
	    }
	   
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public  <T> T get(String path, Class<T> cls) {
		List<NameValuePair> params = null;
		String text = this.get(path, params);
		T obj = null;
		if(text==null || text.equals("")) return null;
		
		if(cls.equals(String.class)){
			obj = (T)text;
		}else{
			obj = JsonUtil.unserializeFromJson(text, cls);
		}
		return obj;
	}
	
	@Override
	public  <T> List<T> getList(String path, Class<T> cls) {
		List<NameValuePair> parameters = null;
		String text = this.get(path, parameters);
		List<T> obj = JsonUtil.unserializeFromJsonAsList(text, cls);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public <T> T get(String path, List<NameValuePair> parameters, Class<T> cls){
		String text = this.get(path, parameters);
		if(!StringUtils.hasText(text)){
			return null;
		}
		T obj = null;
		if(cls.equals(String.class)){
			obj = (T)text;
		}else{
			obj = JsonUtil.unserializeFromJson(text, cls);
		}
		return obj;
	}
	
	@Override
	public <T> List<T> getList(String path, List<NameValuePair> parameters, Class<T> cls){
		String text = this.get(path, parameters);
		if(!StringUtils.hasText(text)){
			return null;
		}
		List<T> obj = JsonUtil.unserializeFromJsonAsList(text, cls);
		return obj;
	}
	
	
	private String get(String path, List<NameValuePair> parameters) {
		if(parameters!=null && parameters.size()>0){
			String queryParamPart = URLEncodedUtils.format(parameters, "UTF-8");
			path = path + "?" + queryParamPart;
		}
		HttpGet get = new HttpGet(buildPath(path)  );
		try {
//			get.addHeader("Accept", accept);
			get.addHeader("Content-Type", CONTENT_TYPE);
			HttpResponse response = httpClient.execute(get);
			
			
			if(response.getEntity() == null) return null;
			String responseText = EntityUtils.toString(
					response.getEntity(), HTTP.UTF_8);
			
			if(response.getStatusLine().getStatusCode() == 404){
				logger.error(responseText);
				throw new RuntimeException("path not found["+get.getURI()+"]");
			}
			
			return responseText;
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage(),e);
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			return null;
		} 
	}

	public String postForm(String path, List<NameValuePair> parameters, List<BasicNameValuePair> formData) {
		if(parameters!=null && parameters.size()>0){
			String queryParamPart = URLEncodedUtils.format(parameters, "UTF-8");
			path = path + "?" + queryParamPart;
		}
		
		HttpPost post = new HttpPost(buildPath(path));
		try {
	        UrlEncodedFormEntity entity = new  UrlEncodedFormEntity(formData,"UTF-8");
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);
			if(response.getStatusLine().getStatusCode() == 404 || response.getStatusLine().getStatusCode() == 500){
				throw new RuntimeException("Http request fail ["+post.getURI()+"],"+response.getStatusLine());
			}
			HttpEntity resEntity = response.getEntity();
			if (resEntity == null) {
				return "";
			}
			return EntityUtils.toString(resEntity, HTTP.UTF_8);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RuntimeException(e);
		} 
	}
	
	@Override
	public String post(String path, List<NameValuePair> parameters, Object obj) {
		String url = path;
		if(parameters!=null && parameters.size()>0){
			String queryParamPart = URLEncodedUtils.format(parameters, "UTF-8");
			url = url + "?" + queryParamPart;
		}
		
		String text = "";
		if(obj != null) {
			if(obj instanceof String){
				text = (String)obj;
			}else{
				text = JsonUtil.serializeToJson(obj);
			}
		}
		
		HttpPost post = new HttpPost(buildPath(url));
		try {
//			post.addHeader("Accept", accept);
			post.addHeader("Content-Type", CONTENT_TYPE);
			StringEntity entityTemplate = new StringEntity(text, CONTENT_TYPE,
					HTTP.UTF_8);
			entityTemplate.setContentEncoding(HTTP.UTF_8);
			post.setEntity(entityTemplate);
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return "";
			}
			return EntityUtils.toString(entity, HTTP.UTF_8);
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage(),e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RuntimeException(e);
		} 
	}

	public String postFiles(String path, MultipartEntity multipartEntity){
		HttpPost filePost = new HttpPost(buildPath(path));
		filePost.setEntity(multipartEntity);
		try {
			HttpResponse response = httpClient.execute(filePost);
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return "";
			}
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode==200 || statusCode==204){
				return EntityUtils.toString(entity, HTTP.UTF_8);
			}else{
				String errMsg = "请求错误:"+response.getStatusLine()+" "+EntityUtils.toString(entity, HTTP.UTF_8);
				logger.error(errMsg);
				throw new RuntimeException(errMsg);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RuntimeException(e);
		} 
		
	}
	
	@Override
	public String put(String path, Object obj) {
		String text = "";
		if(obj != null) {
			if(obj instanceof String){
				text = (String)obj;
			}else{
				text = JsonUtil.serializeToJson(obj);
			}
		}
		
		return put(path,text);
	}
	
	@Override
	public String put(String path, String content) {
		HttpPut put = new HttpPut(buildPath(path));
		try {
//			put.addHeader("Accept", accept);
			put.addHeader("Content-Type", CONTENT_TYPE);
			StringEntity entityTemplate = new StringEntity(content==null?"":content, CONTENT_TYPE,
					HTTP.UTF_8);
			entityTemplate.setContentEncoding(HTTP.UTF_8);
			put.setEntity(entityTemplate);
			HttpResponse response = httpClient.execute(put);
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return "";
			}
			return EntityUtils.toString(entity, HTTP.UTF_8);
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage(),e);
			return "";
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			return "";
		} 
	}

	@Override
	public String delete(String path) {
		return this.delete(path, null);
	}
	
	@Override
	public String delete(String path,
			List<NameValuePair> parameters) {
		try {
//			delete.addHeader("Accept", accept);
//			delete.addHeader("Content-Type", CONTENT_TYPE);
			if(parameters!=null && parameters.size()>0){
				String queryParamPart = URLEncodedUtils.format(parameters, "UTF-8");
				path = path + "?" + queryParamPart;
			}
			
			HttpDelete delete = new HttpDelete(buildPath(path));
			
			HttpResponse response = httpClient.execute(delete);
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return "";
			}
			return EntityUtils.toString(entity, HTTP.UTF_8);
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage(),e);
			return "";
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			return "";
		} 
	}

	private String buildPath(String path){
		if(baseURL==null || path.startsWith("http")) return path;
		if(!path.startsWith("/")){
			return baseURL +"/" + path;
		}else{
			return baseURL + path;
		}
		
	}
	
	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		logger.info("setBaseURL:"+baseURL);
		this.baseURL = baseURL;
	}
	
	public HttpClient getHttpClient() {
		return httpClient;
	}


	public static void main(String[] args) throws Exception {
		RestInvoker r = new RestInvoker();
		String s = r.get("https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxb2c0b31d41880b26&secret=5ec504db317999d7c160526b9a6bc7c1&code=029a8a498292dcbaa99d426dbf28d6c5&state=1395898450992&grant_type=authorization_code",String.class);
		System.out.println(s);
		
		//com.licaike.framework.server.StartWebApp.main(null);

	}

}
