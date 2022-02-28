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
     * 根据条件查询总数
     *
     * @param query
     * @return
     */
    Long queryCount(PositionQuery query);

    /**
     * 根据条件查询
     *
     * @param query
     * @return
     */
    List<PositionDetailRecord> queryList(PositionQuery query);

    /**
     * 根据岗位编码更新
     *
     * @param positionCode
     * @return
     */
    int updateByPositionCode(String positionCode);

    /**
     * 根据业务主键逻辑删除
     *
     * @param businessKey
     * @return
     */
    int deleteByBusinessKey(String businessKey);
}
