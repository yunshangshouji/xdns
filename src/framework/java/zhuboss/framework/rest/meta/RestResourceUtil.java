package zhuboss.framework.rest.meta;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.jboss.resteasy.core.MessageBodyParameterInjector;
import org.jboss.resteasy.core.MethodInjectorImpl;
import org.jboss.resteasy.core.PathParamInjector;
import org.jboss.resteasy.core.QueryParamInjector;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.StringParameterInjector;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.core.registry.RootSegment;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.util.FindAnnotation;

import zhuboss.framework.rest.meta.annotation.RestAttribute;
import zhuboss.framework.rest.meta.annotation.RestDescription;
import zhuboss.framework.rest.meta.annotation.RestParam;
import zhuboss.framework.rest.meta.annotation.RestParams;


public class RestResourceUtil {
	private static final Set<String> ignoreProperties = new HashSet<String>();
	private static Field methodInjectorField;
	private static Field paramNameField;
	private static Field pathNameField;
	private static Field genericTypeField;
	
	static{
		ignoreProperties.add("class");
		ignoreProperties.add("insertUser");
		ignoreProperties.add("insertTime");
		ignoreProperties.add("updateUser");
		ignoreProperties.add("updateTime");
		
		try {
			methodInjectorField = ResourceMethod.class.getDeclaredField("methodInjector");
			methodInjectorField.setAccessible(true);
			//
			paramNameField = StringParameterInjector.class.getDeclaredField("paramName");
			paramNameField.setAccessible(true);
			//
			pathNameField = PathParamInjector.class.getDeclaredField("paramName");
			pathNameField.setAccessible(true);
			//
			genericTypeField = MessageBodyParameterInjector.class.getDeclaredField("genericType");
			genericTypeField.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	
	public static String packages(Registry registry) throws Exception{
//		RootSegment rootSegment = ((ResourceMethodRegistry)jettyRestServer.getRestServlet().getServletContainerDispatcher().getDispatcher().getRegistry()).getRoot();
		RootSegment rootSegment = ((ResourceMethodRegistry)registry).getRoot();
		Map<String, List<ResourceInvoker>> maps =  rootSegment.getBounded();
		List<RestDescriptor> restDescriptorList = new ArrayList<RestDescriptor>();
		for(Map.Entry<String, List<ResourceInvoker>> entry : maps.entrySet()){
			for(ResourceInvoker resourceInvoker :  entry.getValue()){
				if(!(resourceInvoker instanceof ResourceMethod)) continue;
				String path=entry.getKey();
				if(path.startsWith("api")) continue;
				ResourceMethod resourceMethod = (ResourceMethod) resourceInvoker;
				RestDescriptor restDescriptor = new RestDescriptor();
				restDescriptor.setMethod(resourceMethod.getHttpMethods().iterator().next());
				restDescriptor.setPath(path.startsWith("/")?path:("/"+path));
				//consumes
				String consumes="";
				if(resourceMethod.getConsumes()!=null){
					for(MediaType mediaType	: resourceMethod.getConsumes()){
						consumes += (" "+mediaType);
					}
				}
				restDescriptor.setConsumeMediaTypes(consumes);
				//produces
				String produces="";
				if(resourceMethod.getProduces()!=null){
					for(MediaType mediaType	: resourceMethod.getProduces()){
						produces += (" "+mediaType);
					}
				}
				restDescriptor.setProduceMediaTypes(produces);
				//QueryParam & PathParam
				restDescriptor.setQueryParamDescriptors(new ArrayList<QueryParamDescriptor>());
				restDescriptor.setPathParamDescriptors(new ArrayList<PathParamDescriptor>());
				MethodInjectorImpl methodInjectorImpl = (MethodInjectorImpl)methodInjectorField.get(resourceMethod);
				ValueInjector[]  ValueInjectors = methodInjectorImpl.getParams();
				for(ValueInjector valueInjector : ValueInjectors){
					if(valueInjector instanceof QueryParamInjector){
						QueryParamInjector queryParamInjector = (QueryParamInjector)valueInjector;
						QueryParamDescriptor queryParamDescriptor = new QueryParamDescriptor();
						queryParamDescriptor.setParamName((String)paramNameField.get(queryParamInjector));
						restDescriptor.getQueryParamDescriptors().add(queryParamDescriptor);
					}else if(valueInjector instanceof PathParamInjector){
						PathParamInjector pathParamInjector = (PathParamInjector)valueInjector;
						PathParamDescriptor pathParamDescriptor = new PathParamDescriptor();
						pathParamDescriptor.setPathName((String)pathNameField.get(pathParamInjector));
						restDescriptor.getPathParamDescriptors().add(pathParamDescriptor);
					}else if (valueInjector instanceof MessageBodyParameterInjector){
						//consume meta
						MessageBodyParameterInjector messageBodyParameterInjector = (MessageBodyParameterInjector)valueInjector;
						Type type = (Type) genericTypeField.get(messageBodyParameterInjector);
						restDescriptor.setConsumeMeta(type.toString().replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;"));
					}
				}
				//produce meta
				restDescriptor.setProduceMeta(resourceMethod.getMethod().getGenericReturnType().toString().replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;"));
				//PathParam & QueryParam description
				RestParams restParams = FindAnnotation.findAnnotation(resourceMethod.getMethod().getAnnotations(),RestParams.class);
				if(restParams!=null){
					RestParam[] items = restParams.value();
					if(items!=null){
						for(RestParam restParam : items){
							for(PathParamDescriptor pathParamDescriptor : restDescriptor.getPathParamDescriptors()){
								if(pathParamDescriptor.getPathName().equals(restParam.name())){
									pathParamDescriptor.setRemark(restParam.remark());
									break;
								}
							}
							for(QueryParamDescriptor queryParamDescriptor : restDescriptor.getQueryParamDescriptors()){
								if(queryParamDescriptor.getParamName().equals(restParam.name())){
									queryParamDescriptor.setRemark(restParam.remark());
									break;
								}
							}
						}
					}
				}
				//rest description
				RestDescription restDescription = FindAnnotation.findAnnotation(resourceMethod.getMethod().getAnnotations(), RestDescription.class);
				if(restDescription!=null){
					restDescriptor.setDescription(restDescription.value());
				}
				//cache description
//				CacheController cacheControllerDescription = FindAnnotation.findAnnotation(resourceMethod.getMethod().getAnnotations(), CacheController.class);
//				if(cacheControllerDescription != null){
//					restDescriptor.setCacheController("NamespaceDepth:"+cacheControllerDescription.namespaceDepth() +",ValidPeriod:"+cacheControllerDescription.validPeriod());
//				}
				restDescriptorList.add(restDescriptor);
			}
			
		}
		
		
		
		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /></head><body>");
		sb.append("  <style type=\"text/css\"> \n");
		sb.append("  body{margin:0px;padding:0px;font-size: 10;}div{width:100%;overflow:auto;height:500px;}div ul{width:100%;float:left;list-style:none;list-style-type:none;margin:0px;padding:0px;}\n");
		sb.append("  div ul li{height:80px;float:left;word-break:break-all;border:1px #666666 solid;list-style:none;list-style-type:none;}\n");
		sb.append(" .title1{width:15%;height:80px;line-height:80px;text-align:center;}\n");
		sb.append("  .title2{width:210px;height:80px;line-height:80px;text-align:center;}\n");
		sb.append("  .title3{width:15%;height:80px;line-height:80px;text-align:center;}\n");
		sb.append("  .title4{width:15%;height:80px;line-height:80px;text-align:center;}\n");
		sb.append(" .title5{width:20%;height:80px;line-height:80px;text-align:center;}\n");
		sb.append(" .title6{width:10%;height:80px;line-height:80px;text-align:center;}\n");
		sb.append(" .content1{width:15%;}\n");
		sb.append("  .content2{width:210px;}\n");
		sb.append("  .content3{width:15%;overflow:auto;}\n");
		sb.append("  .content4{width:15%;overflow:auto;}\n");
		sb.append("  .content5{width:20%;overflow:auto;}\n");
		sb.append("  .content6{width:10%;overflow:auto;}\n");
		sb.append("  </style>\n");
		sb.append("<br>\n");
		
		sb.append("\n<div style=\"overflow-x: auto; overflow-y: auto; margin:10px; height: 500px; width:98%;\">");
		sb.append("<ul><li class=\"title1\">PATH</li><li class=\"title2\">MediaType</li><li class=\"title3\">参数</li><li class=\"title4\">描述信息</li><li class=\"title5\">返回</li><li class=\"title6\">缓存</li></ul>");
		for(RestDescriptor restDescriptor : restDescriptorList){
			sb.append("<ul>");
			sb.append("<li class=\"content1\">"+restDescriptor.getMethod()+" " + restDescriptor.getPath()+"</li>");
			sb.append("<li class=\"content2\">Consume:"+restDescriptor.getConsumeMediaTypes()+"<br>Produce:"+restDescriptor.getProduceMediaTypes()+"</li>");
			sb.append("<li class=\"content3\">");
			for(PathParamDescriptor pathParamDescriptor : restDescriptor.getPathParamDescriptors()){
				sb.append("Path&lt;"+pathParamDescriptor.getPathName()+"&gt;:" +nvl(pathParamDescriptor.getRemark())+"<br>");
			}
			for(QueryParamDescriptor queryParamDescriptor : restDescriptor.getQueryParamDescriptors()){
				sb.append("Query&lt;"+queryParamDescriptor.getParamName()+"&gt;:" +nvl(queryParamDescriptor.getRemark())+"<br>");
			}
			sb.append("</li>");
			sb.append("<li class=\"content4\"> "+ nvl(restDescriptor.getDescription()) +"</li>");
			sb.append("<li class=\"content5\">返回：" + nvl(restDescriptor.getProduceMeta()) +"<br>输入:"+ nvl(restDescriptor.getConsumeMeta()) + "</li>");
			sb.append("<li class=\"content6\"> "+ nvl(restDescriptor.getCacheController()) +"</li>");
			sb.append("</ul>\n");
		}
		//class info view
		sb.append("\n</div"); 
		sb.append("\n<br><table><tr><td>class:</td><td><input id=\"className\" size=60 type=\"text\"/><input type=\"button\" value=\"查看\" onclick=\"document.getElementById('classIframe').src='/api/class/'+document.getElementById('className').value;\"/> </td><td><IFRAME height=\"150px\" width=\"500px\" id=\"classIframe\"></IFRAME></td></tr></table>");
				
				
		sb.append("</body></html>");
		return sb.toString();
	}
	
	@SuppressWarnings("rawtypes")
    public static String getClassInfo(Class cls) throws Exception{
		PropertyUtilsBean pub = new PropertyUtilsBean();
		PropertyDescriptor[] pds = pub.getPropertyDescriptors(cls);
		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /></head><body>");
		sb.append("<table border=1><tr><td>属性</td><td>类型</td><td>名称</td><td>长度</td><td>Not Null</td><td>描述</td></tr>");
		for(PropertyDescriptor propertyDescriptor : pds){
			if(ignoreProperties.contains(propertyDescriptor.getName())) continue;
			sb.append("<tr><td>");
			sb.append(propertyDescriptor.getName() +"</td><td>" +propertyDescriptor.getPropertyType().getName()+"</td>" );
			Field field = propertyDescriptor.getReadMethod().getDeclaringClass().getDeclaredField(propertyDescriptor.getName());
			if(field!=null){
				RestAttribute restAttribute = FindAnnotation.findAnnotation(field.getAnnotations(),RestAttribute.class);
				if(restAttribute!=null){
					sb.append("<td>"+restAttribute.name()+"</td>" + "<td>"+restAttribute.len()+"</td>" + "<td>"+restAttribute.notnull()+"</td>"+ "<td>"+restAttribute.remark()+"</td>");
				}
			}else{
				sb.append("<td colspan=\"3\"/>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}
	
	private static String nvl(Object obj){
		if(obj==null) return "";
		return obj.toString();
	}
}
