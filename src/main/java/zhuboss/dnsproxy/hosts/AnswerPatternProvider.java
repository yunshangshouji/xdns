package zhuboss.dnsproxy.hosts;

import org.xbill.DNS.Address;
import org.xbill.DNS.Type;

import zhuboss.dnsproxy.config.DomainPatternsContainer;

public class AnswerPatternProvider  {


	/**
	 * When the address configured as "DO_NOTHING",it will not return any
	 * address.
	 */
	public static final String DO_NOTHING = "do_nothing";
	private static final String FAKE_MX_PREFIX = "mail.";
	private static final String FAKE_CANME_PREFIX = "cname.";

	public String getAnswer(DomainPatternsContainer domainPatternsContainer,String query, int type) {
		if (type == Type.PTR) {
			return null;
		}
		
		String ip = domainPatternsContainer.getIp(query);
		if (ip == null || ip.equals(DO_NOTHING)) {
			return null;
		}
		if (type == Type.MX) {
			String fakeMXHost = fakeMXHost(query);
			return fakeMXHost;
		}
		if (type == Type.CNAME) {
			String fakeCNAMEHost = fakeCNAMEHost(query);
			return fakeCNAMEHost;
		}
		
		//默认A记录
		return ip;
	}

	/**
	 * generate a fake MX host
	 * 
	 * @param domain
	 * @return
	 */
	private String fakeMXHost(String domain) {
		return FAKE_MX_PREFIX + domain;
	}

	/**
	 * @param domain
	 * @return
	 */
	private String fakeCNAMEHost(String domain) {
		return FAKE_CANME_PREFIX + domain;
	}

	private String reverseIp(String ip) {
		int[] array = Address.toArray(ip);
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = array.length - 1; i >= 0; i--) {
			stringBuilder.append(array[i] + ".");
		}
		stringBuilder.append("in-addr.arpa.");
		return stringBuilder.toString();
	}

}
