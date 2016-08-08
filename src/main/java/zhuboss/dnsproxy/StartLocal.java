package zhuboss.dnsproxy;

import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;

import zhuboss.dnsproxy.config.DomainPatternsContainer;
import zhuboss.dnsproxy.config.ZonesFileLoader;
import zhuboss.dnsproxy.handler.UDPSeverHandler4Local;
import zhuboss.framework.util.JavaUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class StartLocal {
	public static void main(String[] args) throws Exception {
		Logger logger = LoggerFactory.getLogger(StartLocal.class);
		String backendDnsAddr = "114.114.114.114";
		if(args.length>0 && args[0].matches("([0-9]{1,3}\\.){3}[0-9]{1,3}")){
			backendDnsAddr = args[0] ;
		}
		logger.info("proxy dns server:"+backendDnsAddr);
		String cfgText = JavaUtil.InputStreamTOString(new FileInputStream("zones"), "UTF-8");
		DomainPatternsContainer domainPatternsContainer = ZonesFileLoader.readConfig(cfgText);
		Resolver resolver = new SimpleResolver(backendDnsAddr);
		/**
		 * 启动监听服务
		 */
		  Bootstrap b = new Bootstrap();
		    EventLoopGroup group = new NioEventLoopGroup();
		    b.group(group)
		        .channel(NioDatagramChannel.class)
		        .option(ChannelOption.SO_BROADCAST, true)
		        .handler(new UDPSeverHandler4Local(resolver,domainPatternsContainer));
		        b.bind(53).sync().channel().closeFuture().await();
	}

}
