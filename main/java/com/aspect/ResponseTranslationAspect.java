package com.aspect;

import com.annotation.Amount;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.utils.TextUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(Integer.MAX_VALUE) // 使用最大整数值来确保这个切面尽可能地在最后执行
public class ResponseTranslationAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(logAnno)")
    public Object aroundControllerMethods(ProceedingJoinPoint joinPoint, Amount logAnno) throws Throwable {
        Object response = joinPoint.proceed(); // Continue with the original method call
        // Convert the response to JSON and check if it's JSON format
        JsonNode rootNode = objectMapper.valueToTree(response);
        if (rootNode.isObject()) {
            translateValues((ObjectNode) rootNode);
            return objectMapper.treeToValue(rootNode, Object.class);
        }
        return response;
    }

    private void translateValues(ObjectNode node) {
        node.fields().forEachRemaining(entry -> {
            JsonNode childNode = entry.getValue();
            if (childNode.isTextual()) {
                String value = childNode.asText();
                // 简单示例：检测中文并替换为英文，这里可以添加自己的逻辑进行翻译或替换
                //TODO 替换
                TextUtils.replaceChineseWithEnglish("111");
                if (value.matches(".*[\\u4e00-\\u9fa5]+.*")) {
                    node.put(entry.getKey(), "Some English Text"); // 替换逻辑

                }
            } else if (childNode.isObject()) {
                translateValues((ObjectNode) childNode);
            }
        });
    }
}
