package zhuboss.framework.server.reqlog;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.eclipse.jetty.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zhuboss.framework.server.jetty.MyServletOutputStream;

public class MyHttpServletResponseWrapper extends HttpServletResponseWrapper {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private PrintWriter tmpWriter ;  
	private MyServletOutputStream output ;
	
	public MyHttpServletResponseWrapper(HttpServletResponse response) {
		super(response);
		output = new MyServletOutputStream();
        tmpWriter = new PrintWriter( output );  
	}
	public Response getRawResponse(){
		return (Response)super.getResponse();
	}
	  //覆盖getWriter()方法，使用我们自己定义的Writer      
    public PrintWriter getWriter() throws IOException {      
        return tmpWriter ;      
   }    
     
    public void close() throws IOException {      
        tmpWriter .close();      
   }
    
    @Override
	public ServletOutputStream getOutputStream() throws IOException {
		return this.output;
	}

	public void doFlush() throws IOException{
			tmpWriter.flush(); // 刷新该流的缓冲，详看java.io.Writer.flush()
			super.getResponse().setContentLength(output.getLength());
			output.close();
			super.getResponse().getOutputStream().write(output.getBytes());
			super.getResponse().getOutputStream().flush();
    }
    public String getContent() {
        try {      
			String s = output.getContent();
			return s;
       }  catch (Exception e) {      
    	   logger.error(e.getMessage(), e);
            return null ;      
       }      
   }      
 
}
