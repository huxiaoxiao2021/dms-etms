package com.jd.bluedragon.distribution.print.waybill.handler;

import java.util.HashMap;
import java.util.Map;

import com.jd.bluedragon.core.jsf.dms.CancelWaybillJsfManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.dms.ver.domain.JsfResponse;
import com.jd.dms.ver.domain.WaybillCancelJsfResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
/**
 * 
 * @ClassName: InterceptWaybillHandler
 * @Description: 取消锁定拦截处理
 * @author: wuyoude
 * @date: 2018年1月30日 上午9:18:31
 */
@Service
public class InterceptWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Logger log = LoggerFactory.getLogger(InterceptWaybillHandler.class);
	
    @Autowired
    @Qualifier("cancelWaybillJsfManager")
    private CancelWaybillJsfManager cancelWaybillJsfManager;
	/**
	 * 存储需要拦截的编码、消息对应关系
	 */
	private static Map<Integer,String> NEED_INTERCEPT_CODES_MAP = new HashMap<Integer,String>();
	static {
		//取消订单
		NEED_INTERCEPT_CODES_MAP.put(SortingResponse.CODE_39006, SortingResponse.MESSAGE_29311);
		NEED_INTERCEPT_CODES_MAP.put(SortingResponse.CODE_29311, SortingResponse.MESSAGE_29311);
		NEED_INTERCEPT_CODES_MAP.put(SortingResponse.CODE_29302, SortingResponse.MESSAGE_29311);
		//病单
		NEED_INTERCEPT_CODES_MAP.put(SortingResponse.CODE_29307, SortingResponse.MESSAGE_29307);
		//恶意订单
		NEED_INTERCEPT_CODES_MAP.put(SortingResponse.CODE_29313, SortingResponse.MESSAGE_29313);
		//白条
		NEED_INTERCEPT_CODES_MAP.put(SortingResponse.CODE_29316, SortingResponse.MESSAGE_29316);
		// 被动理赔拦截暂存
		NEED_INTERCEPT_CODES_MAP.put(SortingResponse.CODE_29318, SortingResponse.MESSAGE_29318_SORTING);
	}
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		log.info("包裹标签打印-拦截信息处理");
		InterceptResult<String> result = context.getResult();
        // 验证运单号，是否锁定、删除等
		WaybillCancelJsfResponse waybillCancelJsfResponse = null;
        try {
        	JsfResponse<WaybillCancelJsfResponse> waybillCancelResponse = cancelWaybillJsfManager.dealCancelWaybill(context.getResponse().getWaybillCode());
            if(waybillCancelResponse != null && waybillCancelResponse.isSuccess()){
            	waybillCancelJsfResponse = waybillCancelResponse.getData();
            }
        } catch (Exception e) {
            log.error("InterceptWaybillHandler --> cancelWaybillJsfManager.dealCancelWaybill Error:", e);
        }
        //设置运单拦截状态及信息
        setStatusInfo(waybillCancelJsfResponse,context.getResponse());
		//补打、站长工作台补打，提示拦截
		if(WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType().equals(context.getRequest().getOperateType())
				|| WaybillPrintOperateTypeEnum.SITE_MASTER_PACKAGE_REPRINT.getType().equals(context.getRequest().getOperateType())){
			if(waybillCancelJsfResponse != null
					&& NEED_INTERCEPT_CODES_MAP.containsKey(waybillCancelJsfResponse.getCode())){
				result.toFail(waybillCancelJsfResponse.getCode(), NEED_INTERCEPT_CODES_MAP.get(waybillCancelJsfResponse.getCode()));
			}
		}
		// 换单打印时，拦截处理被动理赔拦截消息，仅有一条时提示；有两条代表又收到可换单消息，不再拦截
        if(WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType().equals(context.getRequest().getOperateType())){
            if(waybillCancelJsfResponse != null){
                ;
            }
        }
		return result;
	}
	//设置运单状态及信息
    private void setStatusInfo(WaybillCancelJsfResponse cancelWaybill, PrintWaybill waybill) {
        waybill.setStatusCode(SortingResponse.CODE_OK);
        waybill.setStatusMessage(SortingResponse.MESSAGE_OK);
        if(null==waybill.getQuantity()||waybill.getQuantity()<=0){
            waybill.setStatusCode(SortingResponse.CODE_293040);
            waybill.setStatusMessage(SortingResponse.MESSAGE_293040);
        }
        if (cancelWaybill != null) {
            if (SortingResponse.CODE_29300.equals(cancelWaybill.getCode())) {
                if (waybill.getStatusCode()==(SortingResponse.CODE_293040)) {
                    waybill.setStatusCode(SortingResponse.CODE_29300);
                    waybill.setStatusMessage(SortingResponse.MESSAGE_29300);
                } else {
                    waybill.setStatusCode(SortingResponse.CODE_293000);
                    waybill.setStatusMessage(SortingResponse.MESSAGE_293000);
                }
            } else if (SortingResponse.CODE_29302.equals(cancelWaybill.getCode())) {
                if (waybill.getStatusCode()==(SortingResponse.CODE_293040)) {
                    waybill.setStatusCode(SortingResponse.CODE_29302);
                    waybill.setStatusMessage(SortingResponse.MESSAGE_29302);
                } else {
                    waybill.setStatusCode(SortingResponse.CODE_293020);
                    waybill.setStatusMessage(SortingResponse.MESSAGE_293020);
                }
            } else if (SortingResponse.CODE_29301.equals(cancelWaybill.getCode())) {
                if (waybill.getStatusCode()==(SortingResponse.CODE_293040)) {
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
