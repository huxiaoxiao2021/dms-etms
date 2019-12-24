package com.jd.bluedragon.distribution.middleend.sorting.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.middleend.sorting.domain.SortingObjectExtend;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.middleend.manager.MiddleEndSortingManager;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.alibaba.fastjson.JSON;
import com.jd.ql.shared.services.sorting.api.ApiResult;
import com.jd.ql.shared.services.sorting.api.dto.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("middleEndSortingService")
public class MiddleEndSortingServiceImpl extends BaseSortingService implements ISortingService, MiddleEndSortingService {
    @Autowired
    private MiddleEndSortingManager middleEndSortingManager;

    @Autowired
    private DeliveryService deliveryService;

    //调中台的租户编码
    private static final String TENANT_CODE = "cn";

    /**
     * 分拣核心操作
     * 纯中台
     * @param sorting
     * @return
     */
    public boolean coreSorting(SortingObjectExtend sorting) {
        try {
            Sorting dmsSorting = sorting.getDmsSorting();

            //判断是否是有容器的
            boolean isHaveContainer = false;
            if (BusinessUtil.isBoxcode(dmsSorting.getBoxCode())) {
                isHaveContainer = true;
            }

            UserEnv operator = buildUserInfo(dmsSorting);

            ApiResult<Void> result = null;

            SortingObject sortingObject = sorting.getMiddleEndSorting();
            if(log.isInfoEnabled()){
                log.info("中台理货接口调用参数:isHaveContainer:{},containerCode：{},SortingObject: {},operator:{},operateTime:{}"
                        ,isHaveContainer,dmsSorting.getBoxCode(),JSON.toJSONString(sortingObject),JSON.toJSONString(operator), dmsSorting.getOperateTime());
            }
            if (isHaveContainer) {
                result = middleEndSortingManager.sort(dmsSorting.getBoxCode(), sortingObject, operator, dmsSorting.getOperateTime());
            } else {
                result = middleEndSortingManager.sortWithoutContainer(sortingObject, operator, dmsSorting.getOperateTime());
            }
            if(log.isInfoEnabled()){
                log.info("中台理货接口调用结果:{}" , JSON.toJSONString(result));
            }
            return result.getCode() == ApiResult.OK_CODE;
        }catch (Exception e){
            log.error("MiddleEndSortingServiceImpl.coreSorting异常.参数:{}", JSON.toJSONString(sorting),e);
            return false;
        }
    }

    /**
     * 取消分拣
     * @param dmsSorting
     */
    public SortingResponse cancelSorting(Sorting dmsSorting) {
        try {
            //先校验
            SortingResponse response = cancelSortingCheck(dmsSorting);
            if (!SortingResponse.CODE_OK.equals(response.getCode())) {
                return response;
            }

            //判断类型
            String barCode = "";
            SortingCancelObjectType sortingCancelType = null;
            if ((StringUtils.isNotBlank(dmsSorting.getBoxCode()))) {
                barCode = dmsSorting.getBoxCode();
                sortingCancelType = SortingCancelObjectType.CONTAINER;
            } else if (StringUtils.isNotBlank(dmsSorting.getPackageCode())) {
                barCode = dmsSorting.getPackageCode();
                sortingCancelType = SortingCancelObjectType.PACKAGE;
            } else if (StringUtils.isNotBlank(dmsSorting.getWaybillCode())) {
                barCode = dmsSorting.getWaybillCode();
                sortingCancelType = SortingCancelObjectType.WAYBILL;
            }

            UserEnv operator = buildUserInfo(dmsSorting);

            SortingCancelObject cancelObject = new SortingCancelObject();
            cancelObject.setCancelObjectCode(barCode);
            cancelObject.setCancelObjectType(sortingCancelType);
            cancelObject.setCancelDirection(SortingDirection.getEunmByType(dmsSorting.getType()));

            if(log.isInfoEnabled()){
                log.info("中台取消理货接口调用参数:barCode:{},sortingCancelType：{},operateSiteId:{},operator:{},operateTime:{}"
                        ,barCode,JSON.toJSONString(sortingCancelType),dmsSorting.getCreateSiteCode(),JSON.toJSONString(operator), dmsSorting.getOperateTime());
            }
            ApiResult<Void> result = middleEndSortingManager.cancelSorting(cancelObject, operator, dmsSorting.getOperateTime());
            if(log.isInfoEnabled()){
                log.info("中台取消理货接口调用结果:{}", JSON.toJSONString(result));
            }

            if (result.getCode() == ApiResult.OK_CODE) {
                afterSortingCancel(dmsSorting);
                return SortingResponse.ok();
            }

            return SortingResponse.exeError();
        }catch (Exception e){
            log.error("MiddleEndSortingServiceImpl.cancelSorting异常.参数:{}", JSON.toJSONString(dmsSorting),e);
            return SortingResponse.exeError();
        }

    }

