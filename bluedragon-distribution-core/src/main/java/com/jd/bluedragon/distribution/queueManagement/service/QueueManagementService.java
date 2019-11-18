package com.jd.bluedragon.distribution.queueManagement.service;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.intelligent.center.api.common.dto.PdaPlatformInfoResponseDto;
import com.jd.intelligent.center.api.common.dto.PdaPlatformRequestDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.intelligent.center.api.common.dto.PlatformCallNumRequestDto;
import com.jd.intelligent.center.api.common.dto.PlatformCallNumResponseDto;
import com.jd.intelligent.center.api.common.dto.PlatformQueueTaskResponseDto;
import com.jd.intelligent.center.api.common.dto.PlatformWorkRequestDto;
import com.jd.intelligent.center.api.service.IPdaDispatchJsfService;
import com.jd.intelligent.common.model.vo.WebResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/** 只能园区业务实现类 */
@Service
public class QueueManagementService {
  private final Log logger = LogFactory.getLog(this.getClass());

  @Autowired
  private IPdaDispatchJsfService pdaDispatchJsfService;

    /**
     * 获取月台、流向、车型信息
     * @param request
     * @return
     */
  public InvokeResult<List<PdaPlatformInfoResponseDto>> getPlatformInfoList(PdaPlatformRequestDto request) {
    InvokeResult<List<PdaPlatformInfoResponseDto>> res = new InvokeResult<List<PdaPlatformInfoResponseDto>>();

    try{
        WebResult<List<PdaPlatformInfoResponseDto>> dt = pdaDispatchJsfService.getPlatformInfoList(request);
        if (dt == null) {
            res.setCode(InvokeResult.SERVER_ERROR_CODE);
            res.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        else {
            if (dt.isSuccess()){
                res.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                res.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
                res.setData(dt.getData());
            }else {
                res.setCode(dt.getResultCode());
                res.setMessage(dt.getResultMessage());
                res.setData(dt.getData());
            }
        }
    } catch (Exception ex) {
      logger.error("获取月台、流向、车型信息接口失败，原因 " + ex + "JSF入参 " + JsonHelper.toJson(request));
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
      WebResult<List<PlatformQueueTaskResponseDto>> dt = pdaDispatchJsfService.getPlatformQueueTaskList(request);
      if (dt == null) {
        res.setCode(InvokeResult.SERVER_ERROR_CODE);
        res.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
      } else {
        if (dt.isSuccess()) {
          res.setCode(InvokeResult.RESULT_SUCCESS_CODE);
          res.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
          res.setData(dt.getData());
        } else {
          res.setCode(dt.getResultCode());
          res.setMessage(dt.getResultMessage());
          res.setData(dt.getData());
        }
      }
    } catch (Exception ex) {
      logger.error("获取排队任务信息列表接口失败，原因 " + ex + "JSF入参 " + JsonHelper.toJson(request));
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
            WebResult<PlatformCallNumResponseDto> dt = pdaDispatchJsfService.callNum(request);
            if (dt == null) {
                res.setCode(InvokeResult.SERVER_ERROR_CODE);
                res.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            } else {
                if (dt.isSuccess()) {
                    res.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                    res.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
                    res.setData(dt.getData());
                } else {
                    res.setCode(dt.getResultCode());
                    res.setMessage(dt.getResultMessage());
                    res.setData(dt.getData());
                }
            }
        } catch (Exception ex) {
            logger.error("叫号接口失败，原因 " + ex + "JSF入参 " + JsonHelper.toJson(request));
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
            WebResult<Boolean> dt = pdaDispatchJsfService.isCoccupyPlatform(request.getPlatformCode(),request.getOperatorInfo());
            if (dt == null) {
                res.setCode(InvokeResult.SERVER_ERROR_CODE);
                res.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            } else {
                if (dt.isSuccess()) {
                    res.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                    res.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
                    res.setData(dt.getData());
                } else {
                    res.setCode(dt.getResultCode());
                    res.setMessage(dt.getResultMessage());
                    res.setData(dt.getData());
                }
            }
        } catch (Exception ex) {
            logger.error("校验月台是否空闲接口失败，原因 " + ex + "JSF入参 " + JsonHelper.toJson(request));
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
            WebResult dt = pdaDispatchJsfService.platformWorkFeedback(request);
            if (dt == null) {
                res.setCode(InvokeResult.SERVER_ERROR_CODE);
                res.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            } else {
                if (dt.isSuccess()) {
                    res.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                    res.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
                } else {
                    res.setCode(dt.getResultCode());
                    res.setMessage(dt.getResultMessage());
                }
            }
        } catch (Exception ex) {
            logger.error("作业状态修改接口失败，原因 " + ex + "JSF入参 " + JsonHelper.toJson(request));
            res.setCode(InvokeResult.SERVER_ERROR_CODE);
            res.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return res;
    }
}
