package com.tencent.wxcloudrun.controller;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tencent.tools.wx.WXSubscriptionRequestUtil;
import com.tencent.tools.wx.XmlParseUtil;
import com.tencent.tools.wx.aes.AesException;
import com.tencent.tools.wx.aes.SHA1;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 微信公众号相关controller
 * 
 * @author 闫嘉玮
 *
 */
@RestController
public class WxSubscriptionInfosyssController {
	private final String TOKEN = "newre";

	/**
	 * 在微信控制台设置的请求地址，此处用于接收微信公众号的服务器发送回来的消息（包括不限于连接测试、用户关注通知、用户发送消息等）
	 * 
	 * @author 闫嘉玮
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@GetMapping(value = "/wx/receiveMessage")
	public void validateMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		PrintWriter out = response.getWriter();
		if (notNullAll(new String[] { signature, timestamp, nonce, echostr })) {
			String encryptionText = "";
			try {
				encryptionText = SHA1.getSHA1(TOKEN, timestamp, nonce, "");// 这里是对三个参数进行加密
			} catch (AesException e) {
				e.printStackTrace();
			}
			if (encryptionText.equals(signature))
				out.print(echostr);
			return;
		}
		out.print("wrong argument");
	}

	@PostMapping(value = "/wx/receiveMessage")
	public void sendMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try {
			// 将request请求，传到Message工具类的转换方法中，返回接收到的Map对象
			Map<String, String> recieveMap = XmlParseUtil.xmlToMap(request, request.getParameter("inputData"));
			// 从集合中，获取XML各个节点的内容
			String toUserName = recieveMap.get("ToUserName");
			String fromUserName = recieveMap.get("FromUserName");
			String createTime = recieveMap.get("CreateTime");
			String msgType = recieveMap.get("MsgType");
			String content = recieveMap.get("Content");
			String msgId = recieveMap.get("MsgId");
			System.out.println(msgId + ": " + recieveMap);
			// TODO 可采用状态模式，或以子类取代类型码
			// MsgService msgService = MsgService.create(recieveMap);
			// 不同对象处理的事件不同，此处可能包含数据库查询等业务
			// msgService.event();
			// String str = msgService.toXml(); // 将内容解析成xml格式
			// out.print(str);
			// msgService = MsgService.create(MsgService.Text);
			// msgService.setToUserName("");
			// more...
			// msgService.toXml()
			if (msgType.equals("text")) {// 判断消息类型是否是文本消息(text)，除text外，还有其他消息类型（例如图片、语音等）
				// 文本消息
				Map<String, Object> message = new HashMap<String, Object>();
				// 原来【接收消息用户】变为回复时【发送消息用户】
				message.put("ToUserName", fromUserName);
				message.put("FromUserName", toUserName);
				message.put("CreateTime", String.valueOf(new Date().getTime()));
				if (content.equals("推送图文")) {
					System.out.println("推送图文中");
					JSONObject paramObject = new JSONObject();
					paramObject.put("type", "news");
					paramObject.put("offset", 0);
					paramObject.put("count", 1);
					JSONObject articles = WXSubscriptionRequestUtil.batchgetMaterial(paramObject).getJSONArray("item")
							.getJSONObject(0).getJSONObject("content").getJSONArray("news_item").getJSONObject(0);
					message.put("MsgType", "news");
					message.put("ArticleCount", 1);
					Map<String, Object> articlesMap = new HashMap<String, Object>();
					Map<String, Object> itemMap = new HashMap<String, Object>();
					itemMap.put("Title", articles.get("title"));
					itemMap.put("Description", articles.get("digest"));
					itemMap.put("PicUrl", articles.get("thumb_url"));
					itemMap.put("Url", articles.get("url"));
					articlesMap.put("item", itemMap);
					message.put("Articles", articlesMap);
				} else if (content.equals("推送视频")) {
					// TODO 失败
					System.out.println("推送视频中");
					message.put("MsgType", "video");
					JSONObject paramObject = new JSONObject();
					paramObject.put("type", "video");
					paramObject.put("offset", 0);
					paramObject.put("count", 1);
					JSONObject video = WXSubscriptionRequestUtil.batchgetMaterial(paramObject).getJSONArray("item")
							.getJSONObject(0);
					Map<String, Object> imageMap = new HashMap<String, Object>();
					imageMap.put("MediaId", video.getString("media_id"));
					imageMap.put("Title", video.getString("name"));
					imageMap.put("Description", video.getString("description"));
					message.put("Video", imageMap);
				} else if (content.equals("推送音频")) {
					System.out.println("推送音频中");
					message.put("MsgType", "music");
					JSONObject paramObject = new JSONObject();
					paramObject.put("type", "voice");
					paramObject.put("offset", 0);
					paramObject.put("count", 1);
					JSONObject music = WXSubscriptionRequestUtil.batchgetMaterial(paramObject).getJSONArray("item")
							.getJSONObject(0);
					Map<String, Object> imageMap = new HashMap<String, Object>();
					imageMap.put("ThumbMediaId", music.getString("media_id"));
					imageMap.put("Title", music.getString("name"));
					message.put("Music", imageMap);
				} else if (content.equals("1")) {
					System.out.println("---------");
					JSONObject messageObject = new JSONObject();
					// 拼装json对象
					messageObject.put("touser", fromUserName);
					messageObject.put("msgtype", "news");
					JSONObject newsObject = new JSONObject();
					JSONArray articles = new JSONArray();
					JSONObject article = new JSONObject();
					JSONObject paramObject = new JSONObject();
					paramObject.put("type", "news");
					paramObject.put("offset", 0);
					paramObject.put("count", 1);
					JSONObject articleObject = WXSubscriptionRequestUtil.batchgetMaterial(paramObject)
							.getJSONArray("item").getJSONObject(0).getJSONObject("content").getJSONArray("news_item")
							.getJSONObject(0);
					article.put("title", articleObject.get("title"));
					article.put("description", articleObject.get("digest"));
					article.put("url", articleObject.get("url"));
					article.put("picurl", articleObject.get("thumb_url"));
					articles.add(article);
					newsObject.put("articles", articles);
					messageObject.put("news", newsObject);
					// 调用已封装的客服发送消息请求方法
					System.out.println(messageObject);
					WXSubscriptionRequestUtil.customSendMessage(messageObject);
				} else {
					message.put("MsgType", "text");
					message.put("Content",
							"您好，" + fromUserName + "\n我是：" + toUserName + "\n您发送的消息类型为：" + msgType + "\n您发送的时间为"
									+ createTime + "\n我回复的时间为：" + message.get("CreateTime") + "\n您发送的内容是：" + content);
				}
				// 转为XML字符串
				String str = XmlParseUtil.mapToXml(message, true);
				System.out.println(str);
				out.print(str);
			} else if (msgType.equals("image")) {
				// 图片消息
				Map<String, Object> message = new HashMap<String, Object>();
				// 原来【接收消息用户】变为回复时【发送消息用户】
				message.put("ToUserName", fromUserName);
				message.put("FromUserName", toUserName);
				message.put("CreateTime", String.valueOf(new Date().getTime()));
				message.put("MsgType", "image");
				Map<String, Object> imageMap = new HashMap<String, Object>();
				imageMap.put("MediaId", recieveMap.get("MediaId"));
				message.put("Image", imageMap);
				// 转为XML字符串
				String str = XmlParseUtil.mapToXml(message, true);
				out.print(str);
			} else if (msgType.equals("voice")) {
				// 语音消息
				Map<String, Object> message = new HashMap<String, Object>();
				// 原来【接收消息用户】变为回复时【发送消息用户】
				message.put("ToUserName", fromUserName);
				message.put("FromUserName", toUserName);
				message.put("CreateTime", String.valueOf(new Date().getTime()));
				message.put("MsgType", "voice");
				Map<String, Object> imageMap = new HashMap<String, Object>();
				imageMap.put("MediaId", recieveMap.get("MediaId"));
				message.put("Voice", imageMap);
				// 转为XML字符串
				String str = XmlParseUtil.mapToXml(message, true);
				out.print(str);
			} else if (msgType.equals("video")) {
				out.print("");
			} else if (msgType.equals("event")) {
				String event = recieveMap.get("Event");
				if (event.equals("subscribe") || event.equals("SCAN")) {
					// 关注
					out.print("");

					String param = recieveMap.get("EventKey");
					JSONObject messageObject = new JSONObject(); // 拼装json对象
					messageObject.put("touser", fromUserName);
					messageObject.put("msgtype", "text");
					JSONObject textObject = new JSONObject();
					textObject.put("content", "二维码携带的参数值为：" + param);
					messageObject.put("text", textObject);
					WXSubscriptionRequestUtil.customSendMessage(messageObject);

					// 图文消息发送
					messageObject = new JSONObject();
					messageObject.put("touser", fromUserName);
					messageObject.put("msgtype", "news");
					JSONObject newsObject = new JSONObject();
					JSONArray articles = new JSONArray();
					JSONObject article = new JSONObject();
					JSONObject paramObject = new JSONObject();
					paramObject.put("type", "news");
					paramObject.put("offset", 0);
					paramObject.put("count", 1);
					JSONObject articleObject = WXSubscriptionRequestUtil.batchgetMaterial(paramObject)
							.getJSONArray("item").getJSONObject(0).getJSONObject("content").getJSONArray("news_item")
							.getJSONObject(0);
					article.put("title", articleObject.get("title"));
					article.put("description", articleObject.get("digest"));
					article.put("url", articleObject.get("url"));
					article.put("picurl", articleObject.get("thumb_url"));
					articles.add(article);
					newsObject.put("articles", articles);
					messageObject.put("news", newsObject);
					// 调用已封装的客服发送消息请求方法
					System.out.println(messageObject);
					WXSubscriptionRequestUtil.customSendMessage(messageObject);

					// 视频发送
					JSONObject video = WXSubscriptionRequestUtil.batchgetMaterial("video", 0, 1).getJSONArray("item")
							.getJSONObject(0);
					messageObject = new JSONObject(); // 拼装json对象
					messageObject.put("touser", fromUserName);
					messageObject.put("msgtype", "video");
					JSONObject videoObject = new JSONObject();
					videoObject.put("media_id", video.getString("media_id"));
					videoObject.put("thumb_media_id", video.getString("media_id"));
					videoObject.put("title", video.getString("name"));
					videoObject.put("description", video.getString("description"));
					messageObject.put("video", videoObject);
					WXSubscriptionRequestUtil.customSendMessage(messageObject);
				} else if (event.equals("unsubscribe")) {
					// 取消关注x
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

	/**
	 * 将value值与字体颜色汇总成json对象
	 * 
	 * @author 闫嘉玮
	 * @param value
	 * @param color
	 * @return json对象，格式为{"value":"","color":""}
	 */
	private JSONObject getNameAndColor(String value, String color) {
		JSONObject data = new JSONObject();
		data.put("value", value);
		data.put("color", color);
		return data;
	}

	/**
	 * 批量判断元素是否为空
	 * 
	 * @author 闫嘉玮
	 * @param valueArray
	 * @return true全部非空
	 */
	private boolean notNullAll(String[] valueArray) {
		if (valueArray == null)
			return true;
		for (String value : valueArray) {
			if (value == null || value.equals(""))
				return false;
		}
		return true;
	}
}
