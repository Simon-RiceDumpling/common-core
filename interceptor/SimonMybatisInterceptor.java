package com.interceptor;

import cn.hutool.extra.spring.SpringUtil;
import com.annotation.CommandType;
import com.interceptor.handler.SqlCommandTypeExecutorHandler;
import com.interceptor.handler.SqlCommandTypeStatementHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Properties;

/**
 * @Author RenBo
 * @Date 2024-06-27 16:33
 * @PackageName:com.interceptor
 * @ClassName: SimonMybstisInterceptor
 * @Description: TODO
 * @Version 1.0
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        //@Signature(type = StatementHandler.class, method = "getBoundSql", args = {}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class SimonMybatisInterceptor implements Interceptor, ApplicationRunner {


    public HashMap<SqlCommandType, SqlCommandTypeExecutorHandler> executorHandlerHashMap = new HashMap<>();
    public HashMap<SqlCommandType, SqlCommandTypeStatementHandler> statementHandlerHashMap = new HashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            if (invocation.getTarget() instanceof Executor) {
                MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
                //执行具体地拦截逻辑
                return executorHandlerHashMap.getOrDefault(mappedStatement.getSqlCommandType(), Invocation::proceed).doExecutorHandler(invocation);
            } else {
                // StatementHandler
                StatementHandler sh = (StatementHandler) invocation.getTarget();
                MetaObject statementHandler = SystemMetaObject.forObject(SystemMetaObject.forObject(sh).getValue("delegate"));
                MappedStatement ms = (MappedStatement) statementHandler.getValue("mappedStatement");
                //处理sql
                return statementHandlerHashMap.getOrDefault(ms.getSqlCommandType(), Invocation::proceed).doStatementHandler(invocation);
            }

        } catch (Exception e) {
            log.error("拦截器执行异常:【{}】", e.getMessage());
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }


    /**
     * 初始化数据
     */
    @Override
    public void run(ApplicationArguments args) {
        SpringUtil.getBeansOfType(SqlCommandTypeExecutorHandler.class).values()
                .stream()
                .filter(x -> x.getClass().isAnnotationPresent(CommandType.class))
                .forEach(x -> {
                    SqlCommandType value = x.getClass().getAnnotation(CommandType.class).value();
                    if (executorHandlerHashMap.containsKey(value)) {
                        throw new RuntimeException("Please check if your SQL parser is duplicated for ExecutorHandler CommandType 【" + value + "】");
                    }
                    executorHandlerHashMap.put(value, x);
                });
        SpringUtil.getBeansOfType(SqlCommandTypeStatementHandler.class).values()
                .stream()
                .filter(x -> x.getClass().isAnnotationPresent(CommandType.class))
                .forEach(x -> {
                    SqlCommandType value = x.getClass().getAnnotation(CommandType.class).value();
                    if (statementHandlerHashMap.containsKey(value)) {
                        throw new RuntimeException("Please check if your SQL parser is duplicated for StatementHandler CommandType 【" + value + "】");
                    }
                    statementHandlerHashMap.put(value, x);
                });
    }
}
