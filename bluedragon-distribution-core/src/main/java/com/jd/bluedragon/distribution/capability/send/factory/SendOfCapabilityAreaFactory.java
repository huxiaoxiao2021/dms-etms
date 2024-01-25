package com.jd.bluedragon.distribution.capability.send.factory;

import com.jd.bluedragon.distribution.capability.send.domain.SendChainEnum;
import com.jd.bluedragon.distribution.capability.send.exce.SendOfCapabilityAreaException;
import com.jd.bluedragon.distribution.capability.send.handler.SendHandlerChain;
import com.jd.bluedragon.distribution.capability.send.handler.deal.*;
import com.jd.bluedragon.distribution.capability.send.handler.init.*;
import com.jd.bluedragon.distribution.capability.send.handler.lock.*;
import com.jd.bluedragon.distribution.capability.send.handler.transfer.SendRespMsgBoxHandler;
import com.jd.bluedragon.distribution.capability.send.handler.verify.*;

import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * 能力域 - 发货 工厂
 *
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/7
 * @Description:
 */
@Service("sendOfCapabilityAreaFactory")
public class SendOfCapabilityAreaFactory {


    /**
     * 获取执行链
     * @param sendBizSourceEnum  参考 SendBizSourceEnum
     * @return
     */
    public SendHandlerChain getSendHandlerChain(SendBizSourceEnum sendBizSourceEnum){

        switch (makeSendChainForSendBizSource (sendBizSourceEnum)){
            case DEFAULT:
                return buildOfDefault();
            case WITH_CYCLE_BOX_MODE:
                return buildOfCycleBoxBind();
        }

        throw new SendOfCapabilityAreaException("getSendHandlerChain not found!");
    }


    /**
     * 根据业务SendBizSourceEnum 匹配 执行链的 SendChainEnum
     * @param sendBizSourceEnum
     * @return
     */
    private SendChainEnum makeSendChainForSendBizSource(SendBizSourceEnum sendBizSourceEnum) {
        if(sendBizSourceEnum == null){
            throw new SendOfCapabilityAreaException("sendBizSourceEnum  not be null!");
        }

        Map<SendBizSourceEnum, SendChainEnum> sendChainMap = new HashMap<>();
        sendChainMap.put(SendBizSourceEnum.WAYBILL_SEND, SendChainEnum.DEFAULT);
        sendChainMap.put(SendBizSourceEnum.NEW_PACKAGE_SEND, SendChainEnum.DEFAULT);
        sendChainMap.put(SendBizSourceEnum.BOARD_SEND, SendChainEnum.DEFAULT);
        sendChainMap.put(SendBizSourceEnum.ANDROID_PDA_SEND, SendChainEnum.DEFAULT);
        sendChainMap.put(SendBizSourceEnum.JY_APP_SEND, SendChainEnum.WITH_CYCLE_BOX_MODE);
        sendChainMap.put(SendBizSourceEnum.JY_APP_TRANSFER_AND_FERRY_SEND, SendChainEnum.WITH_CYCLE_BOX_MODE);
        sendChainMap.put(SendBizSourceEnum.COLD_LOAD_CAR_SEND_NEW, SendChainEnum.DEFAULT);
        sendChainMap.put(SendBizSourceEnum.COLD_LOAD_CAR_KY_SEND_NEW, SendChainEnum.DEFAULT);
        sendChainMap.put(SendBizSourceEnum.SORT_MACHINE_SEND, SendChainEnum.NO_CHECK_MODE);
        sendChainMap.put(SendBizSourceEnum.SCANNER_FRAME_SEND, SendChainEnum.NO_CHECK_MODE);



        SendChainEnum sendChain = sendChainMap.get(sendBizSourceEnum);
        if (sendChain != null) {
            return sendChain;
        }

        throw new SendOfCapabilityAreaException(
                String.format("sendBizSourceEnum %s not find SendChainEnum!", sendBizSourceEnum.getCode()));
    }

