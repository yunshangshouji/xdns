package zhuboss.framework.rest.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

public class FileDownProvider implements MessageBodyWriter<FileDown>{

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return FileDown.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(FileDown t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return t.getFileSize(); //default -1
	}

	@Override
	public void writeTo(FileDown t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		for(Map.Entry<String, String> entry : t.getAddHeaders().entrySet()){
			httpHeaders.add(entry.getKey(), entry.getValue());
		}
		byte[] data = new byte[t.getInputBatchSize()];  
		int count = -1;  
        while((count = t.getInputStream().read(data,0,t.getInputBatchSize())) != -1)  
        	entityStream.write(data, 0, count);  
//        entityStream.flush();
        t.getInputStream().close();
        
	}

}
