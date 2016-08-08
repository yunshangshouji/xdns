package zhuboss.dnsproxy.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Message;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ResolverListener;
import org.xbill.DNS.Type;

import zhuboss.dnsproxy.hosts.HandleResponse;
 
public abstract class UDPSeverHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	Resolver resolver;
	public UDPSeverHandler(Resolver resolver){
		this.resolver = resolver;
	}
	
	
  @Override
  protected void messageReceived(final ChannelHandlerContext ctx,
     final DatagramPacket packet) throws Exception {
	  String clientIP = packet.sender().getAddress().getHostAddress();
	  
        byte[] data = new byte[packet.content().readableBytes()];
        packet.content().readBytes(data);
        
        Message query = new Message(data);
        
        /**
         * IPV6 返回空记录，避免客户端等待超时
         */
        if(query.getQuestion().getType() == Type.AAAA){
        	ctx.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(new Message(query.getHeader().getID()).toWire()),packet.sender()));
        	return;
        }
        
        /**
         * 首先使用HOSTS解析
         */
        HandleResponse response = new HandleResponse(new Message(query.getHeader().getID()));
        
        process(clientIP, query, response);
        
        if(response.isHasRecord()){
        	ctx.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(response.getMessage().toWire()),packet.sender()));
        	return;
        }
        
        //同步请求
//        org.xbill.DNS.Message messge = res.send(query);
        //异步请求
        resolver.sendAsync(query, new ResolverListener(){
	
			@Override
			public void receiveMessage(Object id, Message m) {
//				System.out.println(m);
				 ctx.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(m.toWire()),packet.sender()));
				
			}
	
			@Override
			public void handleException(Object id, Exception e) {
				// TODO Auto-generated method stub
				
			}
	    	
	    });
 
  }

  abstract public void process(String clientIP, Message query, HandleResponse response);
  
  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    super.channelRegistered(ctx);
  }
 
 
 
}