package zhuboss.framework.server.reqlog;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler.Context;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;

import zhuboss.framework.util.JavaUtil;

public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {

	byte[] bytes;
	ServletInputStream sis;
	public MyHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		/**
		 * 注意流只能读取一次，表单提交由于request中的parameter也是从stream构建
		 */
		
		try {
			bytes = JavaUtil.InputStreamToBytes(super.getInputStream());
			final InputStream is = new ByteArrayInputStream(bytes);
			sis = new ServletInputStream(){
				
				@Override
				public int available() throws IOException {
					return is.available();
				}

				
				@Override
				public int read(byte[] b) throws IOException {
					return is.read(b);
				}



				@Override
				public int read(byte[] b, int off, int len) throws IOException {
					return is.read(b, off, len);
				}



				@Override
				public long skip(long n) throws IOException {
					return is.skip(n);
				}



				@Override
				public void close() throws IOException {
					is.close();
				}



				@Override
				public synchronized void mark(int readlimit) {
					is.mark(readlimit);
				}



				@Override
				public synchronized void reset() throws IOException {
					is.reset();
				}



				@Override
				public boolean markSupported() {
					return is.markSupported();
				}



				@Override
				public boolean isFinished() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean isReady() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void setReadListener(ReadListener readListener) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public int read() throws IOException {
					return is.read();
				}};
				
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	
		/**
		 * 如果是form表单提交，则不解析inputstream,因为inputstream不能读取两次
		 * 参考：org.eclipse.jetty.server.Request.extractContentParameters()
		 */
		String contentType =HttpFields.valueParameters( request.getContentType(), null);
         int contentLength = getContentLength();
         if (contentLength != 0)
         {
             if (MimeTypes.Type.FORM_ENCODED.is(contentType) &&
                     (HttpMethod.POST.is(getMethod()) || HttpMethod.PUT.is(getMethod())))
             {
            	 MultiMap<String> params = new MultiMap<>(); 
    	            int maxFormKeys = -1;
    	            Context  _context =((Request)request).getContext();
    	            if (_context != null)
    	            {
    	                maxFormKeys = _context.getContextHandler().getMaxFormKeys();
    	            }
    			 UrlEncoded.decodeTo(new String(bytes),params,getCharacterEncoding(),maxFormKeys);
    			 ((Request)request).setContentParameters(params);
             }
         }
	}

	public String getRequestContent(){
		return new String(bytes);
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
			return sis;
	}

}
