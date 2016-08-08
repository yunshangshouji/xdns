package zhuboss.dnsproxy.hosts;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

import zhuboss.dnsproxy.config.DomainPatternsContainer;
import zhuboss.dnsproxy.config.EhCacheManager;
import zhuboss.dnsproxy.config.ZonesFileLoader;
import zhuboss.dnsproxy.mapper.HostsPOMapper;
import zhuboss.dnsproxy.po.HostsPO;

public class DomainPatternsContainer4IP {
	@Autowired
	EhCacheManager ehCacheManager;
	@Autowired
	HostsPOMapper hostsPOMapper;
	
	
	public DomainPatternsContainer getDomainPatternsContainer(final String clientIP){
		return ehCacheManager.getData(EhCacheManager.CACHE.DNS, clientIP, new Callable<DomainPatternsContainer>(){
			@Override
			public DomainPatternsContainer call() throws Exception {
				HostsPO  hostsPO  = hostsPOMapper.selectByPK(clientIP);
				if(hostsPO==null){
					return new DomainPatternsContainer();
				}
				return ZonesFileLoader.readConfig(hostsPO.getHosts());
			}
			
		});
	}
}
