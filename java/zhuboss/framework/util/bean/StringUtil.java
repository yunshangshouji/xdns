package zhuboss.framework.util.bean;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

/**
 * 字符串工具集合
 * 
 */
@SuppressWarnings({"rawtypes","unused"})
public class StringUtil extends org.apache.commons.lang.StringUtils {
	/** 7位ASCII字符，也叫作ISO646-US、Unicode字符集的基本拉丁块 */
	public static final String US_ASCII = "US-ASCII";

	/** ISO 拉丁字母表 No.1，也叫作 ISO-LATIN-1 */
	public static final String ISO_8859_1 = "ISO-8859-1";

	/** 8 位 UCS 转换格式 */
	public static final String UTF_8 = "UTF-8";

	/** 16 位 UCS 转换格式，Big Endian（最低地址存放高位字节）字节顺序 */
	public static final String UTF_16BE = "UTF-16BE";

	/** 16 位 UCS 转换格式，Little-endian（最高地址存放低位字节）字节顺序 */
	public static final String UTF_16LE = "UTF-16LE";

	/** 16 位 UCS 转换格式，字节顺序由可选的字节顺序标记来标识 */
	public static final String UTF_16 = "UTF-16";

	/** 中文超大字符集 */
	public static final String GBK = "GBK";

	/** 中文超大字符集 */
	public static final String GB2312 = "GB2312";
	
