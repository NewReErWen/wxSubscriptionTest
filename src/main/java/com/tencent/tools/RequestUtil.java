package com.tencent.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class RequestUtil {
	private final static String DEF_CHATSET = "UTF-8";

	/**
	 * 服务器请求及响应
	 * 若请求方法为POST,那么data目前共有两种形式:第一种xx=xx&xx=xx&xx=xx;第二种{"xx":"xx","xx":"xx","xx":"xx"}
	 * json字符串（或json对象）可通过RequestUrl.transToPostData(jsonObject)转换为第一种形式
	 * 
	 * @author 闫嘉玮
	 * @param path
	 *            请求路径地址
	 * @param method
	 *            "GET" / "POST"
	 * @param data
	 *            JSON字符串
	 * @param proxyConfig
	 *            代理配置,可通过RequestProxyConfig.build()进行设置
	 * @return 响应为字符串格式(一部分情况下是JSON字符串)
	 * @throws IOException
	 */
	public static String getOrPostUrl(String path, String method, String data, RequestProxyConfig proxyConfig)
			throws IOException {
		URL url = new URL(path);
		HttpURLConnection conn = null;
		if (proxyConfig != null && proxyConfig.isProxy()) {
			// 创建代理服务器
			InetSocketAddress addr = new InetSocketAddress(proxyConfig.getHost(), proxyConfig.getPort());
			Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
			conn = (HttpURLConnection) url.openConnection(proxy);
		} else
			// 网络请求
			conn = (HttpURLConnection) url.openConnection();

		/** 设置URLConnection的参数和普通的请求属性****start ***/
		conn.setRequestProperty("accept", "*/*");
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		// 如果是json格式字符串，那么需要设置请求头部contentType的值为application/json
		if (isJSONValid(data))
			conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
		/** 设置URLConnection的参数和普通的请求属性****end ***/

		// 设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
		// 最常用的Http请求无非是get和post，get请求可以获取静态页面，也可以把参数放在URL字串后面，传递给servlet，
		// post与get的 不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内。
		conn.setDoOutput(true);
		conn.setDoInput(true);

		method = method.toUpperCase();
		conn.setRequestMethod(method);// GET和POST必须全大写

		/**
		 * 如果只是发送GET方式请求，使用connet方法建立和远程资源之间的实际连接即可；
		 * 如果发送POST方式的请求，需要获取URLConnection实例对应的输出流来发送请求参数。
		 */
		if (method.equals("GET"))
			conn.connect();
		else if (method.equals("POST")) {
			if (data != null) {
				OutputStream out = conn.getOutputStream();// 获取URLConnection对象对应的输出流
				// 此处共有两种请求参数写法，第一种是&连接，第二种是json字符串
				out.write(data.getBytes(DEF_CHATSET));
				out.flush();// 缓冲数据
			}
		}
		// 获取URLConnection对象对应的输入流
		InputStream is = conn.getInputStream();
		// 构造一个字符流缓存
		BufferedReader br = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
		StringBuffer result = new StringBuffer();
		String str = "";
		while ((str = br.readLine()) != null) {
			result.append(str + "\n");
			// 原先使用下行代码时，当中文单个出现时会有乱码(gbk与utf-8转换导致)，现改为构造字符缓冲流时设置编码
			// result.append(new String(str.getBytes(), "UTF-8") + "\n");//
			// 解决中文乱码问题
		}
		// 关闭流
		is.close();
		// 断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断。如果正在被其他线程使用就不切断。
		// 固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常一些。
		conn.disconnect();
		return result.substring(0, result.length() - 1);
	}

	/**
	 * 请求地址返回的不是字符串，而是字节(此处返回值为[byte[], HttpHeaders]) byte[]是文件的字节数组
	 * HttpHeaders集合中包含有响应头部信息，可通过toString进行查看;get获取值
	 * 
	 * @author 闫嘉玮
	 * @param path
	 * @param isPost
	 *            是否是POST请求
	 * @param data
	 * @param proxyConfig
	 *            代理配置,可通过RequestProxyConfig.build()进行设置
	 * @return
	 */
	public static Object[] getOrPostFileBytes(String path, Boolean isPost, Map<String, Object> data,
			RequestProxyConfig proxyConfig) {
		byte[] result = null;
		RestTemplate rest = new RestTemplate();
		if (proxyConfig != null && proxyConfig.isProxy()) {
			SimpleClientHttpRequestFactory reqfac = new SimpleClientHttpRequestFactory();
			reqfac.setProxy(new Proxy(Type.HTTP, new InetSocketAddress(proxyConfig.getHost(), proxyConfig.getPort())));
			rest.setRequestFactory(reqfac);
		}
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(data, headers);
		ResponseEntity<byte[]> entity = rest.exchange(path, isPost ? HttpMethod.POST : HttpMethod.GET, requestEntity,
				byte[].class, new Object[0]);
		result = entity.getBody();
		headers = entity.getHeaders();
		return new Object[] { result, headers };
	}

	/**
	 * json转换为post请求的参数字符串(参数值进行了encode编码)
	 * 
	 * @author 闫嘉玮
	 * @param data
	 * @return 参数字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String transToPostData(JSONObject jsonData) throws UnsupportedEncodingException {
		StringBuffer dataBuffer = new StringBuffer("");
		for (Object obj : jsonData.keySet()) {
			dataBuffer.append(URLEncoder.encode(obj.toString(), "UTF-8") + "="
					+ URLEncoder.encode(jsonData.getString(obj.toString()), "UTF-8") + "&");
		}
		dataBuffer.setLength(dataBuffer.length() - 1);
		return dataBuffer.toString();
	}

	/**
	 * json转换为post请求的参数字符串(参数值进行了encode编码)
	 * 
	 * @author 闫嘉玮
	 * @param data
	 * @return 参数字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String transToPostData(String jsonString) throws UnsupportedEncodingException {
		return transToPostData(JSONObject.fromObject(jsonString));
	}
	
	/**
	 * map转换为post请求的参数字符串(参数值进行了encode编码)
	 * 
	 * @param mapData
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String transToPostData(Map mapData) throws UnsupportedEncodingException {
		StringBuffer dataBuffer = new StringBuffer("");
		for (Object obj : mapData.keySet()) {
			dataBuffer.append(URLEncoder.encode(obj.toString(), "UTF-8") + "="
					+ URLEncoder.encode((mapData.get(obj.toString())).toString(), "UTF-8") + "&");
		}
		dataBuffer.setLength(dataBuffer.length() - 1);
		return dataBuffer.toString();
	}

	/**
	 * 将形如key=value&key=value的字符串转换为相应的Map对象
	 * 
	 * @param result
	 * @return
	 */
	public static Map<String, String> convertResultStringToMap(String result) {
		Map<String, String> map = null;

		if (result != null && !"".equals(result.trim())) {
			if (result.startsWith("{") && result.endsWith("}")) {
				result = result.substring(1, result.length() - 1);
			}
			map = parseQString(result);
		}

		return map;
	}
	

	/**
	 * 解析应答字符串，生成应答要素
	 * 
	 * @param str
	 *            需要解析的字符串
	 * @return 解析的结果map
	 * @throws UnsupportedEncodingException
	 */
	private static Map<String, String> parseQString(String str) {

		Map<String, String> map = new HashMap<String, String>();
		int len = str.length();
		StringBuilder temp = new StringBuilder();
		char curChar;
		String key = null;
		boolean isKey = true;
		boolean isOpen = false;// 值里有嵌套
		char openName = 0;
		if (len > 0) {
			for (int i = 0; i < len; i++) {// 遍历整个带解析的字符串
				curChar = str.charAt(i);// 取当前字符
				if (isKey) {// 如果当前生成的是key

					if (curChar == '=') {// 如果读取到=分隔符
						key = temp.toString();
						temp.setLength(0);
						isKey = false;
					} else {
						temp.append(curChar);
					}
				} else {// 如果当前生成的是value
					if (isOpen) {
						if (curChar == openName) {
							isOpen = false;
						}

					} else {// 如果没开启嵌套
						if (curChar == '{') {// 如果碰到，就开启嵌套
							isOpen = true;
							openName = '}';
						}
						if (curChar == '[') {
							isOpen = true;
							openName = ']';
						}
					}

					if (curChar == '&' && !isOpen) {// 如果读取到&分割符,同时这个分割符不是值域，这时将map里添加
						putKeyValueToMap(temp, isKey, key, map);
						temp.setLength(0);
						isKey = true;
					} else {
						temp.append(curChar);
					}
				}

			}
			putKeyValueToMap(temp, isKey, key, map);
		}
		return map;
	}

	private static void putKeyValueToMap(StringBuilder temp, boolean isKey, String key, Map<String, String> map) {
		if (isKey) {
			key = temp.toString();
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, "");
		} else {
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, temp.toString());
		}
	}

	/**
	 * 判断字符串是否是json格式字符串
	 * 
	 * @author 闫嘉玮
	 * @param test
	 * @return true是json格式字符串
	 */
	private static Boolean isJSONValid(String test) {
		try {
			JSONObject.fromObject(test);
		} catch (JSONException e1) {
			try {
				JSONArray.fromObject(test);
			} catch (JSONException e2) {
				return false;
			}
		}
		return true;
	}
}
