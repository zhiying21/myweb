package com.zhiying.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应封装
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private long current;
    private long size;
    private long total;
    private List<T> records;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> r = new PageResult<>();
        r.setCurrent(page.getCurrent());
        r.setSize(page.getSize());
        r.setTotal(page.getTotal());
        r.setRecords(page.getRecords());
        return r;
    }
}
