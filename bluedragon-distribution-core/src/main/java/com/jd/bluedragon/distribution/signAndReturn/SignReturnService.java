package com.jd.bluedragon.distribution.signAndReturn;

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
     * @param condition
     * @param response
     */
    void toExport(SignReturnCondition condition, HttpServletResponse response);

    PagerResult<SignReturnPrintM> getListByWaybillCode(SignReturnCondition condition);
}
