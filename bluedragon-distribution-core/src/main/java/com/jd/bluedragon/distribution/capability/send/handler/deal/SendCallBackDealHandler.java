package com.jd.bluedragon.distribution.capability.send.handler.deal;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import org.springframework.stereotype.Service;

/**
* 天官赐福 ◎ 百无禁忌
*
* @Auther: 刘铎（liuduo8）
* @Date: 2023/8/24
* @Description:
*      回调处理，提供给外部使用
*/
@Service("sendCallBackDealHandler")
public class SendCallBackDealHandler extends SendDimensionStrategyHandler {


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
