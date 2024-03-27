package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.common.dto.collectpackage.request.BindCollectBagReq;
import com.jd.bluedragon.common.dto.collectpackage.request.CollectPackageReq;
import com.jd.bluedragon.common.dto.collectpackage.request.SearchPackageTaskReq;
import com.jd.bluedragon.common.dto.collectpackage.request.TaskDetailReq;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageResp;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageTaskResp;
import com.jd.bluedragon.core.jsf.collectpackage.CollectPackageManger;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskCollectPackageStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.MixBoxTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import java.util.Collections;
import java.util.List;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_THIRD_ERROR_CODE;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;

@Service("jyCollectLoadingService")
@Slf4j
public class JyCollectLoadingServiceImpl extends JyCollectPackageServiceImpl{

    @Autowired
    private JyBizTaskCollectPackageService jyBizTaskCollectPackageService;
    @Autowired
    private BaseService baseService;

    @Autowired
    @Qualifier("collectLoadingManger")
    private CollectPackageManger collectPackageManger;

    @Override
    public CollectPackageManger getCollectPackageManger() {
        return this.collectPackageManger;
    }

    @Override
    public void collectPackageBizCheck(CollectPackageReq request) {
        //重复集包校验
        reCollectCheck(request);
        //校验箱号：是否存在 +是否已打印+状态合法性+是否已经发货
        boxCheck(request);
        //流向校验
        flowCheck(request);
        //sorting拦截器链
        execInterceptorChain(request);
    }

    @Override
    public void flowCheck(CollectPackageReq request) {
        JyBizTaskCollectPackageEntity collectPackageTask = jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        if (ObjectHelper.isEmpty(collectPackageTask)) {
            throw new JyBizException("集包任务不存在或者已经过期，请刷新界面！");
        }
        if (JyBizTaskCollectPackageStatusEnum.CANCEL.getCode().equals(collectPackageTask.getTaskStatus())) {
            throw new JyBizException("集包任务已作废-操作了批量取消集包！");
        }
        request.setTaskStatus(collectPackageTask.getTaskStatus());
        request.setEndSiteId(collectPackageTask.getEndSiteId());
        if (ObjectHelper.isNotNull(request.getEndSiteId())) {
            BaseStaffSiteOrgDto staffSiteOrgDto = baseService.getSiteBySiteID(request.getEndSiteId().intValue());
            if (ObjectHelper.isNotNull(staffSiteOrgDto) && ObjectHelper.isNotNull(staffSiteOrgDto.getSiteName())) {
                request.setEndSiteName(staffSiteOrgDto.getSiteName());
            }
        }
    }

    @Override
    public PdaOperateRequest assemblePdaOperateRequest(CollectPackageReq request) {
        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setBoxCode(request.getBoxCode());
        pdaOperateRequest.setBusinessType(request.getBusinessType());
        pdaOperateRequest.setIsGather(0);
        pdaOperateRequest.setOperateType(1);
        pdaOperateRequest.setPackageCode(request.getBarCode());
        pdaOperateRequest.setReceiveSiteCode(request.getBoxReceiveId().intValue());
        pdaOperateRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        pdaOperateRequest.setCreateSiteName(request.getCurrentOperate().getSiteName());
        pdaOperateRequest.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(), DateUtil.FORMAT_DATE_TIME));
        pdaOperateRequest.setOperateUserCode(request.getUser().getUserCode());
        pdaOperateRequest.setOperateUserName(request.getUser().getUserName());
        pdaOperateRequest.setJyCollectPackageFlag(false);
        return pdaOperateRequest;
    }

    @Override
    public boolean checkSearchPackageTaskReq(SearchPackageTaskReq request, InvokeResult<CollectPackageTaskResp> result) {
        if (BusinessHelper.isBoxcode(request.getBarCode()) && !BusinessUtil.isLLBoxcode(request.getBarCode())){
            result.setCode(RESULT_THIRD_ERROR_CODE);
            result.setMessage("箱号只支持扫描LL箱号！");
            return false;
        }
        return super.checkSearchPackageTaskReq(request, result);
    }

    @Override
    public void checkBindCollectBagReq(BindCollectBagReq request) {
        if (!ObjectHelper.isNotNull(request.getBoxCode())) {
            throw new JyBizException("参数错误：缺失箱号！");
        }
        if (!BusinessUtil.isBoxcode(request.getBoxCode())) {
            throw new JyBizException("参数错误：非法的箱号！");
        }
        if (!ObjectHelper.isNotNull(request.getMaterialCode())) {
            throw new JyBizException("参数错误：缺失集包袋号！");
        }
        if (!BusinessUtil.isLLBoxBindingCollectionBag(request.getMaterialCode())) {
            throw new JyBizException("参数错误：非法的集包袋号！");
        }
    }

    @Override
    public void collectPackageBaseCheck(CollectPackageReq request) {
        if (!ObjectHelper.isNotNull(request.getBizId())) {
            throw new JyBizException("参数错误：缺失任务bizId！");
        }
        if (!ObjectHelper.isNotNull(request.getBoxCode())) {
            throw new JyBizException("参数错误：缺失箱号！");
        }
        if (!BusinessUtil.isLLBoxcode(request.getBoxCode())){
            throw new JyBizException("参数错误：集装岗只支持操作LL类型箱号！");
        }
        if (!BusinessUtil.isBoxcode(request.getBoxCode())) {
            throw new JyBizException("参数错误：不支持该类型箱号！");
        }
        if (!ObjectHelper.isNotNull(request.getBarCode())) {
            throw new JyBizException("参数错误：缺失包裹号！");
        }
        if (!WaybillUtil.isPackageCode(request.getBarCode())) {
            throw new JyBizException("参数错误：包裹号类型错误，请扫描正确的包裹号码！");
        }
    }

    @Override
    public JyBizTaskCollectPackageEntity getTaskDetailByReq(TaskDetailReq request) {
        if (BusinessUtil.isBoxcode(request.getBarCode()) && BusinessUtil.isLLBoxcode(request.getBarCode())) {
            // 如果是LL箱号
            return jyBizTaskCollectPackageService.findByBoxCode(request.getBarCode());
        }else if (WaybillUtil.isPackageCode(request.getBarCode()) ||
                (BusinessUtil.isBoxcode(request.getBarCode()) && !BusinessUtil.isLLBoxcode(request.getBarCode()))) {
            JyCollectPackageEntity query = new JyCollectPackageEntity();
            query.setPackageCode(request.getBarCode());
            query.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
            JyCollectPackageEntity collectPackage = jyCollectPackageScanRecordService.queryJyCollectPackageRecord(query);
            if (collectPackage == null || StringUtils.isEmpty(collectPackage.getBizId())) {
                return null;
            }
            return jyBizTaskCollectPackageService.findByBizId(collectPackage.getBizId());
        }else if (ObjectHelper.isNotNull(request.getBizId())){
            return jyBizTaskCollectPackageService.findByBizId(request.getBizId());
        }else {
            throw new JyBizException("暂不支持改类型的任务检索方式!");
        }
    }
}
