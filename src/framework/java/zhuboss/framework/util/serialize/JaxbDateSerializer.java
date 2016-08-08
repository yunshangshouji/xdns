package zhuboss.framework.util.serialize;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JaxbDateSerializer extends XmlAdapter<String, Date> {

	@Override
	public Date unmarshal(String v) throws Exception {
		return new Date(new Long(v));
	}

	@Override
	public String marshal(Date v) throws Exception {
		return v.getTime()+"";
	}

}
