package com.tencent.tools.wx;

public class WXConstants {
	/**
	 * 微信服务器地址域名
	 */
	public static final String DOMAIN_API = "https://api.weixin.qq.com";
	/**
	 * 获取二维码的地址域名
	 */
	public static final String DOMAIN_MP_API = "https://mp.weixin.qq.com";
	/**
	 * 获取access_token
	 */
	public static final String ACCESS_TOKEN_URL = "/cgi-bin/token";
	/**
	 * 检测敏感词url
	 */
	public final static String MSG_SEC_CHECK_URL = "/wxa/msg_sec_check";
	/**
	 * 获取jsapi_ticket
	 */
	public static final String JSAPI_TICKET_URL = "/cgi-bin/ticket/getticket";
	/**
	 * 临时二维码获取ticket值
	 */
	public static final String CREATE_QR_SCENE_URL = "/cgi-bin/qrcode/create";
	/**
	 * 使用ticket换取二维码
	 */
	public static final String SHOW_QRCODE_URL = "/cgi-bin/showqrcode";
	/**
	 * 查询自定义菜单
	 */
	public static final String CURRENT_SELFMENU_URL = "/cgi-bin/get_current_selfmenu_info";
	/**
	 * 创建自定义菜单
	 */
	public static final String CREATE_MENU_URL = "/cgi-bin/menu/create";
	/**
	 * 客服接口-发消息
	 */
	public static final String SEND_MESSAGE_BY_CUSTOM_URL = "/cgi-bin/message/custom/send";
	/**
	 * 模板消息发送
	 */
	public static final String SEND_MESSAGE_BY_TEMPLATE_URL = "/cgi-bin/message/template/send";
	/**
	 * 获取临时素材
	 */
	public static final String GET_MEDIA_URL = "/cgi-bin/media/get";
	/**
	 * 获取永久素材列表
	 */
	public static final String BATCHGET_MATERIAL_URL = "/cgi-bin/material/batchget_material";
	/**
	 * access_token发送请求所需常量grant_type
	 */
	public final static String ACCESS_TOKEN_GRANT_TYPE = "client_credential";
	/**
	 * jsapi_ticket发送请求所需常量type
	 */
	public final static String JSAPI_TICKET_TYPE = "jsapi";
}
