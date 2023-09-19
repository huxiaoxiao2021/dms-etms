package com.jd.bluedragon.distribution.external.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.sorting.request.PackSortTaskBody;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import com.jd.bluedragon.distribution.cyclebox.service.BoxMaterialRelationService;
import com.jd.bluedragon.distribution.external.service.DmsSortingService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.task.TaskResource;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingBizSourceEnum;
import com.jd.bluedragon.distribution.sorting.domain.SortingDto;
import com.jd.bluedragon.distribution.sorting.domain.SortingRequestDto;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.distribution.external.service.impl
 * @Description:
 * @date Date : 2023年09月14日 15:14
 */
@Service("dmsSortingService")
public class DmsSortingServiceImpl implements DmsSortingService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private SortingService sortingService;
    @Autowired
    private BoxMaterialRelationService boxMaterialRelationService;
    @Autowired
    private TaskResource taskResource;
    @Autowired
    private CycleBoxService cycleBoxService;
    /**
     *
     * @param packageCode
     * @param createSiteCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DmsSortingServiceImpl.findSortingByPackageCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<List<SortingDto>> findSortingByPackageCode(String packageCode, Integer createSiteCode) {
        logger.info("DmsSortingServiceImpl->findSortingByPackageCode绑定集包袋,包裹号：{}，操作单位：{}", packageCode,createSiteCode);
        InvokeResult<List<SortingDto>> result = new InvokeResult<List<SortingDto>>();
        if(!WaybillUtil.isPackageCode(packageCode)){
            result.parameterError("包裹号不合法");
            return result;
        }
        if(createSiteCode == null){
            result.parameterError("站点编号为空");
            return result;
        }
        List<Sorting> sortingList = sortingService.findByWaybillCodeOrPackageCode(createSiteCode,WaybillUtil.getWaybillCodeByPackCode(packageCode),packageCode);
        result.setData(transferSortingDtoList(sortingList));
        return result;
    }

    /**
     * 转换
     * @param sortingList
     * @return
     */
    private List<SortingDto> transferSortingDtoList(List<Sorting> sortingList) {
        if(CollectionUtils.isEmpty(sortingList)){
            return new ArrayList<>(0);
        }
        List<SortingDto> result = new ArrayList<>(sortingList.size());
        for(Sorting temp : sortingList){
            SortingDto dto = new SortingDto();
            BeanUtils.copyProperties(temp,dto);
        }
        return result;
    }

    /**
     * 分拣理货任务
     * @param request
     * @return
     */
    @Override
    public InvokeResult<String> bindingBoxMaterialPackageRelation(SortingRequestDto request) {
        InvokeResult<String> result = new InvokeResult<String>();
        if(request == null){
            result.parameterError("入参为空");
            return result;
        }
        String boxCode = request.getBoxCode();
        BoxMaterialRelation boxMaterialRelation = boxMaterialRelationService.getDataByBoxCode(boxCode);
        if(boxMaterialRelation == null){
            if(StringUtils.isBlank(request.getMaterialCode())) {
                result.parameterError("您所扫描箱号未绑定容器号，请绑定");
                return result;
            }else{
                //绑定集包袋
                BoxMaterialRelationRequest boxMaterialRelationRequest = createBoxMaterialRelationRequest(request);
                com.jd.bluedragon.distribution.base.domain.InvokeResult bindMaterialRet = cycleBoxService.boxMaterialRelationAlter(boxMaterialRelationRequest);
                if (!bindMaterialRet.codeSuccess()){
                    result.parameterError(bindMaterialRet.getMessage());
                    return result;
                }
            }
        }
        //添加分拣理货任务
        TaskRequest taskPdaRequest = createTaskPdaRequest(request);
        TaskResponse taskResponse = taskResource.add(taskPdaRequest);
        if(!Objects.equals(taskResponse.getCode(),TaskResponse.CODE_OK)){
            result.parameterError(taskResponse.getMessage());
        }
        return result;
    }

    /**
     * 绑定集包袋参数构造
     * @param request
     * @return
     */
    private BoxMaterialRelationRequest createBoxMaterialRelationRequest(SortingRequestDto request) {
        BoxMaterialRelationRequest req = new BoxMaterialRelationRequest();
        req.setBoxCode(request.getBoxCode());
        req.setMaterialCode(request.getMaterialCode());
        req.setBindFlag(Constants.CONSTANT_NUMBER_ONE);
        req.setUserCode(request.getCreateUserCode());
        req.setUserName(request.getCreateUser());
        req.setOperatorERP(request.getCreateUser());
        req.setSiteCode(request.getCreateSiteCode());
        req.setSiteName(request.getCreateSiteName());
        return req;
    }

    /**
     * 创建入参
     * @param request
     * @return
     */
    private TaskRequest createTaskPdaRequest(SortingRequestDto request) {
        PackSortTaskBody taskBody = new PackSortTaskBody();
        taskBody.setBoxCode(request.getBoxCode());
        taskBody.setBusinessType(request.getBusinessType());
        taskBody.setFeatureType(request.getFeatureType());
        taskBody.setIsCancel(request.getIsCancel());
        taskBody.setIsLoss(request.getIsLoss());
        taskBody.setSiteCode(request.getCreateSiteCode());
        taskBody.setSiteName(request.getCreateSiteName());
        taskBody.setPackageCode(request.getPackageCode());
        taskBody.setReceiveSiteCode(request.getReceiveSiteCode());
        taskBody.setReceiveSiteName(request.getReceiveSiteName());

        taskBody.setUserCode(request.getCreateUserCode());
        taskBody.setUserName(request.getCreateUser());
        taskBody.setBizSource(SortingBizSourceEnum.INTERFACE_SORTING.getCode());
        List<PackSortTaskBody> bodyList = new ArrayList<>();
        bodyList.add(taskBody);
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setBoxCode(taskBody.getBoxCode());
        taskRequest.setReceiveSiteCode(taskBody.getReceiveSiteCode());
        taskRequest.setType(Task.TASK_TYPE_SORTING);
        taskRequest.setKeyword1(String.valueOf(taskBody.getSiteCode()));
        taskRequest.setKeyword2(taskBody.getBoxCode());
        taskRequest.setBody(JSON.toJSONString(bodyList));
        taskRequest.setSiteCode(taskBody.getSiteCode());
        return taskRequest;
    }
}
