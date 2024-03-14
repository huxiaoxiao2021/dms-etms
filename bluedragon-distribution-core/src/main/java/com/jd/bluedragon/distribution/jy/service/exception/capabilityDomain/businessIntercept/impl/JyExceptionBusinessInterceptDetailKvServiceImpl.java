package com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain.businessIntercept.impl;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionInterceptDetailKvDao;
import com.jd.bluedragon.distribution.jy.exception.model.JyExceptionInterceptDetailKv;
import com.jd.bluedragon.distribution.jy.exception.query.JyExceptionInterceptDetailKvQuery;
import com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain.businessIntercept.JyExceptionBusinessInterceptDetailKvService;
import com.jd.dms.java.utils.sdk.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 异常任务-拦截异常明细关系服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-19 16:11:46 周五
 */
@Slf4j
@Service
public class JyExceptionBusinessInterceptDetailKvServiceImpl implements JyExceptionBusinessInterceptDetailKvService {

    @Autowired
    private JyExceptionInterceptDetailKvDao jyExceptionInterceptDetailKvDao;

    /**
     * 根据关键字查询最后一条记录
     *
     * @param keyword 关键字
     * @return 明细结果
     * @author fanggang7
     * @time 2024-01-19 16:14:57 周五
     */
    @Override
    public Result<JyExceptionInterceptDetailKv> getLastOneByKeyword(String keyword) {
        log.info("JyExceptionBusinessInterceptDetailKvServiceImpl.getLastOneByKeyword param {}", keyword);
        Result<JyExceptionInterceptDetailKv> result = Result.success();
        try {
            final JyExceptionInterceptDetailKvQuery jyExceptionInterceptDetailKvQuery = new JyExceptionInterceptDetailKvQuery();
            jyExceptionInterceptDetailKvQuery.setKeyword(keyword);
            final JyExceptionInterceptDetailKv jyExceptionInterceptDetailKvExist = jyExceptionInterceptDetailKvDao.selectLastOneByKeyword(jyExceptionInterceptDetailKvQuery);
            return result.setData(jyExceptionInterceptDetailKvExist);
        } catch (Exception e) {
            result.toFail("查询异常");
            log.error("JyExceptionBusinessInterceptDetailKvServiceImpl.getLastOneByKeyword exception {}", keyword, e);
        }
        return result;
    }

    /**
     * 插入一个不重复的数据
     *
     * @param jyExceptionInterceptDetailKv 待插入数据
     * @return 明细结果
     * @author fanggang7
     * @time 2024-01-19 16:14:57 周五
     */
    @Override
    public Result<Integer> addOneNoRepeat(JyExceptionInterceptDetailKv jyExceptionInterceptDetailKv) {
        log.info("JyExceptionBusinessInterceptDetailKvServiceImpl.getLastOneByKeyword param {}", JsonHelper.toJson(jyExceptionInterceptDetailKv));
        Result<Integer> result = Result.success();
        int insertCount = 0;
        try {
            final JyExceptionInterceptDetailKvQuery jyExceptionInterceptDetailKvQuery = new JyExceptionInterceptDetailKvQuery();
            jyExceptionInterceptDetailKvQuery.setKeyword(jyExceptionInterceptDetailKv.getKeyword());
            final JyExceptionInterceptDetailKv jyExceptionInterceptDetailKvExist = jyExceptionInterceptDetailKvDao.selectOne(jyExceptionInterceptDetailKvQuery);
            if (jyExceptionInterceptDetailKvExist != null) {
                return result.setData(0);
            }
            insertCount = jyExceptionInterceptDetailKvDao.insertSelective(jyExceptionInterceptDetailKv);
        } catch (Exception e) {
            result.toFail("插入异常");
            log.error("JyExceptionBusinessInterceptDetailKvServiceImpl.addOneNoRepeat exception {}", JsonHelper.toJson(jyExceptionInterceptDetailKv), e);
        }
        return result.setData(insertCount);
    }
}
