package zhuboss.framework.server.health;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HealthCheckServlet extends HttpServlet{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String INITAL_PARAM_NAME = "CHECK_LIST";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7752747574167672000L;

	private List<IHealthCheck> healthCheckList = new ArrayList<IHealthCheck>();
	
	@Override
	public void init() throws ServletException {
		super.init();
		String checkListString = super.getInitParameter(INITAL_PARAM_NAME);
		if(checkListString == null || checkListString.length()==0){
			return ;
		}
		String[] checkListS = checkListString.split(",");
		for(String check : checkListS){
			try {
				healthCheckList.add((IHealthCheck)Class.forName(check).newInstance());
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new RuntimeException(e);
			} 
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req, resp);
	}
	
	private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		try{
			for(IHealthCheck healthCheck : healthCheckList){
				healthCheck.check();
			}
			resp.getWriter().write("imok");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			resp.setStatus(500, "Service Stop");
			resp.getWriter().write("fail!\n");
			 StringWriter sw = new StringWriter();  
	         PrintWriter pw = new PrintWriter(sw);  
	         e.printStackTrace(pw); 
	         resp.getWriter().write(sw.toString());
			return;
		}
		
		
	}

}
