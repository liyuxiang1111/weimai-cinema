package com.liyuxiang.film.config.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageBean<T> implements Serializable {

    private static final long serialVersionUID = 5868659764964018199L;

    private int pc;// 当前页码page code
    //总页数tp：由tr/ps计算可得
    private int tr;// 总记录数total record
    private int ps;// 每页记录数page size
    private List<T> beanList;// 当前页的记录
}

