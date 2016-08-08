package zhuboss.framework.server.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import zhuboss.framework.server.ThreadLocalUserSession;

public class UserSessionFilter implements Filter{
	private static ThreadLocal<HttpSession> session = new ThreadLocal<HttpSession>();
	
	private static ThreadLocal<String> userid = new ThreadLocal<String>();
	public static String getUserid(){
		return userid.get();
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	public static HttpSession getHttpSession(){
		return UserSessionFilter.session.get();
	}
	
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain filterChain) throws IOException, ServletException {
		/**
		 * 仅仅是记录下WEB session
		 */
		HttpSession session = ((HttpServletRequest)req).getSession();
		userid.set((String)session.getAttribute("userid"));
//		AccountLoginBean loginBean = new AccountLoginBean();
//		loginBean.setFromChannel("0000");
//		loginBean.setCustId("000000002217");
//		
//		AccountRiskBean risk = new AccountRiskBean();
//		risk.setDegreecode("1004");//1004保守 1005 稳健 1006 进取/积极
//		loginBean.setRisk(risk);
//		loginBean.setInvNm("测试猿");
//		
//		session.setAttribute("loginBean", loginBean);
		
		UserSessionFilter.session.set(session);
		/**
		 * 设置用户身份信息
		 */
		Object userSession = session.getAttribute("user");
		ThreadLocalUserSession.set(userSession);

		/**
		 * 设置session变量
		 */
		Map<String, Object> map = (Map<String, Object>)session.getAttribute("vars");
		if(map == null){
			map = new HashMap<String,Object>();
		}
		ThreadLocalUserSession.setVars(map);
		//
		filterChain.doFilter(req, res);
	}

	public void init(FilterConfig arg0) throws ServletException {
		
		
	}

}
