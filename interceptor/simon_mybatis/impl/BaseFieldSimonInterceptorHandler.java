package com.interceptor.simon_mybatis.impl;

import com.constants.BaseFieldConstants;
import com.interceptor.simon_mybatis.SimonInterceptorHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @Author RenBo
 * @Date 2024-06-27 15:56
 * @PackageName:com.interceptor.simon_mybatis
 * @ClassName: BaseFieldSimonInterceptor
 * @Description: 基础字段填充拦截器
 * @Version 1.0
 */
@Component
@Slf4j
public class BaseFieldSimonInterceptorHandler implements SimonInterceptorHandler {

    /**
     * @return void
     * @Author RenBo
     * @Description //新增填充
     * @Date 15:57 2024-06-27
     * @Param [org.apache.ibatis.mapping.MappedStatement, java.lang.Object, org.apache.ibatis.mapping.BoundSql]
     */
    @Override
    public Object insertBefore(MappedStatement ms, Object parameter, BoundSql boundSql) throws Exception {
        if (parameter instanceof Map) {
            parameter = doBatchList(parameter, this::processInsert);
        } else {
            processFields(parameter, this::doInsert);
        }
        return parameter;
    }

    /**
     * @return void
     * @Author RenBo
     * @Description //更新填充
     * @Date 15:57 2024-06-27
     * @Param [org.apache.ibatis.mapping.MappedStatement, java.lang.Object, org.apache.ibatis.mapping.BoundSql]
     */
    @Override
    public Object updateBefore(MappedStatement ms, Object parameter, BoundSql boundSql) throws Exception {
        if (parameter instanceof Map) {
            parameter = doBatchList(parameter, this::processUpdate);
        } else {
            processFields(parameter, this::doUpdate);
        }
        return parameter;
    }

    private Object processInsert(Field[] fields, Object parameter) {
        processFields(parameter, this::doInsert);
        return parameter;
    }

    private Object processUpdate(Field[] fields, Object parameter) {
        processFields(parameter, this::doUpdate);
        return parameter;
    }

    private void processFields(Object parameter, BiFunction<Field, Object, Object> processor) {
        Field[] fields = parameter.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            try {
                field.setAccessible(true);
                processor.apply(field, parameter);
            } catch (Exception e) {
                log.error("Error processing field:【{}】error:【{}】", field.getName(), e.getMessage());
            }
        });
    }


    private Object doBatchList(Object parameter, BiFunction<Field[], Object, Object> processor) {
        Map<?, ?> map = (Map<?, ?>) parameter;
        map.replaceAll((key, value) -> {
            if (value instanceof Iterable) {
                ((Iterable<?>) value).forEach(item -> {
                    Field[] fields = item.getClass().getDeclaredFields();
                    processor.apply(fields, item);
                });
            }
            return value;
        });
        return parameter;
    }


    private Object doInsert(Field field, Object parameter) {
        try {
            field.setAccessible(true);
            if (!Objects.isNull(field.get(parameter))) {
                return parameter;
            }
            String fileName = field.getName();
            if (BaseFieldConstants.CREATE_TIME.equals(fileName)) {
                field.set(parameter, LocalDateTime.now());
            }
            if (BaseFieldConstants.CREATE_USER_ID.equals(fileName)) {
                //登录用户id
                field.set(parameter, 66980521L);
            }
            if (BaseFieldConstants.CREATE_USER_NAME.equals(fileName)) {
                field.set(parameter, "bobo");
            }
            if (BaseFieldConstants.UPDATE_USER_ID.equals(fileName)) {
                field.set(parameter, 66980521L);
            }
            if (BaseFieldConstants.UPDATE_USER_NAME.equals(fileName)) {
                field.set(parameter, "bobo");
            }
            if (BaseFieldConstants.UPDATE_TIME.equals(fileName)) {
                field.set(parameter, LocalDateTime.now());
            }
            if (BaseFieldConstants.DEL_STATUS.equals(fileName)) {
                field.set(parameter, 0);
            }
        } catch (Exception e) {
            log.error("Error inserting fields:【{}】", e.getMessage());
        }
        return parameter;
    }

    private Object doUpdate(Field field, Object parameter) {
        try {
            field.setAccessible(true);
            if (!Objects.isNull(field.get(parameter))) {
                return parameter;
            }
            String fileName = field.getName();
            if (BaseFieldConstants.UPDATE_USER_ID.equals(fileName)) {
                field.set(parameter, 66980521L);
            }
            if (BaseFieldConstants.UPDATE_USER_NAME.equals(fileName) ) {
                field.set(parameter, "bobo");
            }
            if (BaseFieldConstants.UPDATE_TIME.equals(fileName)) {
                field.set(parameter, LocalDateTime.now());
            }
        } catch (Exception e) {
            log.error("Error Updating fields:【{}】", e.getMessage());
        }

        return parameter;
    }
}
