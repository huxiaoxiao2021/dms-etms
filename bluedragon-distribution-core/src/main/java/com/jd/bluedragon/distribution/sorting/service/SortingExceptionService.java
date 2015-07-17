package com.jd.bluedragon.distribution.sorting.service;

import com.jd.bluedragon.distribution.sorting.domain.SortingException;

import java.util.List;

/**
 * 拦截日志服务
 * Created by wangtingwei on 2014/10/21.
 */
public interface SortingExceptionService {

    /**
     * 添加拦截日志
     * @return
     */
    public int add(SortingException domain);

    public List<SortingException> search(String batchCode,Integer siteCode);
}
