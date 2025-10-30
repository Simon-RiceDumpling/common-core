package com.interceptor.handler;

import org.apache.ibatis.plugin.Invocation;

/**
 * @Author RenBo
 * @Date 2024-06-27 17:27
 * @PackageName:com.interceptor.handler
 * @ClassName: SqlCommandTyepHandler
 * @Description: TODO
 * @Version 1.0
 */
public interface SqlCommandTypeExecutorHandler extends SqlCommandTypeBaseHandler {

    Object doExecutorHandler(Invocation invocation) throws Exception;



}
