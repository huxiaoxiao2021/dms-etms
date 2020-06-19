package com.jd.bluedragon.distribution.financialForKA.service;

import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.financialForKA.domain.KaCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * 单号校验服务
 *
 * @author: hujiping
 * @date: 2020/2/26 21:58
 */
public interface WaybillCodeCheckService {

    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    PagerResult<WaybillCodeCheckDto> listData(KaCodeCheckCondition condition);

    /**
     * 获取导出数据
     * @param condition
     * @return
     */
    List<List<Object>> getExportData(KaCodeCheckCondition condition);

    /**
     * 单号校验
     * @param condition
     * @return
     */
    InvokeResult waybillCodeCheck(WaybillCodeCheckCondition condition);

    /**
     * 导出申请
     * @param loginUser
     * @param condition
     * @return
     */
     String exportApply(LoginUser loginUser, KaCodeCheckCondition condition);
}
