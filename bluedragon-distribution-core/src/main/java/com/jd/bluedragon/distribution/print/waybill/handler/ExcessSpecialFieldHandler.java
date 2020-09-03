package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.jsf.domain.MixedSite;
import com.jd.bluedragon.distribution.jsf.domain.PrintQueryRequest;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.TransportTypeEnum;
import com.jd.bluedragon.distribution.mixedPackageConfig.service.MixedPackageConfigService;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 额外的特殊字段处理器
 *
 * @author: hujiping
 * @date: 2020/9/3 11:30
 */
@Service
public class ExcessSpecialFieldHandler implements Handler<WaybillPrintContext, JdResult<String>> {

    private static final Logger logger = LoggerFactory.getLogger(ExcessSpecialFieldHandler.class);

    @Value("${collectionAddress.switch:true}")
    private boolean collectionAddressSwitch;

    @Autowired
    private MixedPackageConfigService mixedPackageConfigService;

    @Override
    public JdResult<String> handle(WaybillPrintContext context) {
        // 是否开启集包地打印
        if(!collectionAddressSwitch){
            return context.getResult();
        }
        BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
        // 获取集包地
        if(basePrintWaybill != null){
            String collectionAddress = getMixedSiteName(basePrintWaybill);
            basePrintWaybill.setCollectionAddress(collectionAddress == null ? Constants.EMPTY_FILL : collectionAddress);
        }
        return context.getResult();
    }

    /**
     * 获取集包地
     * @param basePrintWaybill
     * @return
     */
    private String getMixedSiteName(BasePrintWaybill basePrintWaybill) {
        try {
            PrintQueryRequest printQueryRequest = new PrintQueryRequest();
            printQueryRequest.setOriginalDmsCode(basePrintWaybill.getOriginalDmsCode());
            printQueryRequest.setOriginalDmsName(basePrintWaybill.getOriginalDmsName());
            printQueryRequest.setDestinationDmsCode(basePrintWaybill.getPurposefulDmsCode());
            printQueryRequest.setDestinationDmsName(basePrintWaybill.getPurposefulDmsName());
            // 航空运输为1 公路运输为2
            printQueryRequest.setTransportType(TransportTypeEnum.HIGHWAY_TRANSPORT.getCode());
            if(Constants.SPECIAL_MARK_AIRTRANSPORT.equals(basePrintWaybill.getTransportTypeText())){
                printQueryRequest.setTransportType(TransportTypeEnum.AIR_TRANSPORT.getCode());
            }

            MixedSite mixedSite = mixedPackageConfigService.queryMixedSiteCodeForPrint(printQueryRequest);
            return mixedSite == null ? null : mixedSite.getCollectionAddress();
        }catch (Exception e){
            logger.error("查询集包地异常",e);
        }
        return null;
    }
}
