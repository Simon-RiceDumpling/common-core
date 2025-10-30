package com.utils.http;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.atlas.framework.core.enums.MethodType;
import com.atlas.framework.core.vo.ExecuteResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: atlas_oversea_micro_services
 * @description: TODO
 * @author: renBo
 * @create: 2025-07-24 11:16
 **/
@Slf4j
public class HttpclientUtils {

    @SneakyThrows
    public static ExecuteResult<JSONObject> doPostExecute(String url) {
        return doExecute(MethodType.POST, url, new HashMap<>());
    }

    @SneakyThrows
    public static ExecuteResult<JSONObject> doGetExecute(String url) {
        return doExecute(MethodType.GET, url, new HashMap<>());
    }

    @SneakyThrows
    public static ExecuteResult<JSONObject> doExecute(MethodType httpMethod, String url) {
        return doExecute(httpMethod, url, new HashMap<>());
    }

    @SneakyThrows
    public static ExecuteResult<JSONObject> doExecute(MethodType httpMethod, String url, Map<String, Object> requestParams) {
        return doExecute(httpMethod, url, requestParams, JSONObject.class);
    }

    @SneakyThrows
    public static <T> ExecuteResult<T> doExecute(MethodType httpMethod, String url, Map<String, Object> requestParams, Class<T> tClass) {
        return doExecute(httpMethod, url, new HashMap<>(), requestParams, tClass);
    }


    @SneakyThrows
    public static <T> ExecuteResult<T> doExecute(MethodType methodType, String baseUrl, Map<String, Object> headers, Map<String, Object> requestParams, Class<T> tClass) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (requestParams == null) {
            requestParams = new HashMap<>();
        }
        HttpMethodBase httpMethod;
        switch (methodType) {
            case GET -> httpMethod = new GetMethod();
            case POST -> httpMethod = new PostMethod();
            default -> throw new IllegalArgumentException("不支持的请求类型: " + methodType);
        }
        final StringBuilder[] url = {new StringBuilder(baseUrl)};
        headers.forEach((key, value) -> httpMethod.setRequestHeader(key, value.toString()));
        requestParams.forEach((key, value) -> {
            if (url[0].toString().contains("?")) {
                url[0] = url[0].append("&" + key + "=" + value);
            } else {
                url[0] = url[0].append("?" + key + "=" + value);
            }
        });
        return execute(httpMethod, url[0].toString(), tClass);
    }

    /**
     * 执行调用
     */
    @SneakyThrows
    private static <T> ExecuteResult<T> execute(HttpMethodBase httpMethod, String url, Class<T> tClass) {
        try {
            httpMethod.setRequestHeader("Content-Type", "application/json");
            HttpClient httpClient = new HttpClient();
            httpMethod.setPath(url);
            int httpCode = httpClient.executeMethod(httpMethod);
            //响应转码 统一转为utf-8
            JSONObject jsonObject = parseJson(httpMethod);
            String rawResponse = jsonObject.toString();
            log.info("url->:【{}】响应结果:【{}】 ", url, jsonObject.toString().length() > 1000 ? httpCode : jsonObject);
            // 转换为目标类型
            T data = JSONObject.parseObject(rawResponse, tClass);
            return ExecuteResult.success(httpCode, data, rawResponse);
        } catch (Exception e) {
            log.error("HTTP请求执行失败: {}", e.getMessage(), e);
            return ExecuteResult.error(500, e.getMessage(), null);
        }
    }


    /**
     * 解析数据
     */
    public static JSONObject parseJson(HttpMethodBase httpMethod) {
        try {
            String responseBodyAsString = httpMethod.getResponseBodyAsString();
            if (responseBodyAsString.equals(new String(responseBodyAsString.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
                responseBodyAsString = new String(responseBodyAsString.getBytes("ISO-8859-1"), "utf-8");
            }
            if (StringUtil.isNotBlank(responseBodyAsString) && isJson(responseBodyAsString)) {
                return JSONObject.from(JSON.parseObject(responseBodyAsString));
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("responseMsg", responseBodyAsString);
            jsonObject.put("isJson", Boolean.FALSE);
            return jsonObject;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static Boolean isJson(String responseBodyAsString) {
        try {
            JSONObject.from(JSON.parseObject(responseBodyAsString));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
