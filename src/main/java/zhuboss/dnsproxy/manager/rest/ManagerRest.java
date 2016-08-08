package zhuboss.dnsproxy.manager.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ManagerRest {
	
	@GET
	@Path("fireConfigChange")
	@Produces(MediaType.APPLICATION_JSON)
	public void fireConfigChange(@QueryParam("ip")String ip){
		
	}
	
}
