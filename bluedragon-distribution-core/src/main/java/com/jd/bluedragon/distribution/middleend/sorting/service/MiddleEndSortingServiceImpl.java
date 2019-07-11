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
import com.jd.fastjson.JSON;
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

            logger.info("中台理货接口调用参数:isHaveContainer:" + isHaveContainer + ",containerCode：" + dmsSorting.getBoxCode() + ",SortingObject: " + JSON.toJSONString(sorting) + ",operator:" + JSON.toJSONString(operator) + ",operateTime:" + dmsSorting.getOperateTime());
            if (isHaveContainer) {
                result = middleEndSortingManager.sortWithoutContainer(sorting, operator, dmsSorting.getOperateTime());
            } else {
                result = middleEndSortingManager.sort(dmsSorting.getBoxCode(), sorting, operator, dmsSorting.getOperateTime());
            }
            logger.info("中台理货接口调用结果:" + JSON.toJSONString(result));
            return result.getCode() == ApiResult.OK_CODE;
        }catch (Exception e){
            logger.error("MiddleEndSortingServiceImpl.coreSorting异常.参数:" + JSON.toJSONString(sorting),e);
            return false;
        }
    }

    /**
     * 分拣补验货
     * 1.补验货差异表inspection_ec
     * @param sorting
     */
    public void sortingAddInspection(SortingObjectExtend sorting) {
        dmsSortingService.saveOrUpdateInspectionEC(sorting.getDmsSorting());
    }

    /**
     * 分拣补发货
     * 1.补send_d表
     * 2.补发货全称跟踪
     * @param sorting
     */
    public void sortingAddSend(SortingObjectExtend sorting) {
        List<SendDetail> sendDList = new ArrayList<>();

        sendDList.add(dmsSortingService.addSendDetail(sorting.getDmsSorting()));
        //补发货
        dmsSortingService.fixSendDAndSendTrack(sorting.getDmsSorting(), sendDList);
    }

    /**
     * 分拣写操作日志
     * cassandra日志
     * @param sorting
     */
    public void sortingAddOperationLog(SortingObjectExtend sorting) {
        dmsSortingService.addOpetationLog(sorting.getDmsSorting(), OperationLog.LOG_TYPE_SORTING);
    }

    /**
     * 取件单处理
     * @param sorting
     */
    public void fillSortingIfPickup(SortingObjectExtend sorting) {
        dmsSortingService.fillSortingIfPickup(sorting.getDmsSorting());
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

            logger.info("中台取消理货接口调用参数:barCode:" + barCode + ",sortingCancelType：" + JSON.toJSONString(sortingCancelType) + ",operateSiteId: " + dmsSorting.getCreateSiteCode() + ",operator:" + JSON.toJSONString(operator) + ",operateTime:" + dmsSorting.getOperateTime());
            ApiResult<Void> result = middleEndSortingManager.cancelSorting(barCode, sortingCancelType, dmsSorting.getCreateSiteCode(), operator, dmsSorting.getOperateTime());
            logger.info("中台取消理货接口调用结果:" + JSON.toJSONString(result));

            if (result.getCode() == ApiResult.OK_CODE) {
                afterSortingCancel(dmsSorting);
                return SortingResponse.ok();
            }

            return SortingResponse.exeError();
        }catch (Exception e){
            logger.error("MiddleEndSortingServiceImpl.cancelSorting异常.参数:" + JSON.toJSONString(dmsSorting),e);
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
        //构建User对象
        User user = User.builder()
                .userIdentity(dmsSorting.getCreateUserCode().toString())
                .userName(dmsSorting.getCreateUser())
                .userSource(UserSource.QINGLONG_SYSTEM_USER)
                .build();

        UserEnv operator = UserEnv.builder().operateSortingCenterId(dmsSorting.getCreateSiteCode()).tenantCode(TENANT_CODE).user(user).build();

        return operator;
    }

}
