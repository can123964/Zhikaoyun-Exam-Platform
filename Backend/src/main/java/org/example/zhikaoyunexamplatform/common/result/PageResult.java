package org.example.zhikaoyunexamplatform.common.result;

import lombok.Data;

import java.util.List;

/**
 * 分页结果封装
 *
 * Service 层查询完数据后，用 PageResult.of() 快速构建：
 *   int total = mapper.countByConditions(...);
 *   List<Xxx> list = mapper.selectPage(..., offset, size);
 *   return PageResult.of((long) total, pageNum, size, list);
 */
@Data
public class PageResult<T> {

    private Long total;
    private Integer pages;
    private Integer current;
    private Integer size;
    private List<T> records;

    /** 快速构建分页结果 */
    public static <T> PageResult<T> of(Long total, Integer current, Integer size, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPages(total == 0 ? 0 : (int) Math.ceil((double) total / size));
        result.setCurrent(current);
        result.setSize(size);
        result.setRecords(records);
        return result;
    }
}
