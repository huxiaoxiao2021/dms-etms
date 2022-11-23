package com.jd.bluedragon.core.base;

import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckScrollResult;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;

import java.util.List;

/**
 * 抽检查询包装
 *
 * @author hujiping
 * @date 2021/12/13 3:01 下午
 */
public interface SpotCheckQueryManager {

    /**
     * 新增或更新抽检数据
     *
     * @param dto
     * @return
     */
    Boolean insertOrUpdateSpotCheck(WeightVolumeSpotCheckDto dto);

    /**
     * 分页查询抽检数据
     *
     * @param pager
     * @return
     */
    Pager<WeightVolumeSpotCheckDto> querySpotCheckByPage(Pager<SpotCheckQueryCondition> pager);

    /**
     * scroll方式查询抽检数据
     *
     * @param condition
     * @return
     */
    SpotCheckScrollResult querySpotCheckByScroll(SpotCheckQueryCondition condition);

    /**
     * 根据条件查询已抽检数量
     *
     * @param condition
     * @return
     */
    Integer querySpotCheckCountByCondition(SpotCheckQueryCondition condition);

    /**
     * 根据条件查询已抽检包裹号
     *
     * @param condition
     * @return
     */
    List<String> getSpotCheckPackByCondition(SpotCheckQueryCondition condition);

    /**
     * 根据条件查询抽检数据（查询有效数据）
     *
     * @param condition
     * @return
     */
    List<WeightVolumeSpotCheckDto> querySpotCheckByCondition(SpotCheckQueryCondition condition);

    /**
     * 根据条件查询所有抽检数据
     *
     * @param condition
     * @return
     */
    List<WeightVolumeSpotCheckDto> queryAllSpotCheckByCondition(SpotCheckQueryCondition condition);
}
