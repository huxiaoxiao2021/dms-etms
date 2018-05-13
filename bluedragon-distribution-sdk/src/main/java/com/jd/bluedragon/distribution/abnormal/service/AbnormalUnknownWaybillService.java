package com.jd.bluedragon.distribution.abnormal.service;

import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.Service;

/**
 * @author wuyoude
 * @ClassName: AbnormalUnknownWaybillService
 * @Description: 三无订单申请--Service接口
 * @date 2018年05月08日 15:16:15
 */
public interface AbnormalUnknownWaybillService extends Service<AbnormalUnknownWaybill> {

    /**
     * 查询并上报
     *
     * @return
     */
    public JdResponse<String> queryAndReport(AbnormalUnknownWaybill abnormalUnknownWaybill);

    /**
     * 二次上报
     */
    public JdResponse<String> submitAgain(String waybillCode);
}
