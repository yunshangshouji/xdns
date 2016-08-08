package zhuboss.framework.util;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


/**
 * Des 加密解密工具类
 * @author sunwei
 * Add date:20140428
 */
public class DesToolA {
	
	/**
	 * Decrypt
	 * @param message
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String message, String key) throws Exception {
		byte[] bytesrc =convertHexString(message);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte);
	}
	
	/**
	 * Encrypt
	 * @param message
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(String message, String key) throws Exception {
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		return cipher.doFinal(message.getBytes("UTF-8"));
	}
	
	/**
	 * convertHexString
	 * @param ss
	 * @return
	 */
	public static byte[] convertHexString(String ss){
		byte digest[] = new byte[ss.length() / 2];
		for(int i = 0; i < digest.length; i++) {
			String byteString = ss.substring(2 * i, 2 * i + 2);
			int byteValue = Integer.parseInt(byteString, 16);
			digest[i] = (byte)byteValue;
		}
		return digest;
	}
	
	/**
	 * toHexString
	 * @param b
	 * @return
	 */
	public static String toHexString(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2)
				plainText = "0" + plainText;
			hexString.append(plainText);
		}
		return hexString.toString();
	}
	
	/**
	 * DES 加密方法
	 * @param str
	 * @param key
	 * @return
	 */
	public static String enc(String str, String key) {
		String jiami = "";
		try {
			String estr = java.net.URLEncoder.encode(str, "utf-8");
			jiami = toHexString(encrypt(estr, key)); //default lowercase
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jiami;
	}
	
	/**
	 * DES 解密方法
	 * @param str
	 * @param key
	 * @return
	 */
	public static String dec(String str, String key) {
		String jiemi = "";
		try {
			jiemi = java.net.URLDecoder.decode(decrypt(str, key), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jiemi;
	}
	
    public static void main(String[] args) throws Exception {
    	//调用例子
        String key = "C150407L"; //前海KEY=CL1952QH, 摄影网KEY=CL010203 解159753CL 联合C150407L
        //前海接口：渠道代码|姓名|地址|邮编|证件类型|证件号码|证件有效期|邮箱|手机号码|用户ID|一级网点|二级网点|客户经理
        //摄影接口：渠道代码|姓名|地址|邮编|证件类型|证件号码|证件有效期|邮箱|手机号码|登录密码|用户ID|调用方法
        //机构接口：渠道代码|一级网点|二级网点|用户名|公司全称|组织机构代码|联系人姓名|联系人手机|机构证件类型|机构证件号码|机构证件有效期|机构电话|机构邮箱|经办人证件类型|经办人证件号码|经办人证件有效期|法人代表姓名|法人代表证件类型|法人代表证件号码|法人代表证件有效期|组织机构代码证|组织机构代码证有效期|开户银行|开户网点|银行账号|银行户名|地址|邮编|客户经理
        String value = "0011||||01|||||LH900002|||CLJJ";
        //value = "0009|摄影一|地址||||20180226||13100019991|ECAD2FD9A32357EF|9990001|openbag";
        //value = "0010|||QH086|长量公司|CLJG0000086|测试人|13918888886|10|CLJG0000186|20991231||||||||||||||||||CLJJ";
//        String jiami = enc(value, key);
//        System.out.println("加密后的数据为:"+jiami);
//        String jiemi = dec(jiami, key);
//        System.out.println("解密后的数据:"+jiemi);
//        System.out.println("00090003".contains("0003"));
        System.out.println("00090003".indexOf("0009"));
        key = "Mn099099";
        System.out.println(DesToolA.dec("ff95336dd20b8ae12098ae410b4d4c5c",key));
        System.out.println(DesToolA.enc("99992015092800075348",key));
        System.out.println("1F41EDB3E364CA16122DC4031114AA0B41B6EC25A7EF457E".length());
        String[] strs = new String[] {"400001", "初始化交易远程方法出错"};
        System.out.println(strs.length+strs[0]+strs[1]);
    }
	
}