    /**
     * 检查是否可以取消理货
     * @param dmsSorting
     * @return
     */
    private  SortingResponse cancelSortingCheck(Sorting dmsSorting){
        List<Sorting>  sortingRecords = new ArrayList<>();
        return dmsSortingService.getSortingRecords(dmsSorting,sortingRecords);
    }

    /**
     * 取消理货成功后的操作 1.更新发货 2.更新三方验货差异表 3.记录canssandra日志
     * @param dmsSorting
     */
    private void afterSortingCancel(Sorting dmsSorting){
        List<Sorting> dmsSortingList = new ArrayList<>();
        if (StringUtils.isNotBlank(dmsSorting.getBoxCode())) {
            dmsSortingList.addAll(dmsSortingService.findByBoxCode(dmsSorting));
        } else {
            dmsSortingList.add(dmsSorting);
        }

        for(Sorting dmsSortingItem : dmsSortingList){
            dmsSortingItem.setOperateTime(dmsSorting.getOperateTime());
            dmsSortingItem.setUpdateUserCode(dmsSorting.getUpdateUserCode());
            dmsSortingItem.setUpdateUser(dmsSorting.getUpdateUser());

            deliveryService.canCancel2(parseSendDetail(dmsSortingItem));
            dmsSortingService.addOpetationLog(dmsSortingItem, OperationLog.LOG_TYPE_SORTING_CANCEL);
            if (Constants.BUSSINESS_TYPE_THIRD_PARTY == dmsSortingItem.getType()) {
                // 更新三方验货异常比对表，由少验修改为正常
                dmsSortingService.canCancelInspectionEC(dmsSortingItem);
            }
        }
    }

    /**
     * sorting对象转换成sendD对象
     * @param dmsSorting
     * @return
     */
    private SendDetail parseSendDetail(Sorting dmsSorting) {
        SendDetail sendDetail = new SendDetail();
        sendDetail.setSendType(dmsSorting.getType());
        sendDetail.setCreateSiteCode(dmsSorting.getCreateSiteCode());
        sendDetail.setPackageBarcode(dmsSorting.getPackageCode());
        sendDetail.setWaybillCode(dmsSorting.getWaybillCode());
        if (StringUtils.isNotBlank(dmsSorting.getBoxCode())) {
            sendDetail.setBoxCode(dmsSorting.getBoxCode());
        }
        return sendDetail;
    }


    /**
     * 构建User信息
     * @param dmsSorting
     * @return
     */
    private UserEnv buildUserInfo(Sorting dmsSorting){
        if(dmsSorting == null){
            return null;
        }
        Integer userCode = dmsSorting.getCreateUserCode();
        String  userName = dmsSorting.getCreateUser();
        if(userCode == null || userCode <=0){
            userCode = dmsSorting.getUpdateUserCode();
            userName = dmsSorting.getUpdateUser();
        }
        //构建User对象
        User user = User.builder()
                .userIdentity(String.valueOf(userCode))
                .userName(userName)
                .userSource(UserSource.QINGLONG_SYSTEM_USER)
                .build();

        UserEnv operator = UserEnv.builder().operateSortingCenterId(dmsSorting.getCreateSiteCode()).tenantCode(TENANT_CODE).user(user).build();

        return operator;
    }
}
