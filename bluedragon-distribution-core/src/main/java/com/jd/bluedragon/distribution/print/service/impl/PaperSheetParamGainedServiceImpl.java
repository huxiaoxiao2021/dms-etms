package com.jd.bluedragon.distribution.print.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.MixedSite;
import com.jd.bluedragon.distribution.jsf.domain.PrintQueryRequest;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.service.PaperSheetParamGainedService;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 面单参数获取实现
 *
 * @author: hujiping
 * @date: 2020/8/31 19:36
 */
@Service("paperSheetParamGainedService")
public class PaperSheetParamGainedServiceImpl implements PaperSheetParamGainedService {

    private static final Logger log = LoggerFactory.getLogger(PaperSheetParamGainedServiceImpl.class);

    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;

    @Override
    public String getMixedSiteName(WaybillPrintContext context) {

        try {
            PrintQueryRequest printQueryRequest = new PrintQueryRequest();
            BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
            printQueryRequest.setOriginalDmsCode(basePrintWaybill.getOriginalDmsCode());
            printQueryRequest.setOriginalDmsName(basePrintWaybill.getOriginalDmsName());
            printQueryRequest.setDestinationDmsCode(basePrintWaybill.getPurposefulDmsCode());
            printQueryRequest.setDestinationDmsName(basePrintWaybill.getPurposefulDmsName());
            // 航空运输为1 公路运输为2
            printQueryRequest.setTransportType(2);
            if(basePrintWaybill.getSpecialMark() != null
                    && basePrintWaybill.getSpecialMark().contains(Constants.SPECIAL_MARK_AIRTRANSPORT)){
                printQueryRequest.setTransportType(1);
            }

            WaybillPrintRequest request = context.getRequest();
            Integer operateType = request.getOperateType();
            double packWeight = 0.0;
            // 站点平台打印单独处理
            if(operateType != null && (operateType == WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT.getType()
                    || operateType == WaybillPrintOperateTypeEnum.BATCH_SORT_WEIGH_PRINT.getType())){
                Waybill waybill = context.getWaybill();
                if((request.getWeightOperFlow() == null || request.getWeightOperFlow().getWeight() == 0)
                        && CollectionUtils.isNotEmpty(waybill.getPackList())){
                    for (Pack pack : waybill.getPackList()){
                        if(pack.getIsPrintPack() == 0){
                            packWeight = Double.valueOf(pack.getWeight());
                            break;
                        }
                    }
                }
            }else {
                WaybillPrintResponse printInfo = context.getResponse();
                if((request.getWeightOperFlow() == null || request.getWeightOperFlow().getWeight() == 0)
                        && CollectionUtils.isNotEmpty(printInfo.getPackList())){
                    for (PrintPackage printPackage : printInfo.getPackList()){
                        if(printPackage.getIsPrintPack() != null && !printPackage.getIsPrintPack()){
                            packWeight = printPackage.getWeight();
                            break;
                        }
                    }
                }
            }
            printQueryRequest.setPackageWeight(packWeight);
            JdResult<MixedSite> jdResult = jsfSortingResourceService.queryMixedSiteCodeForPrint(printQueryRequest);
            if(jdResult != null && JdResult.CODE_SUC.equals(jdResult.getCode())
                    && jdResult.getData() != null){
                return jdResult.getData().getCollectionAddress();
            }
        }catch (Exception e){
            log.error("查询集包地异常",e);
        }
        return null;
    }
}
