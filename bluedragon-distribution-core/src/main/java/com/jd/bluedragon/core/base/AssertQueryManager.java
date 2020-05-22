package com.jd.bluedragon.core.base;

import com.jd.ql.asset.dto.MatterPackageRelationDto;
import com.jd.ql.asset.dto.ResultData;

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
