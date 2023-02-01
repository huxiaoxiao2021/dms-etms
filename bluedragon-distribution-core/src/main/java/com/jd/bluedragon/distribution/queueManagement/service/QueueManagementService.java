package com.jd.bluedragon.distribution.queueManagement.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.intelligent.center.api.common.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/** 只能园区业务实现类 */
@Service
public class QueueManagementService {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取月台、流向、车型信息
     * @param request
     * @return
     */
  public InvokeResult<List<PdaPlatformInfoResponseDto>> getPlatformInfoList(PdaPlatformRequestDto request) {
    InvokeResult<List<PdaPlatformInfoResponseDto>> res = new InvokeResult<List<PdaPlatformInfoResponseDto>>();

    try{
        res.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        res.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        List<PdaPlatformInfoResponseDto> list = Lists.newArrayList();
        res.setData(list);
    } catch (Exception ex) {
      log.error("获取月台、流向、车型信息接口失败，JSF入参 {}" , JsonHelper.toJson(request),ex);
      res.setCode(InvokeResult.SERVER_ERROR_CODE);
      res.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
    }
    return res;
  }

  /**
   *获取排队任务信息列表
   * @param request
   * @return
   */
  public InvokeResult<List<PlatformQueueTaskResponseDto>> getPlatformQueueTaskList(PdaPlatformRequestDto request)
  {
    InvokeResult<List<PlatformQueueTaskResponseDto>> res = new InvokeResult<List<PlatformQueueTaskResponseDto>>();

    try {
        res.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        res.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        List<PlatformQueueTaskResponseDto> list = Lists.newArrayList();
        res.setData(list);
    } catch (Exception ex) {
      log.error("获取排队任务信息列表接口失败，JSF入参 {}" , JsonHelper.toJson(request), ex);
      res.setCode(InvokeResult.SERVER_ERROR_CODE);
      res.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
    }
    return res;
  }

    /**
     * 叫号
     * @param request
     * @return
     */
    public InvokeResult<PlatformCallNumResponseDto> callNum(PlatformCallNumRequestDto request)
    {
        InvokeResult<PlatformCallNumResponseDto> res = new InvokeResult<PlatformCallNumResponseDto>();

        try {
            res.setCode(InvokeResult.RESULT_SUCCESS_CODE);
            res.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            PlatformCallNumResponseDto platformCallNumResponseDto = new PlatformCallNumResponseDto();
            res.setData(platformCallNumResponseDto);
        } catch (Exception ex) {
            log.error("叫号接口失败，JSF入参 {}" ,JsonHelper.toJson(request),ex);
            res.setCode(InvokeResult.SERVER_ERROR_CODE);
            res.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return res;
    }

    /**
     * 校验月台是否空闲
     * @param request
     * @return
     */
    public InvokeResult<Boolean> isCoccupyPlatform(PlatformCallNumRequestDto request)
    {
        InvokeResult<Boolean> res = new InvokeResult<Boolean>();

        try {
            res.setCode(InvokeResult.RESULT_SUCCESS_CODE);
            res.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            res.setData(false);
        } catch (Exception ex) {
            log.error("校验月台是否空闲接口失败,JSF入参 {}",JsonHelper.toJson(request), ex);
            res.setCode(InvokeResult.SERVER_ERROR_CODE);
            res.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return res;
    }

    /**
     * 作业状态修改
     * @param request
     * @return
     */
    public InvokeResult<Boolean> platformWorkFeedback(PlatformWorkRequestDto request)
    {
        InvokeResult<Boolean> res = new InvokeResult<Boolean>();

        try {
            res.setCode(InvokeResult.RESULT_SUCCESS_CODE);
            res.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            res.setData(false);
        } catch (Exception ex) {
            log.error("作业状态修改接口失败，JSF入参 {}" , JsonHelper.toJson(request), ex);
            res.setCode(InvokeResult.SERVER_ERROR_CODE);
            res.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return res;
    }
}
