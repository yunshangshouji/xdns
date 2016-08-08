package zhuboss.framework.server;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

public class StartWebApp {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) throws Exception {
		
		PropertyConfigurator.configure(new FileSystemResource("./conf/log4j.properties").getInputStream());
		
		try {
			@SuppressWarnings("unused")
            ApplicationContext applicationContext = new FileSystemXmlApplicationContext(
					new String[] { "./conf/spring.xml", "./conf/extend*.xml" });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
