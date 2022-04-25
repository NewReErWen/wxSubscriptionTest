package com.tencent.wxcloudrun.controller;

/**
 * 消息记录
 *
 */
public class WxMsg {
	private String id;
	// msgType: {name, code}
	private Object msgType;
	// 文本内容
	private String content;
	
	public WxMsg(String id, Object msgType, String content) {
		super();
		this.id = id;
		this.msgType = msgType;
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Object getMsgType() {
		return msgType;
	}
	public void setMsgType(Object msgType) {
		this.msgType = msgType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
