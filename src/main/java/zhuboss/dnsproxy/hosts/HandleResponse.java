package zhuboss.dnsproxy.hosts;

import org.xbill.DNS.Message;

public class HandleResponse {
	
	private Message message;
	
	private boolean hasRecord;
	
	public HandleResponse(Message message) {
		this.message = message;
	}
	
	public Message getMessage() {
		return message;
	}

	public boolean isHasRecord() {
		return hasRecord;
	}

	public void setHasRecord(boolean hasRecord) {
		this.hasRecord = hasRecord;
	}
	
}
