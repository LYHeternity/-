package com.entrepreneur.platform.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装
 */
@Data
@NoArgsConstructor
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<T> records;
    private long total;
    
    public PageResult(List<T> records, long total) {
        this.records = records;
        this.total = total;
    }
}