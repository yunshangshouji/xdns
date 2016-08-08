package zhuboss.framework.rest.provider;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FileDown {
	/**
	 * 文件总大小
	 */
	private int fileSize = -1;
	
	/**
	 * input-size/per-read
	 */
	private int inputBatchSize = 4096;
	
	private InputStream inputStream;
	
	private Map<String,String> addHeaders = new HashMap<String,String>();
	
	public FileDown(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public int getInputBatchSize() {
		return inputBatchSize;
	}

	public void setInputBatchSize(int inputBatchSize) {
		this.inputBatchSize = inputBatchSize;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public Map<String, String> getAddHeaders() {
		return addHeaders;
	}

	
}
