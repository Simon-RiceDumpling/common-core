package com.interceptor.handler.impl;

import com.annotation.CommandType;
import com.interceptor.SimonMybatisInterceptor;
import com.interceptor.handler.SqlCommandTypeExecutorHandler;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
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
@CommandType(value = SqlCommandType.SELECT)
public class SelectHandler implements SqlCommandTypeExecutorHandler {

    @Override
    public Object doExecutorHandler(Invocation invocation) throws SQLException {
        //第一个参数永远是MappedStatement
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = args[1];
        Executor executor = (Executor) invocation.getTarget();
        RowBounds rowBounds = (RowBounds) args[2];
        CacheKey cacheKey;
        BoundSql boundSql;
        //由于逻辑关系，只会进入一次
        if (args.length == 4) {
            //4 个参数时
            boundSql = mappedStatement.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }
        handlers.forEach(x -> {
            try {
                x.selectBefore(mappedStatement, parameter, boundSql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //注：下面的方法可以根据自己的逻辑调用多次，在分页插件中，count 和 page 各调用了一次
        return executor.query(mappedStatement, parameter, rowBounds, (ResultHandler) args[3], cacheKey, boundSql);
    }
}
