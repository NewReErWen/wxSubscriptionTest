package com.tencent.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * JSON与实体类对象转换
 *
 */
public class JSONInfosyss {
	/**
	 * 实体类对象转json对象
	 * 
	 * @param object
	 * @return
	 */
	public static <T> JSONObject beanToJsonObject(T object) {
		if (object != null)
			return JSONObject.fromObject(JSON.toJSONStringWithDateFormat(object, "yyyy-MM-dd HH:mm:ss"));
		else
			return null;
	}

	/**
	 * 实体类对象转json数组
	 *
	 * @param objects
	 * @return JSONArray
	 */
	public static <T> JSONArray beanToJsonArray(Collection<T> objects) {
		JSONArray jsonArray = new JSONArray();
		if (objects != null && !objects.isEmpty()) {
			for (Object object : objects) {
				jsonArray.add(beanToJsonObject(object));
			}
		}
		return jsonArray;
	}

	/**
	 * 实体类对象转json数组
	 * 
	 * @param objects
	 * @return JSONArray
	 */
	public static <T> JSONArray beanToJsonArray(T[] objects) {
		JSONArray jsonArray = new JSONArray();
		if (objects != null && objects.length > 0) {
			for (Object object : objects) {
				jsonArray.add(beanToJsonObject(object));
			}
		}
		return jsonArray;
	}

	/**
	 * 自定义追加实体生成的JSONObject（懒加载追加）
	 *
	 * @param <T>
	 */
	public static abstract class JSONBean<T> {
		public abstract JSONObject beanToJsonObject(T object);
	}

	/**
	 * 自定义追加实体生成的JSONObject（懒加载追加）
	 *
	 * @param objects
	 * @param jsonBean
	 * @return
	 */
	public static <T> JSONArray beanToJsonArray(Collection<T> objects, JSONBean<T> jsonBean) {
		if (jsonBean == null)
			jsonBean = new JSONBean<T>() {
				@Override
				public JSONObject beanToJsonObject(T object) {
					return JSONInfosyss.beanToJsonObject(object);
				}
			};
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		if (objects != null && !objects.isEmpty()) {
			for (T object : objects) {
				jsonObject = jsonBean.beanToJsonObject(object);
				jsonArray.add(jsonObject);
			}
		}
		return jsonArray;
	}

	/**
	 * 自定义追加实体生成的JSONObject（懒加载追加）
	 *
	 * @param objects
	 * @param jsonBean
	 * @return
	 */
	public static <T> JSONArray beanToJsonArray(T[] objects, JSONBean<T> jsonBean) {
		if (jsonBean == null)
			jsonBean = new JSONBean<T>() {
				@Override
				public JSONObject beanToJsonObject(T object) {
					return JSONInfosyss.beanToJsonObject(object);
				}
			};
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		if (objects != null && objects.length > 0) {
			for (T object : objects) {
				jsonObject = jsonBean.beanToJsonObject(object);
				jsonArray.add(jsonObject);
			}
		}
		return jsonArray;
	}

	/**
	 * 基本数据类型转json数组
	 *
	 * @param values
	 * @return JSONArray
	 */
	public static <T> JSONArray toJsonArray(Collection<T> values) {
		JSONArray jsonArray = new JSONArray();
		if (values != null && !values.isEmpty()) {
			for (Object value : values) {
				jsonArray.add(value);
			}
		}
		return jsonArray;
	}

	/**
	 * 基本数据类型转json数组
	 * 
	 * @param values
	 * @return
	 */
	public static <T> JSONArray toJsonArray(T[] values) {
		JSONArray jsonArray = new JSONArray();
		if (values != null && values.length > 0) {
			for (Object value : values) {
				jsonArray.add(value);
			}
		}
		return jsonArray;
	}

	/**
	 * json字符串转实体类对象
	 * 
	 * @param text
	 * @param clazz
	 * @return
	 */
	public static <T> T toBean(String text, Class<T> clazz) {
		if (text == null || text.equals(""))
			return null;
		return JSON.parseObject(text, clazz);
	}

	/**
	 * json对象转实体类对象
	 * 
	 * @param object
	 * @param clazz
	 * @return
	 */
	public static <T> T toBean(JSONObject object, Class<T> clazz) {
		if (object == null)
			return null;
		return toBean(object.toString(), clazz);
	}

	/**
	 * json字符串数组转实体类对象集合
	 * 
	 * @param text
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> toBeanList(String text, Class<T> clazz) {
		if (text == null || text.equals(""))
			return new ArrayList<T>();
		return JSON.parseArray(text, clazz);
	}

	/**
	 * json对象数组转实体类对象集合
	 * 
	 * @param text
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> toBeanList(JSONArray objects, Class<T> clazz) {
		if (objects == null)
			return new ArrayList<T>();
		return toBeanList(objects.toString(), clazz);
	}
}