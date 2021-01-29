package com.jd.bluedragon.distribution.economic.service;

import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetBoxWeightVolumeMq;

import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/1/20 11:07
 * @Description: 经济网业务逻辑接口
 */
public interface IEconomicNetService {

    /**
     * 此箱号是否完成初始三方装箱包关系
     * @param box
     * @return
     */
    boolean isReady(Box box);

    /**
     * 加载存储箱包关系
     * @param box
     * @return
     */
    boolean loadAndSaveBoxPackageData(Box box);

    /**
     * 均分称重量方数据
     * @param box
     * @return
     */
    boolean equalizationWeightAndVolume(Box box, WeightVolumeEntity vo);

    /**
     * 称重量方数据监听 (运单和包裹)
     * @param weightVolumeEntity
     * @return
     */
    boolean packageOrWaybillWeightVolumeListener(WeightVolumeEntity weightVolumeEntity);

    /**
     * 称重量方数据监听 (箱号)
     * @param weightVolumeEntity
     * @return
     */
    boolean boxWeightVolumeListener(WeightVolumeEntity weightVolumeEntity);

    /**
     * 处理箱号分页称重数据
     * @param entities
     * @return
     */
    boolean dealBoxSplitWeightOfPage(List<WeightVolumeEntity> entities);
}
