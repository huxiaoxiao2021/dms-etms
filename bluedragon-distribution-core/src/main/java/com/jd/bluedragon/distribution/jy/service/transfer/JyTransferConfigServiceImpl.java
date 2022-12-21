package com.jd.bluedragon.distribution.jy.service.transfer;

import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.config.JYTransferSiteEntity;
import com.jd.bluedragon.distribution.jy.service.transfer.manager.JYTransferConfigProxy;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.api.domain.transferDp.ConfigTransferDpSite;
import com.jdl.basic.api.dto.transferDp.ConfigTransferDpSiteMatchQo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.jy.service.transfer
 * @ClassName: JyTransferConfigServiceImpl
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/18 21:26
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service
@Slf4j
public class JyTransferConfigServiceImpl implements JyTransferConfigService{

    @Autowired
    private JYTransferConfigProxy jyTransferConfigProxy;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private SiteService siteService;

    @Override
    public InvokeResult<Boolean> isTransferSite(JYTransferSiteEntity entity) {
        InvokeResult<Boolean> result = new InvokeResult<>();

        if (entity == null || StringHelper.isEmpty(entity.getWaybillCode())) {
            result.parameterError("运单参数为空");
            result.setData(false);
            return result;
        }

        if (entity.getOperateSiteCode() == null || entity.getReceiveSiteCode() == null) {
            result.parameterError("操作站点或目的站点为空");
            result.setData(false);
            return result;
        }

        if (entity.getOperateTime() == null) {
            result.parameterError("操作时间为空");
            result.setData(false);
            return result;
        }

        if (entity.getPreSiteCode() == null || entity.getPreSiteCode() < 0) {
            Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(entity.getWaybillCode());
            if (waybill == null || waybill.getOldSiteId() == null || waybill.getOldSiteId() < 0) {
                result.parameterError("该运单的预分拣站点查询失败");
                result.setData(false);
                return result;
            }
            entity.setWaybillSign(waybill.getWaybillSign());
            entity.setPreSiteCode(waybill.getOldSiteId());
        }

        if (StringHelper.isEmpty(entity.getWaybillSign())) {
            Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(entity.getWaybillCode());
            if (waybill == null || waybill.getOldSiteId() == null || waybill.getOldSiteId() < 0) {
                result.parameterError("查询运单信息失败");
                result.setData(false);
                return result;
            }
            entity.setWaybillSign(waybill.getWaybillSign());
        }

        if (entity.getReceiveSiteType() == null) {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = siteService.getSite(entity.getReceiveSiteCode());
            if (baseStaffSiteOrgDto != null) {
                entity.setReceiveSiteType(baseStaffSiteOrgDto.getSiteType());
            }
        }

        // 1.无预转DP标识，发货至非虚拟网点时不校验
        if (!BusinessHelper.isDPWaybill1(entity.getWaybillSign()) && !Objects.equals(entity.getReceiveSiteType(),110)) {
            result.setData(true);
            return result;
        }

        // 2. 无预转DP标识，发货至虚拟网点时拦截，禁止发货
        if (!BusinessHelper.isDPWaybill1(entity.getWaybillSign()) && Objects.equals(entity.getReceiveSiteType(),110)) {
            result.customMessage(110, "禁止操作");
            return result;
        }

        // 3.有预转DP标识，需要看配置表
        ConfigTransferDpSiteMatchQo siteQo = new ConfigTransferDpSiteMatchQo();
        siteQo.setPreSortSiteCode(entity.getPreSiteCode());

        Result<ConfigTransferDpSite> configTransferDpSiteResult = jyTransferConfigProxy.queryMatchConditionRecord(siteQo);

        if (configTransferDpSiteResult == null || configTransferDpSiteResult.isFail()) {
            result.error("站点转发配置查询失败");
            result.setData(false);
            return result;
        }

        // 4. 未配置，则可以发货
        if (configTransferDpSiteResult.getData() == null) {
            result.setData(true);
            return result;
        }

        // 5. 有配置，超有效期可以发货
        ConfigTransferDpSite configTransferDpSite = configTransferDpSiteResult.getData();
        if (configTransferDpSite.getEffectiveStartTime().after(entity.getOperateTime())
                || configTransferDpSite.getEffectiveStopTime().before(entity.getOperateTime())) {
            result.setData(true);
            return result;
        }

        // 6. 有配置，看目的地和当前场地是否是转发场地
        if (Objects.equals(configTransferDpSite.getHandoverSiteCode(), entity.getOperateSiteCode())) {

            if (!Objects.equals(entity.getReceiveSiteType(),110)) {
                result.confirmMessage("您扫描的" + entity.getWaybillCode() +"订单是转德邦订单，与当前流向不符，请确认是否强发。");
                result.setData(false);
                return result;
            }

            if (Objects.equals(entity.getReceiveSiteType(),110)) {
                result.setData(true);
                return result;
            }
        }
        if (!Objects.equals(configTransferDpSite.getHandoverSiteCode(), entity.getOperateSiteCode())) {

            if (!Objects.equals(entity.getReceiveSiteType(),110)) {
                result.setData(true);
                return result;
            }

            if (Objects.equals(entity.getReceiveSiteType(),110)) {
                result.confirmMessage("您扫描的您扫描的"+ entity.getWaybillCode() +"订单不在本场地转德邦，请确认是否强发");
                result.setData(false);
                return result;
            }
        }
        return result;
    }
}
