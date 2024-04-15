package com.jd.bluedragon.distribution.external.service;


import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.sorting.domain.SortingDto;
import com.jd.bluedragon.distribution.sorting.domain.SortingRequestDto;
import com.jd.bluedragon.distribution.sorting.dto.request.SortingReq;

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
    InvokeResult<Boolean> bindingBoxMaterialPackageRelation(SortingRequestDto request);

    /**
     * 取消分拣
     * 取消包裹箱绑定关系
     * @param request
     * @return
     */
    InvokeResult<Boolean> cancelSorting(SortingRequestDto request);

    /**
     * 根据包裹号查询最近一条装箱记录，如果装的不是真实箱则不返回数据
     * 跨场地查询，传入的siteCode只是为了记录入参
     * @param sortingReq 请求入参
     * @return 装箱记录
     * @author fanggang7
     * @time 2024-04-10 21:09:57 周三
     */
    InvokeResult<SortingDto> findLatestSortingBoxByPackageCode(SortingReq sortingReq);
}
