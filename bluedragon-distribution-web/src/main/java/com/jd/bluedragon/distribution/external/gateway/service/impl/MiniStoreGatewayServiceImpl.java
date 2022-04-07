package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.ResponseCodeMapping;
import com.jd.bluedragon.common.dto.ministore.*;
import com.jd.bluedragon.common.task.MiniStoreSyncBindRelationTask;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.ministore.domain.MiniStoreBindRelation;
import com.jd.bluedragon.distribution.ministore.dto.DeviceDto;
import com.jd.bluedragon.distribution.ministore.dto.QueryTaskDto;
import com.jd.bluedragon.distribution.ministore.dto.SealBoxDto;
import com.jd.bluedragon.distribution.ministore.enums.MSDeviceBindEventTypeEnum;
import com.jd.bluedragon.distribution.ministore.service.MiniStoreService;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.enums.SwDeviceStatusEnum;
import com.jd.bluedragon.external.gateway.service.MiniStoreGatewayService;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.cmp.jsf.SwDeviceJsfService;
import com.jd.jddl.common.utils.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

@UnifiedExceptionProcess
public class MiniStoreGatewayServiceImpl implements MiniStoreGatewayService {

    @Autowired
    MiniStoreService miniStoreService;
    @Autowired
    SwDeviceJsfService swDeviceJsfService;
    @Autowired
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    SortingService sortingService;
    @Autowired
    @Qualifier("miniStoreSealBoxProducer")
    private DefaultJMQProducer miniStoreSealBoxProducer;

    @Override
    public JdCResponse validateDeviceStatus(DeviceStatusValidateReq request) {
        //调用保温箱jsf接口查询报文箱子可用状态
        Assert.assertNotNull(request);
        if (ObjectHelper.isNotNull(request.getStoreCode())) {
            Integer availableStatus = swDeviceJsfService.isDeviceUse(request.getStoreCode());
            if (!SwDeviceStatusEnum.AVAILABLE.getCode().equals(availableStatus)) {
                return JdCResponse.errorResponse(ResponseCodeMapping.MINI_STORE_IS_NOT_AVAILABLE);
            }
            Boolean hasBeenBind = miniStoreService.validateStoreBindStatus(request.getStoreCode());
            if (hasBeenBind) {
                return JdCResponse.errorResponse(ResponseCodeMapping.MINI_STORE_HASBEEN_BIND);
            }
        }
        if (ObjectHelper.isNotNull(request.getIceBoardCode())) {
            Integer availableStatus = swDeviceJsfService.isDeviceUse(request.getIceBoardCode());
            if (!SwDeviceStatusEnum.AVAILABLE.getCode().equals(availableStatus)) {
                return JdCResponse.errorResponse(ResponseCodeMapping.INCE_BOARD_IS_NOT_AVAILABLE);
            }
            Boolean hasBeenBind = miniStoreService.validateIceBoardBindStatus(request.getIceBoardCode());
            if (hasBeenBind) {
                return JdCResponse.errorResponse(ResponseCodeMapping.INCE_BOARD_HASBEEN_BIND);
            }
        }
        if (ObjectHelper.isNotNull(request.getBoxCode())) {
            Boolean hasBeenBind = miniStoreService.validateBoxBindStatus(request.getBoxCode());
            if (hasBeenBind) {
                return JdCResponse.errorResponse(ResponseCodeMapping.BOX_HASBEEN_BIND);
            }
        }
        return JdCResponse.successResponse();
    }

