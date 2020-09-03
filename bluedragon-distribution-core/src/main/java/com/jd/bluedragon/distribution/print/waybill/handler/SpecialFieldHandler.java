package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	private static final Logger log = LoggerFactory.getLogger(SpecialFieldHandler.class);
    
    @Autowired
    private BaseMajorManager baseMajorManager;
    /**
     * 分拣中心标识-显示为滑道号的最大长度默认为2
     */
    private static final int CROSS_CODE_MAX_LENGTH = 2;
    /**
     * 分拣中心标识-显示为滑道号的最大长度
     */
    private int crossCodeMaxLength = CROSS_CODE_MAX_LENGTH;
    
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		log.debug("包裹标签打印-特殊字段处理");
		BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
		/**
		 * 显示目的分拣中心标识destinationCityDmsCode
		 * 1、始发分拣与目的分拣不一致，调用基础资料获取目的分拣中心对应的标识
		 * 2、始发分拣与目的分拣一致，显示为目的滑道号（滑道号长度小于等于2）
		 */
		if(basePrintWaybill.getPurposefulDmsCode() != null) {
			if(!basePrintWaybill.getPurposefulDmsCode().equals(basePrintWaybill.getOriginalDmsCode())){
				BaseSiteInfoDto baseSiteInfoDto = baseMajorManager.getBaseSiteInfoBySiteId(basePrintWaybill.getPurposefulDmsCode());
				if(baseSiteInfoDto != null && StringHelper.isNotEmpty(baseSiteInfoDto.getDistributeCode())){
					basePrintWaybill.setDestinationCityDmsCode(baseSiteInfoDto.getDistributeCode());
				}else{
					log.warn("打印-调用基础资料获取目的分拣中心对应的标识为空！siteCode:{}", basePrintWaybill.getPurposefulDmsCode());
				}
			}else{
				String destinationCrossCode = basePrintWaybill.getDestinationCrossCode();
				if(StringHelper.isNotEmpty(destinationCrossCode) 
						&& destinationCrossCode.length() <= crossCodeMaxLength){
					basePrintWaybill.setDestinationCityDmsCode(destinationCrossCode);
				}
			}
		}
		return context.getResult();
	}

	/**
	 * @return the crossCodeMaxLength
	 */
	public int getCrossCodeMaxLength() {
		return crossCodeMaxLength;
	}

	/**
	 * @param crossCodeMaxLength the crossCodeMaxLength to set
	 */
	public void setCrossCodeMaxLength(int crossCodeMaxLength) {
		this.crossCodeMaxLength = crossCodeMaxLength;
	}
}
