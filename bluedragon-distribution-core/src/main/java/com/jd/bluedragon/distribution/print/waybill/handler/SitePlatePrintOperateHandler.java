package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.utils.JsonHelper;
/**
 * 
 * @ClassName: SitePlateWaybillPrintOperateHandler
 * @Description: 站点平台打印操作处理逻辑
 * @author: wuyoude
 * @date: 2018年1月31日 下午5:40:04
 *
 */
public class SitePlatePrintOperateHandler extends AbstractPrintOperateHandler{
	/**
	 * 将waybill对象转成json字符串返回
	 */
	@Override
	public String dealPrintResult(WaybillPrintContext context) {
		Waybill waybill = context.getWaybill();
		LabelPrintingResponse labelPrintingResponse = context.getLabelPrintingResponse();
		/**
		 * 将标签字段值赋给waybill
		 */
		waybill.setCrossCode(String.valueOf(labelPrintingResponse.getOriginalCrossCode()));
		waybill.setTrolleyCode(String.valueOf(labelPrintingResponse.getOriginalTabletrolley()));
		waybill.setTargetDmsCode(labelPrintingResponse.getPurposefulDmsCode());
		waybill.setTargetDmsName(String.valueOf(labelPrintingResponse.getPurposefulDmsName()));
		waybill.setTargetDmsDkh(String.valueOf(labelPrintingResponse.getPurposefulCrossCode()));
		waybill.setTargetDmsLch(String.valueOf(labelPrintingResponse.getPurposefulTableTrolley()));
		waybill.setRoad(labelPrintingResponse.getRoad());
		/**
		 * 打印数据转成json
		 */
		waybill.setJsonData(JsonHelper.toJson(labelPrintingResponse));
		return JsonHelper.toJson(context.getWaybill());
	}
}
