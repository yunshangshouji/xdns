package zhuboss.dnsproxy;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;

import zhuboss.dnsproxy.handler.UDPSeverHandler4IP;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class Starter {
	
	public static ApplicationContext applicationContext;
	
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure(new FileInputStream(new File("./conf/log4j.properties")));
			applicationContext = new ClassPathXmlApplicationContext(
					new String[] { "spring.xml"});
//		CustomizedPropertyPlaceholderConfigurer configure;
		/**
		 * 启动监听服务
		 */
		Resolver resolver = new SimpleResolver("114.114.114.114");
		  Bootstrap b = new Bootstrap();
		    EventLoopGroup group = new NioEventLoopGroup();
		    b.group(group)
		        .channel(NioDatagramChannel.class)
		        .option(ChannelOption.SO_BROADCAST, true)
		        .handler(new UDPSeverHandler4IP(resolver));
		        b.bind(53).sync().channel().closeFuture().await();
		  }


}
