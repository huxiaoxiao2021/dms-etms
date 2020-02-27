package com.jd.bluedragon.distribution.weightVolume.check;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * @author wuzuxiang
 * @since 2020/1/16
 **/
public class WeightVolumeChecker {

    private static List<IWeightVolumeChecker> weightVolumeCheckers = new ArrayList<>();

    /**
     * 注册校验器
     * @param checker
     */
    static void register(IWeightVolumeChecker checker) {
        weightVolumeCheckers.add(checker);
    }

    /**
     * 校验流程处理
     * @param entity
     * @return
     */
    public static InvokeResult<Boolean> check(WeightVolumeEntity entity) {
        for (IWeightVolumeChecker checker : weightVolumeCheckers) {
            InvokeResult<Boolean> result = checker.checkOperateWeightVolume(entity);
            if (null == result || InvokeResult.RESULT_SUCCESS_CODE != result.getCode()) {
                return result;
            }
        }
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.setData(Boolean.TRUE);
        result.success();
        return result;
    }
}
