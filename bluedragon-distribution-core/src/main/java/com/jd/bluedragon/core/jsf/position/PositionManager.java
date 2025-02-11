package com.jd.bluedragon.core.jsf.position;


import com.jdl.basic.api.domain.position.PositionData;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.response.JDResponse;
import com.jdl.basic.common.utils.Result;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/10 16:23
 * @Description:
 */
public interface PositionManager {

    /**
     * 根据岗位编码查询一条记录
     *
     * @param positionCode
     * @return
     */
    Result<PositionDetailRecord> queryOneByPositionCode(String positionCode);

    /**
     * 查询岗位信息
     * @param positionCode
     * @return
     */
    Result<PositionData> queryPositionWithIsMatchAppFunc(String positionCode);

    /**
     * 查询岗位信息，并校验是否关联作业app功能
     *
     * @param positionCode
     * @return
     */
    Result<PositionData> queryPositionInfo(String positionCode);
    /**
     * 根据gridKey查询岗位信息
     *
     * @param gridKey
     * @return
     */
    Result<PositionData> queryPositionByGridKey(String gridKey);
    

    /**
     * 根据业务主键查询岗位码
     *
     * @param refGridKey
     * @return
     */
    String queryPositionCodeByRefGridKey(String refGridKey);

    /**
     * 根据岗位码查询网格码
     * @param positionCode
     * @return
     */
    Result<String> queryWorkGridKeyByPositionCode(String positionCode);
}
