package com.tencent.tools;

import java.io.IOException;

import net.sf.json.JSONObject;

public class WXUtilInfosyss {
	/**
	 * 代理ip
	 */
	public static String proxyIp;
	/**
	 * 代理端口
	 */
	public static Integer proxyPort;
	/**
	 * 是否代理
	 */
	public static Boolean isProxy;
	/**
	 * 小程序appid
	 */
	public static String appId;
	/**
	 * 小程序密钥
	 */
	public static String appSecret;

	/**
	 * 设置公众号appid和密钥
	 * 
	 * @author 闫嘉玮
	 * @param appId
	 * @param appSecret
	 */
	public static void setAppIdAppSecret(String appId, String appSecret) {
		WXUtilInfosyss.appId = appId;
		WXUtilInfosyss.appSecret = appSecret;
	}

	/**
	 * 设置代理
	 * 
	 * @author 闫嘉玮
	 * @param host
	 * @param port
	 * @param isProxy
	 */
	public static void setProxy(String host, Integer port, Boolean isProxy) {
		WXUtilInfosyss.proxyIp = host;
		WXUtilInfosyss.proxyPort = port;
		WXUtilInfosyss.isProxy = isProxy;
	}

	/**
	 * 获取access_token
	 * 
	 * @author 闫嘉玮
	 * @return
	 */
	public static String getAccessToken() {
		String accessToken = null;
		try {
			StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.ACCESS_TOKEN_URL);
			path.append("?grant_type=" + WXConstants.ACCESS_TOKEN_GRANT_TYPE);
			path.append("&appid=" + appId);
			path.append("&secret=" + appSecret);
			String resultArray = null;
			resultArray = RequestUtil.getOrPostUrl(path.toString(), "GET", null,
					RequestProxyConfig.build(proxyIp, proxyPort, isProxy));
			accessToken = JSONObject.fromObject(resultArray).getString("access_token");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessToken;
	}

	/**
	 * 检测敏感词(true有敏感词,false无敏感词)
	 * 
	 * @author 闫嘉玮
	 * @param content
	 * @return
	 */
	public static Boolean checkSecurityMsg(String content) {
		try {
			StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.MSG_SEC_CHECK_URL);
			path.append("?access_token=" + getAccessToken());
			JSONObject data = new JSONObject();
			data.put("content", content);
			String result = RequestUtil.getOrPostUrl(path.toString(), "POST", data.toString(),
					RequestProxyConfig.build(proxyIp, proxyPort, isProxy));
			// errcode==87014表示有敏感词,errcode==0表示无敏感词
			if (JSONObject.fromObject(result).getInt("errcode") == 87014)
				return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
