package com.jd.bluedragon.distribution.capability.send.handler.verify;

import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
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
*      回调校验，提供给外部使用
*/
@Service("sendCallBackVerifyHandler")
public class SendCallBackVerifyHandler extends SendDimensionStrategyHandler {


   /**
    * 所有发货维度处理逻辑相同，只需要重新此方法即可
    * @param context
    * @return
    */
   @Override
   public boolean doHandler(SendOfCAContext context) {


       return super.doHandler(context);
   }
}
