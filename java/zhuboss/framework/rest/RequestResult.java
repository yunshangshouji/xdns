package zhuboss.framework.rest;

public class RequestResult<T> {
	
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	public static final String WARN = "warn";
	
	private String result = SUCCESS;
	
	private String message;
	
	private String messageId;
	
	private T data;
	
	public RequestResult(){
		
	}
	public RequestResult(String result, String message){
		this.result = result;
		this.message = message;
	}
	public RequestResult(String result, String message, T data){
		this.result = result;
		this.message = message;
		this.data = data;
	}
	
	public RequestResult(String result){
		this.result = result;
	}

	public String getMessage() {
		return message;
	}
	public RequestResult<T> setMessage(String message) {
		this.message = message;
		return this;
	}
	public String getResult() {
		return result;
	}
	public RequestResult<T> setResult(String result) {
		this.result = result;
		return this;
	}
	public String getMessageId() {
		return messageId;
	}
	public RequestResult<T> setMessageId(String messageId) {
		this.messageId = messageId;
		return this;
	}
	public T getData() {
		return data;
	}
	public RequestResult<T> setData(T data) {
		this.data = data;
		return this;
	}
}
