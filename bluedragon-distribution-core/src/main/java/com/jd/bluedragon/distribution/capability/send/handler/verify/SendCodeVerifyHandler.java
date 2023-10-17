package com.jd.bluedragon.distribution.capability.send.handler.verify;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 /**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/24
 * @Description:
 *      批次相关校验
 */
@Service("sendCodeVerifyHandler")
public class SendCodeVerifyHandler extends SendDimensionStrategyHandler {

    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Autowired
    private DeliveryService deliveryService;

    /**
     * 所有发货维度处理逻辑相同，只需要重新此方法即可
     * @param context
     * @return
     */
    @Override
    public boolean doHandler(SendOfCAContext context) {

        // 校验批次号基础信息
        InvokeResult<Boolean> chkResult = sendCodeService.validateSendCodeEffective(context.getRequest().getSendCode());
        if (!chkResult.codeSuccess()) {
            context.getResponse().toCustomError(chkResult.getCode(), chkResult.getMessage());
            return false;
        }

        // 校验批次创建场地和操作人场地是否相符
        if(!checkSiteCodeEOpeSiteCode(context)){
            context.getResponse().getData().init(SendResult.CODE_SENDED, HintService.getHint(HintCodeConstants.BATCH_ORIGIN_AND_OPERATOR_ORIGIN_DIFFERENCE));
            return false;
        }

        // 校验批次是否封车 封车时原来的方法返回true
        StringBuffer customMsg = new StringBuffer().append(HintService.getHint(HintCodeConstants.SEND_CODE_SEALED_TIPS_SECOND));
        if(newSealVehicleService.newCheckSendCodeSealed(context.getRequest().getSendCode(), customMsg)){
            context.getResponse().getData().init(SendResult.CODE_SENDED, customMsg.toString());
            return false;
        }


        return super.doHandler(context);
    }

    /**
     * 按包裹
     * @param context
     * @return
     */
    @Override
    public boolean doPackHandler(SendOfCAContext context) {
        //生鲜批次校验
        return checkFreshSendCode(context);
    }

    /**
     * 按运单
     * @param context
     * @return
     */
    @Override
    public boolean doWaybillHandler(SendOfCAContext context) {
        //生鲜批次校验
        return checkFreshSendCode(context);
    }



    /**
     * 按板号校验
     * @param context
     * @return
     */
    @Override
    public boolean doBoardHandler(SendOfCAContext context) {
        //额外校验板号和批次号的目的地是否一致
        SendResult checkResponse = deliveryService.checkBoard(context.getBarCode(), context.getRequestTurnToSendM(),context.getRequest().getIsForceSend());
        if(!SendResult.CODE_OK.equals(checkResponse.getKey())){
            context.getResponse().getData().init(checkResponse.getKey(),checkResponse.getValue());
            return false;
        }
        return true;
    }


    /**
     * 校验生鲜批次禁止扫描非生鲜运单
     * @param context
     * @return
     */
    private boolean checkFreshSendCode(SendOfCAContext context){
        if(sendCodeService.isFreshSendCode(context.getRequestTurnToSendM().getSendCode())){
            if(null != context.getWaybill() &&
                    !BusinessUtil.isFreshWaybill(context.getWaybill().getWaybillSign())){
                context.getResponse().getData().init(SendResult.CODE_SENDED, HintService.getHint(HintCodeConstants.SPECIAL_FRESH_BATCH));
                return false;
            }
        }
        return true;
    }

    /**
     * 比较操作人当前场地与批次场地是否一致
     * @param context
     * @return
     */
    private boolean checkSiteCodeEOpeSiteCode(SendOfCAContext context) {
        String sendCode = context.getRequest().getSendCode();
        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
        if(createSiteCode == null){
            return false;
        }
        if(createSiteCode.equals(context.getRequest().getSiteCode())){
            return true;
        }
        return false;
    }
}
