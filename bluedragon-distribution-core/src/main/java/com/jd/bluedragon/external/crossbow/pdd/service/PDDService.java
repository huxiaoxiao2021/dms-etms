package com.jd.bluedragon.external.crossbow.pdd.service;

import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;

/**
 * <p>
 *     拼多多网络对接接阔的service层处理类
 *
 * @author wuzuxiang
 * @since 2019/10/15
 **/
public interface PDDService {

    /**
     * 拼多多面单打印接口的逻辑处理类
     * @param waybillCode 拼多多电子面单号
     * @return 返回拼多多电子面单处理对象
     */
    PDDWaybillDetailDto queryWaybillDetailByWaybillCode(String waybillCode);
}
