package com.jd.bluedragon.distribution.print.waybill.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.ql.dms.print.utils.StringHelper;

/**
 * 
 * @ClassName: QueryWaybillByBusiCodeHandler
 * @Description: 通过商家单号进行打印
 * @author: wuyoude
 * @date: 2020年5月3日 下午12:47:33
 *
 */
@Service("queryWaybillByBusiCodeHandler")
public class QueryWaybillByBusiCodeHandler implements InterceptHandler<WaybillPrintContext,String> {

    private static final Logger logger = LoggerFactory.getLogger(QueryWaybillByBusiCodeHandler.class);

	@Autowired
	@Qualifier("ldopManager")
	private LDOPManager ldopManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();
        WaybillPrintRequest request = context.getRequest();
        //判断是否商家单号
        if(request.getBarCodeType() != null && request.getBarCodeType() == BarCodeType.BUSINESS_ORDER_CODE.getCode()){
        	String waybillCode = ldopManager.queryWaybillCodeByOrderIdAndCustomerCode(request.getBusinessId(),request.getBarCode());
        	if(StringHelper.isEmpty(waybillCode)){
        		result.toFail("根据商家单号未获取到对应的运单号！");
        		logger.warn("根据商家单号{}-{}未获取到对应的运单号！",request.getBusinessId(),request.getBarCode());
        	}else{
        		context.getRequest().setBarCode(waybillCode);
        	}
        }
        return result;
    }
}
