package zhuboss.framework.server.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import zhuboss.framework.util.ObjectId;

/** 
 * @author 作者 zhuzhengquan: 
 * @version 创建时间：2015年12月2日 上午9:40:58 
 * 在log4j的日志中会打印线程名称，通过每一次请求开始重置线程的name(所有name不重复)，帮助筛选某个请求的所有日志
 */
public class ThreadNameFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Thread.currentThread().setName(ObjectId.get());
		chain.doFilter(request, response);
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
