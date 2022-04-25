package com.tencent.wxcloudrun.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CounterRequest;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.service.CounterService;

/**
 * counter控制器
 */
@RestController

public class CounterController {

	final CounterService counterService;
	final Logger logger;

	public CounterController(@Autowired CounterService counterService) {
		this.counterService = counterService;
		this.logger = LoggerFactory.getLogger(CounterController.class);
	}

	/**
	 * 获取当前计数
	 * 
	 * @return API response json
	 */
	@GetMapping(value = "/api/count")
	ApiResponse get() {
		logger.info("/api/count get request");
		Optional<Counter> counter = counterService.getCounter(1);
		Integer count = 0;
		if (counter.isPresent()) {
			count = counter.get().getCount();
		}

		return ApiResponse.ok(count);
	}

	/**
	 * 更新计数，自增或者清零
	 * 
	 * @param request
	 *            {@link CounterRequest}
	 * @return API response json
	 */
	@PostMapping(value = "/api/count")
	ApiResponse create(@RequestBody CounterRequest request) {
		logger.info("/api/count post request, action: {}", request.getAction());

		Optional<Counter> curCounter = counterService.getCounter(1);
		if (request.getAction().equals("inc")) {
			Integer count = 1;
			if (curCounter.isPresent()) {
				count += curCounter.get().getCount();
			}
			Counter counter = new Counter();
			counter.setId(1);
			counter.setCount(count);
			counterService.upsertCount(counter);
			return ApiResponse.ok(count);
		} else if (request.getAction().equals("clear")) {
			if (!curCounter.isPresent()) {
				return ApiResponse.ok(0);
			}
			counterService.clearCount(1);
			return ApiResponse.ok(0);
		} else {
			return ApiResponse.error("参数action错误");
		}
	}

	/**
	 * 在微信控制台设置的请求地址，此处用于接收微信公众号的服务器发送回来的消息（包括不限于连接测试、用户关注通知、用户发送消息等）
	 * 
	 * @author 闫嘉玮
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	// @RequestMapping(value = "/receiveMessage", method = { RequestMethod.POST,
	// RequestMethod.GET })
	@GetMapping(value = "/api/receiveMessage")
	public void receiveMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// GET请求用于在设置地址时微信服务器发来验证;POST请求用于公众号内某事件触发时微信服务器发来消息
		// System.out.println("start--" + request.getMethod());
		// if (request.getMethod().equals("GET")) {
		// doGet(request, response);
		// } else if (request.getMethod().equals("POST"))
		// doPost(request, response);
		// else
		// throw new Exception("wrong argument");
	}

}