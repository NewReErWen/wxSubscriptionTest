package com.tencent.wxcloudrun.controller;

import java.util.Map;
import java.util.UUID;

/**
 * 微信公众号接收消息后处理内容
 *
 */
public class WxSubscriptionMsgService {
	/**
	 * 根据发送消息人的信息返回消息人员
	 * 
	 * @param openId
	 * @return
	 */
	public User findUserByOpenId(String openId) {
		User user = new User(UUID.randomUUID().toString(), "用户A", openId);
		// User user = userService.findUserByOpenId(openId);
		return user;
	}
}

class User {
	String id;
	String name;
	String openId;

	public User(String id, String name, String openId) {
		super();
		this.id = id;
		this.name = name;
		this.openId = openId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

}