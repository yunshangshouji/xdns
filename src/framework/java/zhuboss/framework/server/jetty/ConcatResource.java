package zhuboss.framework.server.jetty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;

import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;

public class ConcatResource extends Resource {
	final static int BUFFER_SIZE = 4096; 
	FileResource[] frs;
	ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	long len = 0;
	boolean gzip = false;//for check file exists , style.css.gz not exists
    String _uri = null;//usr http header "Content-Type"
    
	public ConcatResource(boolean gzip,String _uri,FileResource[] frs,String[] paths) throws IOException{
		this.gzip = gzip;
		this.frs = frs;
		this._uri = _uri;
		byte[] data = new byte[BUFFER_SIZE];  
	    int count = -1;  
		for(int i=0;i<frs.length;i++){
			FileResource fr = frs[i];
			byte[] sepLine = ("\n/**/"+paths[i]+"**/\n").getBytes() ;
			len += sepLine.length;
			outStream.write(sepLine);
			InputStream is = fr.getInputStream();
			while((count = is.read(data,0,BUFFER_SIZE)) != -1){
				outStream.write(data, 0, count);  
				len += count;
			}
			is.close();//release file handler
			fr.close();
		}
	}
	
	@Override
	public boolean isContainedIn(Resource r) throws MalformedURLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		for(FileResource fr : frs){
			fr.close();
		}

	}

	@Override
	public boolean exists() {
		if(this.gzip) return false;
		
		for(FileResource fr : frs){
			if(!fr.exists()){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isDirectory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long lastModified() {
		long LM = 0;
		for(FileResource fr : frs){
			if(fr.lastModified()>LM){
				LM = fr.lastModified();
			}
		}
		return LM;
	}

	@Override
	public long length() {
		return len;
	}

	@Override
	public URL getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getFile() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(outStream.toByteArray());
	}

	@Override
	public ReadableByteChannel getReadableByteChannel() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete() throws SecurityException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean renameTo(Resource dest) throws SecurityException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource addPath(String path) throws IOException,
			MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public String toString()
    {//for HttpHeader Content-Type:text/css; charset=UTF-8
        return _uri;
    }
	
	
}
