package com.jd.bluedragon.core.jsf.position;


import com.jdl.basic.api.domain.position.PositionDetailRecord;
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
}
