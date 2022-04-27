package com.tencent.wxcloudrun.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tencent.tools.wx.WXSubscriptionRequestUtil;

import net.sf.json.JSONObject;

// 本地调试即可
@RestController
public class TestController {
	/**
	 * 上传临时素材，返回mediaId(ok)
	 * 
	 * @return
	 */
	@GetMapping(value = "/test/upload")
	String upload() {
		String result = null;
		try {
			String fileName = "slider3.jpg";
			InputStream inputStream = new FileInputStream("D:\\备份20210224内容\\共享\\桌面\\测试材料\\" + fileName);
			String type = "image";
			String mediaId = WXSubscriptionRequestUtil.uploadTempMedia(fileName, inputStream, type);
			result = "mediaId:" + mediaId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取临时素材(ok)
	 * 
	 * @param mediaId
	 * @return
	 */
	@GetMapping(value = "/test/get")
	String get(String mediaId) {
		String result = null;
		try {
			byte[] bs = WXSubscriptionRequestUtil.getMedia(mediaId);
			OutputStream outputStream = new FileOutputStream(new File("D:\\备份20210224内容\\共享\\桌面\\测试材料\\000.jpg"));
			outputStream.write(bs);
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 上传永久素材，返回mediaId,url(上传类型为iamge时才有此值)(ok)
	 * 
	 * @return
	 */
	@GetMapping(value = "/test/addMedia")
	String addMedia() {
		String result = null;
		try {
			InputStream inputStream = new FileInputStream("D:\\备份20210224内容\\共享\\桌面\\测试材料\\slider3.jpg");
			String[] data = WXSubscriptionRequestUtil.addMedia("test.png", inputStream, "image");
			result = Arrays.toString(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 删除永久素材，返回是否删除成功(ok)
	 * 
	 * @param mediaId
	 * @return
	 */
	@GetMapping(value = "/test/delMaterial")
	String delMaterial(String mediaId) {
		String result = null;
		try {
			Boolean isDel = WXSubscriptionRequestUtil.delMaterial(mediaId);
			result = isDel.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取永久素材(ok)
	 * 
	 * @param mediaId
	 * @return
	 */
	@GetMapping(value = "/test/getMaterial")
	String getMaterial(String mediaId) {
		String result = null;
		try {
			byte[] bs = WXSubscriptionRequestUtil.getMaterial(mediaId);
			OutputStream outputStream = new FileOutputStream(new File("D:\\备份20210224内容\\共享\\桌面\\测试材料\\000.jpg"));
			outputStream.write(bs);
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取某类型的素材列表(ok)
	 * 
	 * @return
	 */
	@GetMapping(value = "/test/getAllMaterial")
	String getAllMaterial(String type, Integer offset, Integer count) {
		String result = null;
		try {
			JSONObject data = new JSONObject();
			data.put("type", type != null ? type : "image");
			data.put("offset", offset != null ? offset : 0);
			data.put("count", count != null ? count : 20);
			JSONObject resultData = WXSubscriptionRequestUtil.batchgetMaterial(data);
			result = resultData.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取永久素材总数(ok)
	 * 
	 * @return
	 */
	@GetMapping(value = "/test/getMaterialCount")
	String getMaterialCount() {
		String result = null;
		try {
			JSONObject resultData = WXSubscriptionRequestUtil.getMaterialCount();
			result = resultData.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
