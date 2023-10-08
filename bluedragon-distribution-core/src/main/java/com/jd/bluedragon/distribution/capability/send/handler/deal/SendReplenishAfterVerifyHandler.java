package com.jd.bluedragon.distribution.capability.send.handler.deal;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.utils.BusinessHelper;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/9/9
 * @Description: 校验完成后补充逻辑
 */
@Service
public class SendReplenishAfterVerifyHandler extends SendDimensionStrategyHandler {

    /**
     * 按维度维度处理是需要在校验完成后补充首包裹号
     *
     * @param context
     * @return
     */
    @Override
    public boolean doWaybillHandler(SendOfCAContext context) {
        //此逻辑将boxCode替换成包裹号。具体为什么，我也想知道，防止线上出问题只能迁移过来了
        context.getRequestTurnToSendM().setBoxCode(
                BusinessHelper.getFirstPackageCodeByWaybillCode(
                        context.getRequestTurnToSendM().getBoxCode()));
        return true;
    }
}
