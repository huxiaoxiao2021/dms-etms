package com.jd.bluedragon.distribution.print.waybill.handler;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;

/**
 * 站点平台打印限制拦截
 * 
 * @author wuyoude
 *
 */
@Service("sitePlateInterceptHandler")
public class SitePlateInterceptHandler implements InterceptHandler<WaybillPrintContext,String> {
    private static final Logger log = LoggerFactory.getLogger(SitePlateInterceptHandler.class);

    /**
     *  针对B2C&C2C寄付现结的单子，限制使用站点平台打印
     *  	B2C：waybillsign28位等于0且29位不等于8
			C2C：waybillsign29位等于8
			寄付现结：waybillsign25位等于1
     */
    private static Set<Integer> needInterceptOperateTypes = new HashSet<Integer>();
    static {
        needInterceptOperateTypes.add(WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT.getType());//站点平台打印
    }

	@Override
	public InterceptResult<String> handle(WaybillPrintContext target) {
		InterceptResult<String> interceptResult = target.getResult();
        if (needInterceptOperateTypes.contains(target.getRequest().getOperateType())) {
        	String waybillSign = target.getWaybillSign();
            // 针对B2C&C2C寄付现结的单子，限制使用站点平台打印
            if (BusinessUtil.isPrepaid(waybillSign) 
            		&& (BusinessUtil.isB2C(waybillSign) || BusinessUtil.isC2C(waybillSign))) {
                interceptResult.toFail(WaybillPrintMessages.MESSAGE_B2C_C2C_PREPAID_INTERCEPT);
                return interceptResult;
            }
        }
		return interceptResult;
	}

}
