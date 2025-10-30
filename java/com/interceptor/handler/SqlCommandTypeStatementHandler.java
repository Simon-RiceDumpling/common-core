package com.interceptor.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.interceptor.simon_mybatis.SimonInterceptorHandler;
import org.apache.ibatis.plugin.Invocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author RenBo
 * @Date 2024-06-27 17:27
 * @PackageName:com.interceptor.handler
 * @ClassName: SqlCommandTyepHandler
 * @Description: TODO
 * @Version 1.0
 */
public interface SqlCommandTypeStatementHandler extends SqlCommandTypeBaseHandler {

    Object doStatementHandler(Invocation invocation) throws Exception;



}
