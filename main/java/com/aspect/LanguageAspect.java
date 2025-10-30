package com.aspect;


import com.annotation.Amount;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

/**
 * @author renBo
 * @ClassName: LogAspect
 * @Description: 拦截日志切面
 * @date 2023-04-19
 */
@Aspect
@Component
@Slf4j
public class LanguageAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @return java.lang.Object
     * @Author renBo
     * @Description 切面逻辑
     * @Date 11:07 2023-04-25
     * @Param [pjp, logAnno]
     */
    @Around("@annotation(logAnno)")
    public Object doSaveMethodName(ProceedingJoinPoint joinPoint, Amount logAnno) throws Throwable {
        Object response = joinPoint.proceed(); // Proceed with the original method call
        // Convert the response to JSON and check if it's JSON format
        JsonNode rootNode = objectMapper.valueToTree(response);
        if (rootNode.isObject()) {
            translateValues((ObjectNode) rootNode);
            return objectMapper.treeToValue(rootNode, Object.class);
        }

        return response;
    }


    private void translateValues(ObjectNode node) {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            JsonNode childNode = entry.getValue();
            if (childNode.isTextual()) {
                String value = childNode.asText();
                // 简单示例：检测中文并替换为英文，这里你可以添加自己的逻辑进行翻译或替换
                if (value.matches(".*[\\u4e00-\\u9fa5]+.*")) {
                    node.put(entry.getKey(), "Some English Text"); // 替换逻辑
                }
            } else if (childNode.isObject()) {
                translateValues((ObjectNode) childNode);
            }
        }
    }
}