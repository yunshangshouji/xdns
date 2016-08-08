package zhuboss.dnsproxy.rest;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import zhuboss.dnsproxy.config.EhCacheManager;
import zhuboss.dnsproxy.mapper.HostsPOMapper;
import zhuboss.dnsproxy.po.HostsPO;


@Controller
@Path("/manage")
public class ManageRest {
	
	@Autowired
	EhCacheManager ehCacheManager;
	@Autowired
	HostsPOMapper hostsPOMapper;
	
	@GET
	@Path("get")
	@Produces(MediaType.TEXT_HTML)
	public String getHosts(@QueryParam("ip")String ip){
		HostsPO hostsPO = hostsPOMapper.selectByPK(ip);
		if(hostsPO == null){
			return "";
		}else{
			return hostsPO.getHosts();
		}
	}
	
	@POST
	@Path("submit")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void submit(HostsPO param) {
		HostsPO hostsPO = hostsPOMapper.selectByPK(param.getIp());
		if(hostsPO == null){
			hostsPO = new HostsPO();
			hostsPO.setIp(param.getIp());
			hostsPO.setHosts(param.getHosts());
			hostsPO.setMainDate(new Date());
			hostsPOMapper.insert(hostsPO);
		}else{
			hostsPO.setHosts(param.getHosts());
			hostsPO.setMainDate(new Date());
			hostsPOMapper.updateByPK(hostsPO);
		}
		this.clear(param.getIp());
	}
	
	@GET
	@Path("clear")
	public String clear(@QueryParam("ip")String ip){
		String msg ;
		if(StringUtils.hasText(ip)){
			ehCacheManager.getCache(EhCacheManager.CACHE.DNS).remove(ip);
			msg = " clear " + ip +" success";
		}else{
			ehCacheManager.getCache(EhCacheManager.CACHE.DNS).removeAll();
			msg =  "clear ALL success";
		}
		return msg;
	}
	
}
