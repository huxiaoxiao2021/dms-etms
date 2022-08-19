package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.etms.waybill.domain.Waybill;

/**
 * @author liwenji
 * @description 隐藏敏感信息
 * @date 2022-08-18 14:36
 */
public interface HideInfoSmileService {

    /**
     *  对Waybill进行微笑处理，
     * @param waybill
     */
    void setHideInfo( Waybill waybill);
}
