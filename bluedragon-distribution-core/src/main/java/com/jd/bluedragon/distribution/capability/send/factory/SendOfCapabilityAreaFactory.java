package com.jd.bluedragon.distribution.capability.send.factory;

import com.jd.bluedragon.distribution.capability.send.domain.SendChainModeEnum;
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
    public SendHandlerChain getSendHandlerChain(SendChainModeEnum sendChainModeEnum){

        switch (sendChainModeEnum){
            case DEFAULT:
                return buildOfDefault();
            case WITH_CYCLE_BOX_MODE:
                return buildOfCycleBoxBind();
            case NO_CHECK_MODE:
                return buildOfNoCheck();
        }

        throw new SendOfCapabilityAreaException("getSendHandlerChain not found!");
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
     * 构建默认执行链
     * @return
     */
    private SendHandlerChain buildOfNoCheck(){

        SendHandlerChain chain = new SendHandlerChain();

        //初始化
        chain.addHandler(sendInItContextHandler);

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
