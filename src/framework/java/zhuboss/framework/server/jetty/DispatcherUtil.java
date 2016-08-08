package zhuboss.framework.server.jetty;

import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.MultiMap;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

public class DispatcherUtil {
	
	public static TemplateHashModel getFreemarkerDispatcher(){
		TemplateHashModel dispatcherStatics = null;
		try {
			dispatcherStatics = (TemplateHashModel) BeansWrapper.getDefaultInstance().getStaticModels().get(DispatcherUtil.class.getName());
		} catch (TemplateModelException e) {
			throw new RuntimeException(e);
		}  
		return dispatcherStatics;
	}
	
	/**
	 * 
	 * @param uri must starts with '/'
	 * @return
	 * @throws Exception
	 */
     public static String process(String uri) throws Exception{
    	 Request request = HttpChannel.getCurrentHttpChannel().getRequest();
    	 Assert.isTrue(uri.startsWith("/"));
//    	 Assert.isTrue(uri.indexOf("?")==-1);
          MyResponse myResponse = new MyResponse();
          
          /**
           * backup
           */
          final String old_uri=request.getRequestURI();
          final String old_query=request.getQueryString();
          MultiMap<String> old_params=request.getQueryParameters();
          final Attributes old_attr=request.getAttributes();
          
          /**
           * set new
           */
          request.setRequestURI(uri);
          request.setAttributes(null); //include springmvc&resteasy's data
          
          /**
           * query
           */
          String query=getQuery(uri);
          if (query!=null)
          {
              // force parameter extraction
              if (old_params==null)
              {
                  request.extractParameters();
                  old_params=request.getQueryParameters();
              }
              
              request.mergeQueryParameters(query,true);
          }
          
          /**
           * render
           */
          request.getContext().getContextHandler().handle(uri, request, request, myResponse);
//          request.getRequestDispatcher(uri).forward(request, myResponse); //不直接使用是因为springmvc已经commit headers，而forward需求not commited
          
          /**
           * restore
           */
          request.setAttributes(old_attr);
          request.setRequestURI(old_uri);
          request.setQueryParameters(old_params);
          request.setQueryString(old_query);
          String content = myResponse.getContent();
          return content;
     }
     
     public static String getRequestURI(){
    	 Request request = HttpChannel.getCurrentHttpChannel().getRequest();
    	 return request.getRequestURI()+ (request.getQueryString()!=null? (""+request.getQueryString()) : "");
     }
     
     
     public static WebApplicationContext getWebApplicationContext(Request request){
 		WebApplicationContext webApplicationContext = (WebApplicationContext)request.getServletContext().getAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.mvc-dispatcher");
 		return webApplicationContext;
 	}
 	
     private static String getQuery(String uriInContext){
    	 String query = null;
         int q = 0;
         if ((q = uriInContext.indexOf('?')) > 0)
         {
             query = uriInContext.substring(q + 1);
             uriInContext = uriInContext.substring(0,q);
         }
         if ((q = uriInContext.indexOf(';')) > 0)
             uriInContext = uriInContext.substring(0,q);

         return query;
     }
     
}      

