package com.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * 框架统一使用的分页
 * @author : admin
 * @description :
 */
@Data

public class PageQuery implements Serializable {
	
	//@Schema(description = "当前页码", example = "1",defaultValue = "1",required = true)
	private Integer current = 1;
	
	//@Schema(description = "每页数量", example = "10",defaultValue = "10",required = true)
	private Integer size = 10;

}
