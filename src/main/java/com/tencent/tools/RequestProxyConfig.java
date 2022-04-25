package com.tencent.tools;

/**
 * 请求代理配置
 * 
 * @author 闫嘉玮
 *
 */
public class RequestProxyConfig {
	private String host;
	private Integer port;
	private Boolean isProxy;

	private RequestProxyConfig(String host, Integer port, Boolean isProxy) {
		this.host = host;
		this.port = port;
		this.isProxy = isProxy;
	}

	public static RequestProxyConfig build(String host, Integer port, Boolean isProxy) {
		return new RequestProxyConfig(host, port, isProxy);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setIsProxy(Boolean isProxy) {
		this.isProxy = isProxy;
	}

	/**
	 * 是否代理
	 * 
	 * @return
	 */
	public Boolean isProxy() {
		if (isProxy == null || host == null || port == null)
			return false;
		if (!isProxy || host.equals("") || port.equals(""))
			return false;
		return true;
	}
}
