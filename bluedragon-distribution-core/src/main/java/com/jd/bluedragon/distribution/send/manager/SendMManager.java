package com.jd.bluedragon.distribution.send.manager;

import com.jd.bluedragon.distribution.send.domain.SendM;

import java.util.List;

/**
 * Created by hanjiaxing1 on 2018/10/16.
 */
public interface SendMManager {

    /**
     * 新增发货容器数据，将将箱号及创建站点插入索引表
     * @param namespace
     * @param entity
     * @return
     */
    Integer add(String namespace, SendM entity);

    Integer addBatch(List<SendM> param);

    List<SendM> findSendMByBoxCode2(SendM sendM);

    List<SendM> findSendMByBoxCode(SendM sendM);

    boolean insertSendM(SendM dSendM);

}
