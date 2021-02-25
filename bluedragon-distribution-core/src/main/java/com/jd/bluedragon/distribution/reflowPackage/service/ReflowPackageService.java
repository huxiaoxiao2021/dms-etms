package com.jd.bluedragon.distribution.reflowPackage.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.reflowPackage.domain.ReflowPackage;

import java.util.List;

public interface ReflowPackageService {

    /**
     * 新增
     * @param mode
     * @return
     */
    int add(ReflowPackage mode);

    /**
     * 更新
     * @param mode
     * @return
     */
    int update(ReflowPackage mode);

    /**
     * 判断数据是否存在
     * @param mode
     * @return
     */
    boolean isExist(ReflowPackage mode);

    /**
     * 包裹回流扫描提交
     * @param mode
     * @return
     */
    JdCResponse<Boolean> reflowPackageSubmit(ReflowPackage mode);

    /**
     * 根据对象查询符合条件的数据
     * @param mode
     * @return
     */
    List<ReflowPackage> getDataByBean(ReflowPackage mode);
}
