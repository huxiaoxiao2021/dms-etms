package com.jd.bluedragon.distribution.print.waybill.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.StringHelper;

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
	private static final Log logger= LogFactory.getLog(RemarkFieldHandler.class);
    
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("包裹标签打印-备注字段处理");
		BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
		/**
		 * 1、自营运单-备注展示订单号
		 */
		if(WaybillUtil.isJDWaybillCode(basePrintWaybill.getWaybillCode())){
			String orderCode = waybillQueryManager.getOrderCodeByWaybillCode(basePrintWaybill.getWaybillCode(), true);
			if(StringHelper.isNotEmpty(orderCode)){
				String oldRemark = basePrintWaybill.getRemark();
				basePrintWaybill.setRemark(orderCode);
				basePrintWaybill.appendRemark(oldRemark);
			}
		}
		return context.getResult();
	}
}
