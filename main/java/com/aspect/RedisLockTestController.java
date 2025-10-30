package com.aspect;



import com.annotation.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author renBo
 * @ClassName: RedisLockTestController
 * @Description: RedisLock 测试demo
 * @date 2023-04-19
 */
//@RestController
@Slf4j
///@RequestMapping(value = "/redission")
public class RedisLockTestController {


	/**
	 * 写死key测试
	 */
	@RequestMapping(value = "/lockFoSimple")
	@RedisLock(key = "lockFoSimple")
	public void lockFoSimple() throws InterruptedException {
		Thread.sleep(5000);
		log.info("执行逻辑===lockFoSimple=============");
	}


	/**
	 * 简单业务key测试
	 *
	 * @param params
	 */

	@RequestMapping(value = "/lockFoSimpleParams")
	@RedisLock(key = "#params")
	public void lockFoSimpleParams(@RequestParam("params") String params) throws InterruptedException {
		Thread.sleep(5000);
		log.info("执行逻辑===lockFoSimpleParams============={}", params);
	}


	/**
	 * 通过业务参数作为分布式锁的key 如参数params中的farmerUserId 以此类推
	 *
	 * @param params
	 */
	@RequestMapping(value = "/lockForBusNo")
	@RedisLock(key = "#Object.farmerUserId")
	public void lockForBusNo(@RequestBody Object params) throws InterruptedException {
		Thread.sleep(5000);
		log.info("执行逻辑===lockForBusNo==========================【{}】", params);
	}

}
