package zhuboss.framework.server.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthCheckException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 705976017447791348L;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public HealthCheckException() {
		super();
	}

	public HealthCheckException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		logger.error(message,cause);
	}

	public HealthCheckException(String message, Throwable cause) {
		super(message, cause);
		logger.error(message,cause);
	}

	public HealthCheckException(String message) {
		super(message);
		logger.error(message);
	}

	public HealthCheckException(Throwable cause) {
		super(cause);
		logger.error(cause.getMessage(),cause);
	}

}
