package yx.simon.core.utils.http;


import com.alibaba.fastjson2.JSONObject;
import com.aliyuncs.http.MethodType;
import yx.simon.core.vo.ExecuteResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SimonHttpClient {

    /**
     * 函数式HTTP调用
     */
    public static <T> HttpCall<T> call(Class<T> responseClass) {
        return new HttpCall<>(responseClass);
    }

    public static class HttpCall<T> {
        private final Class<T> responseClass;
        private MethodType method = MethodType.GET;
        private String url;
        private Map<String, Object> headers = new HashMap<>();
        private Map<String, Object> params = new HashMap<>();
        private JSONObject body;

        private HttpCall(Class<T> responseClass) {
            this.responseClass = responseClass;
        }

        public HttpCall<T> get(String url) {
            this.method = MethodType.GET;
            this.url = url;
            return this;
        }

        public HttpCall<T> post(String url) {
            this.method = MethodType.POST;
            this.url = url;
            return this;
        }

        public HttpCall<T> header(String key, Object value) {
            this.headers.put(key, value);
            return this;
        }

        public HttpCall<T> param(String key, Object value) {
            this.params.put(key, value);
            return this;
        }

        public HttpCall<T> body(JSONObject body) {
            this.body = body;
            return this;
        }

        public ExecuteResult<T> execute() {
            if (body != null && !body.isEmpty()) {
                Map<String, Object> bodyParams = body.toJavaObject(Map.class);
                return HttpclientUtils.doExecute(method, url, headers, bodyParams, responseClass);
            } else {
                return HttpclientUtils.doExecute(method, url, headers, params, responseClass);
            }
        }
    }

    // 使用示例
    public void example() {
        // 超简洁的调用方式
        ExecuteResult<JSONObject> result = SimonHttpClient.call(JSONObject.class)
                .get("https://api.example.com/data")
                .header("Authorization", "Bearer token")
                .param("id", "123")
                .execute();

        ExecuteResult<JSONObject> postResult = SimonHttpClient.call(JSONObject.class)
                .post("https://api.example.com/submit")
                .header("Content-Type", "application/json")
                .body(new JSONObject().fluentPut("name", "test"))
                .execute();
    }

}