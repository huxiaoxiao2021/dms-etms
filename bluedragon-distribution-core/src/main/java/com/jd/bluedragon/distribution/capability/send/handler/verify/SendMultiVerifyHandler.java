package com.jd.bluedragon.distribution.capability.send.handler.verify;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/9/8
 * @Description: 多次发货校验
 */
@Service
public class SendMultiVerifyHandler extends SendDimensionStrategyHandler {

    @Autowired
    private DeliveryService deliveryService;


    /**
     * 按包裹维度处理多次发货场景
     * @param context
     * @return
     */
    @Override
    public boolean doPackHandler(SendOfCAContext context) {
        return multiSendVerification(context);
    }

    /**
     * 按运单维度处理多次发货场景，目前看仅支持包裹号转运单号场景使用
     * @param context
     * @return
     */
    @Override
    public boolean doWaybillHandler(SendOfCAContext context) {
        return multiSendVerification(context);
    }

    /**
     * 按箱号维度处理多次发货场景
     * @param context
     * @return
     */
    @Override
    public boolean doBoxHandler(SendOfCAContext context) {
        return multiSendVerification(context);
    }

    /**
     * 按板号维度处理多次发货场景
     * @param context
     * @return
     */
    @Override
    public boolean doBoardHandler(SendOfCAContext context) {
        // 校验是否操作过按板发货,按板号和createSiteCode查询send_m表看是是否有记录
        if(deliveryService.checkSendByBoard(context.getRequestTurnToSendM())){
            context.getResponse().getData().init(SendResult.CODE_SENDED, HintService.getHint(HintCodeConstants.BOARD_SENT_ALREADY));
            return false;
        }
        return true;
    }


    private boolean multiSendVerification(SendOfCAContext context) {
        // 多次发货取消上次发货校验

        //此处不能使用上下文中的SendResult ，防止污染
        SendResult multiSendVerificationResult = new SendResult();

        boolean mFlag = deliveryService.multiSendVerification(context.getRequestTurnToSendM(),
                multiSendVerificationResult);


        if(!mFlag){
            //mFlag false时  一定是拦截
            //如果强制拦截就不运行忽略
            if (Objects.equals(multiSendVerificationResult.getKey(), SendResult.CODE_SENDED)) {
                context.getResponse().setData(multiSendVerificationResult);
                return false;
            }
            //弱拦截当传入标识时可以跳过
            if(!context.getRequest().getIsCancelLastSend()){
                context.getResponse().setData(multiSendVerificationResult);
                return false;
            }
        }
        return true;
    }
}
