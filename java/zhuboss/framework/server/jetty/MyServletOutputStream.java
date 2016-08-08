package zhuboss.framework.server.jetty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.WriteListener;

public class MyServletOutputStream  extends javax.servlet.ServletOutputStream{
     private ByteArrayOutputStream output;  
     int length =0 ;
     @Override
     public void write(int b) throws IOException {
          output.write(b);
          length++;
     }

     
     /**
      * 覆盖以提高性能(避免调用write(int b))
      */
     @Override
	public void write(byte[] b, int off, int len) throws IOException {
    	 output.write(b, off, len);
    	 length+=len;
	}


     /**
      * 覆盖以提高性能(避免调用write(int b))
      */
	@Override
	public void write(byte[] b) throws IOException {
		output.write(b);
		length+= b.length;
	}



	public MyServletOutputStream() {
          output = new ByteArrayOutputStream();      
     }

     @Override
     public void flush() throws IOException {
          output.flush();
     }

     @Override
     public void close() throws IOException {
          output.close();
     }

     public byte[] getBytes(){
    	 return output.toByteArray();
     }
     public String getContent() throws UnsupportedEncodingException{
          return output.toString("UTF-8");
     }


	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public void setWriteListener(WriteListener arg0) {
		// TODO Auto-generated method stub
		
	}


	public int getLength() {
		return length;
	}
} 