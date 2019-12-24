package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @ClassName: RemarkFieldHandler
 * @Description: 包裹标签打印-备注字段处理
 * @author: wuyoude
 * @date: 2018年2月5日 下午5:36:29
 *
 */
@Service
public class RemarkFieldHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Logger log = LoggerFactory.getLogger(RemarkFieldHandler.class);
    
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		log.debug("包裹标签打印-备注字段处理");
		BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
		String waybillSign = context.getBigWaybillDto().getWaybill().getWaybillSign();
		/**
		 * 1、自营运单-备注展示订单号
		 */
		if(BusinessUtil.isSelf(waybillSign)){
			String orderCode = waybillQueryManager.getOrderCodeByWaybillCode(basePrintWaybill.getWaybillCode(), true);
			if(StringHelper.isNotEmpty(orderCode)){
				String oldRemark = basePrintWaybill.getRemark();
				basePrintWaybill.setRemark(TextConstants.PRINT_TEXT_ORDER_CODE_PREFIX + orderCode);
				basePrintWaybill.appendRemark(oldRemark);
			}
		}
		return context.getResult();
	}
}
