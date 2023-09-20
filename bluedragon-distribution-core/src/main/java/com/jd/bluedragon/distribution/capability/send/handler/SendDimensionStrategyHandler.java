package com.jd.bluedragon.distribution.capability.send.handler;

import com.jd.bluedragon.distribution.capability.send.domain.SendDimensionEnum;
import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.exce.SendOfCapabilityAreaException;

 /**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/30
 * @Description: 发货维度策略
 */
public abstract class SendDimensionStrategyHandler implements ISendHandler {


    /**
     * 默认执行逻辑
     * 如果所有发货维度处理逻辑相同
     * 则直接重写此方法即可
     *
     * 如果需要单独处理不同发货维度的逻辑
     * 请各个链重写对应维度的方法
     *      如 按包裹重写doPackHandler
     * @param context
     * @return
     */
    public boolean doHandler(SendOfCAContext context){
        //根据不同的维度去调用不同维度的策略
        SendDimensionEnum sendDimension = context.getSendDimension();
        switch (sendDimension){
            case PACK:
                return doPackHandler(context);
            case WAYBILL:
                return doWaybillHandler(context);
            case BOX:
                return doBoxHandler(context);
            case BOARD:
                return doBoardHandler(context);
            case SEND_CODE:
                return doSendCodeHandler(context);
        }
        throw new SendOfCapabilityAreaException(String.format("not find sendDimension %s",sendDimension));
    }

    /**
     * 是否必须执行，返回true时不允许跳过,必须执行。
     * @return
     */
    @Override
    public boolean mustHandler() {
        return false;
    }

    /**
     * 按包裹维度处理逻辑
     * @param context
     * @return
     */
    public boolean doPackHandler(SendOfCAContext context){

        return true;
    }

    /**
     * 按运单维度处理逻辑
     * @param context
     * @return
     */
    public boolean doWaybillHandler(SendOfCAContext context){

        return true;
    }

    /**
     * 按箱号维度处理逻辑
     * @param context
     * @return
     */
    public boolean doBoxHandler(SendOfCAContext context){

        return true;
    }

    /**
     * 按板号维度处理逻辑
     * @param context
     * @return
     */
    public boolean doBoardHandler(SendOfCAContext context){

        return true;
    }

    /**
     * 按批次维度处理逻辑
     * @param context
     * @return
     */
    public boolean doSendCodeHandler(SendOfCAContext context){

        return true;
    }


}
