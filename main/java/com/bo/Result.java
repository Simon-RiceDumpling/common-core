package com.bo;


import com.enums.ErrorEnum;
import com.i18n.I18nUtils;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: renBo.ren
 * @Description:
 * @Data: Created in 17:25 2022/8/1
 */
@Getter
@Setter
public class Result<T> {

    private static I18nUtils i18n = new I18nUtils();
    private boolean result;
    private String info;
    private String retCode;
    private T obj;
    @Getter
    @Setter
    private boolean encryption =false;
    public Result() {
    }

    public boolean isResult() {
        return this.result;
    }

    public Result setResult(boolean result) {
        this.result = result;
        return this;
    }

    public Result(boolean result, String retCode, String info) {
        this.result = result;
        this.info = info;
        this.retCode = retCode;
    }

    public Result(boolean result, String retCode, String info, T obj) {
        this.result = result;
        this.info = info;
        this.retCode = retCode;
        this.obj = obj;
    }

    public static <T> Result<T> ok() {
        return new Result(true, "0000", "", (Object) null);
    }

    public static <T> Result<T> ok(T t) {
        return new Result(true, "0000", "", t);
    }

    public static <T> Result<T> error(String errorCode, String info) {
        String errorMsgI18n = i18n.getMessage(errorCode);
        if (StringUtils.isNotBlank(errorMsgI18n)) {
            info = errorMsgI18n;
        }
        return new Result<>(false, errorCode, info, null);

    }

    public static <T> Result<T> error(ErrorEnum errorEnum) {
        return error(errorEnum.getErrorCode(),errorEnum.getValue());
    }

    public static <T> Result<T> error(String errorCode) {
        return error(errorCode,null);
    }
    public static <T> Result<T> error(String errorCode,String errorCode3,String errorCode2) {
        return error(errorCode,null);
    }
    public static <T> Result<T> error(String errorCode,String errorCode3,String errorCode2,String errorCode4) {
        return error(errorCode,null);
    }
}
