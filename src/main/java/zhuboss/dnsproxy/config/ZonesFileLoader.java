package zhuboss.dnsproxy.config;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ZonesFileLoader {

	private static Logger logger = Logger.getLogger(ZonesFileLoader.class);

	public static DomainPatternsContainer readConfig(String cfgText) {
		try {
			DomainPatternsContainer domainPatternsContainer = new DomainPatternsContainer();
            DomainPatternsContainer nsDomainPatternContainer = new DomainPatternsContainer();
			DoubleKeyMap<String, Pattern, String> customAnswerPatternsTemp = new DoubleKeyMap<String, Pattern, String>(
					new ConcurrentHashMap<String, Map<Pattern, String>>(), LinkedHashMap.class);
			DoubleKeyMap<String, String, String> customAnswerTextsTemp = new DoubleKeyMap<String, String, String>(
					new ConcurrentHashMap<String, Map<String, String>>(), HashMap.class);
			BufferedReader bufferedReader = new BufferedReader(new StringReader(cfgText));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				ZonesPattern zonesPattern = null;
				try{
					zonesPattern = ZonesPattern.parse(line); //注意：可能输入非法ip格式
				}catch(Exception e){
					continue;
				}
				if (zonesPattern == null) {
					continue;
				}
				try {
					if (zonesPattern.getUserIp() == null) {
						for (Pattern pattern : zonesPattern.getPatterns()) {
							domainPatternsContainer.getDomainPatterns().put(pattern, zonesPattern.getTargetIp());
						}
						for (String text : zonesPattern.getTexts()) {
							domainPatternsContainer.getDomainTexts().put(text, zonesPattern.getTargetIp());
						}
					} else {
						for (Pattern pattern : zonesPattern.getPatterns()) {
							customAnswerPatternsTemp.put(zonesPattern.getUserIp(), pattern, zonesPattern.getTargetIp());
						}
						for (String text : zonesPattern.getTexts()) {
							customAnswerTextsTemp.put(zonesPattern.getUserIp(), text, zonesPattern.getTargetIp());
						}
					}
					logger.info("read config success:\t" + line);
				} catch (Exception e) {
					logger.warn("parse config line error:\t" + line + "\t" , e);
				}
				// For NS
				if (line.startsWith("NS")) {
					line = StringUtils.removeStartIgnoreCase(line, "NS").trim();
                    zonesPattern = ZonesPattern.parse(line);
                    if (zonesPattern == null) {
                        continue;
                    }
                    try {
                        for (Pattern pattern : zonesPattern.getPatterns()) {
                            nsDomainPatternContainer.getDomainPatterns().put(pattern,zonesPattern.getTargetIp());
                        }
                        for (String text : zonesPattern.getTexts()) {
                            nsDomainPatternContainer.getDomainTexts().put(text,zonesPattern.getTargetIp());
                        }
                        logger.info("read config success:\t" + line);
                    } catch (Exception e) {
                        logger.warn("parse config line error:\t" + line + "\t" + e);
                    }
				}
			}
			bufferedReader.close();
			return domainPatternsContainer;
		} catch (Throwable e) {
			logger.warn("read config file failed:", e);
			return new DomainPatternsContainer();
		}
	}


}
