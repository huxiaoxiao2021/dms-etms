package com.jd.bluedragon.core.base;

import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;

import java.util.List;

/**
 * 报表服务包装接口
 *
 * @author hujiping
 * @date 2021/8/10 6:00 下午
 */
public interface ReportExternalManager {

    /**
     * 新增或更新抽检数据
     *
     * @param weightVolumeCollectDto
     */
    void insertOrUpdateForWeightVolume(WeightVolumeCollectDto weightVolumeCollectDto);

    /**
     * 根据条件查询抽检数量
     *
     * @param condition
     * @return
     */
    Integer countByParam(WeightVolumeQueryCondition condition);

    /**
     * 根据条件查询已操作抽检包裹号
     *
     * @param condition
     * @return
     */
    List<String> getSpotCheckPackageCodesByCondition(WeightVolumeQueryCondition condition);

    /**
     * 根据条件查询抽检数据
     *
     * @param condition
     * @return
     */
    List<WeightVolumeCollectDto> queryByCondition(WeightVolumeQueryCondition condition);
}
