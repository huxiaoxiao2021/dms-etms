package com.jd.bluedragon.distribution.rest.queueManagement;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.queueManagement.domain.PdaPlatformRequest;
import com.jd.bluedragon.distribution.queueManagement.domain.PlatformCallNumRequest;
import com.jd.bluedragon.distribution.queueManagement.domain.PlatformWorkRequest;
import com.jd.bluedragon.distribution.queueManagement.service.QueueManagementService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.intelligent.center.api.common.dto.PdaPlatformInfoResponseDto;
import com.jd.intelligent.center.api.common.dto.PdaPlatformRequestDto;
import com.jd.intelligent.center.api.common.dto.PlatformCallNumRequestRequestDto;
import com.jd.intelligent.center.api.common.dto.PlatformCallNumResponseDto;
import com.jd.intelligent.center.api.common.dto.PlatformQueueTaskResponseDto;
import com.jd.intelligent.center.api.common.dto.PlatformWorkRequestDto;
import com.jd.intelligent.center.api.common.enums.PlatformWorkTypeEnum;
import com.jd.intelligent.center.api.common.enums.ResourceTypeEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * 智能园区 叫号系统
 * Created by biyubo on 2019/8/21
 */

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class QueueManagementResource {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private QueueManagementService queueManagementService;

    /**
     * 获取月台、流向、车型信息
     * @param request
     * @return
     */
    @POST
    @Path("/queuemanagement/getplatforminfolist")
    @BusinessLog(sourceSys = 1,bizType = 3101)
    @JProfiler(jKey = "DMSWEB.QueueManagementResource.getPlatformInfoList", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<List<PdaPlatformInfoResponseDto>> getPlatformInfoList(PdaPlatformRequest request){
        InvokeResult<List<PdaPlatformInfoResponseDto>> result=new InvokeResult<List<PdaPlatformInfoResponseDto>>();
        if(StringUtils.isEmpty(request.getCurrentStationCode()) || request.getResourceType()<1){
            logger.error(MessageFormat.format("获取月台、流向、车型信息证接口失败-参数错误[{0}]", JsonHelper.toJson(request)));
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }
        PdaPlatformRequestDto req=new PdaPlatformRequestDto();
        req.setCurrentStationCode(request.getCurrentStationCode());
        req.setResourceTypeEnum(ResourceTypeEnum.getByValue(request.getResourceType()));
        try{
            result=queueManagementService.getPlatformInfoList(req);
        } catch (Exception ex) {
            logger.error("获取月台、流向、车型信息接口失败，原因 " + ex);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;
    }

    /**
     * 获取排队任务信息列表
     * @param request
     * @return
     */
    @POST
    @Path("/queuemanagement/getplatformqueuetasklist")
    @BusinessLog(sourceSys = 1,bizType = 3102)
    @JProfiler(jKey = "DMSWEB.QueueManagementResource.getPlatformQueueTaskList", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<List<PlatformQueueTaskResponseDto>> getPlatformQueueTaskList(PdaPlatformRequest request){
        InvokeResult<List<PlatformQueueTaskResponseDto>> result=new InvokeResult<List<PlatformQueueTaskResponseDto>>();
        if(StringUtils.isEmpty(request.getCurrentStationCode()) || StringUtils.isEmpty(request.getPlatformCode()) || request.getResourceType()<1){
            logger.error(MessageFormat.format("获取排队任务信息列表接口失败-参数错误[{0}]", JsonHelper.toJson(request)));
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("请选择月台");
            return result;
        }
        PdaPlatformRequestDto req=new PdaPlatformRequestDto();
        req.setCurrentStationCode(request.getCurrentStationCode());
        req.setPlatformCode(request.getPlatformCode());
        req.setResourceTypeEnum(ResourceTypeEnum.getByValue(request.getResourceType()));

        try{
            result=queueManagementService.getPlatformQueueTaskList(req);
        } catch (Exception ex) {
            logger.error("获取排队任务信息列表接口失败，原因 " + ex);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 校验月台是否空闲
     * @param request
     * @return
     */
    @POST
    @Path("/queuemanagement/isCoccupyPlatform")
    @BusinessLog(sourceSys = 1,bizType = 3103)
    @JProfiler(jKey = "DMSWEB.QueueManagementResource.isCoccupyPlatform", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> isCoccupyPlatform(PlatformCallNumRequest request){
        InvokeResult<Boolean> result=new InvokeResult<Boolean>();
        if(StringUtils.isEmpty(request.getPlatformCode()))
        {
            logger.error(MessageFormat.format("校验月台是否空闲接口失败-参数错误[{0}]", JsonHelper.toJson(request)));
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("请选择月台");
            return result;
        }
        PlatformCallNumRequestRequestDto req=new PlatformCallNumRequestRequestDto();
        req.setPlatformCode(request.getPlatformCode());
        req.getOperatorInfo().setOperatorUserErp(request.getOperatorInfo().getOperatorUserErp());
        req.getOperatorInfo().setCurrentStationCode(request.getOperatorInfo().getCurrentStationCode());
        req.getOperatorInfo().setResourceTypeEnum(ResourceTypeEnum.getByValue(request.getOperatorInfo().getResourceTypeEnum()));
        Date date=new Date();
        req.getOperatorInfo().setOperatorDate(date);

        try{
            result=queueManagementService.isCoccupyPlatform(req);
        } catch (Exception ex) {
            logger.error("校验月台是否空闲接口失败，原因 " + ex);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 叫号业务
     * @param request
     * @return
     */
    @POST
    @Path("/queuemanagement/callnum")
    @BusinessLog(sourceSys = 1,bizType = 3104)
    @JProfiler(jKey = "DMSWEB.QueueManagementResource.callNum", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<PlatformCallNumResponseDto> callNum(PlatformCallNumRequest request){
        InvokeResult<PlatformCallNumResponseDto> result=new InvokeResult<PlatformCallNumResponseDto>();
        if(StringUtils.isEmpty(request.getPlatformCode()) || StringUtils.isEmpty(request.getFlowCode()) || request.getCarType()<0)
        {
            logger.error(MessageFormat.format("叫号接口失败-参数错误[{0}]", JsonHelper.toJson(request)));
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }
        PlatformCallNumRequestRequestDto req=new PlatformCallNumRequestRequestDto();
        req.setPlatformCode(request.getPlatformCode());
        req.setFlowCode(request.getFlowCode());
        req.setCarTypeValue(String.valueOf(request.getCarType()));
        req.setPlatformWorkTypeEnum(getWorkTypeEnum(request.getPlatformWorkTypeEnum()));
        req.getOperatorInfo().setOperatorUserErp(request.getOperatorInfo().getOperatorUserErp());
        req.getOperatorInfo().setCurrentStationCode(request.getOperatorInfo().getCurrentStationCode());
        req.getOperatorInfo().setResourceTypeEnum(ResourceTypeEnum.getByValue(request.getOperatorInfo().getResourceTypeEnum()));
        Date date=new Date();
        req.getOperatorInfo().setOperatorDate(date);

        try{
            result=queueManagementService.callNum(req);
        } catch (Exception ex) {
            logger.error("叫号接口失败，原因 " + ex);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 作业状态修改
     * @param request
     * @return
     */
    @POST
    @Path("/queuemanagement/platformWorkFeedback")
    @BusinessLog(sourceSys = 1,bizType = 3105)
    @JProfiler(jKey = "DMSWEB.QueueManagementResource.platformWorkFeedback", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> platformWorkFeedback(PlatformWorkRequest request){
        InvokeResult<Boolean> result=new InvokeResult<Boolean>();
        if(StringUtils.isEmpty(request.getPlatformCode()) || StringUtils.isEmpty(request.getQueueTaskCode()) || request.getPlatformWorkTypeEnum()<0)
        {
            logger.error(MessageFormat.format("作业状态修改接口失败-参数错误[{0}]", JsonHelper.toJson(request)));
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }
        PlatformWorkRequestDto req=new PlatformWorkRequestDto();
        req.setPlatformCode(request.getPlatformCode());
        req.setQueueTaskCode(request.getQueueTaskCode());
        req.setPlatformWorkTypeEnum(getWorkTypeEnum(request.getPlatformWorkTypeEnum()));
        req.getOperatorInfo().setOperatorUserErp(request.getOperatorInfo().getOperatorUserErp());
        req.getOperatorInfo().setCurrentStationCode(request.getOperatorInfo().getCurrentStationCode());
        req.getOperatorInfo().setResourceTypeEnum(ResourceTypeEnum.getByValue(request.getOperatorInfo().getResourceTypeEnum()));
        Date date=new Date();
        req.getOperatorInfo().setOperatorDate(date);

        try{
            result=queueManagementService.platformWorkFeedback(req);
        } catch (Exception ex) {
            logger.error("作业状态修改接口失败，原因 " + ex);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 根据作业类型获取作业类型枚举
     * @param type
     * @return
     */
    private PlatformWorkTypeEnum getWorkTypeEnum(int type){
        PlatformWorkTypeEnum[] var1 = PlatformWorkTypeEnum.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            PlatformWorkTypeEnum en = var1[var3];
            if (en.getIndex().equals(type)) {
                return en;
            }
        }

        return null;
    }




}
