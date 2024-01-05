package com.jd.bluedragon.distribution.capability.send.handler.verify;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.delivery.constants.SendKeyTypeEnum;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/30
 * @Description:
 *
 *      老校验链处理转换逻辑，当校验逻辑为分拣和发货通用时则使用此逻辑进行处理
 *
 *      目前只有按包裹和按箱维度处理逻辑，其余场景直接跳过不处理
 */
@Service
public class SendOldChainVerifyHandler extends SendDimensionStrategyHandler {

    Logger logger = LoggerFactory.getLogger(SendOldChainVerifyHandler.class);

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private SortingCheckService sortingCheckService;

    @Autowired
    private SiteService siteService;

    /**
     * 通用逻辑处理
     * @param context
     * @return
     */
    @Override
    public boolean doHandler(SendOfCAContext context) {
        if(context.getRequest().getIsForceSend()){
            //强制提交直接返回
            return true;
        }
        return super.doHandler(context);
    }

    /**
     * 按包裹处理逻辑
     * @param context
     * @return
     */
    @Override
    public boolean doPackHandler(SendOfCAContext context) {
        return packOrWaybillHandler(context);
    }

    /**
     * 按运单处理逻辑
     * @param context
     * @return
     */
    @Override
    public boolean doWaybillHandler(SendOfCAContext context) {
        return packOrWaybillHandler(context);
    }

    /**
     * 按箱处理逻辑
     * @param context
     * @return
     */
    @Override
    public boolean doBoxHandler(SendOfCAContext context) {
        if(siteService.getCRouterAllowedList().contains(context.getRequestTurnToSendM().getCreateSiteCode())){
            // 按箱发货，从箱中取出一单校验
            DeliveryResponse response = deliveryService.checkRouterForCBox(context.getRequestTurnToSendM());
            if (DeliveryResponse.CODE_CROUTER_ERROR.equals(response.getCode())) {
                context.getResponse().getData().init(SendResult.CODE_CONFIRM, response.getMessage(), response.getCode(), null);
                return false;
            }
        }
        return true;
    }

    /**
     * 按板处理逻辑
     * @param context
     * @return
     */
    @Override
    public boolean doBoardHandler(SendOfCAContext context) {
        SortingCheck sortingCheck = deliveryService.getSortingCheck(context.getRequestTurnToSendM());
        sortingCheck.setBoard(context.getBoard());
        //加载按板处理校验链
        SortingJsfResponse response = sortingCheckService.doSingleSendCheckWithChain(sortingCheck,Boolean.TRUE,
                sortingCheckService.matchJyDeliveryFilterChain(SendKeyTypeEnum.BY_BOARD));
        if (!response.getCode().equals(JdResponse.CODE_OK)) {
            if (response.getCode() >= SendResult.RESPONSE_CODE_MAPPING_CONFIRM) {
                context.getResponse().getData().init(SendResult.CODE_CONFIRM, response.getMessage(), response.getCode(), Constants.NUMBER_ZERO);
            } else {
                context.getResponse().getData().init(SendResult.CODE_SENDED, response.getMessage(), response.getCode(), Constants.NUMBER_ZERO);
            }
            return false;
        }
        return true;
    }

    /**
     * 运单和包裹通用校验链
     * @param context
     * @return
     */
    private boolean packOrWaybillHandler(SendOfCAContext context){
        SortingCheck sortingCheck = deliveryService.getSortingCheck(context.getRequestTurnToSendM());
        // 按包裹或者运单发货分拣校验
        SortingJsfResponse response = sortingCheckService.singleSendCheckAndReportIntercept(sortingCheck);

        if (!response.getCode().equals(JdResponse.CODE_OK)) {
            //如果校验不OK
            //获得运单的预分拣站点
            Integer preSortingSiteCode = null;
            try {
                com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findWaybillAndPack(WaybillUtil.getWaybillCode(sortingCheck.getBoxCode()), true, false, false, false);
                if (null != waybill) {
                    preSortingSiteCode = waybill.getSiteCode();
                }
            } catch (Throwable e) {
                logger.error("一车一单获取预分拣站点异常，单号：{}",sortingCheck.getBoxCode(), e);
            }

            if (response.getCode() >= SendResult.RESPONSE_CODE_MAPPING_CONFIRM) {
                context.getResponse().getData().init(SendResult.CODE_CONFIRM, response.getMessage(), response.getCode(), preSortingSiteCode);
            } else {
                context.getResponse().getData().init(SendResult.CODE_SENDED, response.getMessage(), response.getCode(), preSortingSiteCode);
            }
            return false;
        }
        return true;
    }
}
