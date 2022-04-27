package com.tencent.tools.wx;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.tencent.tools.RequestProxyConfig;
import com.tencent.tools.RequestUtil;

import net.sf.json.JSONObject;

public class WXSubscriptionRequestUtil {
	// TODO 临时存在的内容(由于这些变量在某时间范围内保持有效，所以需要建立缓存，此处用单例代替)
	public static String accessToken;
	private static String jsapiTicket;

	/**
	 * 获取access_token
	 * 
	 * @author 闫嘉玮
	 * @return
	 */
	public static String getAccessToken() {
		if (accessToken != null && !accessToken.equals(""))
			return accessToken;
		try {
			StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.ACCESS_TOKEN_URL);
			path.append("?grant_type=" + WXConstants.ACCESS_TOKEN_GRANT_TYPE);
			path.append("&appid=" + WXConfig.APPID);
			path.append("&secret=" + WXConfig.SECRET);
			String resultArray = null;
			Boolean isProxy = new Boolean(false);
			resultArray = RequestUtil.getOrPostUrl(path.toString(), "GET", null,
					RequestProxyConfig.build(null, null, isProxy));
			accessToken = JSONObject.fromObject(resultArray).getString("access_token");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessToken;
	}

	/**
	 * 获取jsapi_ticket(微信网页开发使用)
	 * 
	 * @author 闫嘉玮
	 * @return
	 */
	public static String getJsapiTicket() {
		if (jsapiTicket != null && !jsapiTicket.equals(""))
			return jsapiTicket;
		try {
			StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.JSAPI_TICKET_URL);
			path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
			path.append("&type=" + WXConstants.JSAPI_TICKET_TYPE);
			Boolean isProxy = new Boolean(false);
			String result = RequestUtil.getOrPostUrl(path.toString(), "GET", null,
					RequestProxyConfig.build(null, null, isProxy));
			jsapiTicket = JSONObject.fromObject(result).getString("ticket");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsapiTicket;
	}

	/**
	 * 获取临时二维码所需ticket
	 * 
	 * @author 闫嘉玮
	 * @param param
	 * @return
	 */
	// TODO 需验证
	public static String getORSceneTicket(JSONObject param) {
		String ticket = null;
		try {
			StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.CREATE_QR_SCENE_URL);
			path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
			Boolean isProxy = new Boolean(false);
			String result = RequestUtil.getOrPostUrl(path.toString(), "POST", param.toString(),
					RequestProxyConfig.build(null, null, isProxy));
			ticket = JSONObject.fromObject(result).getString("ticket");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ticket;
	}

