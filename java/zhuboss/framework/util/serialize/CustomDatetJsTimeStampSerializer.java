package zhuboss.framework.util.serialize;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class CustomDatetJsTimeStampSerializer extends JsonSerializer<Date> {
	
	@Override
	public void serialize(Date value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeString(String.valueOf(value.getTime()/1000)); //to JS TIMESTAMP
		
	}

	@Override
	public Class<Date> handledType() {
		return Date.class;
	}
	
	
}
