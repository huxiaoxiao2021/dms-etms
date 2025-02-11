package com.jd.bluedragon.distribution.send.manager;

import com.jd.bluedragon.distribution.send.domain.SendM;

import java.util.List;

/**
 * Created by hanjiaxing1 on 2018/10/16.
 */
public interface SendMManager {

    /**
     * 新增发货数据
     * @param namespace
     * @param entity
     * @return
     */
    Integer add(String namespace, SendM entity);

    /**
     * 新增发货数据
     * @param dSendM
     * @return
     */
    boolean insertSendM(SendM dSendM);

    /**
     * 通过箱号和始发地获取发货数据
     * @param sendM
     * @return
     */
    List<SendM> findSendMByBoxCode(SendM sendM);

    /**
     * <ul>
     *     <li>批量保存sendM</li>
     *     <li>箱号状态置为关闭</li>
     * </ul>
     *
     * <p>需要手动生成主键ID</p>
     *
     * @param sendMList
     * @return
     */
    int batchSaveSendM(List<SendM> sendMList);


    /**
     * 批量获取发货数据
     * @param sendM
     * @return
     */
    List<SendM> batchQuerySendMListBySiteAndBoxes(SendM sendM);
}
