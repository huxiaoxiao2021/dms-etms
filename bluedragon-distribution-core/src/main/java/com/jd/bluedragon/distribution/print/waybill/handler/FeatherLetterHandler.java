package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.IotServiceWSManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.fastRefund.service.WaybillCancelClient;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 鸡毛信拦截校验
 */
@Service("featherLetterHandler")
public class FeatherLetterHandler implements Handler<WaybillPrintContext,JdResult<String>> {
    private static final Log logger= LogFactory.getLog(FeatherLetterHandler.class);

    @Autowired
    private WaybillCommonService waybillCommonService;
    
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private IotServiceWSManager iotServiceWSManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = new InterceptResult<String>();
        result.toSuccess();
        String waybillSign = context.getWaybill().getWaybillSign();
        if(!BusinessUtil.isFeatherLetter(waybillSign)){
            context.getResponse().setFeatherLetterWaybill(Boolean.FALSE);
            return result;
        }
        context.getResponse().setFeatherLetterWaybill(Boolean.TRUE);
        WaybillPrintRequest request = context.getRequest();
        //取消鸡毛信
        if(request.isCancelFeatherLetter()){
            return result;
        }
        if(StringUtils.isEmpty(request.getFeatherLetterDeviceNo())){
            result.toFail(JdResponse.CODE_FEATHER_LETTER_ERROR, JdResponse.MESSAGE_FEATHER_LETTER_ERROR);
            return result;
        }
        Boolean isEnable = iotServiceWSManager.isDeviceCodeEnable(request.getFeatherLetterDeviceNo());
        if(Objects.equals(isEnable,Boolean.FALSE)){
            result.toFail(JdResponse.CODE_FEATHER_LETTER_DISABLE_ERROR, JdResponse.MESSAGE_FEATHER_LETTER_DISABLE_ERROR);
            return result;
        }
        return result;
    }
    /**
     * 获取运单信息
     * @param waybillCode 运单号
     * @param packOpeFlowFlg 是否获取称重信息
     * @return 运单实体
     */
    private Waybill loadBasicWaybillInfo(WaybillPrintContext context,String waybillCode,Integer packOpeFlowFlg) {
    	BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getWaybillDataForPrint(waybillCode);
    	if (baseEntity == null 
    			||baseEntity.getResultCode() != 1
    			||baseEntity.getData()==null) {
    		return null;
    	}
    	context.setBigWaybillDto(baseEntity.getData());
    	boolean loadPweight = Constants.INTEGER_FLG_TRUE.equals(packOpeFlowFlg);
    	Waybill waybill = this.waybillCommonService.convWaybillWS(baseEntity.getData(), true, true,true,loadPweight);
        if (waybill == null) {
            return waybill;
        }
        // 增加SOP订单EMS全国直发
        if (Constants.POP_SOP_EMS_CODE.equals(waybill.getSiteCode())) {
            waybill.setSiteName(Constants.POP_SOP_EMS_NAME);
        }

        this.setWaybillStatus(waybill);
        return waybill;
    }

    /**
     * 设置运单装态
     * @param waybill 运单实体
     */
    private void setWaybillStatus(Waybill waybill) {
        if (waybill == null || StringUtils.isBlank(waybill.getWaybillCode())) {
            return;
        }

        Boolean isDelivery = waybill.isDelivery();
        if (isDelivery) {
            waybill.setStatusAndMessage(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        } else {
            waybill.setStatusAndMessage(SortingResponse.CODE_293040, SortingResponse.MESSAGE_293040);
        }

        // 验证运单号，是否锁定、删除等
        com.jd.bluedragon.distribution.fastRefund.domain.WaybillResponse cancelWaybill = null;
        try {
            cancelWaybill = WaybillCancelClient.getWaybillResponse(waybill.getWaybillCode());
        } catch (Exception e) {
            logger.error("WaybillResource --> setWaybillStatus get cancelWaybill Error:", e);
        }

        if (cancelWaybill != null) {
            if (SortingResponse.CODE_29300.equals(cancelWaybill.getCode())) {
                if (isDelivery) {
                    waybill.setStatusAndMessage(SortingResponse.CODE_29300, SortingResponse.MESSAGE_29300);
                } else {
                    waybill.setStatusAndMessage(SortingResponse.CODE_293000, SortingResponse.MESSAGE_293000);
                }
            } else if (SortingResponse.CODE_29302.equals(cancelWaybill.getCode())) {
                if (isDelivery) {
                    waybill.setStatusAndMessage(SortingResponse.CODE_29302, SortingResponse.MESSAGE_29302);
                } else {
                    waybill.setStatusAndMessage(SortingResponse.CODE_293020, SortingResponse.MESSAGE_293020);
                }
            } else if (SortingResponse.CODE_29301.equals(cancelWaybill.getCode())) {
                if (isDelivery) {
                    waybill.setStatusAndMessage(SortingResponse.CODE_29301, SortingResponse.MESSAGE_29301);
                } else {
                    waybill.setStatusAndMessage(SortingResponse.CODE_293010, SortingResponse.MESSAGE_293010);
                }
            } else if (SortingResponse.CODE_29303.equals(cancelWaybill.getCode())) {
                waybill.setStatusAndMessage(SortingResponse.CODE_29303, SortingResponse.MESSAGE_29303);
            }
        }
    }

}
