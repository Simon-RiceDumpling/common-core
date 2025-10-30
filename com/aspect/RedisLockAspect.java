package com.aspect;



import com.annotation.RedisLock;
import com.exception.AssemblyCatServiceException;
import com.utils.RedissonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author renBo
 * @ClassName: RedisLockAspect
 * @Description: 分布式索自定义注解解析器
 * @date 2023-04-19
 *
 * <plugin>
 * 				<artifactId>maven-compiler-plugin</artifactId>
 * 				<version>3.8.0</version>
 * 				<configuration>
 * 					<source>1.8</source>
 * 					<target>1.8</target>
 * 					<encoding>utf8</encoding>
 * 					<compilerArgs>
 * 						<arg>-parameters</arg>
 * 					</compilerArgs>
 * 				</configuration>
 * 			</plugin>
 * 		</plugins>
 */
@Aspect
@Component
@Slf4j
@ConditionalOnProperty(value = "spring.redis.enable", havingValue = "true", matchIfMissing = false)
public class RedisLockAspect {

	private static HashMap<String, Class> map = new HashMap<String, Class>() {
		{
			put("java.lang.Integer", int.class);
			put("java.lang.Double", double.class);
			put("java.lang.Float", float.class);
			put("java.lang.Long", long.class);
			put("java.lang.Short", short.class);
			put("java.lang.Boolean", boolean.class);
			put("java.lang.Char", char.class);
		}
	};
	private static final String REDIS_LOCK = "xmd:redisson:lock:";

	/**
	 * 切入点为所有的@RedisLock 注解方法
	 */
	@Pointcut("@annotation(com.annotation.RedisLock)")
	private void serviceAspect() {
	}

	/**
	 * @return java.lang.Object
	 * @Author renBo
	 * @Description 主要切入逻辑
	 * @Date 2023-04-19
	 * @Param [point]
	 */
	@SneakyThrows
	@Around("serviceAspect()")
	public Object around(ProceedingJoinPoint point) {
		Method method = ((MethodSignature) point.getSignature()).getMethod();
		RedisLock redisLock = method.getAnnotation(RedisLock.class);
		String redisLockKey = REDIS_LOCK.concat(method.getName())
				.concat(":")
				.concat(redisLock.key().contains("#") ? getParamsKey(point, redisLock.key().replace("#", "").split("\\.")) : redisLock.key());
		//尝试获取锁资源
		boolean lock = RedissonUtils.getTryLock(redisLockKey, TimeUnit.SECONDS, redisLock.waitTime(), redisLock.leaseTime());
		try {
			if (lock) {
				//获取到锁资源 执行业务逻辑
				return point.proceed();
			}
			//获取分布式锁失败 抛出异常
			throw new AssemblyCatServiceException("分布式锁获取失败");
		} finally {
			//释放锁
			if (lock) {
				RedissonUtils.unlock(redisLockKey);
			}
		}
	}

	/**
	 * @return java.lang.String
	 * @Author renBo
	 * @Description 动态获取配置的key值  @eg:a.b.c.d
	 * @Date 2023-04-19
	 * @Param [point, regexParams]
	 *
	 */
	public String getParamsKey(ProceedingJoinPoint point, String[] regexParams) throws Exception {
		if (regexParams.length == 1) {
			String classType = point.getTarget().getClass().getName();
			String methodName = point.getSignature().getName();
			// 参数值
			Object[] args = point.getArgs();
			Class<?>[] classes = new Class[args.length];
			for (int k = 0; k < args.length; k++) {
				if (!args[k].getClass().isPrimitive()) {
					// 获取的是封装类型而不是基础类型
					String result = args[k].getClass().getName();
					Class s = map.get(result);
					classes[k] = s == null ? args[k].getClass() : s;
				}
			}
			ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
			// 获取指定的方法，第二个参数可以不传，但是为了防止有重载的现象，还是需要传入参数的类型
			Method method = Class.forName(classType).getMethod(methodName, classes);
			// 参数名
			String[] parameterNames = pnd.getParameterNames(method);
			// 通过map封装参数和参数值
			for (int i = 0; i < parameterNames.length; i++) {
				if (parameterNames[i].equals(regexParams[0])) {
					return (String) args[i];
				}
			}
		}
		//获取到方法参数名称
		//查询参数名称是否有匹配的参数 没有返回null
		Object obj = Arrays.stream(point.getArgs())
				.filter(arg -> arg.getClass().getSimpleName().equalsIgnoreCase(regexParams[0]))
				.findFirst()
				.orElseThrow(() -> new AssemblyCatServiceException("没有匹配的参数值,请检查RedisLock key()"));
		//获取改参数的所有属性 查找下一个符合条件的参数
		//获取参数值 返回
		return getParamsKey(obj, obj.getClass().getDeclaredFields(), regexParams, 1);
	}


	/**
	 * @return java.lang.String
	 * @Author renBo
	 * @Description 动态获取配置的key值
	 * @Date 2023-04-19
	 * @Param [obj, fields, regexParams, i]
	 */
	@SneakyThrows
	public String getParamsKey(Object obj, Field[] fields, String[] regexParams, int i) {
		if (i > regexParams.length) {
			throw new AssemblyCatServiceException("没有匹配的参数值,请检查RedisLock key()");
		}
		//获取游标为i的参数名称
		String paramName = regexParams[i];
		Field field = Arrays.stream(fields)
				.filter(f -> f.getName().equals(paramName))
				.findFirst()
				.orElseThrow(() -> new AssemblyCatServiceException("没有匹配的参数值,请检查RedisLock key()"));
		field.setAccessible(true);
		return i == regexParams.length - 1 ? MD5Str(String.valueOf(field.get(obj))) : getParamsKey(field.get(obj), field.getType().getDeclaredFields(), regexParams, ++i);
	}


	/**
	 * @return java.lang.String
	 * @Author renBo
	 * @Description md5加密 防止key过大
	 * @Date 2023-04-19
	 * @Param [str]
	 */
	public static String MD5Str(String str) {
		try {
			byte[] digest = MessageDigest.getInstance("md5").digest(str.getBytes("utf-8"));
			//16是表示转换为16进制数
			return new BigInteger(1, digest).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("MD5加密出错");
			return str;
		}

	}
}