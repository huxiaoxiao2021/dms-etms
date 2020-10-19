package com.jd.bluedragon.distribution.packageWeighting;


import com.jd.bluedragon.distribution.packageWeighting.domain.PackageWeighting;
import java.util.List;

/**
 * Created by jinjingcheng on 2018/4/22.
 */
public interface PackageWeightingService {
    /**
     * 查询重量和体积都大于0的称重流水
     *
     * @param waybillCode
     * @param packageCode
     * @param businessTypes
     * @return
     */
    List<PackageWeighting> findWeightVolume(String waybillCode, String packageCode, List<Integer> businessTypes);

    /**
     * 保存
     *
     * @param packageWeighting
     * @return
     */
    int add(PackageWeighting packageWeighting);

    /**
     * 校验包裹或订单是否有称重量方
     */
    boolean weightVolumeValidate(String waybillCode, String packageCode);
}
