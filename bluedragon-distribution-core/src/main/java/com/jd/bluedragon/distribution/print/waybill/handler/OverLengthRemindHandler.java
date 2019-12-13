package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 包裹超长提示 处理器
 *
 * @author: hujiping
 * @date: 2019/12/6 18:20
 */
@Service("overLengthRemindHandler")
public class OverLengthRemindHandler implements InterceptHandler<WaybillPrintContext,String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 包裹最长边CM
     * */
    private static final int MAX_LENGTH = 100;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = new InterceptResult<String>();
        interceptResult.toSuccess();
        WeightOperFlow weightOperFlow = context.getRequest().getWeightOperFlow();
        if(weightOperFlow == null
                || (weightOperFlow.getLength() <= MAX_LENGTH
                && weightOperFlow.getWidth() <= MAX_LENGTH
                && weightOperFlow.getHigh() <= MAX_LENGTH)){
            return interceptResult;
        }
        String traderSign = context.getTraderSign();
        String waybillSign = context.getWaybill()==null?null:context.getWaybill().getWaybillSign();
        if(BusinessUtil.isOverLength(traderSign)
                && BusinessUtil.isB2CPureMatch(waybillSign)
                && BusinessUtil.isMonthFinish(waybillSign) &&
                (BusinessUtil.isPreferentialSend(waybillSign)
                        || BusinessUtil.isNextMorningArrived(waybillSign)
                        || BusinessUtil.isSameCityArrived(waybillSign))){
            context.getResponse().setLongPack(Boolean.TRUE);
            interceptResult.toWeakSuccess(JdResult.CODE_SUC, String.format(String.valueOf(MAX_LENGTH),WaybillPrintMessages.MESSAGE_PACKAGE_OVER_LENGTH_REMIND));
        }
        return interceptResult;
    }

}
