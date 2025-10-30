package com.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author RenBo
 * @Date 2024-01-02 10:12
 * @PackageName:com.xmd.xiaomuding.project.server.controller.zizhou
 * @ClassName: liveStockDataController
 * @Description: TODO
 * @Version 1.0
 */
public class liveStockDataUtils {

    public static Map<String, String> livestockMap = new HashMap<>();


    static {
        //livestockMap.put("2134", "5425");
    }

    /**
     牧场名称：靖边县珂洋农牧发展有限公司
     farm_id：1734397234109345793
     varietyType：1
     耳标前缀：66609120000000000010     后边加上手写的4位，案例：666091200000000000105481
     测温耳标前缀：23080101     后边加上手写的4位，案例：230801012157
     */

    public static void main(String[] args) {
        for (Map.Entry<String, String> stringStringEntry : livestockMap.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            //测温耳标
            jsonObject.put("tagId", "23080101" + stringStringEntry.getKey());
            //耳标
            jsonObject.put("earNumber", "66609120000000000010" + stringStringEntry.getValue());
            //农场id
            jsonObject.put("farmId", "1734397234109345793");
            //栏id
            jsonObject.put("hurdleId", "1734550125264543745");
            //类型
            jsonObject.put("varietyType", "1");
            //性别
            jsonObject.put("sex", "2");
            jsonObject.put("type", "0");
            jsonObject.put("remarks", "");
            jsonObject.put("source", "1");
            jsonObject.put("status", "0");
            jsonObject.put("livestockType", "1");
            jsonObject.put("orderType", "1");
            jsonObject.put("parityHistory", "0");
            jsonObject.put("breedStatus", "0");
            enter(jsonObject);
        }
    }


    public static void enter(JSONObject jsonObject) {
        try {
            // 创建URL对象
            URL url = new URL("http://vmuyun.com/livestock/livestockInfo/saveLivestockInfo"); // 请将URL替换为您要发送POST请求的API端点
            // 创建HttpURLConnection对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为POST
            connection.setRequestMethod("POST");
            // 设置请求头部信息
            connection.setRequestProperty("Content-Type", "application/json"); // 根据需要设置合适的Content-Type
            connection.setRequestProperty("Authorization", "Bearer 71bdf5ae-8eba-4b09-8c87-6b4f70b9f7e8"); // 添加授权头部信息
            // 启用输入输出
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // 创建POST请求的数据
            String postData = jsonObject.toJSONString(); // 根据API的要求创建要发送的数据
            // 获取连接的输出流
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(postData.getBytes());
            outputStream.flush();
            // 获取响应
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            // 读取响应内容
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            // 输出响应内容
            System.out.println("Response: " + response.toString());
            // 关闭连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
