package com.jd.bluedragon.distribution.labelPrint.dao.farmar;

import com.jd.bluedragon.distribution.label.domain.farmar.FarmarEntity;

import java.util.List;

/**
 * 砝码打印DAO
 *
 * @author hujiping
 * @date 2022/2/25 5:46 PM
 */
public interface FarmarPrintRecordDao {

    /**
     * 新增
     *
     * @param record
     * @return
     */
    int insert(FarmarEntity record);

    /**
     * 批量新增
     *
     * @param list
     * @return
     */
    int batchInsert(List<FarmarEntity> list);

    /**
     * 根据岗位编码查询
     *
     * @param positionCode
     * @return
     */
    FarmarEntity queryByFarmarCode(String positionCode);
}
