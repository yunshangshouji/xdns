package zhuboss.framework.util.kaptcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;



import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
/**
 * <table ><tr>
<td >
<strong>Constant</strong>
</td>
<td >
<strong>Description</strong>
</td>
<td >
<strong>Default</strong>
</td>
</tr>
<tr>
<td >kaptcha.border</td>
<td >Border around kaptcha. Legal values are yes or no.</td>
<td >yes</td>
</tr>
<tr>
<td >kaptcha.border.color</td>
<td >Color of the border. Legal values are r,g,b (and optional alpha) or white,black,blue.</td>
<td >black</td>
</tr>
<tr>
<td >kaptcha.border.thickness</td>
<td >Thickness of the border around kaptcha. Legal values are &gt; 0.</td>
<td >1</td>
</tr>
<tr>
<td >kaptcha.image.width</td>
<td >Width in pixels of the kaptcha image.</td>
<td >200</td>
</tr>
<tr>
<td >kaptcha.image.height</td>
<td >Height in pixels of the kaptcha image.</td>
<td >50</td>
</tr>
<tr>
<td >kaptcha.producer.impl</td>
<td >The image producer.</td>
<td >com.google.code.kaptcha.impl.DefaultKaptcha</td>
</tr>
<tr>
<td >kaptcha.textproducer.impl</td>
<td >The text producer.</td>
<td >com.google.code.kaptcha.text.impl.DefaultTextCreator</td>
</tr>
<tr>
<td >kaptcha.textproducer.char.string</td>
<td >The characters that will create the kaptcha.</td>
<td >abcde2345678gfynmnpwx</td>
</tr>
<tr>
<td >kaptcha.textproducer.char.length</td>
<td >The number of characters to display.</td>
<td >5</td>
</tr>
<tr>
<td >kaptcha.textproducer.font.names</td>
<td >A list of comma separated font names.</td>
<td >Arial, Courier</td>
</tr>
<tr>
<td >kaptcha.textproducer.font.size</td>
<td >The size of the font to use.</td>
<td >40px.</td>
</tr>
<tr>
<td >kaptcha.textproducer.font.color</td>
<td >The color to use for the font. Legal values are r,g,b.</td>
<td >black</td>
</tr>
<tr>
<td >kaptcha.noise.impl</td>
<td >The noise producer.</td>
<td >com.google.code.kaptcha.impl.DefaultNoise</td>
</tr>
<tr>
<td >kaptcha.noise.color</td>
<td >The noise color. Legal values are r,g,b.</td>
<td >black</td>
</tr>
<tr>
<td >kaptcha.obscurificator.impl</td>
<td >The obscurificator implementation.</td>
<td >com.google.code.kaptcha.impl.WaterRipple</td>
</tr>
<tr>
<td >kaptcha.background.impl</td>
<td >The background implementation.</td>
<td >com.google.code.kaptcha.impl.DefaultBackground</td>
</tr>
<tr>
<td >kaptcha.background.clear.from</td>
<td >Starting background color. Legal values are r,g,b.</td>
<td >light grey</td>
</tr>
<tr>
<td >kaptcha.background.clear.to</td>
<td >Ending background color. Legal values are r,g,b.</td>
<td >white</td>
</tr>
<tr>
<td >kaptcha.word.impl</td>
<td >The word renderer implementation.</td>
<td >com.google.code.kaptcha.text.impl.DefaultWordRenderer</td>
</tr>
<tr>
<td >kaptcha.session.key</td>
<td >The value for the kaptcha is generated and is put into the HttpSession. This is the key value for that item in the session.</td>
<td >KAPTCHA_SESSION_KEY</td>
</tr>
<tr>
<td >kaptcha.session.date</td>
<td >The date the kaptcha is generated is put into the HttpSession. This is the key value for that item in the session.</td>
<td >KAPTCHA_SESSION_DATE</td>
</tr>
</table>
 * @author Administrator
 *
 */
public class KaptchaSimple {
	
	private static final String KAPTCHA_SESSION_KEY= "KAPTCHA_SESSION_KEY";
	
	/**
	 * 生成校验码图片返回，并将图片文本记入HttpSession
	 * @param httpSession
	 * @return
	 * @throws IOException
	 */
    public static Response gen(HttpSession httpSession) throws IOException{
        Properties properties = new java.util.Properties();
        properties.put("kaptcha.border", "yes");
        properties.put("kaptcha.textproducer.char.length", "4");
        properties.put("kaptcha.textproducer.font.color", "white");
        properties.put("kaptcha.textproducer.font.size", "32");
        properties.put("kaptcha.noise.color", "red");
        properties.put("kaptcha.background.clear.from", "blue");
        properties.put("kaptcha.background.clear.to", "black");
        properties.put("kaptcha.image.width", "120");
        properties.put("kaptcha.image.height", "40");
        properties.put("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");
        
        return KaptchaSimple.gen(httpSession, properties);
    }
	
	/**
	 * 生成校验码图片返回，并将图片文本记入HttpSession
	 * @param httpSession
	 * @return
	 * @throws IOException
	 */
	public static Response gen(HttpSession httpSession,Properties properties ) throws IOException{
		Config config = new com.google.code.kaptcha.util.Config(properties);
		DefaultKaptcha captchaProducer = new com.google.code.kaptcha.impl.DefaultKaptcha();
		captchaProducer.setConfig(config);
		 String capText = captchaProducer.createText();  
		BufferedImage bi = captchaProducer.createImage(capText);
		httpSession.setAttribute(KAPTCHA_SESSION_KEY, capText);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bi, "jpg", byteArrayOutputStream);  
		return Response.ok(byteArrayOutputStream.toByteArray())
				.header("Cache-Control", "no-store, no-cache, must-revalidate") // Set standard HTTP/1.1 no-cache headers
				.header("Cache-Control", "post-check=0, pre-check=0") // Set IE extended HTTP/1.1 no-cache headers (use addHeader).  
				.header("Pragma", "no-cache") //Set standard HTTP/1.0 no-cache header.  
				.header("Content-Type", "image/jpg").build();
	}

	/**
	 * 生成字符串验证码文本放入session并返回
	 * @param httpSession
	 * @return
	 * @throws IOException
	 */
	public static String returnGenCode(HttpSession httpSession) throws IOException{
		int[] codes=new int[]{0,1,2,3,4,5,6,7,8,9};
		String code="";
		Random random = new  Random();
		
		for (int i = 0; i < 4; i++) {
			int index=random.nextInt(codes.length);
			int j = codes[index];
			code+=j;
		}
		httpSession.setAttribute(KAPTCHA_SESSION_KEY,code);
		return code;
	}
	
	/**
	 * 校验returnCheckCode是否与最新记入的校验码一致
	 * @param httpSession
	 * @param returnCheckCode
	 * @return
	 */
	public static boolean check(HttpSession httpSession, String returnCheckCode){
		String capText = (String)httpSession.getAttribute(KAPTCHA_SESSION_KEY);
		if (capText==null) return false;
		if(capText.equals(returnCheckCode)) {
			return true;
		}else{
			return false;
		}
	}
}