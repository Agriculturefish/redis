package com.ydt.lockredis.controller;

import com.ydt.lockredis.service.GoodsStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/storage")
public class StorageController {
	
	@Resource(name = "goodsStoreServiceImpl1")
	private GoodsStoreService goodsStoreService;

	@Autowired
	private RedisTemplate redisTemplate;

	@RequestMapping("testMaxMemory")
	@ResponseBody
	private void testMaxMemory(){
		for (int i = 0; i < 100000; i++) {
			redisTemplate.opsForValue().set("index: "+i , "index of " + i);
			redisTemplate.expire("index: "+i,i, TimeUnit.SECONDS);
		}
	}
	@RequestMapping("testMaxMemory2")
	@ResponseBody
	private void testMaxMemory2(){
		for (int i = 0; i < 100000; i++) {
			System.out.println(redisTemplate.opsForValue().get("index: "+i));
		}
	}
	
	/**
	 * 进入测试页面
	 * @param model
	 * @return
	 */
	@GetMapping("test")
	public ModelAndView stepOne(Model model){
		return new ModelAndView("test", "model", model);
	}
	
	/**
	 * 秒杀提交
	 * @param code
	 * @param num
	 * @return
	 */
	@PostMapping("secKill")
	@ResponseBody
	public String secKill(@RequestParam(value="code",required=true) String code,@RequestParam(value="num",required=true) Integer num){
		String reString = goodsStoreService.updateGoodsStore(code, num);
		return reString;
	}
}
