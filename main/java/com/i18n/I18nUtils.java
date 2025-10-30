package com.i18n;

import com.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Slf4j
public class I18nUtils {

    private MessageSource messageSource;
    private String basenameCommon = "i18nCommon/messages";
    @Value("${spring.messages.basename}")
    private String basename = "i18n/messages";

    @Value("${spring.messages.cache-seconds}")
    private long cacheMillis = 1;
    @Value("${spring.messages.encoding}")
    private String encoding = "UTF-8";

    /**
     * 初始�?
     *
     * @return
     */
    private MessageSource initMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(new String[]{basename, basenameCommon});
        messageSource.setDefaultEncoding(encoding);
        messageSource.setCacheMillis(cacheMillis);
        return messageSource;
    }

    /**
     * 设置当前的返回信�?
     *
     * @param request
     * @param code
     * @return
     */
    public String getMessage(String code) {
        HttpServletRequest request = WebUtils.getRequest();
        if (messageSource == null) {
            messageSource = initMessageSource();
        }
        String lauage = request.getHeader("Accept-Language");
        // 默认没有就是请求地区的语�?
        Locale locale;
        if (lauage == null) {
            locale = request.getLocale();
        } else if ("en".equals(lauage)) {
            locale = Locale.ENGLISH;
        } else if ("cn".equals(lauage)) {
            locale = Locale.CHINA;
        }
        // 其余的不正确的默认就是本地的语言
        else {
            locale = request.getLocale();
        }
        return getMessage(code, locale);
    }

    private String getMessage(String code, Locale locale) {
        try {
            return messageSource.getMessage(code, null, locale);
        } catch (NoSuchMessageException e) {
            log.error("Cannot find the error message of internationalization, return the original error message.", e);
        }
        return null;
    }

}