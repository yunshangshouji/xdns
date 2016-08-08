package zhuboss.framework.util.xls;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import net.sf.jxls.transformer.XLSTransformer;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class XlsUtil {
	
	/**
	 * 
	 * @param templatePath excel模板的classpath路径
	 * @param beans	el表达式的context bean
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
    public static byte[] genExcel(String templatePath,Map beanMap) throws Exception {
		Resource templateResource = new ClassPathResource(templatePath);
		InputStream inputStream = templateResource.getInputStream();
		XLSTransformer transformer = new XLSTransformer();
		ByteArrayOutputStream baos=new ByteArrayOutputStream();

		transformer.transformXLS( inputStream, beanMap).write(baos);
		return baos.toByteArray();
	}
	
}