	/**
	 * 获取二维码的字节数组
	 * 
	 * @author 闫嘉玮
	 * @param param
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	// TODO 需验证
	public static byte[] showQRScene(JSONObject param) throws UnsupportedEncodingException {
		StringBuffer path = new StringBuffer(WXConstants.DOMAIN_MP_API + WXConstants.SHOW_QRCODE_URL);
		path.append("?ticket=" + URLEncoder.encode(WXSubscriptionRequestUtil.getORSceneTicket(param), "UTF-8"));
		System.out.println(path);
		Map<String, Object> data = new HashMap<String, Object>();
		Object[] result = RequestUtil.getOrPostFileBytes(path.toString(), false, data,
				RequestProxyConfig.build(null, null, false));
		return (byte[]) result[0];
	}

	/**
	 * 查询自定义菜单
	 * 
	 * @author 闫嘉玮
	 * @return
	 * @throws IOException
	 */
	public static JSONObject getSelfmenu() throws IOException {
		StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.CURRENT_SELFMENU_URL);
		path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
		Boolean isProxy = new Boolean(false);
		String result = RequestUtil.getOrPostUrl(path.toString(), "GET", null,
				RequestProxyConfig.build(null, null, isProxy));
		return JSONObject.fromObject(result);
	}

	/**
	 * 创建自定义菜单
	 * 
	 * @author 闫嘉玮
	 * @return
	 * @throws IOException
	 */
	public static Boolean createMenu(JSONObject menuObject) throws IOException {
		StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.CREATE_MENU_URL);
		path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
		String result = null;
		Boolean isProxy = new Boolean(false);
		result = RequestUtil.getOrPostUrl(path.toString(), "POST", menuObject.toString(),
				RequestProxyConfig.build(null, null, isProxy));
		System.out.println(result);
		if (JSONObject.fromObject(result).getString("errmsg").equals("ok"))
			return true;
		else
			return false;
	}

	/**
	 * 客服接口发送消息
	 * 
	 * @author 闫嘉玮
	 * @param messageObject
	 * @return
	 * @throws IOException
	 */
	public static JSONObject customSendMessage(JSONObject messageObject) throws IOException {
		StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.SEND_MESSAGE_BY_CUSTOM_URL);
		path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
		String result = null;
		Boolean isProxy = new Boolean(false);
		result = RequestUtil.getOrPostUrl(path.toString(), "POST", messageObject.toString(),
				RequestProxyConfig.build(null, null, isProxy));
		return JSONObject.fromObject(result);
	}

	/**
	 * 模板发送消息
	 * 
	 * @author 闫嘉玮
	 * @param messageObject
	 * @return
	 * @throws IOException
	 */
	public static JSONObject templateSendMessage(JSONObject messageObject) throws IOException {
		StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.SEND_MESSAGE_BY_TEMPLATE_URL);
		path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
		String result = null;
		Boolean isProxy = new Boolean(false);
		result = RequestUtil.getOrPostUrl(path.toString(), "POST", messageObject.toString(),
				RequestProxyConfig.build(null, null, isProxy));
		System.out.println(result);
		return JSONObject.fromObject(result);
	}

	/**
	 * 上传临时素材，返回mediaId
	 * 
	 * @param fileName
	 *            文件名
	 * @param inputStream
	 *            上传文件的输入流
	 * @param type
	 *            媒体文件类型，分别有图片(image)、语音(voice)、视频(video)和缩略图(thumb)
	 * @return
	 * @throws IOException
	 */
	public static String uploadTempMedia(String fileName, InputStream inputStream, String type) throws IOException {
		StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.UPLOAD_MEDIA_URL);
		path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
		path.append("&type=" + type);
		// 获取到要上传的文件的输入流信息，通过ByteArrayOutputStream流转成byte[]
		BufferedInputStream bis = null;
		byte[] body_data = null;
		bis = new BufferedInputStream(inputStream);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c = 0;
		byte[] buffer = new byte[8 * 1024];
		try {
			while ((c = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, c);
				baos.flush();
			}
			body_data = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Boolean isProxy = new Boolean(false);
		String result = RequestUtil.doPostSubmitBody(path.toString(), null, "media", fileName, body_data, "utf-8",
				RequestProxyConfig.build(null, null, isProxy));
		String mediaId = null;
		try {
			JSONObject data = JSONObject.fromObject(result);
			mediaId = data.getString("media_id");
		} catch (Exception e) {
		}
		return mediaId;
	}

	/**
	 * 根据素材id获取临时素材
	 * 
	 * @author 闫嘉玮
	 * @param mediaId
	 * @return byte[]文件字节数组
	 * @throws IOException
	 */
	public static byte[] getMedia(String mediaId) throws IOException {
		StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.GET_MEDIA_URL);
		path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
		path.append("&media_id=" + mediaId);
		Map<String, Object> data = new HashMap<String, Object>();
		Object[] result = RequestUtil.getOrPostFileBytes(path.toString(), false, data,
				RequestProxyConfig.build(null, null, false));
		if (result != null && result.length > 0)
			return (byte[]) result[0];
		else
			return null;
	}

	/**
	 * 上传永久素材，返回[mediaId, url]，第二个参数仅新增图片素材时会返回该字段
	 * 
	 * @param fileName
	 *            文件名
	 * @param inputStream
	 *            上传文件的输入流
	 * @param type
	 *            媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
	 * @return
	 * @throws IOException
	 */
	public static String[] addMedia(String fileName, InputStream inputStream, String type) throws IOException {
		StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.ADD_MATERIAL_URL);
		path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
		path.append("&type=" + type);
		// 获取到要上传的文件的输入流信息，通过ByteArrayOutputStream流转成byte[]
		BufferedInputStream bis = null;
		byte[] body_data = null;
		bis = new BufferedInputStream(inputStream);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c = 0;
		byte[] buffer = new byte[8 * 1024];
		try {
			while ((c = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, c);
				baos.flush();
			}
			body_data = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Boolean isProxy = new Boolean(false);
		String result = RequestUtil.doPostSubmitBody(path.toString(), null, "media", fileName, body_data, "utf-8",
				RequestProxyConfig.build(null, null, isProxy));
		String mediaId = null;
		String url = null;
		try {
			JSONObject data = JSONObject.fromObject(result);
			mediaId = data.getString("media_id");
			url = data.getString("url");
		} catch (Exception e) {
		}
		return new String[] { mediaId, url };
	}

	/**
	 * 删除永久素材
	 * 
	 * @param mediaId
	 * @return
	 * @throws IOException
	 */
	public static Boolean delMaterial(String mediaId) throws IOException {
		StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.DEL_MATERIAL_URL);
		path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
		JSONObject paramObject = new JSONObject();
		paramObject.put("media_id", mediaId);
		String result = RequestUtil.getOrPostUrl(path.toString(), "POST", paramObject.toString(),
				RequestProxyConfig.build(null, null, false));
		try {
			JSONObject data = JSONObject.fromObject(result);
			if (data.getInt("errcode") == 0)
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 根据素材id获取永久素材
	 * 
	 * @author 闫嘉玮
	 * @param mediaId
	 * @return [byte[]文件字节数组, 文件类型(image/jpeg、audio/amr、video/mpeg4等)]
	 * @throws IOException
	 */
	public static byte[] getMaterial(String mediaId) throws IOException {
		StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.GET_MATERIAL_URL);
		path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("media_id", mediaId);
		Object[] result = RequestUtil.getOrPostFileBytes(path.toString(), true, data,
				RequestProxyConfig.build(null, null, false));
		if (result != null && result.length > 0)
			return (byte[]) result[0];
		else
			return null;
	}

	/**
	 * 获取素材列表
	 * 
	 * @author 闫嘉玮
	 * @param type
	 *            图片（image）、视频（video）、语音 （voice）、图文（news）
	 * @param offset
	 *            从全部素材的该偏移位置开始返回，0表示从第一个素材 返回
	 * @param count
	 *            返回素材的数量，取值在1到20之间
	 * @return
	 * @throws IOException
	 */
	public static JSONObject batchgetMaterial(String type, Integer offset, Integer count) throws IOException {
		JSONObject paramObject = new JSONObject();
		paramObject.put("type", "video");
		paramObject.put("offset", 0);
		paramObject.put("count", 1);
		return WXSubscriptionRequestUtil.batchgetMaterial(paramObject);
	}

	/**
	 * 获取素材列表
	 * 
	 * @author 闫嘉玮
	 * @param paramObject
	 *            {type: '', offset: '', count: ''}
	 * @return {item_count: 当前数量, total_count: 素材总数, item: [{update_time, name,
	 *         media_id, url, tags:[]}]}
	 * @throws IOException
	 */
	public static JSONObject batchgetMaterial(JSONObject paramObject) throws IOException {
		StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.BATCHGET_MATERIAL_URL);
		path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
		String result = RequestUtil.getOrPostUrl(path.toString(), "POST", paramObject.toString(),
				RequestProxyConfig.build(null, null, false));
		return JSONObject.fromObject(result);
	}

	/**
	 * 获取永久素材数量，返回:
	 * {"voice_count":音乐数,"video_count":视频数,"image_count":图片数,"news_count":图文数}
	 * 
	 * @return
	 * @throws IOException
	 */
	public static JSONObject getMaterialCount() throws IOException {
		StringBuffer path = new StringBuffer(WXConstants.DOMAIN_API + WXConstants.GET_MATERIAL_COUNT_URL);
		path.append("?access_token=" + WXSubscriptionRequestUtil.getAccessToken());
		String result = RequestUtil.getOrPostUrl(path.toString(), "GET", null,
				RequestProxyConfig.build(null, null, false));
		return JSONObject.fromObject(result);
	}
}
