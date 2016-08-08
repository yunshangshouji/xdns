package zhuboss.dnsproxy.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Message;
import org.xbill.DNS.Resolver;

import zhuboss.dnsproxy.config.DomainPatternsContainer;
import zhuboss.dnsproxy.hosts.HandleResponse;
import zhuboss.dnsproxy.hosts.QueryProcesser;
 
public class UDPSeverHandler4Local extends UDPSeverHandler {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	static QueryProcesser queryProcesser = new QueryProcesser();
	
	DomainPatternsContainer domainPatternsContainer;
	
	public UDPSeverHandler4Local(Resolver resolver,DomainPatternsContainer domainPatternsContainer) {
		super(resolver);
		this.domainPatternsContainer = domainPatternsContainer;
	}


	@Override
	public void process(String clientIP, Message query, HandleResponse response) {
		queryProcesser.handle(domainPatternsContainer, query, response);
		
	}
	
	
 
 
}