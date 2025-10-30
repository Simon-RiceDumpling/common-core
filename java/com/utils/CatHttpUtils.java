package com.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.util.Map;


/**
 * @ClassName HttpUtils
 * @Description
 * @Author renBo renbo.ren@cyberklick.com.cn
 * @Date 2022/8/4 17:42
 */
@Slf4j
public class CatHttpUtils {

    /**
     * Post请求调用
     */
    @SneakyThrows
    public static Object doPostExecute(String url, Map<String, Object> params, Class tClass) {
        PostMethod postMethod = new PostMethod();
        JSONObject json = new JSONObject();
        json.putAll(params);
        postMethod.setRequestBody(json.toJSONString());
        return execute(postMethod, url, tClass);
    }

    @SneakyThrows
    public static Object doPostExecute(String url, JSONObject json, Class tClass) {
        PostMethod postMethod = new PostMethod();
        postMethod.setRequestBody(json.toJSONString());
        return execute(postMethod, url, tClass);
    }

    @SneakyThrows
    public static Object doPostExecute(String url, JSONObject params, Map<String, Object> headers, Class tClass) {
        PostMethod postMethod = new PostMethod();
        headers.entrySet().forEach(set -> postMethod.setRequestHeader(set.getKey(), set.getValue().toString()));
        postMethod.setRequestBody(params.toJSONString());
        return execute(postMethod, url, tClass);
    }

    /**
     * Get请求调用
     */
    @SneakyThrows
    public static Object doGetExecuteHeaderParams(String url, Map<String, String> headers, Class tClass) {
        GetMethod getMethod = new GetMethod();
        headers.entrySet().forEach(set -> getMethod.addRequestHeader(set.getKey(), set.getValue()));
        return execute(getMethod, url, tClass);
    }

    @SneakyThrows
    public static Object doGetExecuteRequestParams(String baseUrl, Map<String, String> params, Class tClass) {
        GetMethod getMethod = new GetMethod();
        final StringBuilder[] url = {new StringBuilder(baseUrl)};
        params.entrySet().forEach(obj -> {
            if (url[0].toString().contains("?")) {
                url[0] = url[0].append("&" + obj.getKey() + "=" + obj.getValue());
            } else {
                url[0] = url[0].append("?" + obj.getKey() + "=" + obj.getValue());
            }
        });
        return execute(getMethod, url[0].toString(), tClass);
    }

    /**
     * 执行调用
     */
    /**
     * 执行调用
     */
    @SneakyThrows
    private static Object execute(HttpMethodBase httpMethod, String url, Class tClass) {
        httpMethod.setRequestHeader("Content-Type", "application/json");
        HttpClient httpClient = new HttpClient();
        httpMethod.setPath(url);
        int httpCode = httpClient.executeMethod(httpMethod);
        //响应转码 统一转为utf-8
        JSONObject jsonObject = parseJson(httpMethod);
        jsonObject.put("httpCode", httpCode);
        log.info("url->:【{}】响应结果:【{}】 ", url, jsonObject);
        return JSONObject.parseObject(jsonObject.toString(), tClass);
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
            if (isJson(responseBodyAsString)) {
                return JSON.parseObject(responseBodyAsString);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("responseMsg", responseBodyAsString);
            return jsonObject;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static Boolean isJson(String responseBodyAsString) {
        try {
            JSON.parseObject(responseBodyAsString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}