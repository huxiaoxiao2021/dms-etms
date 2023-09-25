package com.jd.bluedragon.core.base;

import com.jd.bluedragon.sdk.common.dto.ApiResult;
import com.jd.bluedragon.sdk.modules.recyclematerial.dto.MaterialAbolishReq;
import com.jd.bluedragon.sdk.modules.recyclematerial.dto.MaterialAbolishRes;
import com.jd.bluedragon.sdk.modules.recyclematerial.dto.RecycleMaterial;

import java.util.List;

/**
 * 循环物资包装接口
 *
 * @author hujiping
 * @date 2023/4/12 6:08 PM
 */
public interface RecycleMaterialManager {

    /**
     * 批量新增物资
     *
     * @param list
     * @return
     */
    ApiResult<Integer> batchInsertRecycleMaterial(List<RecycleMaterial> list);

    /**
     * 查询物资详情
     *
     * @param materialCode
     * @return
     */
    ApiResult<RecycleMaterial> findByMaterialCode(String materialCode);

    /**
     * 作废物资
     *
     * @param materialCode
     * @param operateUserErp
     * @param operateSiteCode
     * @return
     */
    ApiResult<RecycleMaterial> disableMaterialByCode(String materialCode, String operateUserErp, Integer operateSiteCode);

    /**
     * 批量作废物资
     *
     * @param abolishRequest
     * @return
     */
    ApiResult<MaterialAbolishRes> batchAbolishMaterial(MaterialAbolishReq abolishRequest);

    /**
     *
     * @param siteCode
     * @param typeCode 3 小 4 大
     * @return
     */
    ApiResult<Integer> countMaterialByCondition(Integer siteCode, Integer typeCode);
}
