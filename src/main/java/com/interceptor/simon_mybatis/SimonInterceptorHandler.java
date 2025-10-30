package com.interceptor.simon_mybatis;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

import java.sql.Connection;

/**
 * @Author RenBo
 * @Date 2024-06-27 15:05
 * @PackageName:com.interceptor.simon_mybatis
 * @ClassName: SimonInterceptor
 * @Description: TODO
 * @Version 1.0
 */
public interface SimonInterceptorHandler {


    /**
     * 查询前置处理
     */
    default Object selectBefore(MappedStatement ms, Object parameter, BoundSql boundSql) {
        // do nothing
        return parameter;
    }


    /**
     * 查询前置处理
     */
    default Object updateBefore(MappedStatement ms, Object parameter, BoundSql boundSql) throws Exception{
        // do nothing
        return parameter;
    }

    /**
     * 查询前置处理
     */
    default Object insertBefore(MappedStatement ms, Object parameter, BoundSql boundSql) throws Exception {
        // do nothing
        return parameter;
    }

    default Object deleteBefore(MappedStatement ms, Object parameter, BoundSql boundSql) {
        // do nothing
        return parameter;
    }

    default Object beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        return null;
    }


}
