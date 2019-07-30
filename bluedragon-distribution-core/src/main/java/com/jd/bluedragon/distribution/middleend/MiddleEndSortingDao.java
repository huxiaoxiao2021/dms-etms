package com.jd.bluedragon.distribution.middleend;

import com.github.pagehelper.PageInfo;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.SharedSortingQueryManager;
import com.jd.bluedragon.distribution.middleend.sorting.domain.DmsCustomSite;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ql.shared.services.sorting.api.ApiResult;
import com.jd.ql.shared.services.sorting.api.dto.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MiddleEndSortingDao implements ISortingDao {

    @Autowired
    private SharedSortingQueryManager sharedSortingQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 根据箱号查询分拣记录
     *
     * @param sorting
     * @return
     */
    public List<Sorting> findByBoxCode(Sorting sorting) {
        //把dmsSorting包装成middleEndSorting
        ApiResult<List<com.jd.ql.shared.services.sorting.api.dto.Sorting>> apiResult = sharedSortingQueryManager.queryByContainerAndFlow(sorting.getBoxCode(), buildSortingFlow(sorting), SortingDirection.getEunmByType(sorting.getType()));
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            List<com.jd.ql.shared.services.sorting.api.dto.Sorting> middleEndSortingList = apiResult.getData();
            return middleendSorting2DmsSortingBatch(middleEndSortingList);
        } else {
            return null;
        }
    }


    /**
     * 根据包裹号判断是否存在分拣记录
     *
     * @param sorting
     * @return
     */
    public Boolean existSortingByPackageCode(Sorting sorting) {
        ApiResult<Boolean> apiResult = sharedSortingQueryManager.existSortingByPackageCode(sorting.getCreateSiteCode(), sorting.getPackageCode());
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            return apiResult.getData();
        } else {
            return null;
        }
    }


    /**
     * 获取分拣数量
     *
     * @param createSiteCode
     * @param boxCode
     * @return
     */
    public Integer findPackCount(Integer createSiteCode, String boxCode) {
        ApiResult<Integer> apiResult = sharedSortingQueryManager.queryContainerPackNum(createSiteCode, boxCode);
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            return apiResult.getData();
        } else {
            return null;
        }
    }

    /**
     * 根据箱号和始发站点查询分拣明细
     *
     * @param createSiteCode
     * @param boxCode
     * @return
     */
    public Sorting findBoxDescSite(Integer createSiteCode, String boxCode) {
        ApiResult<com.jd.ql.shared.services.sorting.api.dto.Sorting> apiResult = sharedSortingQueryManager.queryContainerDescSite(createSiteCode, boxCode);
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            return middleendSorting2DmsSorting(apiResult.getData());
        } else {
            return null;
        }
    }

    /**
     * 查询分拣明细
     * select box_code, package_code, waybill_code, create_time
     *
     * @param sorting
     * @return
     */
    public List<Sorting> findBoxPackList(Sorting sorting) {
        List<Sorting> sortingList = new ArrayList<>();
        String objectCode = sorting.getPackageCode();
        if (StringUtils.isBlank(objectCode)) {
            objectCode = sorting.getWaybillCode();
        }

        ApiResult<PageInfo<com.jd.ql.shared.services.sorting.api.dto.Sorting>> apiResult = null;
        PageInfo<com.jd.ql.shared.services.sorting.api.dto.Sorting> pageInfo = new PageInfo<>();
        pageInfo.setPageSize(5000);
        pageInfo.setPageNum(1);

        apiResult = sharedSortingQueryManager.queryPackList(sorting.getCreateSiteCode(), sorting.getWaybillCode(), objectCode, SortingDirection.getEunmByType(sorting.getType()), pageInfo);
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            PageInfo<com.jd.ql.shared.services.sorting.api.dto.Sorting> resultData = apiResult.getData();
            sortingList.addAll(middleendSorting2DmsSortingBatch(resultData.getList()));

            //读取分页数
            int totalPage = apiResult.getData().getPages();

            //循环获取剩余数据
            for (int i = 2; i <= totalPage; i++) {
                pageInfo.setPageSize(i);
                apiResult = sharedSortingQueryManager.queryPackList(sorting.getCreateSiteCode(), sorting.getWaybillCode(), objectCode, SortingDirection.getEunmByType(sorting.getType()), pageInfo);
                sortingList.addAll(middleendSorting2DmsSortingBatch(apiResult.getData().getList()));
            }

            return sortingList;
        } else {
            return null;
        }
    }

    /**
     * 根据运单号或包裹号查询分拣明细
     *
     * @param sorting
     * @return
     */
    public List<Sorting> queryByCode(Sorting sorting) {
        String objectCode = sorting.getPackageCode();
        if (StringUtils.isBlank(objectCode)) {
            objectCode = sorting.getWaybillCode();
        }

        ApiResult<List<SortingObject>> apiResult = sharedSortingQueryManager.queryBySiteCodeAndPackage(sorting.getCreateSiteCode(), sorting.getWaybillCode(), objectCode, SortingDirection.getEunmByType(sorting.getType()));
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            return sortingObject2DmsSortingBatch(apiResult.getData());
        } else {
            return null;
        }
    }

    /**
     * 根据运单号或者包裹号查询分拣明细
     * 无sortingType条件
     *
     * @param sorting
     * @return
     */
    public List<Sorting> queryByCode2(Sorting sorting){
        return queryByCode(sorting);
    }

    /**
     * 根据批次号查询分拣明细
     *
     * @param sorting
     * @return
     */
    public List<Sorting> findByBsendCode(Sorting sorting){
        List<Sorting> sortingList = new ArrayList<>();

        ApiResult<PageInfo<SortingObject>> apiResult = null;
        PageInfo<SortingObject> pageInfo = new PageInfo<>();
        pageInfo.setPageSize(5000);
        pageInfo.setPageNum(1);

        apiResult = sharedSortingQueryManager.queryByBatchCode(sorting.getCreateSiteCode(), sorting.getBsendCode(), pageInfo);
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            PageInfo<SortingObject> resultData = apiResult.getData();
            sortingList.addAll(sortingObject2DmsSortingBatch(resultData.getList()));

            //读取分页数
            int totalPage = apiResult.getData().getPages();

            //循环获取剩余数据
            for (int i = 2; i <= totalPage; i++) {
                pageInfo.setPageSize(i);
                apiResult = sharedSortingQueryManager.queryByBatchCode(sorting.getCreateSiteCode(), sorting.getBsendCode(), pageInfo);
                sortingList.addAll(sortingObject2DmsSortingBatch(apiResult.getData().getList()));
            }

            return sortingList;
        } else {
            return null;
        }
    }

    /**
     * 根据包裹号，当前站点查询所有分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    public List<Sorting> findByPackageCode(Sorting sorting){
        return queryByCode(sorting);
    }

    /**
     * 根据箱号，当前站点查询有限的分拣记录
     *
     * @param boxCode
     * @param createSiteCode
     * @param fetchNum
     * @return
     */
    public List<Sorting> findByBoxCodeAndFetchNum(String boxCode, int createSiteCode, int fetchNum){
        ApiResult<List<SortingObject>> apiResult = sharedSortingQueryManager.queryByContainerCodeAndFetchNum(createSiteCode,boxCode,fetchNum);
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            return sortingObject2DmsSortingBatch(apiResult.getData());
        } else {
            return null;
        }
    }

    /**
     * 根据运单号或者包裹号，当前站点查询已分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    public List<Sorting> findByWaybillCodeOrPackageCode(Sorting sorting){
        return queryByCode(sorting);
    }


    /**
     * 分页查询分拣记录
     */
    public List<Sorting> findPageSorting(Map<String, Object> params){
        PageInfo<SortingObject> pageInfo = new PageInfo<>();
        pageInfo.setPageSize(1);
        pageInfo.setPageNum(1);

        ApiResult<PageInfo<com.jd.ql.shared.services.sorting.api.dto.Sorting>> apiResult = sharedSortingQueryManager.queryByPage((Integer)params.get("createSiteCode"),(String)params.get("packageCode"),pageInfo);
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            return middleendSorting2DmsSortingBatch(apiResult.getData().getList());
        } else {
            return null;
        }
    }

    private Flow buildSortingFlow(Sorting sorting) {
        Integer createSiteCode = sorting.getCreateSiteCode();
        Integer receiveSiteCode = sorting.getReceiveSiteCode();

        Flow.FlowBuilder flowBuilder = Flow.builder();
        if (createSiteCode != null) {
            DmsCustomSite createSite = baseMajorManager.getDmsCustomSiteBySiteId(createSiteCode);
            if (createSite != null) {
                flowBuilder.fromSiteCode(createSite.getSiteCode()).fromSiteId(createSite.getSiteId()).fromSiteType(SiteType.getEunmByType(createSite.getCustomSiteType()));
            }
        }
        if (receiveSiteCode != null) {
            DmsCustomSite receiveSite = baseMajorManager.getDmsCustomSiteBySiteId(receiveSiteCode);
            if (receiveSite != null) {
                flowBuilder.toSiteCode(receiveSite.getSiteCode()).toSiteId(receiveSite.getSiteId()).toSiteType(SiteType.getEunmByType(receiveSite.getCustomSiteType()));
            }
        }

        return flowBuilder.build();
    }

    private Sorting sortingObject2DmsSorting(SortingObject sortingObject) {

        Sorting sorting = new Sorting();
        sorting.setBsendCode(sortingObject.getBatchCode());
        sorting.setWaybillCode(sortingObject.getWaybillCode());

        String objectCode = sortingObject.getObjectCode();
        if (WaybillUtil.isPackageCode(objectCode)) {
            sorting.setPackageCode(objectCode);
        }
        sorting.setPickupCode(sortingObject.getRelatedCode());

        Flow flow = sortingObject.getFlow();
        if (flow != null) {
            sorting.setCreateSiteCode(flow.getFromSiteId());
            sorting.setCreateSiteName();
            sorting.setReceiveSiteCode(flow.getToSiteId());
            sorting.setReceiveSiteCode();
        }

        sorting.setStatus(sortingObject.getStatus().type());

        sorting.setType(sortingObject.getSortingDirection().type());

        sorting.setIsCancel(sortingObject.getIsCancel() == true ? 1 : 0);

        sorting.setFeatureType();
        sorting.setIsLoss();
        return sorting;

    }

    private List<Sorting> sortingObject2DmsSortingBatch(List<SortingObject> sortingObjectList) {
        List<Sorting> sortingList = new ArrayList<>();
        for(SortingObject sortingObject : sortingObjectList){
            sortingList.add(sortingObject2DmsSorting(sortingObject));
        }
        return sortingList;

    }

    private Sorting middleendSorting2DmsSorting(com.jd.ql.shared.services.sorting.api.dto.Sorting middleendSorting) {
        Sorting sorting = new Sorting();
        if (BusinessUtil.isBoxcode(middleendSorting.getContainerCode())) {
            sorting.setBoxCode(middleendSorting.getContainerCode());
        }
        if (middleendSorting.getSortingObject() != null) {
            SortingObject sortingObject = middleendSorting.getSortingObject();
            sorting.setBsendCode(sortingObject.getBatchCode());
            sorting.setWaybillCode(sortingObject.getWaybillCode());

            String objectCode = sortingObject.getObjectCode();
            if (WaybillUtil.isPackageCode(objectCode)) {
                sorting.setPackageCode(objectCode);
            }
            sorting.setPickupCode(sortingObject.getRelatedCode());

            Flow flow = sortingObject.getFlow();
            if (flow != null) {
                sorting.setCreateSiteCode(flow.getFromSiteId());
                sorting.setCreateSiteName();
                sorting.setReceiveSiteCode(flow.getToSiteId());
                sorting.setReceiveSiteCode();
            }

            User createUser = middleendSorting.getCreateUser();
            if (createUser != null) {
                sorting.setCreateUser(createUser.getUserName());
                sorting.setCreateUserCode(Integer.valueOf(createUser.getUserIdentity()));
            }

            User updateUser = middleendSorting.getUpdateUser();
            if (updateUser != null) {
                sorting.setUpdateUser(updateUser.getUserName());
                sorting.setUpdateUserCode(Integer.valueOf(updateUser.getUserIdentity()));
            }

            sorting.setCreateTime(middleendSorting.getCreateTime());
            sorting.setUpdateTime(middleendSorting.getUpdateTime());
            sorting.setOperateTime(middleendSorting.getOperateTime());

            sorting.setStatus(sortingObject.getStatus().type());

            sorting.setType(sortingObject.getSortingDirection().type());

            sorting.setIsCancel(sortingObject.getIsCancel() == true ? 1 : 0);

            sorting.setFeatureType();
            sorting.setIsLoss();
        }
        return sorting;
    }

    private List<Sorting> middleendSorting2DmsSortingBatch(List<com.jd.ql.shared.services.sorting.api.dto.Sorting> middleendSortingList) {
        List<Sorting> sortingList = new ArrayList<>();
        for (com.jd.ql.shared.services.sorting.api.dto.Sorting middendSorting : middleendSortingList) {
            sortingList.add(middleendSorting2DmsSorting(middendSorting));
        }
        return sortingList;
    }
}
