package com.jd.bluedragon.distribution.abnormal.dao;

import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 * @author wuyoude
 * @ClassName: AbnormalUnknownWaybillDao
 * @Description: 三无订单申请--Dao接口
 * @date 2018年05月08日 15:16:15
 */
public interface AbnormalUnknownWaybillDao extends Dao<AbnormalUnknownWaybill> {

    /**
     * 根据运单号查询 查询哪些运单查过明细
     *
     * @param waybillCodes
     * @return
     */
    public List<String> queryHasDetailWaybillCode(List<String> waybillCodes);

    /**
     * 查最后一次上报
     */
    public AbnormalUnknownWaybill findLastReportByWaybillCode(String waybillCode);

    /**
     * 回写结果
     * @param abnormalUnknownWaybill
     * @return
     */
    public int updateReceive(AbnormalUnknownWaybill abnormalUnknownWaybill);


    /**
     * 根据运单号查询
     * @param waybillCode
     * @return
     */
    public List<AbnormalUnknownWaybill> queryByWaybillCode(String waybillCode);
}
