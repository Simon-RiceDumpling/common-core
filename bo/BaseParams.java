package com.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * @Author: renBo.ren
 * @Description:
 * @Data: Created in 15:43 2022/8/2
 */
@Getter
@Setter
@ToString
public class BaseParams implements Serializable {

    protected String id;
    /**
     * 创建时间
     */
    @JsonIgnore
    protected LocalDateTime createTime;
    /**
     * 创建用户姓名
     */
    @JsonIgnore
    protected String createUserName;
    /**
     * 创建用户id
     */
    @JsonIgnore
    protected String createUserId;
    /**
     * 更新时间
     */
    @JsonIgnore
    protected LocalDateTime updateTime;
    /**
     * 更新用户姓名
     */
    @JsonIgnore
    protected String updateUserName;
    /**
     * 更新用户id
     */
    @JsonIgnore
    protected String updateUserId;
    /**
     * 删除状态 初始化为0-未删除 1-删除
     */
    @JsonIgnore
    protected String delStatus;


    /**
     *  初始化数据 自雷实现
     */
    public void init() {
        this.setCreateTime(LocalDateTime.now());
        this.setDelStatus("0");
        this.setCreateUserName("system");
        //do nothing
    }
}
