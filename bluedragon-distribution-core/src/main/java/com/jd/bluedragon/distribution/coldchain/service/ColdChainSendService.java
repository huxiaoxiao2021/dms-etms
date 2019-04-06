package com.jd.bluedragon.distribution.coldchain.service;

import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.send.domain.SendM;

import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ColdChainSendService
 * @date 2019/3/28
 */
public interface ColdChainSendService {

    /**
     * 新增
     *
     * @param coldChainSend
     * @return
     */
    boolean add(ColdChainSend coldChainSend);

    /**
     * 批量新增
     *
     * @param coldChainSends
     * @return
     */
    Integer batchAdd(List<ColdChainSend> coldChainSends);

    /**
     * 根据发货信息和运输计划编号 批量新增
     *
     * @param sendMList
     * @param transPlanCode
     * @return
     */
    Integer batchAdd(List<SendM> sendMList, String transPlanCode);

    /**
     * 根据运单号和运输计划号获取冷链发货信息
     *
     * @param transPlanCode
     * @return
     */
    ColdChainSend getByTransCode(String transPlanCode);

    /**
     * 根据运单号和批次号获取冷链发货信息
     *
     * @param waybillCode
     * @param sendCode
     * @return
     */
    ColdChainSend getBySendCode(String waybillCode, String sendCode);


}
