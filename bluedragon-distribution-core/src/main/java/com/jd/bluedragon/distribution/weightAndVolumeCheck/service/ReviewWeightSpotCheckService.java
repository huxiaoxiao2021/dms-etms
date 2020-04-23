package com.jd.bluedragon.distribution.weightAndVolumeCheck.service;

import com.jd.bluedragon.distribution.weightAndVolumeCheck.ReviewWeightSpotCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckExcelData;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckInfo;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * @ClassName: ReviewWeightSpotCheckService
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/4/23 9:35
 */
public interface ReviewWeightSpotCheckService {


    /**
     * 获取导出数据
     * @param condition
     * @return
     */
    List<List<Object>> getExportData(WeightAndVolumeCheckCondition condition);

    /**
     * 校验模板数据
     * @param dataList
     * @return
     */
    String checkExportData(List<SpotCheckExcelData> dataList, String importErpCode);

    /**
     * 批量插入数据
     * @param dataList
     */
    void batchInsert(List<SpotCheckExcelData> dataList);

    /**
     * 根据机构编码和业务类型查询
     * @param spotCheckInfo
     * @return
     */
    SpotCheckInfo queryBySiteCode(SpotCheckInfo spotCheckInfo);

    /**
     * 更新
     * @param spotCheckInfo
     */
    int updateBySiteCode(SpotCheckInfo spotCheckInfo);

    /**
     * 新增
     * @param spotCheckInfo
     */
    int insert(SpotCheckInfo spotCheckInfo);

    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    PagerResult<ReviewWeightSpotCheck> listData(WeightAndVolumeCheckCondition condition);

    /**
     * 导出抽查任务
     * @return
     */
    List<List<Object>> exportSpotData();

}
