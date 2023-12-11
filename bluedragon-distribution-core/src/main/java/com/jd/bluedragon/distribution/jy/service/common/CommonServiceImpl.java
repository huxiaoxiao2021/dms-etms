package com.jd.bluedragon.distribution.jy.service.common;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.common.BoxNextSiteDto;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/12/11 17:53
 * @Description
 */
public class CommonServiceImpl implements CommonService{
    private static final Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Autowired
    private RouterService routerService;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private DeliveryService deliveryService;

    @Override
    public BoxNextSiteDto getRouteNextSiteByBox(Integer curSiteId, String boxCode) {
        List<String> waybillCodes = deliveryService.getWaybillCodesByBoxCodeAndFetchNum(boxCode, 3);
        // 获取运单对应的路由
        Integer boxRouteNextSiteId = null;
        String key = null;
        if (CollectionUtils.isNotEmpty(waybillCodes)) {
            for (String waybillCode : waybillCodes) {
                RouteNextDto routeNextDto = routerService.matchRouterNextNode(curSiteId, waybillCode);
                if (routeNextDto != null && routeNextDto.getFirstNextSiteId() != null) {
                    boxRouteNextSiteId = routeNextDto.getFirstNextSiteId();
                    key = waybillCode;
                    break;
                }
            }
        }

        if(!Objects.isNull(boxRouteNextSiteId)) {
            BoxNextSiteDto res = new BoxNextSiteDto();
            res.setNextSiteId(boxRouteNextSiteId);
            res.setBoxConfirmNextSiteKey(key);
            res.setBoxCode(boxCode);
            BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(boxRouteNextSiteId);
            if(!Objects.isNull(dto)) {
                res.setNextSiteName(dto.getSiteName());
            }
            return res;
        }
        return null;
    }

    @Override
    public BaseStaffSiteOrgDto getRouteNextSiteByWaybillCode(Integer curSiteId, String waybillCode) {
        InvokeResult<BaseStaffSiteOrgDto> res = new InvokeResult<>();
        RouteNextDto routeNextDto = routerService.matchRouterNextNode(curSiteId, waybillCode);
        Integer nextSiteId = (routeNextDto == null || routeNextDto.getFirstNextSiteId() == null) ? null : routeNextDto.getFirstNextSiteId();
        if(!Objects.isNull(nextSiteId)) {
            return baseMajorManager.getBaseSiteBySiteId(nextSiteId);
        }
        return null;
    }

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
}
