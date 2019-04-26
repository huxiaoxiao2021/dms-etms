package com.jd.bluedragon.distribution.jsf.service;

import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.domain.MixedPackageConfigResponse;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;

import java.util.List;
import java.util.Map;

public interface JsfSortingResourceService {
	public SortingJsfResponse check(SortingCheck sortingCheck);
    public SortingJsfResponse isCancel(String packageCode);
    List<MixedPackageConfigResponse> getMixedConfigsBySitesAndTypes(Integer createSiteCode, Integer receiveSiteCode, Integer transportType, Integer ruleType);
    public Integer getWaybillCancelByWaybillCode(String waybillCode);
    public String getRouterByWaybillCode(String waybillCode);
    public BoardCombinationJsfResponse boardCombinationCheck(BoardCombinationRequest request);
    /**
     * 批量查询路由
     * @param waybillCodes
     * @return
     */
    public Map<String,String> getRouterByWaybillCodes(List<String> waybillCodes);

    /**
     * 校验滑道号
     * @return true 滑道号正确，false 不正确
     */
    public Boolean checkPackageCrossCode(String waybillCode, String packageCode);

    /**
     * 发货校验
     * @param request
     * @return
     */
    JdResult packageSendCheck(DeliveryRequest request);
}
