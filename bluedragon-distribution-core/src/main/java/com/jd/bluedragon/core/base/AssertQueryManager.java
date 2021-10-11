package com.jd.bluedragon.core.base;

import com.jd.etms.asset.material.base.ResultData;
import com.jd.etms.asset.material.dto.MatterPackageRelationDto;

import java.util.List;

/**
 * 集包袋相关服务管理类
 *
 * @author: hujiping
 * @date: 2020/5/22 16:30
 */
public interface AssertQueryManager {


    /**
     * 根据运单号获取集包袋
     *
     * @param dto
     * @return
     */
    ResultData<List<String>> queryBindMaterialByCode(MatterPackageRelationDto dto);

}
