package com.bo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @Author: renBo.ren renbo.ren@cyberklick.com.cn
 * @Description:
 * @Data: Created in 17:42 2022/8/12
 */
@Slf4j
@Getter
@Setter
@ToString(callSuper = true)

public class UserInfo extends BaseParams {
    //@JsonIgnore
    private String id;
    /**
     * 用户唯一Id
     */
    //@JsonIgnore
    private String userId;
    /**
     * 姓名
     */

    private String staffName;
    /**
     * 邮箱
     */

    @NotNull(message = "邮箱【mail】不能为空")
    private String mail;
    /**
     * 手机号
     */

    @NotNull(message = "手机号【phoneNo】不能为空")
    private String phoneNo;
    /**
     * 用户密码
     */

    @NotNull(message = "用户密码【password】不能为空")
    private String password;
    /**
     * 身份编码（0-广告主，1-流量主，2-内部）
     */
    @NotNull(message = "身份编码（0-广告主，1-流量主，2-管理员）【identityNo】不能为空")
    private String identityNo;
    /**
     * 账号状态：0-启用，0-停用
     */
    private String accountStatus;
    /**
     *  主体编码
     */
    private String companyCode;
    /**
     *  秘钥
     */
    private String advertKey;
    /**
     * 账号
     */
    private String userAccount;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime registerTime;
    /**
     * 初始化
     */
    public void init() {
        this.setRegisterTime(LocalDateTime.now());
        this.setAccountStatus("0");
    }

}
