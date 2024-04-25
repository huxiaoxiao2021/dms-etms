package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;

public interface PositionQueryJsfManager {

    Result<PositionDetailRecord> queryOneByPositionCode(String positionCode);
    /**
     * 给网格码负责人推送京me
     * @param operatorErp 当前操作人
     * @param positionCode  岗位码
     * @param title 推送标题
     * @param content   推送内容
     * @return
     */
    InvokeResult pushInfoToPositionMainErp(String operatorErp, String positionCode, String title, String content);

}
