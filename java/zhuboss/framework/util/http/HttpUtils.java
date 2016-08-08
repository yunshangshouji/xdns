package zhuboss.framework.util.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;

public class HttpUtils {
	private static final Pattern pattern = Pattern.compile("filename=\"([^\"]+\\.\\w+)\"");
	
	public static String getUploadFileName(InputPart inputPart){
		//TODO header中的信息未做转码
		String contentDisposition = inputPart.getHeaders().get("Content-Disposition").get(0);
		Matcher mather = pattern.matcher(contentDisposition); 
		mather.find();
		String fileName = mather.group(1);
		return fileName;
	}
}
