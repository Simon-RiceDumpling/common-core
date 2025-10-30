package com.exception;


import com.alibaba.fastjson.JSON;
import com.bo.Result;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author renBo
 * @ClassName: LivestockGlobalExceptionHandlerResolver
 * @Description: 非业务性质抛出异常的处理器
 * @date 2023-04-19 1:21
 */
@Slf4j
//@RestControllerAdvice
public class LivestockGlobalExceptionHandlerResolver {


	/**
	 * @return com.xmd.xiaomuding.common.core.util.R
	 * @Author renBo
	 * @Description 全局异常处理器 处理非业务性质排除异常的报错问题
	 * @Date 9:03 2023-04-19
	 * @Param [e, request]
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Result handleGlobalException(Exception e) {
		return Result.error(StringUtil.isNotEmpty(MethodFindAspect.methodName.get()) ? MethodFindAspect.methodName.get() : "统一失败信息" + "失败");
	}



}
