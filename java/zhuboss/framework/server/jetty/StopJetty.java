package zhuboss.framework.server.jetty;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class StopJetty {
	
	private static final String JETTY_PROPERTIES="./conf/jetty.properties";
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(JETTY_PROPERTIES));
		String port="STOP.PORT="+properties.getProperty("jetty.start.STOP.PORT");
		String key="STOP.KEY="+properties.getProperty("jetty.start.STOP.KEY");
		org.eclipse.jetty.start.Main.main(new String[]{port,key,"--stop"});
	}

}
