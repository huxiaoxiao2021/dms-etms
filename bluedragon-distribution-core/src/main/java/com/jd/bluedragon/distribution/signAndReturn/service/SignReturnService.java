package com.jd.bluedragon.distribution.signAndReturn.service;

import com.jd.bluedragon.distribution.signAndReturn.domain.SignReturnPrintM;
import com.jd.bluedragon.distribution.signReturn.SignReturnCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: SignReturnService
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2018/11/27 18:20
 */
public interface SignReturnService {

    /**
     * 导出
     * @param response
     * @param response
     */
    void toExport(PagerResult<SignReturnPrintM> result, HttpServletResponse response);

    /**
     * 根据运单号获得签单返回打印交接单信息
     * @param condition
     * @return
     */
    PagerResult<SignReturnPrintM> getListByWaybillCode(SignReturnCondition condition);

    /**
     * 新增
     * @param signReturnPrintM
     * @return
     */
    int add(SignReturnPrintM signReturnPrintM);
}
