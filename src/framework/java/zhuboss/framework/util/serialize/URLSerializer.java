package zhuboss.framework.util.serialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/** 
 * @author 作者 zhuzhengquan: 
 * @version 创建时间：2015年12月29日 上午10:30:32 
 * 类说明 
 */
public class URLSerializer {
	
	public static Map<String,String[]> deserialize(String query){
		List<NameValuePair> nvpList = new ArrayList<>();
		URLEncodedUtils.parse(nvpList, new Scanner(query), "UTF-8");
		Map<String,String[]> nvpMap = new HashMap<String,String[]>();
		for(NameValuePair nameValuePair : nvpList){
			if(nvpMap.containsKey(nameValuePair.getName())){
				List<String> list = new ArrayList<String>();
				list.addAll(Arrays.asList(nvpMap.get(nameValuePair.getName())));
				list.add(nameValuePair.getValue());
				String[] x = null;
				nvpMap.put(nameValuePair.getName(),list.toArray(x));
			}else{
				nvpMap.put(nameValuePair.getName(), new String[]{nameValuePair.getValue()});
			}
			
		}
		return nvpMap;
	}
	
	public static void main(String[] args){
		String txt = "access_token=2B30330A6E52192DEE347F1C9A15DE1F&expires_in=7776000&refresh_token=DFEE536B03DB068C996391DE9DE2981D";
		Map<String,String[]>  map = URLSerializer.deserialize(txt);
		System.out.println(JsonUtil.serializeToJson(map));
	}
}
