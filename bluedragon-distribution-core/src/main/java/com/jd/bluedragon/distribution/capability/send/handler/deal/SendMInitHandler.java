package com.jd.bluedragon.distribution.capability.send.handler.deal;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.distribution.send.manager.SendMManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/27
 * @Description: 初始化sendM数据逻辑
 */
@Service
public class SendMInitHandler extends SendDimensionStrategyHandler {

    @Autowired
    private SendMManager sendMManager;


    /**
     *  初始化发货send_m数据 按包裹初始化
     * @param context
     * @return
     */
    @Override
    public boolean doPackHandler(SendOfCAContext context) {
        //发货sendm初始化
        this.sendMManager.insertSendM(context.getRequestTurnToSendM());

        return true;
    }

    /**
     *  初始化发货send_m数据 按箱号初始化
     * @param context
     * @return
     */
    @Override
    public boolean doBoxHandler(SendOfCAContext context) {
        //发货sendm初始化
        this.sendMManager.insertSendM(context.getRequestTurnToSendM());

        return true;
    }
}
