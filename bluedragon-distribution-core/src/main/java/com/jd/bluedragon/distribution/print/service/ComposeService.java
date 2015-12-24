package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.PrintWaybill;

/**
 * Created by wangtingwei on 2015/12/23.
 */
public interface ComposeService {

    /**
     * 合成器
     * @param waybill           运单信息
     * @param dmsCode           始发分拣中心ID
     * @param targetSiteCode    目的站点ID
     */
    void handle(final PrintWaybill waybill,Integer dmsCode,Integer targetSiteCode);
}
