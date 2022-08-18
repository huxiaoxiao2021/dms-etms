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
    JDResponse<PositionData> queryPositionWithIsMatchAppFunc(String positionCode);

    /**
     * 查询岗位信息，并校验是否关联作业app功能
     *
     * @param positionCode
     * @return
     */
    JDResponse<PositionData> queryPositionInfo(String positionCode);
}
