package com.tencent.tools;

public class WXConstants {
	/**
	 * 微信服务器地址域名
	 */
	public static final String DOMAIN_API = "https://api.weixin.qq.com";
	/**
	 * 获取access_token
	 */
	public static final String ACCESS_TOKEN_URL = "/cgi-bin/token";
	/**
	 * 检测敏感词url
	 */
	public final static String MSG_SEC_CHECK_URL = "/wxa/msg_sec_check";
	/**
	 * access_token发送请求所需常量grant_type
	 */
	public final static String ACCESS_TOKEN_GRANT_TYPE = "client_credential";
}
