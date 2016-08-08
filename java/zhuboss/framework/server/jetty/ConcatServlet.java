package zhuboss.framework.server.jetty;

import java.io.IOException;

import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Example: <br>
 * /_s/_s.css?css/a.css,css/b.css<br>
 * /_s/_s.js?a.js,b.js
 * @author Administrator
 *
 */
public class ConcatServlet extends DefaultServlet {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2296574971941679120L;
	
	@Override
	public Resource getResource(String pathInContext) {
		Request request = HttpChannel.getCurrentHttpChannel().getRequest();
		String queryString = request.getQueryString();
		if(queryString==null||queryString.equals("")) return null;
		String[] paths = queryString.split(",");
		FileResource[] resources = new FileResource[paths.length];
		
		for(int i=0;i<paths.length;i++){
			resources[i] = (FileResource)super.getResource("/"+paths[i]);
		}
		
		ConcatResource concatResource = null;
		try {
			concatResource = new ConcatResource( (pathInContext.endsWith(".gz")?true:false),pathInContext, resources,paths);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return concatResource;
	}

	
	
}