	/**
	 * 判断字符串的编码
	 * 
	 * @param str
	 * @return
	 */
	public static String getEncoding(String str) {
		String[] encodeStr={UTF_8,ISO_8859_1,GBK,GB2312,US_ASCII,UTF_16BE,UTF_16LE,UTF_16};
		String encode="";
		for (String string : encodeStr) {
			encode=checkEncoding(str, string);
			if(isNotBlank(encode)){
				return encode;
			}
		}
		return "";
	}  
	public static String checkEncoding(String str,String encode){
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception exception) {
		}
		return "";
	}
	/**
	 * 字母Z使用了两个标签，这里有２７个值
	 * 
	 * i, u, v都不做声母, 跟随前面的字母
	 */
	private char[] chartable = { '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒', '塌', '塌', '塌', '挖',
			'昔', '压', '匝', '座' };

	private char[] alphatable = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'X', 'Y', 'Z' };

	/**
	 * 切割字符长度
	 * @param str 源字符串
	 * @param len 需要的长度
	 * @param gb 中文字占2位 
	 * @return List<String>
	 */
	public static List<String> subGbstring(String str,int len,boolean gb){
		List<String> list = new ArrayList<String>();;
		if(gb){
			int nowlen=0;
			int start=0;
			StringUtil obj = new StringUtil();
			for (int i = 0; i < str.length(); i++) {
				int strgb = obj.gbValue(str.charAt(i));
				if (strgb < obj.table[0]){//非中文简体
					nowlen++;
				}else{
					nowlen+=2;
				}
				if(nowlen==len){
					list.add(str.substring(start, i+1));
					start=i+1;
					nowlen=0;
				}else if(nowlen>len){
					list.add(str.substring(start, i));
					start=i;
					nowlen=0;
				}else if(i+1==str.length()){
					list.add(str.substring(start, i+1));
					start=i;
					nowlen=0;
				}
			}
		}else{
			int end=len;
			for (int start = 0; start < str.length();) {
				if(end+1>str.length()){
					end=str.length();
				}
				list.add(str.substring(start, end));
				start=end+1;
				end+=start;
			}
		}
		return list;
	}
	
	public int[] table = new int[27];
	{// 初始化
		for (int i = 0; i < 27; ++i) {
			table[i] = gbValue(chartable[i]);
		}
	}

	/**
	 * 主函数,输入字符得到他的声母,
	 * 
	 * 英文字母返回对应的大写字母
	 * 
	 * 其他非简体汉字返回 '0'
	 * 
	 * @param ch
	 * @return
	 */
	public char Char2Alpha(char ch) {
		if (ch >= 'a' && ch <= 'z')
			return (char) (ch - 'a' + 'A');
		if (ch >= 'A' && ch <= 'Z')
			return ch;
		int gb = gbValue(ch);
		if (gb < table[0]){
			return '0';
		}
		int i;
		for (i = 0; i < 26; ++i) {
			if (match(i, gb))
				break;
		}
		if (i >= 26)
			return '0';
		else
			return alphatable[i];
	}

	/**
	 * 根据一个包含汉字的字符串返回一个汉字拼音首字母的字符串
	 * 
	 * @param SourceStr
	 * @return
	 */
	public String String2Alpha(String SourceStr) {
		String Result = "";
		int StrLength = SourceStr.length();
		int i;
		try {
			for (i = 0; i < StrLength; i++) {
				Result += Char2Alpha(SourceStr.charAt(i));
			}
		} catch (Exception e) {
			Result = "";
		}
		return Result;
	}

	private boolean match(int i, int gb) {
		if (gb < table[i])
			return false;
		int j = i + 1;
		// 字母Z使用了两个标签
		while (j < 26 && (table[j] == table[i]))
			++j;
		if (j == 26)
			return gb <= table[j];
		else
			return gb < table[j];
	}

	// 取出汉字的编码
	private int gbValue(char ch) {
		String str = new String();
		str += ch;
		try {
			byte[] bytes = str.getBytes("GB2312");
			if (bytes.length < 2) {
				return 0;
			}
			return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * Md5加密
	 * 
	 * @param s
	 * @return
	 */
	public static String getMd5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 得到一个n位的随机数 第一位不能为0
	 * 
	 * @param n
	 *            位数
	 * @return
	 */
	public static String getRand(int n) {
		Random rnd = new Random();
		String pass = "0";
		int x = rnd.nextInt(9);
		/** 过滤第一位为0 */
		while (x == 0) {
			x = rnd.nextInt(9);
		}
		pass = String.valueOf(x);
		for (int i = 1; i < n; i++) {
			pass = pass + String.valueOf(rnd.nextInt(9));
		}
		return pass;
	}

	/**
	 * java按要求长度截取字段
	 * 
	 * @param str
	 *            字符
	 * @param num
	 *            长度
	 * @return
	 */
	public static String getStrLen(String str, int num) {
		int forNum = 0;
		int alli = 0;//
		int strLen = 0;// 要循环的长度
		if (num <= 0) {
			return str;
		}
		if (null == str) {
			return null;
		}
		if (str.length() >= num) {
			strLen = num;

		} else {
			strLen = str.length();
		}
		for (int i = 0; i < strLen; i++) {
			if (num == Math.floor(forNum / 2f))
				break;
			if (str.substring(i, i + 1).getBytes().length > 1) {
				// 如果是字符
				alli = alli + 1;
			}
			alli = alli + 1;
			if (alli >= num) {
				return str.substring(0, i);
			}
		}
		return str.substring(0, strLen);
	}

	/**
	 * 判断字符是否超过长度
	 * 
	 * @param str
	 * @param num
	 * @return 超过规定字符返回true
	 */
	public static boolean isLen(String str, int num) {
		int forNum = 0;
		int alli = 0;//
		int strLen = 0;// 要循环的长度
		if (str.length() >= num) {
			strLen = num;
			return true;// 超过规定字符返回true
		} else {
			strLen = str.length();
		}
		for (int i = 0; i < strLen; i++) {
			if (num == Math.floor(forNum / 2f))
				break;
			if (str.substring(i, i + 1).getBytes().length > 1) {
				// 如果是字符
				alli = alli + 1;
			}
			alli = alli + 1;
		}
		if (alli > num) {
			return true;// 超过规定字符返回true
		}
		return false;// 不超过规定字符返回False
	}
	/**
	 * 填充左边字符
	 * 
	 * @param source
	 *            源字符串
	 * @param fillChar
	 *            填充字符
	 * @param len
	 *            填充到的长度
	 * @return 填充后的字符串
	 */
	public static String fillLeft(String source, char fillChar, int len) {
		StringBuffer ret = new StringBuffer();
		if (null == source)
			ret.append("");
		if (source.length() > len) {
			ret.append(source);
		} else {
			int slen = source.length();
			while (ret.toString().length() + slen < len) {
				ret.append(fillChar);
			}
			ret.append(source);
		}
		return ret.toString();
	}
	/**
	 * 填充右边字符
	 * 
	 * @param source
	 *            源字符串
	 * @param fillChar
	 *            填充字符
	 * @param len
	 *            填充到的长度
	 * @return 填充后的字符串
	 */
	public static String filRight(String source, char fillChar, int len) {
		StringBuffer ret = new StringBuffer();
		if (null == source)
			ret.append("");
		if (source.length() > len) {
			ret.append(source);
		} else {
			ret.append(source);
			while (ret.toString().length() < len) {
				ret.append(fillChar);
			}
		}
		return ret.toString();
	}
	public static String filterStr(String str) {
		if (null == str || "".equals(str)) {
			return str;
		}
		str = str.replaceAll("'", "''");
		return str;
	}

	/**
	 * 检测字符是否是数字
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isDigit(char c) {
		String nums = "0123456789.";
		if (nums.indexOf(String.valueOf(c)) == -1) {
			return false;
		}
		return true;
	}

	public static String substring(String str, int num) {
		byte[] substr = new byte[num];
		System.arraycopy(str.getBytes(), 0, substr, 0, num);
		str = new String(substr);
		return str;
	}

	public static String checkStr(String inputStr) {
		String error = "";
		if (null != inputStr && !"".equals(inputStr.trim())) {
			char c;
			for (int i = 0; i < inputStr.length(); i++) {
				c = inputStr.charAt(i);
				if (c == '"') {
					error += " 特殊字符[\"]";
				}
				if (c == '\'') {
					error += " 特殊字符[']";
				}
				if (c == '<') {
					error += " 特殊字符[<]";
				}
				if (c == '>') {
					error += " 特殊字符[>]";
				}
				if (c == '&') {
					error += " 特殊字符[&]";
				}
				if (c == '%') {
					error += " 特殊字符[%]";
				}
			}
		}
		return error;
	}
	/**
	 * 检测字符是否为空,为空的时候返回提示
	 * @param str
	 * @param msg 为空的时候返回提示
	 * @return
	 */
	public static String isBlankToMsg(String str,String msg){
		String returnstr="";
		if (StringUtils.isBlank(str)) {
			returnstr=msg+",";
		}
		return returnstr;
	}
	
	public static String getFileName(String filepath) {
		if (StringUtils.isNotBlank(filepath)) {
			return filepath.substring(filepath.lastIndexOf("\\") + 1, filepath.length());
		}
		return "";
	}

	public static String changeCharset(String str, String oldCharset, String newCharset) {
		if (str != null) {
			// 用默认字符编码解码字符串。
			byte[] bs = null;
			try {
				if (StringUtil.isNotBlank(oldCharset)) {
					bs = str.getBytes(oldCharset);
				} else {
					bs = str.getBytes();
				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			// 用新的字符编码生成字符串
			try {
				String newstr = new String(bs, newCharset);
				return newstr;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	/**
	 * 拆分规格1.00*1000*1000拆成1.00 1000 1000
	 * @param str
	 * 			规格
	 * @return	拆分后的数组,里面是数字,不能包括非数字
	 */
	public static StringBuilder[] getSplit(String str,String split){
		String[] ggStr=null;
		//取出数组
		if(null==str){
			return null;
		}
		ggStr=str.split(split);
		StringBuilder[] b = new StringBuilder[ggStr.length]; 
		//处理每个数组里的非数字字符
		for (int i=0;i < ggStr.length;i++){
			StringBuilder sb=new StringBuilder();
			char[] c = ggStr[i].toCharArray();
			int data=0;
			for(int j=0;j<c.length;j++){				
				if(StringUtil.isDigit(c[j])){
					data++;
					sb.append(c[j]);
				}else if(data>0){
					break; 
				}
			}
			if(null==sb||"".equals(sb)||sb.length()==0){
				sb.append('0');
			}
			b[i]=sb;
		}
		return b;
	}
	/**
	 * 处理对象里的规格信息,把规格拆成小规格
	 * @param obj 数据对象,里面要有规格属性(goodsSpec)和对应的小规格属性(goodsSpec1,goodsSpec2,goodsSpec3,goodsSpec4,goodsSpec5)
	 */
	public static void setGoodsSpec(Object obj){
		if(null!=obj){
			setGoodsSpec(obj,"goodsSpec");
		}
	}
	/**
	 * 处理对象里的规格信息,把规格拆成小规格
	 * @param obj 数据对象,里面要有规格属性(goodsSpec)和对应的小规格属性(goodsSpec1,goodsSpec2,goodsSpec3,goodsSpec4,goodsSpec5)
	 * @param specName 规格属性名
	 */
	public static void setGoodsSpec(Object obj,String specName){
		if(null!=obj){
			Object specObj=BeanUtils.forceGetProperty(obj, specName);
			if(null!=specObj){
				StringBuilder[] specs = StringUtil.getSplit(specObj.toString(), "\\*");
				for (int i = 0; i < 5; i++) {
					int j=i+1;
					Double value=0d;
					if(specs.length>=j){
						value=Double.valueOf(specs[i].toString());
					}
					try {
						BeanUtils.forceSetProperty(obj, specName+j,value );
					} catch (Exception e) {
					}
				}
			}
		}
	}
	public static String makeSign(String value){
		String str="";
		if(null==value){
			return str;
		}
		str=value.trim();//去掉前后空格
		str=str.replaceAll(">", "&gt;");
		str=str.replaceAll("<", "&lt;");
		str=str.replaceAll("&", "&amp;");
		str=str.replaceAll("\"", "&quot;");
		return str;
	}
	/**
	 * 截取超长的信息，多余用...
	 * @param str 备注
	 * @param len 长
	 * @return 截取后的信息
	 */
	public static String intercept(String str,int len){
		String newstr = "";
		if(null==str){
			return newstr;
		}
		if(str.length()>len){
			newstr = str.substring(0, len)+"...";
		}else{
			newstr = str;
		}
		return newstr;
	} 
	/**
	 * 转义页面输入的特殊符号
	 * @param str
	 * @return
	 */
	public static String replaceHtml(String str){
		if(null==str){
			return "";
		}
		str = StringUtils.replace(str, "&", "&amp;");
		str = StringUtils.replace(str, "'", "&apos;");
		str = StringUtils.replace(str, "\"", "&quot;");
		str = StringUtils.replace(str, "\n", "<br>");
		str = StringUtils.replace(str, "\t", "&nbsp;&nbsp;");// 替换跳格
		str = StringUtils.replace(str, " ", "&nbsp;");// 替换空格
		return str;
	}
	/**
	 * 反向转义页面输入的特殊符号
	 * @param str
	 * @return
	 */
	public static String reReplaceHtml(String str){
		if(null==str){
			return "";
		}
		str = StringUtils.replace(str, "&amp;","&");
		str = StringUtils.replace(str,"&apos;","'" );
		str = StringUtils.replace(str, "&quot;", "\"");
		str = StringUtils.replace(str, "<br>", "\n");
		str = StringUtils.replace(str, "&nbsp;&nbsp;", "\t");// 替换跳格
		str = StringUtils.replace(str, "&nbsp;", " ");// 替换空格
		return str;
	}
	/**
	 * 所有参数为空的时候返回true
	 * @param args
	 * @return true false
	 */
	public static Boolean isBlankAll(Object... args){
		Boolean flag=true;
		for (int i = 0; i < args.length; i++) {
			if(args[i] instanceof String){
				if(!isBlank((String) args[i])){
					flag=false;
				}
			}else{
				if(null!=args[i]){
					flag=false;
				}	
			}
		}
		return flag;
	}
	/**
	 * 只要有一个参数为空就返回true
	 * @param args
	 * @return true false
	 */
	public static Boolean isBlankOne(Object... args){
		Boolean flag=false;
		for (int i = 0; i < args.length; i++) {
			if(args[i] instanceof String){
				if(isBlank((String) args[i])){
					flag=true;
				}
			}else{
				if(null==args[i]){
					flag=true;
				}
			}
		}
		return flag;
	}
	
	/**
	 * 把字符串第一个字母转成大写
	 * @param str
	 * @return
	 */
	public static String getFirstUpper(String str){
		String newStr="";
		if(str.length()>0){
			newStr=str.substring(0, 1).toUpperCase()+str.substring(1, str.length());
		}
		return newStr;
	}
	/**
	 * 获取一个字符在一个字符串里出现的次数
	 * @param tagetStr
	 * @param str
	 * @return 
	 */
	public static int indexOfAll(String tagetStr,String str){
		int i=0;
		if(null!=tagetStr){
			i=tagetStr.length()-tagetStr.replace(str, "").length();
		}
		return i;
	}
	/**
	 * 转null字符串为""
	 * @param str
	 * @return
	 */
	public static String getNullTo(String str){
		if(isBlank(str)){
			str="";
		}
		return str;
	}
	/**
	 * 比较两个Long是否相等
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(Long a,Long b){
		boolean flag=false;
		if(null==a){
			a=0L;
		}
		if(null==b){
			b=0L;
		}
		if(a.equals(b)){
			flag=true;
		}
		return flag;
	}
	/**
	 * 比较两个对象是否相等
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(Object a,Object b){
		boolean flag=false;
		if(null==a){
			a="";
		}
		a=String.valueOf(a);
		if(null==b){
			b="";
		}
		b=String.valueOf(b);
		if(a.equals(b)){
			flag=true;
		}
		return flag;
	}
	/**
	 * 比较两个字符串是否相等
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(String a,String b){
		boolean flag=false;
		if(null==a){
			a="";
		}
		if(null==b){
			b="";
		}
		if(a.equals(b)){
			flag=true;
		}
		return flag;
	}
	
	/**
	 * 通过字符组成类型数组
	 * @param str
	 * @return
	 */
	public static Class[] getClass(String str,String value,List<Object> valueList){
		if(isNotBlank(str)){
			String[] s=str.split(",");
			if(null!=s){
				List<Class> list = new ArrayList<Class>();
				String[] valueS=null;
				if(isNotBlank(value)){
					valueS=value.split(";");
				}
				Class[] c=new Class[s.length];
				for (int i = 0; i < s.length; i++) {
					String str1 = s[i];
					String v=null;
					if(null!=valueS&&valueS.length>i){
						v=valueS[i];
					}
					if(StringUtil.isNotBlank(str1)){
						if(str1.equals("String")){
							c[i]=String.class;
							list.add(String.class);
							if(null!=v){
								valueList.add(v);
							}
						}else if(str1.equals("int")){
							c[i]=int.class;
							list.add(int.class);
							if(null!=v){
								valueList.add(Long.valueOf(v).intValue());
							}
						}else if(str1.equals("Long")){
							c[i]=Long.class;
							list.add(Long.class);
							if(null!=v){
								valueList.add(Long.valueOf(v));
							}
						}else if(str1.equals("Double")){
							c[i]=Double.class;
							list.add(Double.class);
							if(null!=v){
								valueList.add(Double.valueOf(v));
							}
						}else if(str1.equals("double")){
							c[i]=double.class;
							list.add(double.class);
							if(null!=v){
								valueList.add(Double.valueOf(v));
							}
						}else if(str1.equals("Date")){
							c[i]=Date.class;
							list.add(Date.class);
							if(null!=v){
								valueList.add(DateUtil.getDateToString(v, DateUtil.DATESHOWFORMAT));
							}
						}else if(str1.equals("Integer")){
							c[i]=Integer.class;
							list.add(Integer.class);
							if(null!=v){
								valueList.add(Integer.valueOf(v));
							}
						}else if(str1.equals("boolean")||str1.equals("Boolean")){
							c[i]=boolean.class;
							list.add(boolean.class);
							if(null!=v){
								if("true".equals(v)){
									valueList.add(true);
								}else{
									valueList.add(false);
								}
							}
						}else if(str1.equals("Object")){
							c[i]=Object.class;
							list.add(Object.class);
							if(null!=v){
								valueList.add(v);
							}
						}else{//javabean对象
							Class clazz=null;
							try {
								clazz = Class.forName(str1);
								c[i]=clazz;
								list.add(clazz);
								if(null!=v){//是json格式的
									Object o=clazz.newInstance();
									Map<String,String> map=null;//JsonUtil.getJsonToMap(v);
									if(null!=map&&!map.isEmpty()){
										for (String key : map.keySet()) {
											BeanUtils.forceSetProperty(o, key, map.get(key));
										}
									}
									valueList.add(o);
								}
							} catch (Exception e) {
							}
						}
					}
				}
				return c;
			}
		}
		return null;
	}
	public static void main(String[] args) {
		String s="中文";
		System.out.println(getEncoding(s));
	}
	
	public static String getFirstLetter(String name) {
		
		StringUtil obj = new StringUtil();
		return obj.String2Alpha(name);
	}
	
	/**
	 * 如果字符超出长度则将后面的内容替换
	 * @param str 原字符串
	 * @param length 超过多少长度
	 * @param splitString 添加到截取的字符串之后
	 * @return
	 */
	public static String ifStringOverLengthSplit(String str,Integer length,String splitString){
		if(str.length() > length){
			str = str.substring(0,length-1)+splitString;
		}
		return str;
	}
}

