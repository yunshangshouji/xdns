package zhuboss.framework.rest.client;

public class HttpSubRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7545584308565298410L;

	public HttpSubRequestException() {
		super();
	}

	public HttpSubRequestException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HttpSubRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpSubRequestException(String message) {
		super(message);
	}

	public HttpSubRequestException(Throwable cause) {
		super(cause);
	}

	
}
