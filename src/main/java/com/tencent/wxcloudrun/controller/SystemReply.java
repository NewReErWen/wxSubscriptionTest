package com.tencent.wxcloudrun.controller;

import java.util.UUID;

/**
 * 系统回复
 * 
 */
public class SystemReply implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5617224590316281411L;

	private String id;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * text、image、video、voice(参数名称)
	 */
	private String replyType;
	/**
	 * 回复内容
	 */
	private String content;
	/**
	 * 文件随机名
	 */
	private String fileRandomName;
	/**
	 * 格式为json字符串，"{replyType: 'text', content: 'xxx', fileRandomNameArray:
	 * ['xxx']}"
	 */
	private String jsonContent;
	/**
	 * 状态(2有效)
	 */
	private Integer state;
	/**
	 * 系统问题
	 */
	private SystemMessage systemMessage;

	public SystemReply() {
		this.setId(UUID.randomUUID().toString());
	}

	public SystemReply(String id) {
		this.setId(id);
	}

	public SystemReply(String id, Integer sort, String replyType, String content, String fileRandomName,
			String jsonContent, Integer state, SystemMessage systemMessage) {
		super();
		this.id = id;
		this.sort = sort;
		this.replyType = replyType;
		this.content = content;
		this.fileRandomName = fileRandomName;
		this.jsonContent = jsonContent;
		this.state = state;
		this.systemMessage = systemMessage;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getReplyType() {
		return replyType;
	}

	public void setReplyType(String replyType) {
		this.replyType = replyType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFileRandomName() {
		return fileRandomName;
	}

	public void setFileRandomName(String fileRandomName) {
		this.fileRandomName = fileRandomName;
	}

	public String getJsonContent() {
		return jsonContent;
	}

	public void setJsonContent(String jsonContent) {
		this.jsonContent = jsonContent;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public SystemMessage getSystemMessage() {
		return systemMessage;
	}

	public void setSystemMessage(SystemMessage systemMessage) {
		this.systemMessage = systemMessage;
	}
}