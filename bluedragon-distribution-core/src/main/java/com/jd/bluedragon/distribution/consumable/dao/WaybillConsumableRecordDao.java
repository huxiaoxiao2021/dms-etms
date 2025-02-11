package com.jd.bluedragon.distribution.consumable.dao;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableExportDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecordCondition;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: WaybillConsumableRecordDao
 * @Description: 运单耗材记录表--Dao接口
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public interface WaybillConsumableRecordDao extends Dao<WaybillConsumableRecord> {

    /**
     * 根据查询条件查询一条记录
     * @param condition
     * @return
     */
    public WaybillConsumableRecord queryOneByCondition(WaybillConsumableRecordCondition condition);
    /**
     * 根据ID批量更新多条记录
     * @param records
     * @return
     */
    public int updateByIds(List<WaybillConsumableRecord> records);
    /**
     * 根据ID批量更新多条记录
     * @param ids
     * @return
     */
    public List<WaybillConsumableRecord> findByIds(List<Long> ids);

    /**
     * 查询导出数据总量
     * @param condition
     * @return
     */
    public Integer exportCountByWebCondition(WaybillConsumableRecordCondition condition);

    /**
     * 查询导出数据明细
     * @param condition
     * @return
     */
    public List<WaybillConsumableExportDto> exportInfoByWebCondition(WaybillConsumableRecordCondition condition);
}
