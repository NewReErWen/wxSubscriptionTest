/**
 * 对公众平台发送给公众账号的消息加解密示例代码.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.tencent.tools.wx.aes;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * SHA1 class
 *
 * 计算公众平台的消息签名接口.
 */
public class SHA1 {

	/**
	 * 用SHA1算法生成安全签名
	 * 
	 * @param token
	 *            票据
	 * @param timestamp
	 *            时间戳
	 * @param nonce
	 *            随机字符串
	 * @param encrypt
	 *            密文
	 * @return 安全签名
	 * @throws AesException
	 */
	public static String getSHA1(String token, String timestamp, String nonce, String encrypt) throws AesException {
		try {
			String[] array = new String[] { token, timestamp, nonce, encrypt };
			StringBuffer sb = new StringBuffer();
			// 字符串排序
			Arrays.sort(array);
			for (int i = 0; i < 4; i++) {
				sb.append(array[i]);
			}
			return getSHA1(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ComputeSignatureError);
		}
	}

	/**
	 * 对存放参数的Map集合进行排序后拼接参数进行sha1加密
	 * 
	 * @author 闫嘉玮
	 * @param map
	 * @return
	 * @throws AesException
	 */
	public static String getSHA1(Map<String, String> data) throws AesException {
		Set<String> keySet = data.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keyArray);
		StringBuilder sb = new StringBuilder();
		for (String k : keyArray) {
			// 参数值不为空,参与签名
			if (data.get(k) != null && !data.get(k).trim().equals(""))
				sb.append(k).append("=").append(data.get(k).trim()).append("&");
		}
		if (sb.length() > 0)
			sb.setLength(sb.length() - 1);
		return getSHA1(sb.toString());
	}

	/**
	 * 将拼接好的字符串进行sha1加密
	 * 
	 * @author 闫嘉玮
	 * @param params
	 * @return
	 * @throws AesException
	 */
	public static String getSHA1(String params) throws AesException {
		try {
			// SHA1签名生成
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(params.getBytes());
			byte[] digest = md.digest();

			StringBuffer hexstr = new StringBuffer();
			String shaHex = "";
			for (int i = 0; i < digest.length; i++) {
				shaHex = Integer.toHexString(digest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexstr.append(0);
				}
				hexstr.append(shaHex);
			}
			return hexstr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ComputeSignatureError);
		}
	}
}
