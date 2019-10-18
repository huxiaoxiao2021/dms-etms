package com.jd.bluedragon.distribution.external.pdd;


import com.jd.bluedragon.distribution.external.pdd.domain.PDDWaybillPrintInfoDto;
import com.jd.bluedragon.distribution.external.pdd.domain.PDDWaybillPrintInfoRequest;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;

/**
 * <p>
 *     拼多多网络接口服务类
 *
 * <doc>
 *     拼多多的外部接口对接是通过架构部的crossBow组件进行的代理，对京东内部来说，我们都通过crossBow的组件来调用外部接口，
 *     crossBow组件此时相当于对外部接口进行了一次代理，屏蔽了外部接口的接口协议和网络权限的差异性
 * </doc>
 *
 * @author wuzuxiang
 * @since 2019/10/14
 **/
public interface DMSExternalInPDDService {

    /**
     * 根据拼多多的电子面单号获取拼多多的运单详细信息
     * @param waybillCode 电子面单号
     * @return 返回拼多多运单信息
     */
    @Deprecated
    BaseEntity<PDDWaybillPrintInfoDto> queryPDDWaybillByWaybillCode(String waybillCode);

    /**
     * 根据拼多多的电子面单号获取拼多多的运单详细信息
     * @param request 电子面单号 和 系统标识
     * @return 返回拼多多运单信息
     */
    BaseEntity<PDDWaybillPrintInfoDto> queryWaybillByWaybillCode(PDDWaybillPrintInfoRequest request);
}
