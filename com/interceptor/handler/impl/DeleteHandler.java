package com.interceptor.handler.impl;

import com.annotation.CommandType;
import com.interceptor.handler.SqlCommandTypeExecutorHandler;
import com.interceptor.simon_mybatis.SimonInterceptorHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * @Author RenBo
 * @Date 2024-06-27 17:28
 * @PackageName:com.interceptor.handler.impl
 * @ClassName: SelectHandler
 * @Description: TODO
 * @Version 1.0
 */
@Component
@CommandType(value = SqlCommandType.DELETE)
@Slf4j
public class DeleteHandler implements SqlCommandTypeExecutorHandler {

    @Override
    public Object doExecutorHandler(Invocation invocation) throws SQLException {
        Executor executor = (Executor) invocation.getTarget();
        Object[] args = invocation.getArgs();
        Object parameter = args[1];
        MappedStatement mappedStatement = (MappedStatement) args[0];
        for (SimonInterceptorHandler handler : handlers) {
            try {
                //便利构建实体类
                parameter = handler.deleteBefore(mappedStatement, parameter, mappedStatement.getBoundSql(parameter));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return executor.update(mappedStatement, parameter);
    }
}
