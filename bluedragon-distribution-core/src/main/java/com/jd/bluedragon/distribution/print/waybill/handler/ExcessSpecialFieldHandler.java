package com.jd.bluedragon.distribution.print.waybill.handler;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedSite;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.PrintQueryRequest;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.TransportTypeEnum;
import com.jd.bluedragon.distribution.mixedPackageConfig.service.MixedPackageConfigService;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 额外的特殊字段处理器
 *
 * @author: hujiping
 * @date: 2020/9/3 11:30
 */
@Service
public class ExcessSpecialFieldHandler implements Handler<WaybillPrintContext, JdResult<String>> {

    private static final Logger logger = LoggerFactory.getLogger(ExcessSpecialFieldHandler.class);

    /**
     * -1 代表开启全国场地的集包地配置
     * */
    private static final String COLLECTION_ADDRESS_ALLSITE = "-1";

    @Autowired
    private MixedPackageConfigService mixedPackageConfigService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Override
    public JdResult<String> handle(WaybillPrintContext context) {
        // 是否开启集包地打印
        WaybillPrintRequest request = context.getRequest();
        Integer siteCode = request == null ? null : request.getDmsSiteCode();
        BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
        /**
         * 获取waybillSign信息
         */
        String waybillSign = context.getWaybillSign();
        //设置逆向信息
        setReverseInfo(waybillSign,basePrintWaybill);
        //设置集包地信息
        setCollectionAddress(siteCode,basePrintWaybill);
        return context.getResult();
    }
    /**
     * 设置逆向信息
     * @param request
     * @param basePrintWaybill
     */
    private void setReverseInfo(String waybillSign,BasePrintWaybill basePrintWaybill) {
    	// 逆向并且非签单返还，追加‘退’标识
        if (waybillSign != null 
        		&& !BusinessUtil.isForeignForwardAndWaybillMarkForward(waybillSign)
        		&& !BusinessUtil.isSignBack(waybillSign)){
        	basePrintWaybill.appendSpecialMark(TextConstants.REVERSE_FLAG);
        }
	}
	/**
     * 设置集包地信息
     * @param siteCode 操作人站点
     * @param basePrintWaybill 打印数据对象
     */
    private void setCollectionAddress(Integer siteCode,BasePrintWaybill basePrintWaybill) {
        if(!checkCollectionAddressIsSwitchOn(siteCode)){
            return;
        }
        // 获取集包地
        if(basePrintWaybill != null){
            String collectionAddress = getMixedSiteName(basePrintWaybill);
            basePrintWaybill.setCollectionAddress(collectionAddress == null ? Constants.EMPTY_FILL : collectionAddress);
        }
	}

	/**
     * 校验场地是否开启集包地配置
     * @param siteCode
     * @return
     */
    private boolean checkCollectionAddressIsSwitchOn(Integer siteCode) {
        try{
            if(siteCode == null){
                return false;
            }
            String collectionAddressSiteCodes = uccPropertyConfiguration.getCollectionAddressSiteCodes();
            if(StringUtils.isEmpty(collectionAddressSiteCodes)){
                return false;
            }
            // -1代表全国场地都开启
            if(COLLECTION_ADDRESS_ALLSITE.equals(collectionAddressSiteCodes)){
                return true;
            }
            List<String> siteCodes = Arrays.asList(collectionAddressSiteCodes.split(Constants.SEPARATOR_COMMA));
            return siteCodes.contains(siteCode);
        }catch (Exception e){
            logger.error("获取集包地场地配置开关异常",e);
        }
        return false;
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
