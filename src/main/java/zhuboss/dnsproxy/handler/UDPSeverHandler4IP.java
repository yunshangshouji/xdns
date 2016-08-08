package zhuboss.dnsproxy.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Message;
import org.xbill.DNS.Resolver;

import zhuboss.dnsproxy.Starter;
import zhuboss.dnsproxy.hosts.DomainPatternsContainer4IP;
import zhuboss.dnsproxy.hosts.HandleResponse;
import zhuboss.dnsproxy.hosts.QueryProcesser;
 
public class UDPSeverHandler4IP extends UDPSeverHandler {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	static QueryProcesser queryProcesser = new QueryProcesser();
	
	public UDPSeverHandler4IP(Resolver resolver) {
		super(resolver);
	}

	public void process(String clientIP, Message query, HandleResponse response) {
		DomainPatternsContainer4IP domainPatternsContainer4IP = Starter.applicationContext.getBean(DomainPatternsContainer4IP.class);
		queryProcesser.handle(domainPatternsContainer4IP.getDomainPatternsContainer(clientIP),query, response);
	}
  
 
 
}