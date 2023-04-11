package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 先到先动增值服务换单打印允许操作站点校验
 *
 * @author fanggang7
 * @time 2023-04-10 11:20:50 周一
 */
@Service("halfSendExchangeNewWaybillCheckSiteHandler")
public class HalfSendExchangeNewWaybillCheckSiteHandler implements InterceptHandler<WaybillPrintContext,String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HalfSendExchangeNewWaybillCheckSiteHandler.class);

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WaybillService waybillService;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();

        final WaybillPrintRequest waybillPrintRequest = context.getRequest();
        final Integer operateType = waybillPrintRequest.getOperateType();
        // 只有站长工作台换单打印才校验
        if(!Objects.equals(WaybillPrintOperateTypeEnum.SITE_MASTER_REVERSE_CHANGE_PRINT, operateType)){
            return result;
        }
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(waybillPrintRequest.getSiteCode());
        // 7. 分批配送运单，不允许在分拣场地操作换单
        if (!BusinessUtil.isKySite(siteInfo.getSiteType(), siteInfo.getSubType())) {
            String oldBarCode = context.getRequest().getOldBarCode();/* 获取输入旧单号 */
            String oldWaybillCode = WaybillUtil.getWaybillCode(oldBarCode);/* 获取旧运单号 */
            final Result<Boolean> isDeliveryManyBatchResult = waybillService.checkIsDeliveryManyBatch(oldWaybillCode);
            if(isDeliveryManyBatchResult.isFail()){
                result.toError(JdResponse.CODE_WRONG_STATUS, isDeliveryManyBatchResult.getMessage());
                return result;
            }
            if (isDeliveryManyBatchResult.getData()) {
                result.toError(JdResponse.CODE_HALF_SEND_NOT_ALLOW_CHANGE_NEW_WAYBILL, JdResponse.MESSAGE_HALF_SEND_NOT_ALLOW_CHANGE_NEW_WAYBILL);
                return result;
            }
        }
        return result;
    }
}
