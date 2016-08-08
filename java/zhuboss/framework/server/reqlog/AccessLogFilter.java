package zhuboss.framework.server.reqlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import zhuboss.framework.util.JavaUtil;

public class AccessLogFilter implements Filter{
	Logger logger = LoggerFactory.getLogger("accesslog");
	
	Set<String> reqMap = new HashSet<String>();
	Set<String> resMap = new HashSet<String>();
	private boolean logFlag = false;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.read("./conf/accesslog.req.cfg", reqMap);
		this.read("./conf/accesslog.res.cfg", resMap);
	}

	private void read(String path,Set<String> set){
		FileSystemResource fsr = new FileSystemResource(path);
		if (fsr.exists()){
			logFlag = true;
			try {
				InputStream is = fsr.getInputStream();
				BufferedReader  br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
				String line; 
		        while ((line=br.readLine()) != null) { // 如果 line 为空说明读完了
		            if(line!=null && !line.startsWith("#") && line.trim().length()>0){
		            	set.add(line);
		            }
		        }
		        br.close();
		        is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if(!logFlag
				){
			chain.doFilter(request,response);
			return;
		}
		long beginTime = System.currentTimeMillis();
		String requestPath = ((HttpServletRequest)request).getRequestURI();
		String requestContent = "-";
		String requestHeader = "-";
		ServletRequest req = request;
		if(reqMap.contains(requestPath)){
			req = new MyHttpServletRequestWrapper((HttpServletRequest)request);
			requestContent = ((MyHttpServletRequestWrapper)req).getRequestContent();
			requestHeader = ((Request)request).getHttpFields().toString();
		}
		
		ServletResponse res = response;
		boolean logResponseFlag = resMap.contains(requestPath);
		if(logResponseFlag){
			res = new MyHttpServletResponseWrapper((HttpServletResponse)response);
		}
		
		chain.doFilter(req, res);
		String responseContent = "-";
		if(logResponseFlag){
			((MyHttpServletResponseWrapper)res).doFlush();
			responseContent = convetLine(((MyHttpServletResponseWrapper)res).getContent());
		}
		long endTime = System.currentTimeMillis(); 
		/**
		 * 记录日志
		 */
		List<String> list = new ArrayList<String>();
		 String addr = null;
             addr = ((HttpServletRequest)req).getHeader(HttpHeader.X_FORWARDED_FOR.toString());
         if (addr == null)
             addr = request.getRemoteAddr();
         list.add(addr);
         list.add(sdf.format(new Date()));
         list.add(((HttpServletResponse)response).getStatus()+"");
         list.add(((double)endTime - (double)beginTime)/1000 + "");
         String queryString = ((HttpServletRequest)request).getQueryString();
         list.add(requestPath +  (queryString!=null && queryString.length()>0?("?"+queryString):""));
         list.add(((Response)response).getContentLength()+"");
         list.add(requestContent);
         list.add(requestHeader);
         list.add(responseContent);
         this.printRecord(list);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	private String convetLine(String s){
		if(s == null) return null;
		return s.replaceAll("\r", "").replaceAll("\n", "\\n");
	}
	
	public void printRecord(List<String> list){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<list.size();i++){
			if(i>0){
				sb.append("\t");
			}
			sb.append(convetLine(list.get(i)));
		}
		logger.info(sb.toString());
	}

}
