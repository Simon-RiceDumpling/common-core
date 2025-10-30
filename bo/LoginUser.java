package com.bo;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.utils.IpUtils;
import com.utils.ServletUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 用户信息
 *
 * @author renbo
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名id
     */
    private String userId;

    /**
     * 登录时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;
    /**
     * 用户信息
     */
    private UserInfo userInfo;

    private String token;

    /**
     * @return
     * @Author renBo.ren renbo.ren@cyberklick.com.cn
     * @Description 构建这模式生成 LoginUser对象
     * @Date 17:16 2022/8/15
     * @Param
     **/
    public static LoginUser createLoginUser(UserInfo userInfo, String token) {
        LoginUser loginUser = new LoginUser();
        //用户id
        loginUser.setUserId(userInfo.getUserId());
        loginUser.setLoginTime(LocalDateTime.now());
        //userInfo.setCreateTime(LocalDateTime.now());
        loginUser.setIpaddr(IpUtils.getIpAddr(ServletUtils.getRequest()));
        loginUser.setUserInfo(userInfo);
        loginUser.setToken(token);
        return loginUser;
    }
}
