package com.jd.bluedragon.distribution.jy.evaluate;

import java.io.Serializable;
import java.util.List;

/**
 * @author pengchong28
 * @description 装车评价申诉新增对象
 * @date 2024/3/14
 */
public class JyEvaluateRecordAppealAddDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 被评价目标业务主键
     */
    private String targetBizId;
    /**
     * 装车评价申诉新增对象
     */
    private List<JyEvaluateRecordAppealDto> jyEvaluateRecordAppealDtoList;

    public String getTargetBizId() {
        return targetBizId;
    }

    public void setTargetBizId(String targetBizId) {
        this.targetBizId = targetBizId;
    }

    public List<JyEvaluateRecordAppealDto> getJyEvaluateRecordAppealDtoList() {
        return jyEvaluateRecordAppealDtoList;
    }

    public void setJyEvaluateRecordAppealDtoList(List<JyEvaluateRecordAppealDto> jyEvaluateRecordAppealDtoList) {
        this.jyEvaluateRecordAppealDtoList = jyEvaluateRecordAppealDtoList;
    }
}
