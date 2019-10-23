package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.task.domain.Task;

import java.util.List;

public interface SealBoxService {

    Integer add(SealBox sealBox);

    Integer update(SealBox sealBox);

    void saveOrUpdate(SealBox sealBox);

    void doSealBox(Task task);

    SealBox findBySealCode(String sealCode);

    SealBox findByBoxCode(String boxCode);

    List<SealBox> findListByBoxCodes(List<String> boxCodeList);

    /**
     * 增加封箱信息
     * 
     * @param sealBox
     * @return
     */
    public int addSealBox(SealBox sealBox);
}
