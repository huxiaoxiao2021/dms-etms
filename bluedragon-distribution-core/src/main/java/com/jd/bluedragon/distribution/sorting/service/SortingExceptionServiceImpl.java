package com.jd.bluedragon.distribution.sorting.service;

import com.jd.bluedragon.distribution.sorting.dao.SortingExceptionDao;
import com.jd.bluedragon.distribution.sorting.domain.SortingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangtingwei on 2014/10/21.
 */
@Service("SortingExceptionService")
public class SortingExceptionServiceImpl implements SortingExceptionService {

    private static final Logger log = LoggerFactory.getLogger(SortingExceptionServiceImpl.class);

    @Autowired
    @Qualifier("sortingExceptionDao")
    private SortingExceptionDao sortingExceptionDao;

    /**
     * 插入分拣拦截日志
     * @param domain    分拣对象
     * @return
     */
    @Override
    public int add(SortingException domain) {
        log.info(domain.toString());
        return this.sortingExceptionDao.add(domain);
    }

    /**
     * 查寻单一站点单一波次拦截日志信息
     * @param batchCode
     * @param siteCode
     * @return
     */
    @Override
    public List<SortingException> search(String batchCode, Integer siteCode) {
        return sortingExceptionDao.search(batchCode,siteCode);
    }
}
