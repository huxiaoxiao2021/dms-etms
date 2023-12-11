package com.jd.bluedragon.distribution.jy.service.common;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.common.BoxNextSiteDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * 一些很多功能都在用，但是每个功能都自己写一份的逻辑抽取一下，方法最小职责原则
 * 尽量不要在这里写业务逻辑，如果有的话，请抽取到对应的service中去
 * @version 1.0

 * @Author zhengchengfa
 * @Date 2023/12/11 17:53
 * @Description
 */
public interface CommonService {

    BoxNextSiteDto getRouteNextSiteByBox(Integer curSiteId, String box);

    /**
     * 根据运单号查询该运单路由的下一流向场地
     * 【可能为空】
     * @param curSiteId
     * @param waybillCode
     * @return
     */
    BaseStaffSiteOrgDto getRouteNextSiteByWaybillCode(Integer curSiteId, String waybillCode);
}
