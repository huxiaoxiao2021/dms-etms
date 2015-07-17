package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface SealBoxService {

    Integer add(SealBox sealBox);

    Integer update(SealBox sealBox);

    void saveOrUpdate(SealBox sealBox);

    void doSealBox(Task task);

    SealBox findBySealCode(String sealCode);

    SealBox findByBoxCode(String sealCode);
    

    /**
     * 增加封箱信息
     * 
     * @param sealVehicle
     * @return
     */
    public int addSealBox(SealBox sealBox);
}
