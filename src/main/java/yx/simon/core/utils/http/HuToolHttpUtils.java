package yx.simon.core.utils.http;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;

import com.aliyuncs.http.MethodType;
import yx.simon.core.vo.ExecuteResult;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;


/**
 * @ClassName HttpUtils
 * @Description
 * @Author renBo
 * @Date 2025/4/15 12:12
 */
@Slf4j
public class HuToolHttpUtils {


    public static ThreadLocal<Boolean> isPrint = ThreadLocal.withInitial(() -> false);



    public static ExecuteResult<JSONObject> doPostExecute(String url) {
        return doExecute(MethodType.POST, url);
    }

    public static ExecuteResult<JSONObject> doGetExecute(String url) {
        return doExecute(MethodType.GET, url);
    }

    public static ExecuteResult<JSONObject> doExecute(MethodType methodType, String url) {
        return doExecute(methodType, url, new JSONObject());
    }

    public static ExecuteResult<JSONObject> doExecute(MethodType methodType, String url, JSONObject params) {
        return doExecute(methodType, url, new HashMap<>(), params);
    }

    public static ExecuteResult<JSONObject> doExecute(MethodType methodType, String baseUrl, Map<String, Object> headers, JSONObject params) {
        StringBuilder[] url = {new StringBuilder(baseUrl)};
        params.entrySet().forEach(obj -> {
            if (url[0].toString().contains("?")) {
                url[0] = url[0].append("&" + obj.getKey() + "=" + obj.getValue());
            } else {
                url[0] = url[0].append("?" + obj.getKey() + "=" + obj.getValue());
            }
        });
        return doExecute(methodType, url[0].toString(), headers);
    }

    public static ExecuteResult<JSONObject> doExecute(MethodType methodType, String url, Map<String, Object> headers) {
        return doExecute(methodType, url, new HashMap<>(), headers);
    }


    public static ExecuteResult<JSONObject> doExecute(MethodType methodType, String url, Map<String, Object> formParams, Map<String, Object> headers) {
        return doExecute(methodType, url, formParams, headers, JSONObject.class);
    }

    /**
     * 执行http请求
     */
    public static <T> ExecuteResult<T> doExecute(MethodType methodType, String url, Map<String, Object> formParams, Map<String, Object> headers, Class<T> tClass) {
        if(isPrint.get()){
            log.warn("执行http请求 method:【{}】 url:【{}】 formParams:【{}】 headers:【{}】  tClass:【{}】 ", methodType, url, formParams, headers, tClass.getName());
        }
        try {
            HttpRequest request;
            switch (methodType) {
                case GET -> request = HttpUtil.createGet(url);
                case POST -> request = HttpUtil.createPost(url);
                default -> throw new IllegalArgumentException("不支持的请求类型: " + methodType);
            }
            //设置请求头和表单数据
            headers.forEach((k, v) -> request.header(k, v.toString()));
            // 设置请求表单参数
            if (!formParams.isEmpty()) {
                request.form(formParams);
            }
            // 执行请求并获取完整响应
            cn.hutool.http.HttpResponse httpResponse = request.execute();
            int statusCode = httpResponse.getStatus();
            String response = httpResponse.body();
            if(isPrint.get()){
                log.warn("请求状态码:【{}】响应结果:【{}】", statusCode, response);
            }
            // 解析响应并转换为目标类型
            JSONObject jsonObject = JSONObject.parseObject(response);
            T data = jsonObject.to(tClass);
            return ExecuteResult.success(statusCode, data, response);
        } catch (Exception e) {
            log.error("HTTP请求执行失败: {}", e.getMessage(), e);
            return ExecuteResult.error(500, e.getMessage(), null);
        }
    }


}