package com.jd.bluedragon.distribution.print.waybill.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseSiteInfoDto;

/**
 * 
 * @ClassName: SpecialFieldHandler
 * @Description: 包裹标签打印-特殊字段处理
 * @author: wuyoude
 * @date: 2018年2月5日 下午5:36:29
 *
 */
@Service
public class SpecialFieldHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Log logger= LogFactory.getLog(SpecialFieldHandler.class);
    
    @Autowired
    private BaseMajorManager baseMajorManager;
    
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("包裹标签打印-特殊字段处理");
		BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
		/**
		 * 显示目的分拣中心标识destinationCityDmsCode
		 * 1、始发分拣与目的分拣不一致，调用基础资料获取目的分拣中心对应的标识
		 * 2、始发分拣与目的分拣一致，显示为目的滑道号
		 */
		if(basePrintWaybill.getPurposefulDmsCode() != null) {
			if(!basePrintWaybill.getPurposefulDmsCode().equals(basePrintWaybill.getOriginalDmsCode())){
				BaseSiteInfoDto baseSiteInfoDto = baseMajorManager.getBaseSiteInfoBySiteId(basePrintWaybill.getPurposefulDmsCode());
				if(baseSiteInfoDto != null && StringHelper.isNotEmpty(baseSiteInfoDto.getDistributeCode())){
					basePrintWaybill.setDestinationCityDmsCode(baseSiteInfoDto.getDistributeCode());
				}
			}else{
				String destinationCrossCode = basePrintWaybill.getDestinationCrossCode();
				if(StringHelper.isNotEmpty(destinationCrossCode) 
						&& destinationCrossCode.length()<=2){
					basePrintWaybill.setDestinationCityDmsCode(destinationCrossCode);
				}
			}
		}
		return context.getResult();
	}
}