    /**
     * 构建默认执行链
     * @return
     */
    private SendHandlerChain buildOfDefault(){
        SendHandlerChain chain = new SendHandlerChain();

        //初始化
        chain.addHandler(sendInItContextHandler);

        //校验
        chain.addHandler(sendCodeVerifyHandler);
        chain.addHandler(sendLockVerifyHandler);
        chain.addHandler(sendOldChainVerifyHandler);
        chain.addHandler(sendMultiVerifyHandler);

        //校验后执行的补充逻辑
        chain.addHandler(sendReplenishAfterVerifyHandler);

        //锁
        chain.addHandler(sendLockHandler);

        //执行
        chain.addHandler(sendCancelLastHandler);
        chain.addHandler(sendCancelBoardHandler);
        chain.addHandler(sendMInitHandler);
        chain.addHandler(sendResetCancelDetailHandler);
        chain.addHandler(sendMakeUpForSortingHandler);
        chain.addHandler(sendTurnoverBoxHandler);
        chain.addHandler(sendAsyncTaskHandler);
        chain.addHandler(sendTransitHandler);
        chain.addHandler(sendGoodsNoticeHandler);
        chain.addHandler(sendUrgentHintHandler);
        chain.addHandler(sendFileBoxHandler);

        //解锁，目前沿用老锁 仍保留在异步任务逻辑中
        chain.addHandler(sendUnLockHandler);

        //转换响应对象
        chain.addHandler(sendRespMsgBoxHandler);
        return chain;
    }


    /**
     * 支持绑定集包袋模式
     * @return
     */
    private SendHandlerChain buildOfCycleBoxBind(){
        SendHandlerChain chain = new SendHandlerChain();

        //初始化
        chain.addHandler(sendInItContextHandler);

        //校验
        chain.addHandler(sendCodeVerifyHandler);
        chain.addHandler(sendLockVerifyHandler);
        chain.addHandler(sendOldChainVerifyHandler);
        chain.addHandler(sendMultiVerifyHandler);
        chain.addHandler(sendCycleBoxBindVerifyHandler);

        //校验后执行的补充逻辑
        chain.addHandler(sendReplenishAfterVerifyHandler);

        //锁
        chain.addHandler(sendLockHandler);

        //执行
        chain.addHandler(sendCycleBoxBindHandler);
        chain.addHandler(sendCancelLastHandler);
        chain.addHandler(sendCancelBoardHandler);
        chain.addHandler(sendMInitHandler);
        chain.addHandler(sendResetCancelDetailHandler);
        chain.addHandler(sendMakeUpForSortingHandler);
        chain.addHandler(sendTurnoverBoxHandler);
        chain.addHandler(sendAsyncTaskHandler);
        chain.addHandler(sendTransitHandler);
        chain.addHandler(sendGoodsNoticeHandler);
        chain.addHandler(sendUrgentHintHandler);
        chain.addHandler(sendFileBoxHandler);

        //解锁，目前沿用老锁 仍保留在异步任务逻辑中
        chain.addHandler(sendUnLockHandler);

        //转换响应对象
        chain.addHandler(sendRespMsgBoxHandler);
        return chain;
    }


    @Autowired
    private SendInItContextHandler sendInItContextHandler;
    @Autowired
    private SendCodeVerifyHandler sendCodeVerifyHandler;
    @Autowired
    private SendLockVerifyHandler sendLockVerifyHandler;
    @Autowired
    private SendOldChainVerifyHandler sendOldChainVerifyHandler;
    @Autowired
    private SendMultiVerifyHandler sendMultiVerifyHandler;
    @Autowired
    private SendCycleBoxBindVerifyHandler sendCycleBoxBindVerifyHandler;
    @Autowired
    private SendLockHandler sendLockHandler;
    @Autowired
    private SendUnLockHandler sendUnLockHandler;
    @Autowired
    private SendCancelBoardHandler sendCancelBoardHandler;
    @Autowired
    private SendCancelLastHandler sendCancelLastHandler;
    @Autowired
    private SendGoodsNoticeHandler sendGoodsNoticeHandler;
    @Autowired
    private SendAsyncTaskHandler sendAsyncTaskHandler;
    @Autowired
    private SendMakeUpForSortingHandler sendMakeUpForSortingHandler;
    @Autowired
    private SendMInitHandler sendMInitHandler;
    @Autowired
    private SendResetCancelDetailHandler sendResetCancelDetailHandler;
    @Autowired
    private SendTransitHandler sendTransitHandler;
    @Autowired
    private SendTurnoverBoxHandler sendTurnoverBoxHandler;
    @Autowired
    private SendUrgentHintHandler sendUrgentHintHandler;
    @Autowired
    private SendReplenishAfterVerifyHandler sendReplenishAfterVerifyHandler;
    @Autowired
    private SendFileBoxHandler sendFileBoxHandler;
    @Autowired
    private SendCycleBoxBindHandler sendCycleBoxBindHandler;
    @Autowired
    private SendRespMsgBoxHandler sendRespMsgBoxHandler;

}
