package com.jd.bluedragon.distribution.position.dao;

import com.jd.bluedragon.distribution.position.domain.PositionDetailRecord;
import com.jd.bluedragon.distribution.position.domain.PositionRecord;
import com.jd.bluedragon.distribution.position.query.PositionQuery;

import java.util.List;

/**
 * 岗位查询DAO
 *
 * @author hujiping
 * @date 2022/2/25 5:46 PM
 */
public interface PositionRecordDao {

    /**
     * 新增
     *
     * @param record
     * @return
     */
    int insert(PositionRecord record);

    /**
     * 批量新增
     *
     * @param list
     * @return
     */
    int batchInsert(List<PositionRecord> list);

    /**
     * 根据岗位编码更新
     *
     * @param positionRecord
     * @return
     */
    int updateByPositionCode(PositionRecord positionRecord);

    /**
     * 根据业务主键逻辑删除
     *
     * @param positionRecord
     * @return
     */
    int deleteByBusinessKey(PositionRecord positionRecord);

    /**
     * 根据岗位编码查询
     *
     * @param positionCode
     * @return
     */
    PositionRecord queryByPositionCode(String positionCode);
    /**
     * 根据岗位编码查询详细信息
     *
     * @param positionCode
     * @return
     */
    PositionDetailRecord queryDetailByPositionCode(String positionCode);

    /**
     * 根据业务主键查询数据
     *
     * @param businessKey
     * @return
     */
    PositionRecord queryByBusinessKey(String businessKey);

    /**
     * 根据条件分页查询
     *
     * @param query
     * @return
     */
    List<PositionDetailRecord> queryList(PositionQuery query);

    /**
     * 根据条件查询总数
     *
     * @param query
     * @return
     */
    Long queryCount(PositionQuery query);
}
