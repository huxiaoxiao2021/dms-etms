package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 隐藏特殊始发场地名称
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-05-28 14:44:53 周五
 */
@Service("hideSpecialStartSiteNameHandler")
public class HideSpecialStartSiteNameHandler implements InterceptHandler<WaybillPrintContext,String> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DmsConfigManager dmsConfigManager;

    /**
     * 执行处理，返回处理结果
     * @param context 上下文
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.HideSpecialStartSiteNameHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = context.getResult();
        try {
            if(!dmsConfigManager.getPropertyConfig().getHidePrintSpecialStartSiteNameSwitchOn()){
                return interceptResult;
            }
            final BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
            Integer purposefulDmsCode = basePrintWaybill.getPurposefulDmsCode();
            if(purposefulDmsCode != null && dmsConfigManager.getPropertyConfig().matchHidePrintSpecialStartSitDestinationSiteList(purposefulDmsCode)){
                log.info("hideSpecialStartSiteNameHandler match waybillCode [{}], purposefulDmsCode [{}]", basePrintWaybill.getWaybillCode(), purposefulDmsCode);
                // 始发分拣中心名称改为空
                basePrintWaybill.setOriginalDmsName(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                // 寄件人信息修改为空
                basePrintWaybill.setConsignerAddress(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsigner(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsignerTel(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsignerMobile(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsigneeCompany(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsignerTelText(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsignerPrefixText(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setSenderCompany(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                // 隐藏路由信息
                basePrintWaybill.setRouterNode1(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode2(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode3(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode4(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode5(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode6(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode7(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode8(dmsConfigManager.getPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
            }
        } catch (Exception e) {
            log.error("HideSpecialStartSiteNameHandler.handle exception ", e);
        }
        return interceptResult;
    }
}
