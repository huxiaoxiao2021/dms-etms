package com.jd.bluedragon.distribution.weightAndMeasure.service;

import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;

import java.util.List;

public interface DmsOutWeightAndVolumeService {
    /**
     * 保存分拣应付重量体积信息
     * @param dmsOutWeightAndVolume
     * @return
     */
    void saveOrUpdate(DmsOutWeightAndVolume dmsOutWeightAndVolume);

    /**
     * 查询这个分拣中心对该箱号/包裹的记录
     * @param barCode
     * @param dmsCode
     * @return
     */
    List<DmsOutWeightAndVolume> getListByBarCodeAndDms(String barCode, Integer dmsCode);

    /**
     * 查询这个分拣中心对该箱号/包裹的一条记录
     * @param barCode
     * @param dmsCode
     * @return
     */
    DmsOutWeightAndVolume getOneByBarCodeAndDms(String barCode,Integer dmsCode);

    /**
     * 批量查询分拣中心对该箱号/包裹的记录
     *
     * @param barCodeList
     * @param createSiteCode
     * @return
     */
    List<DmsOutWeightAndVolume> getListByBarCodesAndDms(List<String> barCodeList, Integer createSiteCode);

    /**
     * 将数据置成无效is_delete=1
     * @param barCode
     * @param dmsCode
     */
    void deleteByBarCodeAndDms(String barCode,Integer dmsCode);
}
