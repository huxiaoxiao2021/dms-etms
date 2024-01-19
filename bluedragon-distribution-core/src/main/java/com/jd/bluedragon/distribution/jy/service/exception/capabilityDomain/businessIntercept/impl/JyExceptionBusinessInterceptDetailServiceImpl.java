package com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain.businessIntercept.impl;

import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionInterceptDetailDao;
import com.jd.bluedragon.distribution.jy.exception.model.JyExceptionInterceptDetail;
import com.jd.bluedragon.distribution.jy.exception.query.JyExceptionInterceptDetailQuery;
import com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain.businessIntercept.JyExceptionBusinessInterceptDetailService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 异常任务-拦截异常明细服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-19 16:13:00 周五
 */
@Slf4j
@Service("jyExceptionBusinessInterceptDetailService")
public class JyExceptionBusinessInterceptDetailServiceImpl implements JyExceptionBusinessInterceptDetailService {

    @Autowired
    private JyExceptionInterceptDetailDao jyExceptionInterceptDetailDao;

    /**
     * 根据条件查询一个明细
     *
     * @param jyExceptionInterceptDetailQuery 查询入参
     * @return 明细结果
     * @author fanggang7
     * @time 2024-01-19 16:14:57 周五
     */
    @Override
    public Result<JyExceptionInterceptDetail> selectOne(JyExceptionInterceptDetailQuery jyExceptionInterceptDetailQuery) {
        Result<JyExceptionInterceptDetail> result = Result.success();
        try {
            if (jyExceptionInterceptDetailQuery.getSiteId() == null) {
                return result.toFail("参数错误，siteId不能为空");
            }
            final JyExceptionInterceptDetail jyExceptionInterceptDetail = jyExceptionInterceptDetailDao.selectOne(jyExceptionInterceptDetailQuery);
            result.setData(jyExceptionInterceptDetail);
        } catch (Exception e) {
            result.toFail("系统异常");
            log.error("JyExceptionBusinessInterceptDetailServiceImpl.selectOne exception {}", JsonHelper.toJson(jyExceptionInterceptDetailQuery), e);
        }
        return result;
    }
}
