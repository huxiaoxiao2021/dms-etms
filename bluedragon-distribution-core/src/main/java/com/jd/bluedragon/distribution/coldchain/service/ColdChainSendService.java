package com.jd.bluedragon.distribution.coldchain.service;

import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.coldchain.domain.TransPlanDetailResult;
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

    /**
     * 根据始发分拣中心和目的分拣中心 获取当日的运输计划明细信息
     *
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    List<TransPlanDetailResult> getTransPlanDetail(Integer createSiteCode, Integer receiveSiteCode);

    /**
     * 冷链发货获取批次号(若该运输计划编码已经发过一次，则获取上次批次号，若第一次发货则返回新批次号)
     *
     * @param transPlanCode
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    String getSendCode(String transPlanCode, Integer createSiteCode, Integer receiveSiteCode);

}
