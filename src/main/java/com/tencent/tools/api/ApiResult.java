package com.tencent.tools.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * REST API 返回结果<br>
 * 共返回五个字段: code、msg、success、data、time code,msg取自ApiCode.SUCCESS <br>
 * code:响应码 <br>
 * msg:响应消息 <br>
 * success:是否成功 <br>
 * data:响应数据 <br>
 * time:响应时间<br>
 *
 */
@Data
@Accessors(chain = true)
//@Builder
@AllArgsConstructor
public class ApiResult implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 响应码: 状态码为200才算请求成功 */
	private int code;

	/** 响应消息(集合) */
	private JSONArray msgList;

	/** 是否成功 */
	private boolean success;

	/** 响应数据 */
	private JSONObject data;

	/** 响应时间 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date time;

	private ApiResult() {
		this.msgList = new JSONArray();
		this.data = new JSONObject();
		this.time = new Date(System.currentTimeMillis());
	}

	/**
	 * 默认生成成功响应结果{ApiCode.SUCCESS, success: true, data: null, time:
	 * java.util.Date}
	 * 
	 * @return
	 */
	public static ApiResult build() {
		return new ApiResult().success();
	}

	/**
	 * 返回结果:{ApiCode.SUCCESS, success: true, data: null, time: java.util.Date }
	 * 
	 * @return
	 */
	public ApiResult success() {
		return of(true, ApiCode.SUCCESS);
	}

	/**
	 * 返回结果:{ ApiCode.FAIL, success: false, data: null, time: java.util.Date }
	 * 
	 * @return
	 */
	public ApiResult fail() {
		return of(false, ApiCode.FAIL);
	}

	/**
	 * 返回结果: {自定义API响应码, success: false, data: null, time: java.util.Date }
	 * 
	 * @param resultCode
	 * @return
	 */
	public ApiResult fail(final ApiCode resultCode) {
		return of(false, resultCode);
	}

	/**
	 * 自定义响应消息(集合)
	 * 
	 * @param msgList
	 * @return
	 */
	public void messageList(final JSONArray msgList) {
		this.msgList = msgList;
		this.time = new Date(System.currentTimeMillis());
	}

	/**
	 * 获取响应消息(集合)
	 * 
	 * @return
	 */
	public JSONArray messageList() {
		return this.msgList;
	}

	/**
	 * 添加响应消息(集合)
	 * 
	 * @param msg
	 * @return
	 */
	public void addMessage(final String msg) {
		this.msgList.add(msg);
		this.time = new Date(System.currentTimeMillis());
	}

	/**
	 * 添加响应消息(集合)
	 * 
	 * @param msgCollection
	 * @return
	 */
	public void addAllMessage(final Collection<String> msgCollection) {
		this.msgList.addAll(msgCollection);
		this.time = new Date(System.currentTimeMillis());
	}

	/**
	 * 清空响应消息(集合)
	 */
	public void clearMessage() {
		this.msgList.clear();
	}

	/**
	 * 添加响应数据
	 * 
	 * @param data
	 * @return
	 */
	public void putAll(final Map<String, Object> data) {
		this.data.putAll(data);
		this.time = new Date(System.currentTimeMillis());
	}

	/**
	 * 添加响应数据
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public void put(final String key, final Object value) {
		this.data.put(key, value);
		this.time = new Date(System.currentTimeMillis());
	}

	/**
	 * 【扩展】效率相比于在代码中直接判断编写要低(效率主要低在循环语句,内部逻辑相同),但可读性会提升,可依据实际情况进行取舍<br>
	 * data.put("personnel.user.name", "abc", true);<br>
	 * 相当于<br>
	 * userData.put("name", "abc");<br>
	 * personnelData.put("user",userData);<br>
	 * data.put("personnel",personnelData);
	 * 
	 * @param key
	 * @param value
	 * @param needDivision
	 * @return
	 */
	public void put(final String key, final Object value, final Boolean needDivision) {
		if (!needDivision) {
			this.put(key, value);
			return;
		}
		String[] keys = key.split("\\.");
		JSONObject data = this.data;
		// 循环除最后一个值以外的其余内容,例:data.user.name,此处循环的key是data和user，值均为json对象
		int lastIndex = keys.length - 1;
		for (int i = 0; i < lastIndex; i++) {
			if (!data.containsKey(keys[i]))
				data.put(keys[i], new JSONObject());
			data = this.data.getJSONObject(keys[i]);
		}
		data.put(keys[lastIndex], value);
		this.time = new Date(System.currentTimeMillis());
	}

	/**
	 * 获取响应数据
	 * 
	 * @return
	 */
	public JSONObject data() {
		return this.data;
	}

	private ApiResult of(final boolean result, final ApiCode resultCode) {
		this.success = result;
		// this.msg = resultCode.getMsg();
		this.code = resultCode.getCode();
		return this;
	}
}
