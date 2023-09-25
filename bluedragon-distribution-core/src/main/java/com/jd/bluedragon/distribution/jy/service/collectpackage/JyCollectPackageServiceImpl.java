package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;

@Service
@Slf4j
public class JyCollectPackageServiceImpl implements JyCollectPackageService{
    @Override
    public InvokeResult<CollectPackageResp> collectScan(CollectPackageReq request) {
        //基础校验
        collectPackageBaseCheck(request);
        //集包业务校验
        collectPackageBizCheck(request);
        //执行集包
        CollectPackageResp response =new CollectPackageResp();
        execCollectPackage(request,response);

        return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,response);
    }

    private void execCollectPackage(CollectPackageReq request, CollectPackageResp response) {
    }

    private void collectPackageBizCheck(CollectPackageReq request) {
    }

    private void collectPackageBaseCheck(CollectPackageReq request) {
        if (!ObjectHelper.isNotNull(request.getBizId())){
            throw new JyBizException("参数错误：缺失任务bizId！");
        }
        if (!ObjectHelper.isNotNull(request.getBoxCode())){
            throw new JyBizException("参数错误：缺失箱号！");
        }
        if (!BusinessUtil.isBoxcode(request.getBoxCode())){
            throw new JyBizException("参数错误：不支持该类型箱号！");
        }
        if (!ObjectHelper.isNotNull(request.getPackageCode())){
            throw new JyBizException("参数错误：缺失包裹号！");
        }
        if (!WaybillUtil.isPackageCode(request.getPackageCode())){
            throw new JyBizException("参数错误：包裹号类型错误，请扫描正确的包裹号码！");
        }
    }

    private void collectBaseCheck(CollectPackageReq request) {
    }


    @Override
    public InvokeResult<CollectPackageTaskResp> listCollectPackageTask(CollectPackageTaskReq request) {
        return null;
    }

    @Override
    public InvokeResult<TaskDetailResp> queryTaskDetail(TaskDetailReq request) {
        return null;
    }

    @Override
    public InvokeResult<SealingBoxResp> sealingBox(SealingBoxReq request) {
        return null;
    }

    @Override
    public InvokeResult<BindCollectBagResp> bindCollectBag(BindCollectBagReq request) {
        return null;
    }

    @Override
    public InvokeResult<CancelCollectPackageResp> cancelCollectPackage(CancelCollectPackageReq request) {
        return null;
    }
}
