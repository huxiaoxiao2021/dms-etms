package com.jd.bluedragon.distribution.middleend.sorting.dao;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.SharedSortingQueryManager;
import com.jd.bluedragon.distribution.middleend.sorting.domain.DmsCustomSite;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.shared.services.sorting.api.ApiResult;
import com.jd.ql.shared.services.sorting.api.dto.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiddleEndSortingDao implements ISortingDao {

    private final Logger logger = Logger.getLogger(MiddleEndSortingDao.class);

    @Autowired
    private SharedSortingQueryManager sharedSortingQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    private static final Integer BATCH_SIZE = 1000;

    /**
     * 根据箱号查询分拣记录
     *
     * @param sorting
     * @return
     */
    public List<Sorting> findByBoxCode(Sorting sorting) {
        //把dmsSorting包装成middleEndSorting
        Integer type = sorting.getType();
        SortingDirection direction = null;
        if(type != null){
            direction = SortingDirection.getEunmByType(sorting.getType());
        }
        ApiResult<List<com.jd.ql.shared.services.sorting.api.dto.Sorting>> apiResult = sharedSortingQueryManager.queryByContainerAndFlow(sorting.getBoxCode(), buildSortingFlow(sorting),direction );
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            List<com.jd.ql.shared.services.sorting.api.dto.Sorting> middleEndSortingList = apiResult.getData();
            if(middleEndSortingList == null){
                logger.error("调用中台接口findByBoxCode返回data为空.sorting:" + JSON.toJSONString(sorting) + ",返回值为:" + JSON.toJSONString(apiResult));
                return null;
            }
            return middleEndSorting2DmsSortingBatch(middleEndSortingList);
        } else {
            logger.error("调用中台接口queryByContainerAndFlow失败.sorting:" + JSON.toJSONString(sorting) + ",返回值为:" + JSON.toJSONString(apiResult));
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
            logger.error("调用中台接口existSortingByPackageCode失败.sorting:" + JSON.toJSONString(sorting) + ",返回值为:" + JSON.toJSONString(apiResult));
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
            logger.error("调用中台接口queryContainerPackNum失败.createSiteCode:" + createSiteCode + ",boxCode" + boxCode + ",返回值为:" + JSON.toJSONString(apiResult));
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
            if(apiResult.getData() == null){
                logger.error("调用中台接口findBoxDescSite返回data为空.createSiteCode:" + createSiteCode + ",boxCode" + boxCode +",返回值为:" + JSON.toJSONString(apiResult));
                return null;
            }
            return middleEndSorting2DmsSorting(apiResult.getData());
        } else {
            logger.error("调用中台接口findBoxDescSite失败.createSiteCode:" + createSiteCode + ",boxCode" + boxCode + ",返回值为:" + JSON.toJSONString(apiResult));
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
            objectCode = null;
        }

        SortingDirection direction = null;
        if(sorting.getType()!=null){
            direction = SortingDirection.getEunmByType(sorting.getType());
        }

        //分页获取sorting明细列表
        ApiResult<PageBean<com.jd.ql.shared.services.sorting.api.dto.Sorting>> apiResult = null;
        PageBean<com.jd.ql.shared.services.sorting.api.dto.Sorting> pageBean = new PageBean<>();
        pageBean.setPageSize(BATCH_SIZE);
        pageBean.setPageNum(1);

        apiResult = sharedSortingQueryManager.queryPackList(sorting.getCreateSiteCode(), sorting.getWaybillCode(), objectCode, direction, pageBean);
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            PageBean<com.jd.ql.shared.services.sorting.api.dto.Sorting> resultData = apiResult.getData();
            sortingList.addAll(middleEndSorting2DmsSortingBatch(resultData.getList()));

            //读取分页数
            int totalPage = apiResult.getData().getPages();
            logger.info("调用中台接口queryPackList,分页总数" + totalPage + ",每页大小" + BATCH_SIZE);

            //循环获取剩余数据
            for (int i = 2; i <= totalPage; i++) {
                pageBean.setPageSize(i);
                apiResult = sharedSortingQueryManager.queryPackList(sorting.getCreateSiteCode(), sorting.getWaybillCode(), objectCode, direction, pageBean);
                if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
                    logger.error("调用中台接口queryPackList查询结果不成功.:" + sorting.getCreateSiteCode() +","+ sorting.getWaybillCode() +","+objectCode +"," + JSON.toJSONString(pageBean) + ",返回值为:" + JSON.toJSONString(apiResult));
                    return null;
                }
                sortingList.addAll(middleEndSorting2DmsSortingBatch(apiResult.getData().getList()));
            }

            return sortingList;
        } else {
            logger.error("调用中台接口queryPackList失败.sorting:" + JSON.toJSONString(sorting) + ",返回值为:" + JSON.toJSONString(apiResult));
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
        if(StringUtils.isBlank(objectCode)){
            return findBoxPackList(sorting);
        } else {
            SortingDirection direction = null;
            if(sorting.getType()!=null){
                direction = SortingDirection.getEunmByType(sorting.getType());
            }

            ApiResult<List<com.jd.ql.shared.services.sorting.api.dto.Sorting>> apiResult = sharedSortingQueryManager.queryBySiteCodeAndPackage(sorting.getCreateSiteCode(), sorting.getWaybillCode(), objectCode, direction);
            if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
                if(apiResult.getData() == null){
                    logger.error("调用中台接口findBoxDescSite返回data为空.sorting:" + JSON.toJSONString(sorting) +",返回值为:" + JSON.toJSONString(apiResult));
                    return null;
                }
                return middleEndSorting2DmsSortingBatch(apiResult.getData());
            } else {
                logger.error("调用中台接口queryBySiteCodeAndPackage失败.sorting:" + JSON.toJSONString(sorting) + ",返回值为:" + JSON.toJSONString(apiResult));
                return null;
            }
        }
    }

    /**
     * 根据运单号或者包裹号查询分拣明细
     * 无sortingType条件
     *
     * @param sorting
     * @return
     */
    public List<Sorting> queryByCode2(Sorting sorting) {
        return queryByCode(sorting);
    }

    /**
     * 根据批次号查询分拣明细
     *
     * @param sorting
     * @return
     */
    public List<Sorting> findByBsendCode(Sorting sorting) {
        List<Sorting> sortingList = new ArrayList<>();

        ApiResult<PageBean<com.jd.ql.shared.services.sorting.api.dto.Sorting>> apiResult = null;
        PageBean<com.jd.ql.shared.services.sorting.api.dto.Sorting> pageBean = new PageBean<>();
        pageBean.setPageSize(BATCH_SIZE);
        pageBean.setPageNum(1);

        apiResult = sharedSortingQueryManager.queryByBatchCode(sorting.getCreateSiteCode(), sorting.getBsendCode(), pageBean);
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            PageBean<com.jd.ql.shared.services.sorting.api.dto.Sorting> resultData = apiResult.getData();
            sortingList.addAll(middleEndSorting2DmsSortingBatch(resultData.getList()));

            //读取分页数
            int totalPage = apiResult.getData().getPages();
            logger.info("调用中台接口queryByBatchCode,分页总数" + totalPage + ",每页大小" + BATCH_SIZE);

            //循环获取剩余数据
            for (int i = 2; i <= totalPage; i++) {
                pageBean.setPageSize(i);
                apiResult = sharedSortingQueryManager.queryByBatchCode(sorting.getCreateSiteCode(), sorting.getBsendCode(), pageBean);
                if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
                    logger.error("调用中台接口queryByBatchCode查询结果不成功.:" + sorting.getCreateSiteCode() +","+ sorting.getBsendCode() + JSON.toJSONString(pageBean) + ",返回值为:" + JSON.toJSONString(apiResult));
                    return null;
                }
                sortingList.addAll(middleEndSorting2DmsSortingBatch(apiResult.getData().getList()));
            }

            return sortingList;
        } else {
            logger.error("调用中台接口queryByBatchCode失败.sorting:" + JSON.toJSONString(sorting) + ",返回值为:" + JSON.toJSONString(apiResult));
            return null;
        }
    }

    /**
     * 根据包裹号，当前站点查询所有分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    public List<Sorting> findByPackageCode(Sorting sorting) {
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
    public List<Sorting> findByBoxCodeAndFetchNum(String boxCode, int createSiteCode, int fetchNum) {
        ApiResult<List<com.jd.ql.shared.services.sorting.api.dto.Sorting>> apiResult = sharedSortingQueryManager.queryByContainerCodeAndFetchNum(createSiteCode, boxCode, fetchNum);
        if (apiResult != null && ApiResult.OK_CODE == apiResult.getCode()) {
            if(apiResult.getData() == null){
                logger.error("调用中台接口queryByContainerCodeAndFetchNum返回data为空.sorting:" +boxCode + ",createSiteCode:" + createSiteCode + ",fetchNum:" + fetchNum  +",返回值为:" + JSON.toJSONString(apiResult));
                return null;
            }
            return middleEndSorting2DmsSortingBatch(apiResult.getData());
        } else {
            logger.error("调用中台接口queryByBatchCode失败.boxCode:" + boxCode + ",createSiteCode:" + createSiteCode + ",fetchNum:" + fetchNum + ",返回值为:" + JSON.toJSONString(apiResult));
            return null;
        }
    }

    /**
     * 根据运单号或者包裹号，当前站点查询已分拣记录
     *
     * @param sorting 运单号
     * @return
     */
    public List<Sorting> findByWaybillCodeOrPackageCode(Sorting sorting) {
        return queryByCode(sorting);
    }

    /**
     * 构建Flow对象
     * @param sorting
     * @return
     */
    private Flow buildSortingFlow(Sorting sorting) {
        if(sorting == null){
            return null;
        }
        Integer createSiteCode = sorting.getCreateSiteCode();
        Integer receiveSiteCode = sorting.getReceiveSiteCode();

        Flow.FlowBuilder flowBuilder = Flow.builder();

        //始发
        if (createSiteCode != null) {
            DmsCustomSite createSite = baseMajorManager.getDmsCustomSiteBySiteId(createSiteCode);
            if (createSite != null) {
                flowBuilder.fromSiteCode(createSite.getSiteCode()).fromSiteId(createSite.getSiteId()).fromSiteType(SiteType.getEunmByType(createSite.getCustomSiteType()));
            }
        }
        //目的
        if (receiveSiteCode != null) {
            DmsCustomSite receiveSite = baseMajorManager.getDmsCustomSiteBySiteId(receiveSiteCode);
            if (receiveSite != null) {
                flowBuilder.toSiteCode(receiveSite.getSiteCode()).toSiteId(receiveSite.getSiteId()).toSiteType(SiteType.getEunmByType(receiveSite.getCustomSiteType()));
            }
        }

        return flowBuilder.build();
    }

    /**
     * 中台的sorting对象转换成分拣的sorting对象
     * @param middleendSorting
     * @return
     */
    private Sorting middleEndSorting2DmsSorting(com.jd.ql.shared.services.sorting.api.dto.Sorting middleendSorting) {
        Sorting sorting = new Sorting();

        if(middleendSorting == null){
            return null;
        }

        //设置箱号
        if (BusinessUtil.isBoxcode(middleendSorting.getContainerCode())) {
            sorting.setBoxCode(middleendSorting.getContainerCode());
        }

        if (middleendSorting.getSortingObject() != null) {
            SortingObject sortingObject = middleendSorting.getSortingObject();

            //设置批次、运单号、包裹号、取件单号
            sorting.setBsendCode(sortingObject.getBatchCode());
            sorting.setWaybillCode(sortingObject.getWaybillCode());

            String objectCode = sortingObject.getObjectCode();
            if (WaybillUtil.isPackageCode(objectCode)) {
                sorting.setPackageCode(objectCode);
            }
            sorting.setPickupCode(sortingObject.getRelatedCode());

            //设置始发分拣和目的分拣
            Flow flow = sortingObject.getFlow();
            if (flow != null) {
                sorting.setCreateSiteCode(flow.getFromSiteId());
                BaseStaffSiteOrgDto createSiteDto = baseMajorManager.getBaseSiteBySiteId(flow.getFromSiteId());
                if (createSiteDto != null) {
                    sorting.setCreateSiteName(createSiteDto.getSiteName());
                }

                sorting.setReceiveSiteCode(flow.getToSiteId());
                BaseStaffSiteOrgDto receiveSiteDto = baseMajorManager.getBaseSiteBySiteId(flow.getFromSiteId());
                if (receiveSiteDto != null) {
                    sorting.setReceiveSiteName(receiveSiteDto.getSiteName());
                }
            }

            //设置创建人和更新人信息
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

            //设置创建时间、更新时间、操作时间
            sorting.setCreateTime(middleendSorting.getCreateTime());
            sorting.setUpdateTime(middleendSorting.getUpdateTime());
            sorting.setOperateTime(middleendSorting.getOperateTime());

            //设置正向,逆向,三方
            if(sortingObject.getSortingDirection()!=null) {
                sorting.setType(sortingObject.getSortingDirection().type());
            }

            //设置是否取消
            sorting.setIsCancel(sortingObject.getIsCancel() == true ? 1 : 0);

            //设置isLoss、featureType
            SortingObjectStatus status = sortingObject.getStatus();
            if (SortingObjectStatus.NORMAL.equals(status)) {
                sorting.setIsLoss(status.type());
            } else if (SortingObjectStatus.LOST.equals(status)) {
                sorting.setIsLoss(status.type());
            } else if (SortingObjectStatus.TRIPARTITE_RETURN_SPARE_WAREHOUSE.equals(status)) {
                sorting.setFeatureType(status.type());
            }

            //设置status、spareReason
            sorting.setStatus(Sorting.STATUS_DONE);
            sorting.setSpareReason(sortingObject.getComment());

        }
        return sorting;
    }

    /**
     * 中台的sorting对象转换成分拣的sorting对象
     * 批量操作
     * @param middleendSortingList
     * @return
     */
    private List<Sorting> middleEndSorting2DmsSortingBatch(List<com.jd.ql.shared.services.sorting.api.dto.Sorting> middleendSortingList) {
        List<Sorting> sortingList = new ArrayList<>();

        if(middleendSortingList==null){
            return null;
        }
        for (com.jd.ql.shared.services.sorting.api.dto.Sorting middendSorting : middleendSortingList) {
            sortingList.add(middleEndSorting2DmsSorting(middendSorting));
        }
        return sortingList;
    }

    @Override
    public List<Sorting> findPackageCodesByWaybillCode(Sorting sorting) {
        ApiResult<List<com.jd.ql.shared.services.sorting.api.dto.Sorting>> result = sharedSortingQueryManager.findPackageBoxCodesByWaybillCode(sorting.getCreateSiteCode(), sorting.getWaybillCode());
        if(result.getCode() == ApiResult.OK_CODE){
            if(CollectionUtils.isNotEmpty(result.getData())){
                List<Sorting> sortingList = new ArrayList<>(result.getData().size());
                for(com.jd.ql.shared.services.sorting.api.dto.Sorting entry : result.getData()){
                    Sorting dmsSorting = new Sorting();
                    dmsSorting.setBoxCode(entry.getContainerCode());
                    dmsSorting.setPackageCode(entry.getSortingObject().getObjectCode());
                    sortingList.add(dmsSorting);
                }
                return sortingList;
            }
        }else{
            logger.error("调用中台接口findPackageCodesByWaybillCode失败.createSiteCode:" + sorting.getCreateSiteCode() + ",waybillCode:" + sorting.getWaybillCode() + ",返回值为:" + JSON.toJSONString(result));
        }

        return Collections.EMPTY_LIST;
    }
}
