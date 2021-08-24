package com.jd.bluedragon.distribution.loadAndUnload;

import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.loadAndUnload.service.LoadAndUnloadCommonService;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("loadAndUnloadCommonService")
public class LoadAndUnloadCommonServiceImpl implements LoadAndUnloadCommonService {

    private static final Logger logger = LoggerFactory.getLogger(LoadAndUnloadCommonServiceImpl.class);

    @Resource
    private BoardCommonManager boardCommonManager;

    @Override
    public InvokeResult<String> interceptValidateUnloadCar(String barCode) {
        InvokeResult<String> result = new InvokeResult<>();
        result.customMessage(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE);
        // ver组板拦截
        try {
            return boardCommonManager.interceptValidateUnloadCar(barCode);
        } catch (Exception e) {
            logger.error("ver组板拦截出现异常，请求参数barCode={},error=", JsonHelper.toJson(barCode), e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @Override
    public InvokeResult<Void> boardCombinationCheck(BoardCommonRequest request) {
        InvokeResult<Void> result = new InvokeResult<>();
        result.customMessage(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE);
        // ver组板拦截
        try {
            InvokeResult invokeResult = boardCommonManager.boardCombinationCheck(request);
            if (invokeResult != null) {
                result.setCode(invokeResult.getCode());
                result.setMessage(invokeResult.getMessage());
            }
            return result;
        } catch (Exception e) {
            logger.error("ver组板拦截出现异常，请求参数BoardCommonRequest={},error=", JsonHelper.toJson(request), e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }
}
