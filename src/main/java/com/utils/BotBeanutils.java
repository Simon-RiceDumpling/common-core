package com.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @program: atlas_oversea_micro_services
 * @description: TODO
 * @author: renBo
 * @create: 2025-05-13 17:30
 **/
public class BotBeanutils extends BeanUtils {

    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = new HashMap<>();
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new HashMap<>();

    static {
        PRIMITIVE_DEFAULTS.put(boolean.class, false);
        PRIMITIVE_DEFAULTS.put(byte.class, (byte) 0);
        PRIMITIVE_DEFAULTS.put(short.class, (short) 0);
        PRIMITIVE_DEFAULTS.put(int.class, 0);
        PRIMITIVE_DEFAULTS.put(long.class, 0L);
        PRIMITIVE_DEFAULTS.put(float.class, 0f);
        PRIMITIVE_DEFAULTS.put(double.class, 0d);
        PRIMITIVE_DEFAULTS.put(char.class, '\u0000');

        PRIMITIVE_WRAPPER_MAP.put(boolean.class, Boolean.class);
        PRIMITIVE_WRAPPER_MAP.put(byte.class, Byte.class);
        PRIMITIVE_WRAPPER_MAP.put(short.class, Short.class);
        PRIMITIVE_WRAPPER_MAP.put(int.class, Integer.class);
        PRIMITIVE_WRAPPER_MAP.put(long.class, Long.class);
        PRIMITIVE_WRAPPER_MAP.put(float.class, Float.class);
        PRIMITIVE_WRAPPER_MAP.put(double.class, Double.class);
        PRIMITIVE_WRAPPER_MAP.put(char.class, Character.class);
    }


    public static void copyNotNullProperties(Object source, Object target, String[] ignoreProperties) throws BeansException {
        copyNotNullProperties(source, target, null, ignoreProperties);
    }

    public static void copyNotNullProperties(Object source, Object target, Class<?> editable) throws BeansException {
        copyNotNullProperties(source, target, editable, null);
    }


    public static <T, E> T convertBean(E source, Class<T> targetClazz) {
        if (null == source) {
            return null;
        }
        T t = null;
        try {
            t = targetClazz.newInstance();
            BeanUtils.copyProperties(source, t);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 创建目标对象并拷贝 source 中非 null 的属性
     */
    public static <T> T copyNotNullProperties(Object source, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            copyNotNullProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("对象拷贝失败", e);
        }
    }

    /**
     * 拷贝非 null 属性（包装类 null → 基础类型默认值）
     */
    public static void copyNotNullProperties(Object source, Object target) throws BeansException {
        String[] ignoreProperties = Arrays.stream(source.getClass().getDeclaredFields()).filter(field -> {
            field.setAccessible(true);
            try {
                return Objects.isNull(field.get(source));
            } catch (IllegalAccessException e) {
                return true;
            }
        }).map(Field::getName).toArray(String[]::new);

        copyNotNullProperties(source, target, null, ignoreProperties);
    }

    private static void copyNotNullProperties(Object source, Object target, Class<?> editable, String[] ignoreProperties) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() + "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

        for (PropertyDescriptor targetPd : targetPds) {
            if (targetPd.getWriteMethod() != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null && sourcePd.getReadMethod() != null) {
                    try {
                        Method readMethod = sourcePd.getReadMethod();
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }
                        Object value = readMethod.invoke(source);

                        // 空集合/空Map 跳过
                        if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
                            continue;
                        } else if (value instanceof Map && ((Map<?, ?>) value).isEmpty()) {
                            continue;
                        }

                        // 获取目标写入方法
                        Method writeMethod = targetPd.getWriteMethod();
                        if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                            writeMethod.setAccessible(true);
                        }

                        Class<?> paramType = writeMethod.getParameterTypes()[0];

                        // 包装类为 null 且目标是基本类型，赋默认值
                        if (value == null && paramType.isPrimitive()) {
                            value = PRIMITIVE_DEFAULTS.get(paramType);
                        }

                        // 类型兼容性检查和自动类型转换（支持 JSONObject → String）
                        if (value != null) {
                            Class<?> valueClass = value.getClass();
                            boolean compatible = paramType.isAssignableFrom(valueClass);

                            if (!compatible && paramType == String.class) {
                                // 自动转为 JSON 字符串
                                value = value.toString();
                                compatible = true;
                            } else if (!compatible && paramType.isPrimitive()) {
                                Class<?> wrapper = PRIMITIVE_WRAPPER_MAP.get(paramType);
                                compatible = wrapper != null && wrapper.isAssignableFrom(valueClass);
                            }
                            if (!compatible) {
                                throw new IllegalArgumentException("Cannot assign value of type [" +
                                        valueClass.getName() + "] to property [" + targetPd.getName() +
                                        "] of type [" + paramType.getName() + "]");
                            }
                        }


                        writeMethod.invoke(target, value);
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        throw new FatalBeanException("Could not copy properties from source to target", ex);
                    }
                }
            }
        }
    }

    /**
     * 复制集合
     *
     * @param <E>
     * @param source
     * @param destinationClass
     * @return
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <E> List<E> copyToList(List<?> source, Class<E> destinationClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (source.size() == 0) return Collections.emptyList();
        List<E> res = new ArrayList<E>(source.size());
        for (Object o : source) {
            E e = destinationClass.newInstance();
            copyNotNullProperties(o, e);
            //BeanUtils.copyProperties(o, e);
            res.add(e);
        }
        return res;
    }

    /**
     * 设置对象中指定属性的值。
     *
     * @param target 对象
     * @param field  属性
     * @param value  值
     */
    public static void setField(Object target, Field field, Object value) {
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(target, value);
            field.setAccessible(accessible);
        } catch (Exception e) {
            throw new IllegalStateException("设置对象的属性[" + field.getName()
                    + "]值失败", e);
        }
    }


    /**
     * 处理Hibernate懒加载属性。
     *
     * @param fieldValue 属性值
     * @return 如果是Hibernate懒加载属性则执行代理方法返回实际的属性对象，否则直接返回。
     */
    private static Object processHibernateLazyField(Object fieldValue) {
        try {
            Class<?> hibernateProxyClass = Class
                    .forName("org.hibernate.proxy.HibernateProxy");
            if (hibernateProxyClass.isAssignableFrom(fieldValue.getClass())) {
                Method method = fieldValue.getClass().getMethod(
                        "getHibernateLazyInitializer");
                Object lazyInitializer = method.invoke(fieldValue);
                method = lazyInitializer.getClass().getMethod(
                        "getImplementation");
                return method.invoke(lazyInitializer);
            } else {
                return fieldValue;
            }
        } catch (Exception e) {
            return fieldValue;
        }
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> toMap(Object bean) {
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.convertValue(bean, Map.class);
    }
}
