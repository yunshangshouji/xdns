package zhuboss.framework.server.jetty;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.core.io.FileSystemResource;

public class StartJetty {
	private static final String JETTY_PROPERTIES="./conf/jetty.properties";
	private static final String JETTY_XML ="./conf/jetty.xml";
	private static final String JETTY_START_ARG_PREFIX="jetty.start.";
	
	public static void main(String[] args) throws Exception{
		final String  logProPath = "./conf/log4j.properties";
		if(!new File(logProPath).exists()){
			throw new Exception("log4j.properties文件不存在");
		}
		//see org.eclipse.jetty.util.log.Log staic{}
		System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.Slf4jLog");
		PropertyConfigurator.configure(new FileSystemResource(logProPath).getInputStream());

//		String[] params = new String[]{
//				JETTY_PROPERTIES,
//				JETTY_XML,
//			"STOP.PORT=28282",
//			"STOP.KEY=secret"
//		};
		List<String> paramList = new ArrayList<String>();
		paramList.add(JETTY_PROPERTIES);
		paramList.add(JETTY_XML);
		Properties properties = new Properties();
		properties.load(new FileInputStream(JETTY_PROPERTIES));
		Set<Entry<Object,Object>> set = properties.entrySet();
		for(Entry<Object,Object> entry : set){
			String key = entry.getKey().toString();
			if(key.startsWith(JETTY_START_ARG_PREFIX)){
				paramList.add(key.substring(JETTY_START_ARG_PREFIX.length())+"="+entry.getValue().toString());
			}
		}
		
		org.eclipse.jetty.start.Main.main(paramList.toArray(new String[]{}));
		
		//>java -jar start.jar STOP.PORT=28282 STOP.KEY=secret --stop
		
//		XmlConfiguration.main(new String[]{"./conf/jetty.properties",
//				"./conf/jetty.xml"});//notice:property files must be before
		
		
/**
 * 		
		File jettyConfigFile = new File("./conf/jetty.xml");
		FileInputStream fis = new FileInputStream(jettyConfigFile);
		XmlConfiguration xmlConfiguration = new XmlConfiguration(fis);
		Server server = (Server)xmlConfiguration.configure();
		server.start();
*/
	}
	
}
