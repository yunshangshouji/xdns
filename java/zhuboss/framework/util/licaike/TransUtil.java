package zhuboss.framework.util.licaike;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import zhuboss.framework.util.CodeUtils;

public class TransUtil {
	private static final java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	/**
	 * 理财客TRANS接口的身份验证
	 * @param bnvs
	 * @return
	 */
	public static List<BasicNameValuePair> appendVerifyCode(String transVerifyKey,List<BasicNameValuePair> bnvs ){
		StringBuffer qryStr =  new StringBuffer();
		for(BasicNameValuePair bnv : bnvs){
			if(qryStr.length()>0){
				qryStr.append(";");
			}
			qryStr.append(bnv.getName()+":"+bnv.getValue());
		}
		try {
			StringBuffer raw= new StringBuffer().append( transVerifyKey +sdf.format(new Date())+CodeUtils.urlDecode(qryStr.toString(),CodeUtils.UTF8_CHARSET));
			System.out.println("加密明文：" + raw.toString());
			String verifyCode;
			verifyCode = CodeUtils.md5Digest(raw.toString());
			List<BasicNameValuePair> returnList = new ArrayList<BasicNameValuePair>(bnvs);
			returnList.add(new BasicNameValuePair("verifyCode", verifyCode));
			return returnList;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
