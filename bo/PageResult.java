package com.bo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private Integer pageNum;
    private Integer pageSize;
    private Integer total;
    private Integer totalPage;
    private List<T> list;
    private T extObj;
}