    @Override
    public JdCResponse bindMiniStoreDevice(BindMiniStoreDeviceReq request) {
        DeviceDto deviceDto = BeanUtils.copy(request, DeviceDto.class);
        Boolean bindStatus = miniStoreService.validatDeviceBindStatus(deviceDto);
        if (null != bindStatus && bindStatus) {
            return JdCResponse.errorResponse(ResponseCodeMapping.DEVICE_HASBEEN_BIND);
        }
        Boolean bindSuccess = miniStoreService.bindMiniStoreDevice(deviceDto);
        if (bindSuccess) {
            return JdCResponse.successResponse();
        }
        return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR);
    }

    @Override
    public JdCResponse sealBox(SealBoxReq sealBoxReq) {
        SealBoxDto sealBoxDto = BeanUtils.copy(sealBoxReq, SealBoxDto.class);
        Boolean success = miniStoreService.updateProcessStatusAndSyncMsg(sealBoxDto);
        if (success) {
            MiniStoreSyncBindRelationTask task = new MiniStoreSyncBindRelationTask(MSDeviceBindEventTypeEnum.SEAL_BOX, sealBoxDto.getMiniStoreBindRelationId(), miniStoreSealBoxProducer, miniStoreService, sortingService);
            taskExecutor.execute(task);
            return JdCResponse.successResponse();
        }
        return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR);
    }

    @Override
    public JdCResponse<Integer> querySortCount(String boxCode) {
        Integer count = miniStoreService.queryMiniStoreSortCount();
        if (count != null) {
            return JdCResponse.successResponse(count);
        }
        return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR);
    }

    @Override
    public JdCResponse<UnBoxValidateResp> unBoxValidateBindRelation(UnBoxValidateReq unBoxValidateReq) {
        DeviceDto deviceDto = BeanUtils.copy(unBoxValidateReq, DeviceDto.class);
        MiniStoreBindRelation miniStoreBindRelation = miniStoreService.selectBindRelation(deviceDto);
        if (null != miniStoreBindRelation) {
            UnBoxValidateResp unBoxValidateResp = BeanUtils.copy(miniStoreBindRelation, UnBoxValidateResp.class);
            unBoxValidateResp.setMiniStoreBindRelationId(miniStoreBindRelation.getId());
            return JdCResponse.successResponse(unBoxValidateResp);
        }
        return JdCResponse.errorResponse(ResponseCodeMapping.NO_LEGAL_BIND_RELATIONSHIP);
    }

    @Override
    public JdCResponse validateSortRelation(ValidateSortRelationReq validateSortRelationReq) {
        boolean success = miniStoreService.validateSortRelation(validateSortRelationReq.getBoxCode(), validateSortRelationReq.getPackageCode(), validateSortRelationReq.getCreateSiteCode());
        if (success) {
            return JdCResponse.successResponse();
        }
        return JdCResponse.errorResponse(ResponseCodeMapping.NO_BIND_RELATION_BETWEEN_BOX_AND_PACKAGE);
    }

    @Override
    public JdCResponse unBox(UnBoxReq unBoxReq) {
        DeviceDto deviceDto = BeanUtils.copy(unBoxReq, DeviceDto.class);
        Boolean success = miniStoreService.updateProcessStatusAndInvaliSortRealtion(deviceDto);
        if (success) {
            MiniStoreSyncBindRelationTask task = new MiniStoreSyncBindRelationTask(MSDeviceBindEventTypeEnum.SEAL_BOX, unBoxReq.getMiniStoreBindRelationId(), miniStoreSealBoxProducer, miniStoreService, sortingService);
            taskExecutor.execute(task);
            return JdCResponse.successResponse();
        }
        return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR);
    }

    @Override
    public JdCResponse<List<BindAndNoSortTaskResp>> queryBindAndNoSortTaskList(BindAndNoSortTaskReq bindAndNoSortTaskReq) {
        QueryTaskDto queryTaskDto = new QueryTaskDto();
        queryTaskDto.setCreateUserCode(bindAndNoSortTaskReq.getCreateUserCode());
        Page page =PageHelper.startPage(bindAndNoSortTaskReq.getPageNo(), bindAndNoSortTaskReq.getPageSize());
        List<MiniStoreBindRelation> miniStoreBindRelationList = miniStoreService.queryBindAndNoSortTaskList(queryTaskDto);
        if (miniStoreBindRelationList != null && miniStoreBindRelationList.size() > 0) {
            List<BindAndNoSortTaskResp> bindAndNoSortTaskRespList = BeanUtils.copy(miniStoreBindRelationList, BindAndNoSortTaskResp.class);
            PageObject<BindAndNoSortTaskResp> pageObject = new PageObject.Builder().pageNo(page.getPageNum())
                .pageSize(page.getPageSize()).offset(page.getStartRow()).totalElements(page.getTotal())
                .totalPages(page.getPages()).data(bindAndNoSortTaskRespList).build();
            return JdCResponse.successResponse(pageObject);
        }
        return JdCResponse.errorResponse(ResponseCodeMapping.NO_BIND_DATA);
    }

    @Override
    public JdCResponse unBind(UnBindReq unBindReq) {
        boolean success = miniStoreService.unBind(unBindReq.getMiniStoreBindRelationId(), unBindReq.getUpdateUserCode(), unBindReq.getUpdateUser());
        if (success) {
            return JdCResponse.successResponse();
        }
        return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR);
    }

}
