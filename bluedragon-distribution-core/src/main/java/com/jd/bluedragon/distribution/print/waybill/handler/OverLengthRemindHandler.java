package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

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
     * 包裹长CM
     * */
    @Value("${overLengthRemindHandler.maxLength:150}")
    private int maxLength;
    /**
     * 包裹宽CM
     * */
    @Value("${overLengthRemindHandler.maxWidth:100}")
    private int maxWidth;
    /**
     * 包裹高CM
     * */
    @Value("${overLengthRemindHandler.maxHigh:100}")
    private int maxHigh;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = new InterceptResult<String>();
        interceptResult.toSuccess();
        WeightOperFlow weightOperFlow = context.getRequest().getWeightOperFlow();
        if(weightOperFlow == null
                || (weightOperFlow.getLength() <= maxLength
                && weightOperFlow.getWidth() <= maxWidth
                && weightOperFlow.getHigh() <= maxHigh)){
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
            interceptResult.toWeakSuccess(JdResult.CODE_SUC,
                    MessageFormat.format(WaybillPrintMessages.MESSAGE_PACKAGE_OVER_LENGTH_REMIND,maxLength,maxWidth,maxHigh));
        }
        return interceptResult;
    }

}
