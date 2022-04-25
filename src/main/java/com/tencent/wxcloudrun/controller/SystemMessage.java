package com.tencent.wxcloudrun.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 系统问题
 * 
 */
public class SystemMessage implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7605853313402821262L;

	private String id;
	/**
	 * 内容描述
	 */
	private String content;
	/**
	 * 机构Id(维护修改均需赋值,用于检索)
	 */
	private String organizationId;
	/**
	 * 回复时机Id(维护修改均需赋值,用于检索)
	 */
	private String messageTypeId;
	/**
	 * 回复数量：1、随机回复（从回复语列表中选第一条回复）、0回复全部（回复语按排序顺序依次回复）(0、1、N)、n、按顺序回复
	 */
	private Integer replyCount;
	/**
	 * 状态（0维护中、2有效）
	 */
	private Integer state;
	/**
	 * 创建时间
	 */
	private Date createTime = new Date();
	/**
	 * 最后修改时间
	 */
	private Date finalUpdateTime;
	/**
	 * 回复时机:
	 * 欢迎语（1、没有消息的请求）、回复语（n、只有在关键字匹配时回复）、统一回复（1、参数控制【分钟】:当用户发送消息超过时间时回复）、其他（1没有匹配关键字时随机回复一条）
	 */
	private Parameter messageType;
	/**
	 * 回复语
	 */
	private Set<SystemReply> systemReplys = new HashSet<SystemReply>();

	public SystemMessage() {
		this.setId(UUID.randomUUID().toString());
	}

	public SystemMessage(String id) {
		this.setId(id);
	}

	public SystemMessage(String id, String content, String organizationId, String messageTypeId, Integer replyCount,
			Integer state, Date createTime, Date finalUpdateTime, Parameter messageType,
			Set<SystemReply> systemReplys) {
		super();
		this.id = id;
		this.content = content;
		this.organizationId = organizationId;
		this.messageTypeId = messageTypeId;
		this.replyCount = replyCount;
		this.state = state;
		this.createTime = createTime;
		this.finalUpdateTime = finalUpdateTime;
		this.messageType = messageType;
		this.systemReplys = systemReplys;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getMessageTypeId() {
		return messageTypeId;
	}

	public void setMessageTypeId(String messageTypeId) {
		this.messageTypeId = messageTypeId;
	}

	public Integer getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getFinalUpdateTime() {
		return finalUpdateTime;
	}

	public void setFinalUpdateTime(Date finalUpdateTime) {
		this.finalUpdateTime = finalUpdateTime;
	}

	public Parameter getMessageType() {
		return messageType;
	}

	public void setMessageType(Parameter messageType) {
		this.messageType = messageType;
	}

	public Set<SystemReply> getSystemReplys() {
		return systemReplys;
	}

	public void setSystemReplys(Set<SystemReply> systemReplys) {
		this.systemReplys = systemReplys;
	}
}