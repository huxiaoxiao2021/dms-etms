package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
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
    private UccPropertyConfiguration uccPropertyConfiguration;

    /**
     * 执行处理，返回处理结果
     * @param context 上下文
     * @return 处理结果
     */
    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = context.getResult();
        try {
            if(!uccPropertyConfiguration.getHidePrintSpecialStartSiteNameSwitchOn()){
                return interceptResult;
            }
            final BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
            Integer purposefulDmsCode = basePrintWaybill.getPurposefulDmsCode();
            if(purposefulDmsCode != null && uccPropertyConfiguration.matchHidePrintSpecialStartSitDestinationSiteList(purposefulDmsCode)){
                log.info("hideSpecialStartSiteNameHandler match waybillCode [{}], purposefulDmsCode [{}]", basePrintWaybill.getWaybillCode(), purposefulDmsCode);
                // 始发分拣中心名称改为空
                basePrintWaybill.setOriginalDmsName(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                // 寄件人信息修改为空
                basePrintWaybill.setConsignerAddress(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsigner(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsignerTel(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsignerMobile(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsigneeCompany(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsignerTelText(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setConsignerPrefixText(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                // 隐藏路由信息
                basePrintWaybill.setRouterNode1(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode2(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode3(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode4(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode5(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode6(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode7(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
                basePrintWaybill.setRouterNode8(uccPropertyConfiguration.getHideSpecialStartSitePrintReplaceSymbol());
            }
        } catch (Exception e) {
            log.error("HideSpecialStartSiteNameHandler.handle exception ", e);
        }
        return interceptResult;
    }
}
