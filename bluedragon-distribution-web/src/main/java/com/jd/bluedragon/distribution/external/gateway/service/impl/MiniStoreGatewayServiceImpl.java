package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
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
import com.jd.bluedragon.external.gateway.service.MiniStoreGatewayService;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.cmp.jsf.SwDeviceJsfService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@UnifiedExceptionProcess
public class MiniStoreGatewayServiceImpl implements MiniStoreGatewayService {

    @Autowired
    MiniStoreService miniStoreService;
    @Autowired
    SwDeviceJsfService swDeviceJsfService;
    @Autowired
    SortingService sortingService;
    @Autowired
    @Qualifier("miniStoreSealBoxProducer")
    private DefaultJMQProducer miniStoreSealBoxProducer;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.MiniStoreGatewayServiceImpl.validateDeviceStatus", mState = {JProEnum.TP})
    public JdCResponse validateDeviceStatus(DeviceStatusValidateReq request) {
        DeviceDto deviceDto = BeanUtils.copy(request, DeviceDto.class);
        boolean avaiable = miniStoreService.validateDeviceCodeStatus(deviceDto);
        if (avaiable) {
            return new JdCResponse(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage());
        }
        return new JdCResponse(MSCodeMapping.UNKNOW_ERROR.getCode(), MSCodeMapping.UNKNOW_ERROR.getMessage());
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.MiniStoreGatewayServiceImpl.bindMiniStoreDevice", mState = {JProEnum.TP})
    public JdCResponse bindMiniStoreDevice(BindMiniStoreDeviceReq request) {
        DeviceDto deviceDto = BeanUtils.copy(request, DeviceDto.class);
        Boolean bindSuccess = miniStoreService.bindMiniStoreDevice(deviceDto);
        if (bindSuccess) {
            return new JdCResponse(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage());
        }
        return new JdCResponse(MSCodeMapping.UNKNOW_ERROR.getCode(), MSCodeMapping.UNKNOW_ERROR.getMessage());
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.MiniStoreGatewayServiceImpl.sealBox", mState = {JProEnum.TP})
    public JdCResponse sealBox(SealBoxReq sealBoxReq) {
        SealBoxDto sealBoxDto = BeanUtils.copy(sealBoxReq, SealBoxDto.class);
        Boolean success = miniStoreService.updateProcessStatusAndSyncMsg(sealBoxDto);
        if (success) {
            MiniStoreSyncBindRelationTask task = new MiniStoreSyncBindRelationTask(MSDeviceBindEventTypeEnum.SEAL_BOX, sealBoxDto.getMiniStoreBindRelationId(), miniStoreSealBoxProducer, miniStoreService, sortingService);
            task.run();
            return new JdCResponse(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage());
        }
        return new JdCResponse(MSCodeMapping.UNKNOW_ERROR.getCode(), MSCodeMapping.UNKNOW_ERROR.getMessage());
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.MiniStoreGatewayServiceImpl.querySortCount", mState = {JProEnum.TP})
    public JdCResponse<Integer> querySortCount(String boxCode) {
        Integer count = miniStoreService.queryMiniStoreSortCount();
        if (count != null) {
            return new JdCResponse(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage(),count);
        }
        return new JdCResponse(MSCodeMapping.UNKNOW_ERROR.getCode(), MSCodeMapping.UNKNOW_ERROR.getMessage());
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.MiniStoreGatewayServiceImpl.unBoxValidateBindRelation", mState = {JProEnum.TP})
    public JdCResponse<UnBoxValidateResp> unBoxValidateBindRelation(UnBoxValidateReq unBoxValidateReq) {
        DeviceDto deviceDto = BeanUtils.copy(unBoxValidateReq, DeviceDto.class);
        MiniStoreBindRelation miniStoreBindRelation = miniStoreService.selectBindRelation(deviceDto);
        if (null != miniStoreBindRelation) {
            UnBoxValidateResp unBoxValidateResp = BeanUtils.copy(miniStoreBindRelation, UnBoxValidateResp.class);
            unBoxValidateResp.setMiniStoreBindRelationId(miniStoreBindRelation.getId());
            return new JdCResponse(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage(),unBoxValidateResp);
        }
        return new JdCResponse(MSCodeMapping.NO_LEGAL_BIND_RELATIONSHIP.getCode(), MSCodeMapping.NO_LEGAL_BIND_RELATIONSHIP.getMessage());
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.MiniStoreGatewayServiceImpl.validateSortRelation", mState = {JProEnum.TP})
    public JdCResponse validateSortRelation(ValidateSortRelationReq validateSortRelationReq) {
        boolean success = miniStoreService.validateSortRelation(validateSortRelationReq.getBoxCode(), validateSortRelationReq.getPackageCode(), validateSortRelationReq.getCreateSiteCode());
        if (success) {
            return new JdCResponse(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage());
        }
        return new JdCResponse(MSCodeMapping.NO_BIND_RELATION_BETWEEN_BOX_AND_PACKAGE.getCode(), MSCodeMapping.NO_BIND_RELATION_BETWEEN_BOX_AND_PACKAGE.getMessage());
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.MiniStoreGatewayServiceImpl.unBox", mState = {JProEnum.TP})
    public JdCResponse<List<String>> unBox(UnBoxReq unBoxReq) {
        DeviceDto deviceDto = BeanUtils.copy(unBoxReq, DeviceDto.class);
        boolean success = miniStoreService.updateProcessStatusAndInvaliSortRealtion(deviceDto);
        if (success) {
            MiniStoreSyncBindRelationTask task = new MiniStoreSyncBindRelationTask(MSDeviceBindEventTypeEnum.UN_SEAL_BOX, unBoxReq.getMiniStoreBindRelationId(), miniStoreSealBoxProducer, miniStoreService, sortingService);
            task.run();
            return new JdCResponse(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage(),task.getPackageCodes());
        }
        return new JdCResponse(MSCodeMapping.UNKNOW_ERROR.getCode(), MSCodeMapping.UNKNOW_ERROR.getMessage());
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.MiniStoreGatewayServiceImpl.queryBindAndNoSortTaskList", mState = {JProEnum.TP})
    public JdCResponse<PageObject<BindAndNoSortTaskResp>> queryBindAndNoSortTaskList(BindAndNoSortTaskReq bindAndNoSortTaskReq) {
        QueryTaskDto queryTaskDto = new QueryTaskDto();
        queryTaskDto.setCreateUserCode(bindAndNoSortTaskReq.getCreateUserCode());
        Page page = PageHelper.startPage(bindAndNoSortTaskReq.getPageNo(), bindAndNoSortTaskReq.getPageSize());
        List<MiniStoreBindRelation> miniStoreBindRelationList = miniStoreService.queryBindAndNoSortTaskList(queryTaskDto);
        if (miniStoreBindRelationList != null && miniStoreBindRelationList.size() > 0) {
            List<BindAndNoSortTaskResp> bindAndNoSortTaskRespList = BeanUtils.copy(miniStoreBindRelationList, BindAndNoSortTaskResp.class);
            PageObject<BindAndNoSortTaskResp> pageObject = new PageObject.Builder().pageNo(page.getPageNum()).pageSize(page.getPageSize()).offset(page.getStartRow()).totalElements(page.getTotal()).totalPages(page.getPages()).data(bindAndNoSortTaskRespList).build();
            return new JdCResponse(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage(),pageObject);
        }
        return new JdCResponse(MSCodeMapping.NO_BIND_DATA.getCode(), MSCodeMapping.NO_BIND_DATA.getMessage());
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.MiniStoreGatewayServiceImpl.unBind", mState = {JProEnum.TP})
    public JdCResponse unBind(UnBindReq unBindReq) {
        boolean success = miniStoreService.unBind(unBindReq.getMiniStoreBindRelationId(), unBindReq.getUpdateUserCode(), unBindReq.getUpdateUser());
        if (success) {
            return new JdCResponse(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage());
        }
        return new JdCResponse(MSCodeMapping.UNKNOW_ERROR.getCode(), MSCodeMapping.UNKNOW_ERROR.getMessage());
    }

    @Override
    public JdCResponse incrSortCount(IncrSortCountReq req) {
        int success =miniStoreService.incrSortCount(req.getId(),req.getUpdateUser(),req.getUpdateUserCode());
        if (success>0){
            return new JdCResponse(MSCodeMapping.SUCCESS.getCode(), MSCodeMapping.SUCCESS.getMessage());
        }
        return new JdCResponse(MSCodeMapping.UNKNOW_ERROR.getCode(), MSCodeMapping.UNKNOW_ERROR.getMessage());
    }

}
