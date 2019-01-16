package com.jd.bluedragon.distribution.abnormal.service;

import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybillCondition;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

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
    public JdResponse<String> queryAndReport(AbnormalUnknownWaybill abnormalUnknownWaybill, LoginUser loginUser);

    /**
     * 二次上报
     */
    public JdResponse<String> submitAgain(String waybillCode);

    /**
     * 查最后一次上报
     */
    public AbnormalUnknownWaybill findLastReportByWaybillCode(String waybillCode);

    /**
     * 回写结果
     *
     * @param abnormalUnknownWaybill
     * @return
     */
    public int updateReceive(AbnormalUnknownWaybill abnormalUnknownWaybill);

    /**
     * 整理导出数据
     *
     * @param arBookingSpaceCondition
     * @return
     */
    public List<List<Object>> getExportData(AbnormalUnknownWaybillCondition arBookingSpaceCondition);

    /**
     * 加载数据
     * @param abnormalUnknownWaybillCondition
     * @return
     */
    public PagerResult<AbnormalUnknownWaybill> queryByPagerCondition(AbnormalUnknownWaybillCondition abnormalUnknownWaybillCondition);

    /**
     * 根据运单号查询
     * @param waybillCodes
     * @return
     */
    public JdResponse<String> queryByWaybillCode(List<String> waybillCodes);
}
