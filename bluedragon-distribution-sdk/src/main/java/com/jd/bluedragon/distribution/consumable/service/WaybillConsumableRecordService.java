package com.jd.bluedragon.distribution.consumable.service;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableExportDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecordCondition;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: WaybillConsumableRecordService
 * @Description: 运单耗材记录表--Service接口
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public interface WaybillConsumableRecordService extends Service<WaybillConsumableRecord> {

    //运单耗材记录表：未处理状态
    Integer UNTREATED_STATE = 0;
    //运单耗材记录表：已处理状态
    Integer TREATED_STATE = 1;
    //每次批量处理最大条数
    Integer MAX_ROWS = 500;
    //每次导出最大运单数
    Integer EXPORT_ROW_LIMIT = 50000;

    /**
     * 根据运单号查询一条记录
     * @param waybillCode
     * @return
     */
    public WaybillConsumableRecord queryOneByWaybillCode(String waybillCode);

    /**
     * 根据ID批量更新多条记录
     * @param records
     * @return
     */
    public int confirmByIds(List<WaybillConsumableRecord> records);

    /**
     * 查询运单是否已经确认
     * @param records
     * @return
     */
    public Boolean isConfirmed(String waybillCode);

    /**
     * 运单耗材是否支持修改
     * @param waybillCode
     * @return
     */
    public boolean canModify(String waybillCode);

    /**
     * 更新单条数据：必须有ID或者运单号
     * @param record
     * @return
     */
    public boolean updateByCondition(WaybillConsumableRecord record);

    /**
     * 获取导出数据总量
     * @param condition
     * @return
     */
    public int exportCountByWebCondition(WaybillConsumableRecordCondition condition);
    /**
     * 查询导出数据明细
     * @param condition
     * @return
     */
    public List<WaybillConsumableExportDto> exportInfoByWebCondition(WaybillConsumableRecordCondition condition);

}
