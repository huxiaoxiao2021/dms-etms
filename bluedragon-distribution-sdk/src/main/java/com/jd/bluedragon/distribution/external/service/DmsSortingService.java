package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.coldChain.domain.ColdSendResult;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.sorting.domain.SortingDto;
import com.jd.bluedragon.distribution.sorting.domain.SortingRequestDto;

import java.util.List;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.distribution.external.service
 * @Description:
 * @date Date : 2023年09月14日 15:13
 */
public interface DmsSortingService {

    /**
     * 查询报货是否已经分拣理货
     * @param packageCode
     * @param createSiteCode
     * @return
     */
    InvokeResult<List<SortingDto>> findSortingByPackageCode(String packageCode, Integer createSiteCode);

    /**
     * 包裹号绑定箱号
     * @param request
     * @return
     */
    InvokeResult<ColdSendResult> bindingBoxMaterialPackageRelation(SortingRequestDto request);

    /**
     * 取消分拣
     * 取消包裹箱绑定关系
     * @param request
     * @return
     */
    InvokeResult<String> cancelSorting(SortingRequestDto request);
}
