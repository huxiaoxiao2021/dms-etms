package com.jd.bluedragon.distribution.print.waybill.handler.reverse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.Waybill;

/**
 * 换单打印面单数据处理
 * @author wuyoude
 *
 */
@Service("dealReversePrintInfoHandler")
public class DealReversePrintInfoHandler implements InterceptHandler<WaybillPrintContext,String> {

    private static final Logger logger = LoggerFactory.getLogger(DealReversePrintInfoHandler.class);

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
    	logger.info("换单打印面单数据处理");
        InterceptResult<String> result = context.getResult();
        String oldSendPay = null;
        if (context.getOldBigWaybillDto() != null && context.getOldBigWaybillDto().getWaybill() != null) {
        	oldSendPay = context.getOldBigWaybillDto().getWaybill().getSendPay();
        }
        //原运单号 SendPay第297位等于1时，为预售未付全款， 逆向换单后的新单面单打印【预】字
        if(BusinessUtil.isPreSellWithNoPay(oldSendPay)){
        	context.getBasePrintWaybill().setBcSign(TextConstants.PRE_SELL_FLAG);
        }
        boolean isPickUp = WaybillUtil.isBusiSurfaceCode(context.getBasePrintWaybill().getWaybillCode()) 
        		|| WaybillUtil.isSurfaceCode(context.getBasePrintWaybill().getWaybillCode());
        //换单打印:判断售后取件单一单一件,没有称重并且包裹重量为空，设置为运单重量
        if(isPickUp
        		&& context.getBigWaybillDto().getPackageList() != null
        		&& context.getBigWaybillDto().getPackageList().size() == 1 
        		&& !context.getRequest().hasWeighted()
        		&& context.getResponse().getPackList() != null
        		&& context.getResponse().getPackList().size() > 0){
        	PrintPackage printPackage = context.getResponse().getPackList().get(0);
        	if(!NumberHelper.gt0(printPackage.getWeight())){
        		//取运单重量展示:优先取againWeight
        		Waybill tmsWaybill = context.getBigWaybillDto().getWaybill();
        		if(tmsWaybill != null){
        			Double weight = context.getBigWaybillDto().getWaybill().getAgainWeight();
        			//againWeight无效，取goodWeight
        			if(!NumberHelper.gt0(weight)){
        				weight = context.getBigWaybillDto().getWaybill().getGoodWeight();
        			}
        			//重量有效设置包裹重量
        			if(NumberHelper.gt0(weight)){
        				printPackage.setWeightAndUnit(weight, Constants.MEASURE_UNIT_NAME_KG);
        			}
        		}
        	}
        }
        return result;
    }
}
