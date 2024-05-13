package com.jd.bluedragon.core.base;

import com.jd.lbcc.rule.api.dto.request.PackageTypeQueryDTO;

import java.util.Map;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * 运营打标中心件型接口试算服务
 *
 * @author: 刘铎（liuduo8）
 * @date: 2024/3/20
 * @description: 运营打标中心件型接口试算服务
 *
 */
public interface LbccOperationSignQueryApiManager {

    /**
     * 件型计算服务
     * https://joyspace.jd.com/pages/oyVAwkDjB4KFo2AA9UID
     * @param packageTypeQueryDTO
     * @return  key 是传入列表中的packageCode  value是具体件型
     */
    Map<String, String> shapeCalculate(PackageTypeQueryDTO packageTypeQueryDTO);
}
