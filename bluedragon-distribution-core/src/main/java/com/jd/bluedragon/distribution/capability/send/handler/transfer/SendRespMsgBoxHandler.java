package com.jd.bluedragon.distribution.capability.send.handler.transfer;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/11/20
 * @Description: 返回体 MsgBox 处理逻辑 在最后执行
 */
@Service("sendRespMsgBoxHandler")
public class SendRespMsgBoxHandler extends SendDimensionStrategyHandler {

    /**
     * 必须执行
     * @return
     */
    @Override
    public boolean mustHandler() {
        return true;
    }

    /**
     * 截止目前，任何处理维度执行逻辑都相同
     * @param context
     * @return
     */
    @Override
    public boolean doHandler(SendOfCAContext context) {

        JdVerifyResponse<SendResult> response = context.getResponse();
        SendResult sendResult = response.getData();
        if(sendResult == null){
            return true;
        }
        if (Objects.equals(sendResult.getKey(), SendResult.CODE_SENDED)) {
            response.addInterceptBox(sendResult.getKey(), sendResult.getValue());
        }

        if (Objects.equals(sendResult.getKey(), SendResult.CODE_CONFIRM)) {
            response.addConfirmBox(sendResult.getKey(), sendResult.getValue());
        }

        if (Objects.equals(sendResult.getKey(), SendResult.CODE_WARN)) {
            response.addWarningBox(sendResult.getKey(), sendResult.getValue());
        }

        return true;
    }
}
