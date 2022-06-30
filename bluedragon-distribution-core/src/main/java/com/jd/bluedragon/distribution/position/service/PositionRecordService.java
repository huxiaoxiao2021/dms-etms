package com.jd.bluedragon.distribution.position.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.PositionData;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.position.domain.PositionDetailRecord;
import com.jd.bluedragon.distribution.position.domain.PositionRecord;
import com.jd.bluedragon.distribution.position.query.PositionQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.List;

/**
 * 岗位查询服务
 *
 * @author hujiping
 * @date 2022/2/25 5:47 PM
 */
public interface PositionRecordService {

    /**
     * 新增
     *
     * @param record
     * @return
     */
    Result<Integer> insertPosition(PositionRecord record);

    /**
     * 批量新增
     *
     * @param list
     * @return
     */
    Result<Integer> batchInsert(List<PositionRecord> list);

    /**
     * 按条件分页查询
     *
     * @param query
     * @return
     */
    Result<PageDto<PositionDetailRecord>> queryPageList(PositionQuery query);

    /**
     * 根据岗位编码查询一条记录
     * 
     * @param positionCode
     * @return
     */
    Result<PositionDetailRecord> queryOneByPositionCode(String positionCode);

    /**
     * 根据岗位编码更新
     *
     * @param positionRecord
     * @return
     */
    Result<Boolean> updateByPositionCode(PositionRecord positionRecord);

    /**
     * 根据业务主键删除
     *
     * @param positionRecord
     * @return
     */
    Result<Boolean> deleteByBusinessKey(PositionRecord positionRecord);

    /**
     * 根据条件查询总数
     *
     * @param query
     * @return
     */
    Result<Long> queryCountByCondition(PositionQuery query);

    /**
     * 同步所有数据
     *
     * @return
     */
    void syncAllData();
    /**
     * 查询岗位信息
     * @param positionCode
     * @return
     */
	JdCResponse<PositionData> queryPositionWithIsMatchAppFunc(String positionCode);

    /**
     * 查询岗位信息，并校验是否关联作业app功能
     *
     * @param positionCode
     * @return
     */
	JdCResponse<PositionData> queryPositionInfo(String positionCode);
}
