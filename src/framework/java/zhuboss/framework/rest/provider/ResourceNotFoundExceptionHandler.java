package zhuboss.framework.rest.provider;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import zhuboss.framework.exception.ResourceNotFoundException;
import zhuboss.framework.util.DateUtils;
import zhuboss.framework.util.JavaUtil;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;

/** 
 * @author 作者 zhuzhengquan: 
 * @version 创建时间：2016年5月4日 下午5:08:14 
 * 类说明 
 */
@Provider
public class ResourceNotFoundExceptionHandler implements
		ExceptionMapper<ResourceNotFoundException> {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	final String TEMPLATE_NAME = "error";
	Configuration cfg = new Configuration();
	StringTemplateLoader templateLoader;
	public ResourceNotFoundExceptionHandler(){
		templateLoader = new StringTemplateLoader();
		String ftl = "";
		try {
			ftl = JavaUtil.InputStreamTOString(new ClassPathResource("404.ftl").getInputStream(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		templateLoader.putTemplate(TEMPLATE_NAME, ftl);
		cfg.setTemplateLoader(templateLoader);
		cfg.setOutputEncoding("UTF-8");
	}
	
	@Override
	public Response toResponse(ResourceNotFoundException exception) {
		StringWriter sw = new StringWriter();
		try {
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("exception", exception);
			data.put("dispDate",DateUtils.toFullDateString(new Date()));
			cfg.getTemplate(TEMPLATE_NAME).process(data, sw);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} 
		String text = sw.toString();
		return Response.status(Status.OK).entity(text).build();
	}

}
