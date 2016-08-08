/*
 * @(#)CookieHelper.java 1.0 2009-12-28下午02:48:45
 *
 * 和讯信息科技有限公司-第三方理财事业部-第三方理财事业部
 * Copyright (c) 1996-2012 HexunFSD, Inc. All rights reserved.
 */
 package zhuboss.framework.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * <dl>
 *    <dt><b>Title:</b></dt>
 *    <dd>
 *    	Cookie帮助类
 *    </dd>
 *    <dt><b>Description:</b></dt>
 *    <dd>
 *    	<p>none
 *    </dd>
 * </dl>
 *
 * @author eric
 * @version 1.0, 2009-12-28
 * @since framework-1.4
 * 
 */
public class CookieHelper {
	
	//private static Log logger = LogFactory.getLog(CookieHelper.class);
	
	public static final int ZERO_AGE = 0;
	public static final int THREE_MONTH_AGE = 3*30*24*60*60;
	public static final int FOREVER_AGE = -1;
	
	public static String DEFAULT_PATH = "/";
	
    static public void setCookie(HttpServletResponse response,String cookieDomain,String cookiePath, String name, String value) {  
        int age = THREE_MONTH_AGE; 
        setCookie( response, cookieDomain, cookiePath,  name, value, age);
    }

    static public void setCookie(HttpServletResponse response,String cookieDomain,String cookiePath, String name, String value, int  age) {  
    	Assert.notNull(response);
    	Assert.hasText(name,"Must specify cookie name");
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(age);
        if (StringUtils.isNotBlank(cookieDomain)) {
        	cookie.setDomain(cookieDomain);
        }
        if (StringUtils.isBlank(cookiePath)) {
        	cookiePath = DEFAULT_PATH;
        }
        cookie.setPath(cookiePath);
        //cookie.setSecure(true);
        response.addHeader("P3P","CP=CAO PSA OUR"); 
        response.addCookie(cookie);
    }

    
    /**
     * 得到cookie
     * @param request
     * @param name
     * @return
     */
    public static String getCookie(HttpServletRequest request, String name) {
    	Assert.notNull(request);
    	Assert.hasText(name,"Must specify cookie name");
        Cookie[] cookies = request.getCookies();
        String ret = null;
        if (cookies!=null){
            for (int i = 0; i < cookies.length; i++) {
            	Cookie cookie = cookies[i];
                if (cookie.getName().equals(name)) {
                	ret = cookie.getValue();
                    break;
                }
    		}
        }
        return ret;
    }
    
    public static void removeCookie(HttpServletResponse response, String name ){
    	Assert.notNull(response);
    	Assert.hasText(name,"Must specify cookie name");    	
    	Cookie cookie = new Cookie(name, null); 
    	cookie.setMaxAge(ZERO_AGE); 
    	cookie.setPath(DEFAULT_PATH);
    	response.addCookie(cookie);

    }

    public static void removeCookie(HttpServletResponse response, String name , String cookieDomain){
    	Assert.notNull(response);
    	Assert.hasText(name,"Must specify cookie name");    	
    	Cookie cookie = new Cookie(name, null); 
    	cookie.setMaxAge(ZERO_AGE); 
    	cookie.setPath(DEFAULT_PATH);
    	if (StringUtils.isNotBlank(cookieDomain)) {
        	cookie.setDomain(cookieDomain);
        }
    	response.addCookie(cookie);

    }

    
    
	private CookieHelper(){}
}
