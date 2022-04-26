package com.tencent.wxcloudrun.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	final Logger logger;

	public WxSubscriptionInfosyssController() {
		this.logger = LoggerFactory.getLogger(CounterController.class);
	}

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

	private WxSubscriptionMsgService wxMsgService = new WxSubscriptionMsgService();

	@PostMapping(value = "/wx/receiveMessage")
	public void sendMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try {
			// 将request请求，传到Message工具类的转换方法中，返回接收到的Map对象
			Map<String, String> recieveMap = XmlParseUtil.xmlToMap(request, request.getParameter("inputData"));
			// 从集合中，获取XML各个节点的内容
			// 微信开发者的微信号
			String toUserName = recieveMap.get("ToUserName");
			// openId
			String fromUserName = recieveMap.get("FromUserName");
			String createTime = recieveMap.get("CreateTime");
			String msgType = recieveMap.get("MsgType");
			// 发送消息的内容
			String content = recieveMap.get("Content");
			String msgId = recieveMap.get("MsgId");

			// 获取发送人信息
			User sendUser = wxMsgService.findUserByOpenId(fromUserName);
			// 获取接收人（即公众号开发者）信息
			User receiveUser = new User(UUID.randomUUID().toString(), "开发者", toUserName);

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
			List<SystemMessage> systemMessageList = null;
			JSONObject businessMessageContent = new JSONObject();
			// 不回复，回复统一使用客服接口
			Map<String, Object> message = new HashMap<String, Object>();
			message.put("ToUserName", fromUserName);
			message.put("FromUserName", toUserName);
			message.put("CreateTime", String.valueOf(new Date().getTime()));
			message.put("MsgType", "text");
			message.put("Content", "您好，" + fromUserName + "\n我是：" + toUserName + "\n您发送的消息类型为：" + msgType + "\n您发送的时间为"
					+ createTime + "\n我回复的时间为：" + message.get("CreateTime") + "\n您发送的内容是：" + content);
			// 转为XML字符串
			String str = XmlParseUtil.mapToXml(message, true);
			out.print("");
			if (msgType.equals("text")) {// 判断消息类型是否是文本消息(text)，除text外，还有其他消息类型（例如图片、语音等）
				businessMessageContent.put("replyType", "text");
				businessMessageContent.put("content", content);
				systemMessageList = this.replyContent(businessMessageContent, "reply", 1, 5);
			} else if (msgType.equals("image")) {
			} else if (msgType.equals("voice")) {
			} else if (msgType.equals("video")) {
			} else if (msgType.equals("event")) {
				String event = recieveMap.get("Event");
				if (event.equals("subscribe") || event.equals("SCAN")) {
					systemMessageList = this.replyContent(businessMessageContent, "welcome", 1, 5);
				} else if (event.equals("unsubscribe")) {
					// 取消关注x
				}
			}
			if (systemMessageList != null && !systemMessageList.isEmpty()) {
				if (systemMessageList.size() == 1) {
					// 匹配到的问题作为回复
					SystemMessage systemMessage = systemMessageList.get(0);
					for (SystemReply systemReply : systemMessage.getSystemReplys()) {
						JSONObject messageObject = new JSONObject();
						JSONObject data = new JSONObject();
						// 拼装json对象
						messageObject.put("touser", fromUserName);
						if (systemReply.getReplyType().equals("text")) {
							messageObject.put("msgtype", "text");
							data.put("content", systemReply.getContent());
							messageObject.put("text", data);
						} else if (systemReply.getReplyType().equals("image")) {
							// TODO 临时素材添加并获取
							String media_id = "";
							messageObject.put("msgtype", "image");
							data.put("media_id", media_id);
							messageObject.put("video", data);
						} else if (systemReply.getReplyType().equals("voice")) {
							// TODO 临时素材添加并获取
							String media_id = "";
							messageObject.put("msgtype", "voice");
							data.put("media_id", media_id);
							messageObject.put("video", data);
						} else if (systemReply.getReplyType().equals("video")) {
							// TODO 临时素材添加并获取media_id
							String media_id = "";
							messageObject.put("msgtype", "video");
							data.put("media_id", media_id);
							messageObject.put("video", data);
						} else if (systemReply.getReplyType().equals("news")) {
							// 图文内容，目前没有
							JSONObject newsObject = new JSONObject();
							JSONArray articles = new JSONArray();
							JSONObject article = new JSONObject();
							JSONObject paramObject = new JSONObject();
							paramObject.put("type", "news");
							paramObject.put("offset", 0);
							paramObject.put("count", 1);
							JSONObject articleObject = WXSubscriptionRequestUtil.batchgetMaterial(paramObject)
									.getJSONArray("item").getJSONObject(0).getJSONObject("content")
									.getJSONArray("news_item").getJSONObject(0);
							article.put("title", articleObject.get("title"));
							article.put("description", articleObject.get("digest"));
							article.put("url", articleObject.get("url"));
							article.put("picurl", articleObject.get("thumb_url"));
							articles.add(article);
							newsObject.put("articles", articles);
							messageObject.put("news", newsObject);
						}
						// 调用已封装的客服发送消息请求方法
						WXSubscriptionRequestUtil.customSendMessage(messageObject);
					}
				} else {
					// 推荐问题列表
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

	public List<SystemMessage> replyContent(JSONObject businessMessageContent, String operateType, Integer pageNum,
			Integer pageRowNum) {
		// 系统问题列表
		List<SystemMessage> systemMessageList = new ArrayList<SystemMessage>();
		if (operateType != null && !operateType.equals("")) {
			if (operateType.equals("welcome") || operateType.equals("reply") || operateType.equals("unifiedReply")) {
				// 回复时机（欢迎语、回复语、统一回复、其他）
				Parameter messageTypeParameter = null;
				List<Parameter> messageTypeList = new ArrayList<Parameter>();
				if (operateType.equals("welcome")) {
					// 回复时机-欢迎语
					// messageTypeParameter =
					// findParameterByCode(welcomeMessageTypeCode);
					messageTypeParameter = new Parameter();
					messageTypeList.add(messageTypeParameter);
					// systemMessageList = systemMessageService
					// .findSystemMessageListByStateListMessageTypeListOrganizationContentLikeContent(stateList,
					// messageTypeList, organization, null, null);
					SystemMessage systemMessage1 = new SystemMessage();
					Set<SystemReply> systemReplys = new HashSet<SystemReply>();
					SystemReply r1 = new SystemReply();
					r1.setContent("谢谢关注");
					r1.setReplyType("text");
					systemReplys.add(r1);
					systemMessage1.setSystemReplys(systemReplys);
					systemMessageList.add(systemMessage1);
				} else if (operateType.equals("unifiedReply")) {
					// 回复时机-欢迎语
					// messageTypeParameter =
					// parameterService.findParameterByCode(unifiedReplyMessageTypeCode);
					messageTypeParameter = new Parameter();
					messageTypeList.add(messageTypeParameter);
					// systemMessageList = systemMessageService
					// .findSystemMessageListByStateListMessageTypeListOrganizationContentLikeContent(stateList,
					// messageTypeList, organization, null, null);
					SystemMessage systemMessage1 = new SystemMessage();
					systemMessageList.add(systemMessage1);
				} else if (operateType.equals("reply")) {
					String keyword = null;
					// if (businessMessageById != null) {
					if (true) {
						// 用户输入内容
						if (businessMessageContent.getString("replyType") != null
								&& businessMessageContent.getString("replyType").equals("text")) {
							// 回复时机-回复语
							keyword = businessMessageContent.getString("content");
							// messageTypeParameter =
							// parameterService.findParameterByCode(replyMessageTypeCode);
							messageTypeParameter = new Parameter();
							messageTypeList.add(messageTypeParameter);
							// systemMessageList = systemMessageService
							// .findSystemMessageListByStateListMessageTypeListOrganizationContentLikeContent(
							// stateList, messageTypeList, organization,
							// keyword, null);
							SystemMessage systemMessage1 = new SystemMessage();
							Set<SystemReply> systemReplys = new HashSet<SystemReply>();
							SystemReply r1 = new SystemReply();
							r1.setContent("测试数据");
							r1.setReplyType("text");
							systemReplys.add(r1);
							SystemReply r2 = new SystemReply();
							r2.setContent("测试数据");
							r2.setReplyType("text");
							systemReplys.add(r1);
							systemReplys.add(r2);
							systemMessage1.setSystemReplys(systemReplys);
							systemMessageList.add(systemMessage1);
							if (systemMessageList == null || systemMessageList.size() != 1) {
								// 回复内容不存在或结果不唯一，再次进行模糊查询
								if (pageNum == null || pageNum.equals(""))// 页数
									pageNum = 1;
								if (pageRowNum == null || pageRowNum.equals("")) // 每页条数
									pageRowNum = 5;
								// Object[] objectArray = systemMessageService
								// .findObjectByStateListProductListOrganizationContentPageNumPageRowNum(stateList,
								// messageTypeList, organization, keyword,
								// pageNum, pageRowNum);
								Object[] objectArray = new Object[2];
								objectArray[0] = 2;
								objectArray[1] = new ArrayList<SystemMessage>();
								if (objectArray != null) {
									// 0总条数, 1系统问题列表
									if (objectArray[1] != null)
										systemMessageList = (List<SystemMessage>) objectArray[1];
								}
							}
						}
						// 未找到匹配的回复语时，回复其他
						if (systemMessageList == null || systemMessageList.isEmpty()) {
							// 回复时机-其他
							messageTypeList.clear();
							// messageTypeParameter =
							// parameterService.findParameterByCode(otherMessageTypeCode);
							messageTypeParameter = new Parameter();
							messageTypeList.add(messageTypeParameter);
							// systemMessageList = systemMessageService
							// .findSystemMessageListByStateListMessageTypeListOrganizationContentLikeContent(
							// stateList, messageTypeList, organization, null,
							// null);
							SystemMessage systemMessage1 = new SystemMessage();
							Set<SystemReply> systemReplys = new HashSet<SystemReply>();
							SystemReply r1 = new SystemReply();
							r1.setContent("测试数据");
							r1.setReplyType("text");
							systemReplys.add(r1);
							systemMessage1.setSystemReplys(systemReplys);
							systemMessageList.add(systemMessage1);
						}
					}
				}
			}
		}
		return systemMessageList;
	}
}
