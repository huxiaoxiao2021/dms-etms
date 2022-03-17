package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.send.domain.SendM;

import java.util.Date;
import java.util.List;

public interface SendMService {

    /**
     * 查询当前分拣中心的所有发货记录
     * @param createSiteCode
     * @param boxCode
     * @return
     */
    public List<SendM> findDeliveryRecord(Integer createSiteCode, String boxCode);

    /**
     * 根据起始时间查询所有发货批次信息
     * @param createSiteCode
     * @param receiveSiteCode
     * @param startDate
     * @return
     */
    public List<SendM> findAllSendCodesWithStartTime(Integer createSiteCode, Integer receiveSiteCode, Date startDate);

    /**
     * 批量查询已发货批次
     *
     * @param createSiteCode
     * @param receiveSiteCodes
     * @param startDate
     * @return
     */
    List<SendM> batchSearchBySiteCodeAndStartTime(Integer createSiteCode, List<Integer> receiveSiteCodes, Date startDate, Date endDate);

    /**
     * 根据条件查询发货记录
     *
     * @param params
     * @return
     */
    List<SendM> findByParams(SendM params);
    /**
     * 根据始发分拣中心、目的分拣中心、箱号确定send_m的一条发货记录
     * @param createSiteCode
     * @param boardCode
     * @param sendmStatus
     * @return
     */
    SendM selectOneBoardSend(Integer createSiteCode, String boardCode, Integer sendmStatus);
}
