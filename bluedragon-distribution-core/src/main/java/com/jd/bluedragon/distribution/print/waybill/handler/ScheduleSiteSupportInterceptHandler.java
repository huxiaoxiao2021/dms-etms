package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.dms.BlockerQueryWSJsfManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.service.ScheduleSiteSupportInterceptService;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <P>
 *     返调度预分拣站点为3PL时 判断商家是否支持
 *     预分拣正常时判断黑白名单
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/8
 */
@Service("scheduleSiteSupportInterceptHandler")
public class ScheduleSiteSupportInterceptHandler implements InterceptHandler<WaybillPrintContext,String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleSiteSupportInterceptHandler.class);

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseMinorManager baseMinorManager;

    /* 运单查询 */
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillPrintService waybillPrintService;

    @Autowired
    private BlockerQueryWSJsfManager blockerQueryWSJsfManager;

    @Autowired
    private ScheduleSiteSupportInterceptService scheduleSiteSupportInterceptService;

    @Autowired
    private WaybillService waybillService;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();

        /* 非返调度操作时targetSiteCode为空 */
        if (null == context.getRequest().getTargetSiteCode() || context.getRequest().getTargetSiteCode() <= 0) {
            return result;
        }

        LOGGER.debug("ScheduleSiteSupportInterceptHandler.handle-->现场操作返调度:{}", JsonHelper.toJson(context.getRequest()));

       /* 校验基本参数是否确实 */
        if (null == context.getRequest().getDmsSiteCode() || context.getRequest().getDmsSiteCode() <= 0) {
            LOGGER.error("ScheduleSiteSupportInterceptHandler.handle-->调度分拣中心无效:{}", JsonHelper.toJson(context.getRequest()));
            result.toError(JdResponse.CODE_PARAM_ERROR,"调度分拣中心无效");
            return result;
        }

        String waybillCode = WaybillUtil.getWaybillCode(context.getRequest().getBarCode());
        Waybill waybill = context.getWaybill();

        BaseStaffSiteOrgDto scheduleSiteOrgDto;
        try {
            scheduleSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(context.getRequest().getTargetSiteCode());
            if (null == scheduleSiteOrgDto) {
                LOGGER.error("ScheduleSiteSupportInterceptHandler.handle-->返调度站点信息查询失败{}",context.getRequest().getTargetSiteCode());
                result.toError(JdResponse.CODE_NO_SITE,JdResponse.MESSAGE_NO_SITE);
                return result;
            }

            if (Constants.BASE_SITE_OPERATESTATE.equals(scheduleSiteOrgDto.getOperateState())) {
                LOGGER.error("ScheduleSiteSupportInterceptHandler.handle-->不能预分拣到已经线下运营的站点：{}",context.getRequest().getStartSiteType());
                result.toError(JdResponse.CODE_SITE_OFFLINE_ERROR, JdResponse.MESSAGE_SITE_OFFLINE_ERROR);
                return result;
            }

            Integer localScheduleSiteType = scheduleSiteOrgDto.getSiteType();
            if (Constants.THIRD_SITE_TYPE.equals(localScheduleSiteType)) {
                /* 属于3pl站点,商家不支持三方时给出提示 */
                BasicTraderInfoDTO traderDto = baseMinorManager.getBaseTraderById(context.getResponse().getBusiId());
                if (traderDto != null && !BusinessHelper.canThreePLSchedule(traderDto.getTraderSign())) {
                    LOGGER.warn("ScheduleSiteSupportInterceptHandler.handle-->商家不支持转3方配送，返调度到3方站点失败：{}"
                            ,JsonHelper.toJson(context.getRequest()));
                    result.toError(JdResponse.CODE_THREEPL_SCHEDULE_ERROR, JdResponse.MESSAGE_THREEPL_SCHEDULE_ERROR);
                    return result;
                }
            }

            // 站点类型不能操作得物类型货物返调度
            final boolean matchTerminalSiteReSortDewuCondition = waybillService.matchTerminalSiteReSortDewuCondition(context.getBigWaybillDto().getWaybill().getCustomerCode(), context.getRequest().getSiteCode());
            if(matchTerminalSiteReSortDewuCondition){
                result.toError(InvokeResult.RESULT_INTERCEPT_CODE, HintService.getHint(HintCodeConstants.TERMIANL_RE_SORT_DEWU_FORBID));
                return result;
            }

            // 特殊品类自营逆向单不能返调度到仓
            if(BusinessUtil.isSelfReverse(waybill.getWaybillSign()) && BusinessHelper.isSpecialOrder(waybill.getSendPayMap())
                    && BusinessUtil.isWmsSite(scheduleSiteOrgDto.getSiteType())){
                result.toError(JdResponse.CODE_SELF_REVERSE_SCHEDULE_ERROR, JdResponse.MESSAGE_SELF_REVERSE_SCHEDULE_ERROR);
                return result;
            }

            if(waybillPrintService.isCodMoneyGtZeroAndSiteThird(scheduleSiteOrgDto.getSiteType(),scheduleSiteOrgDto.getSubType()
                    ,waybill.getCodMoney())){
                LOGGER.warn("codMoney大于0不能分配三方站点waybillCode[{}]codMoney[{}]",waybillCode,String.valueOf(waybill.getCodMoney()));
                result.toError(JdResponse.CODE_CODMONAY_THIRD_SITE_ERROR, JdResponse.MESSAGE_CODMONAY_THIRD_SITE_ERROR);
                return result;
            }

            /* 根据运单打标和预分拣站点判断是否需要进行黑名单校验 :逆向退仓判断 */
            if (BusinessHelper.isReverseToStore(context.getResponse().getWaybillSign())) {
                Integer cky2 = null;
                Integer storeId = null;
                // 是否逆向换单运单号
                if (WaybillUtil.isSwitchCode(waybillCode)) {
                    WaybillManageDomain oldWaybillState = this.getOldWaybillStateByReturnWaybillCode(waybillCode);
                    if (oldWaybillState != null) {
                        cky2 = oldWaybillState.getCky2();
                        storeId = oldWaybillState.getStoreId();
                    }
                } else {
                    cky2 = context.getResponse().getCky2();
                    storeId = context.getResponse().getStoreId();
                }
                if (this.doValidateBlackList(cky2, storeId)) {
                    BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(context.getResponse().getPrepareSiteCode());
                    if (siteOrgDto != null && BusinessHelper.isWms(siteOrgDto.getSiteType())) {
                        LOGGER.warn("ScheduleSiteSupportInterceptHandler.handler-->此运单禁止更改逆向目的仓，必须按照出库原仓退回!{}",
                                JsonHelper.toJson(context.getRequest()));
                        result.toError(JdResponse.CODE_STORE_BLACKLIST_ERROR, JdResponse.MESSAGE_STORE_BLACKLIST_ERROR);
                        return result;
                    } else {
                        // 操作现场预分拣新站点只能选择站点类型不能选仓，若选择仓则给出相应提示
                        if (BusinessHelper.isWms(localScheduleSiteType)) {
                            LOGGER.warn("ScheduleSiteSupportInterceptHandler.handler-->此运单禁止更改逆向目的仓，必须按照出库原仓退回!{}",
                                    JsonHelper.toJson(context.getRequest()));
                            result.toError(JdResponse.CODE_SITE_BLACKLIST_ERROR, JdResponse.MESSAGE_SITE_BLACKLIST_ERROR);
                            return result;
                        }
                    }
                }
            }

            //规则4-已退款的禁止 操作现场预分拣
            JdCResponse jdCResponse = blockerQueryWSJsfManager.queryExceptionOrders(waybillCode);
            if(!jdCResponse.getCode().equals(JdCResponse.CODE_SUCCESS)){
                result.toError(InvokeResult.RESULT_INTERCEPT_CODE,jdCResponse.getMessage());
                return result;
            }

            //自营逆向单（waybill_sign第一位=T），且为全球购订单（sendPay第8位 = 6），禁止反调度到普通库房「类型为wms」
            if(BusinessUtil.isReverseGlobalWaybill(waybill.getWaybillSign(), waybill.getSendPay())){
                if(BusinessUtil.isWmsSite(scheduleSiteOrgDto.getSiteType())){
                    result.toError(InvokeResult.RESULT_INTERCEPT_CODE, JdResponse.MESSAGE_SELF_REVERSE_SCHEDULE_ERROR);
                    return result;
                }
            }

            //针对运费到付「waybillSign第25位=2」的运单，禁止反调度到三方网点「同cod限制逻辑，siteType = 16」
            if(BusinessUtil.isDF(waybill.getWaybillSign())){
                if(Objects.equals(Constants.THIRD_SITE_TYPE, scheduleSiteOrgDto.getSiteType())){
                    result.toError(InvokeResult.RESULT_INTERCEPT_CODE, JdResponse.MESSAGE_FORBIDDEN_SCHEDULE_TO_PARTNER_SITE);
                    return result;
                }
            }

            //校验预分拣归属滑道信息
            InvokeResult<String> crossResult = scheduleSiteSupportInterceptService.checkCrossInfo(waybill.getWaybillSign(),waybill.getSendPay(),waybillCode,
                    context.getRequest().getTargetSiteCode(),context.getRequest().getDmsSiteCode());
            if(!crossResult.codeSuccess()){
                LOGGER.warn("ScheduleSiteSupportInterceptHandler.handler 预分拣站点滑道信息为空,targetSiteCode:{},dmsSiteCode:{},waybillCode:{}",
                        context.getRequest().getTargetSiteCode(),context.getRequest().getDmsSiteCode(),waybillCode);
                result.toWeakSuccess(crossResult.getCode(),crossResult.getMessage());
                return result;
            }

        } catch (Exception e) {
            LOGGER.error("Support3PLInterceptHandler.handle-->现场预分拣获取返调度目的地信息出错：{}" ,JsonHelper.toJson(context.getRequest()), e);
            result.toError(JdResponse.CODE_SERVICE_ERROR, "查询返调度目的地信息失败!");
            return result;
        }
        return result;
    }

    /**
     * 通过返单运单号获取旧单的运单中waybillState对象
     *
     * @param waybillCode
     * @return
     */
    private WaybillManageDomain getOldWaybillStateByReturnWaybillCode(String waybillCode) {
        BaseEntity<com.jd.etms.waybill.domain.Waybill> oldBaseWaybill = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
        if (oldBaseWaybill.getResultCode() == 1 && oldBaseWaybill.getData() != null) {
            BaseEntity<BigWaybillDto> oldBaseEntity = waybillQueryManager.getDataByChoice(oldBaseWaybill.getData().getWaybillCode(), false, false, true, false);
            if (oldBaseEntity.getResultCode() == 1 && oldBaseEntity.getData() != null && oldBaseEntity.getData().getWaybillState() != null) {
                return oldBaseEntity.getData().getWaybillState();
            }
        }
        return null;
    }

    /**
     * 调用运单接口进黑名单校验
     *
     * @param cky2
     * @param storeId
     * @return
     */
    private boolean doValidateBlackList(Integer cky2, Integer storeId) throws Exception {
        // 调用运单接口
        if (cky2 != null && storeId != null) {
            try {
                // 如果接口调用成功且是强制换单返回true，否则返回false
                Boolean result = waybillQueryManager.ifForceCheckByWarehouse(cky2, storeId);
                if (result != null) {
                    return result;
                }
            } catch (Exception e){
                LOGGER.error("调用运单JSF接口-根据配送中心ID和仓ID查询是否强制换单-发生异常", e);
                throw e;
            }
        }
        return false;
    }

}
