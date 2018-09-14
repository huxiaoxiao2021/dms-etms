package com.jd.bluedragon.distribution.weightAndMeasure.service;

import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;

public interface DmsOutWeightAndVolumeService {
    /**
     * 保存分拣应付重量体积信息
     * @param dmsOutWeightAndVolume
     * @return
     */
    void saveOrUpdate(DmsOutWeightAndVolume dmsOutWeightAndVolume);

    /**
     * 查询这个分拣中心对该箱号/包裹的查询记录
     * @param barCode
     * @param dmsCode
     * @return
     */
    DmsOutWeightAndVolume getByBarCodeAndDms(String barCode,Integer dmsCode);
}
