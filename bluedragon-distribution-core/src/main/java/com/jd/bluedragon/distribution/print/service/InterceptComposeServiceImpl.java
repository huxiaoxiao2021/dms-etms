package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.core.jsf.dms.CancelWaybillJsfManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.fastRefund.service.WaybillCancelClient;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.dms.ver.domain.JsfResponse;
import com.jd.dms.ver.domain.WaybillCancelJsfResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 取消锁定拦截（不再维护旧打印逻辑，代码已迁移至InterceptWaybillHandler）
 * Created by wangtingwei on 2015/12/24.
 */
@Service("interceptComposeService")
@Deprecated
public class InterceptComposeServiceImpl implements ComposeService {

    private static final Logger log = LoggerFactory.getLogger(InterceptComposeServiceImpl.class);


    @Autowired
    @Qualifier("cancelWaybillJsfManager")
    private CancelWaybillJsfManager cancelWaybillJsfManager;

    @Override
    public void handle(final PrintWaybill waybill, Integer dmsCode, Integer targetSiteCode) {
        waybill.setStatusCode(SortingResponse.CODE_OK);
        waybill.setStatusMessage(SortingResponse.MESSAGE_OK);
        if(null==waybill.getQuantity()||waybill.getQuantity()<=0){
            waybill.setStatusCode(SortingResponse.CODE_293040);
            waybill.setStatusMessage(SortingResponse.MESSAGE_293040);
        }

        // 验证运单号，是否锁定、删除等
        com.jd.bluedragon.distribution.fastRefund.domain.WaybillResponse cancelWaybill = null;
        try {
            JsfResponse<WaybillCancelJsfResponse> waybillCancelResponse = cancelWaybillJsfManager.dealCancelWaybill(waybill.getWaybillCode());
            if(waybillCancelResponse != null && waybillCancelResponse.isSuccess()){
                cancelWaybill = new com.jd.bluedragon.distribution.fastRefund.domain.WaybillResponse();
                cancelWaybill.setCode( waybillCancelResponse.getData().getCode());
            }
        } catch (Exception e) {
            this.log.error("WaybillResource --> setWaybillStatus get cancelWaybill Error:", e);
        }

        if (cancelWaybill != null) {
            if (SortingResponse.CODE_29300.equals(cancelWaybill.getCode())) {
                if (SortingResponse.CODE_293040.equals(waybill.getStatusCode())) {
                    waybill.setStatusCode(SortingResponse.CODE_29300);
                    waybill.setStatusMessage(SortingResponse.MESSAGE_29300);
                } else {
                    waybill.setStatusCode(SortingResponse.CODE_293000);
                    waybill.setStatusMessage(SortingResponse.MESSAGE_293000);
                }
            } else if (SortingResponse.CODE_29302.equals(cancelWaybill.getCode())) {
                if (SortingResponse.CODE_293040.equals(waybill.getStatusCode())) {
                    waybill.setStatusCode(SortingResponse.CODE_29302);
                    waybill.setStatusMessage(SortingResponse.MESSAGE_29302);
                } else {
                    waybill.setStatusCode(SortingResponse.CODE_293020);
                    waybill.setStatusMessage(SortingResponse.MESSAGE_293020);
                }
            } else if (SortingResponse.CODE_29301.equals(cancelWaybill.getCode())) {
                if (SortingResponse.CODE_293040.equals(waybill.getStatusCode())) {
                    waybill.setStatusCode(SortingResponse.CODE_29301);
                    waybill.setStatusMessage(SortingResponse.MESSAGE_29301);
                } else {
                    waybill.setStatusCode(SortingResponse.CODE_293010);
                    waybill.setStatusMessage(SortingResponse.MESSAGE_293010);
                }
            } else if (SortingResponse.CODE_29303.equals(cancelWaybill.getCode())) {
                waybill.setStatusCode(SortingResponse.CODE_29303);
                waybill.setStatusMessage(SortingResponse.MESSAGE_29303);
            }
        }
    }
}
