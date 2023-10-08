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
            if(!dmsConfigManager.getUccPropertyConfig().getHidePrintSpecialStartSiteNameSwitchOn()){
                return interceptResult;
            }
            final BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
            Integer purposefulDmsCode = basePrintWaybill.getPurposefulDmsCode();
            if(purposefulDmsCode != null && dmsConfigManager.getUccPropertyConfig().matchHidePrintSpecialStartSitDestinationSiteList(purposefulDmsCode)){
                log.info("hideSpecialStartSiteNameHandler match waybillCode [{}], purposefulDmsCode [{}]", basePrintWaybill.getWaybillCode(), purposefulDmsCode);
                // 始发分拣中心名称改为空
                basePrintWaybill.setOriginalDmsName(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                // 寄件人信息修改为空
                basePrintWaybill.setConsignerAddress(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsigner(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsignerTel(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsignerMobile(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsigneeCompany(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsignerTelText(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsignerPrefixText(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setSenderCompany(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                // 隐藏路由信息
                basePrintWaybill.setRouterNode1(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode2(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode3(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode4(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode5(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode6(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode7(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode8(dmsConfigManager.getUccPropertyConfig().getHideSpecialStartSitePrintReplaceSymbol());
            }
        } catch (Exception e) {
            log.error("HideSpecialStartSiteNameHandler.handle exception ", e);
        }
        return interceptResult;
    }
}
