package com.interceptor.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.interceptor.simon_mybatis.SimonInterceptorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author RenBo
 * @Date 2024-06-27 17:56
 * @PackageName:com.interceptor.handler.impl
 * @ClassName: SqlCommandTypeBaseHandler
 * @Description: TODO
 * @Version 1.0
 */
public interface SqlCommandTypeBaseHandler {
    List<SimonInterceptorHandler> handlers = new ArrayList<>(SpringUtil.getBeansOfType(SimonInterceptorHandler.class).values());

}
