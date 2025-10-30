package com.exception;


import com.bo.Result;
import com.enums.ErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: renBo.ren
 * @Description:
 * @Data: Created in 17:24 2022/8/1
 */
@Slf4j
@ControllerAdvice
@Order(1)
@ResponseBody
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        String collect = errors.entrySet().stream().map(x -> "值[" + x.getKey().concat("]").concat(x.getValue())).collect(Collectors.joining("|"));
        return Result.error(collect);
    }
    /**
     * 自定义验证异常拦截
     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        return Result.error(e.getBindingResult().getFieldError().getDefaultMessage());
//    }

    /**
     * 自定义服务异常拦截
     */
    @ExceptionHandler(value = AssemblyCatServiceException.class)
    public Result assemblyCatServiceException(AssemblyCatServiceException e) {
        log.error("服务出错：{}", e.getMsg());
        return Result.error(e.getCode(), e.getMsg());
    }

    /**
     * 其余异常拦截
     */
    @ExceptionHandler(value = Exception.class)
    public Result otherException(Exception e) {
        e.printStackTrace();
        log.error("服务出错：{}", e.getMessage());
        return Result.error(ErrorEnum.IS00001.getErrorCode(), e.getMessage());
    }
}