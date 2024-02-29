package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.common.dto.collectpackage.request.BindCollectBagReq;
import com.jd.bluedragon.common.dto.collectpackage.request.CollectPackageReq;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Collections;
import java.util.List;

import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;

@Service("jyCollectLoadingService")
@Slf4j
public class JyCollectLoadingServiceImpl extends JyCollectPackageServiceImpl{

    @Resource
    private FuncSwitchConfigService funcSwitchConfigService;
    @Resource
    private CycleBoxService cycleBoxService;
    @Autowired
    private JyBizTaskCollectPackageService jyBizTaskCollectPackageService;
    @Autowired
    private BaseService baseService;


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

    private void execCollectLoading(CollectPackageReq request, CollectPackageResp response) {
    }

    private void collectLoadingBizCheck(CollectPackageReq request) {
        collectPackageBizCheck(request);
        cycleBagBindCheck(request);
    }

    private void cycleBagBindCheck(CollectPackageReq request) {
        // 不是BC类型的不拦截
        if (!BusinessHelper.isBCBoxType(request.getBoxType())) {
            return;
        }
        // 开关关闭不拦截
        if (!funcSwitchConfigService.getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(), request.getCurrentOperate().getSiteCode())) {
            return;
        }
        String materialCode = cycleBoxService.getBoxMaterialRelation(request.getBarCode());
        if (StringUtils.isEmpty(materialCode)) {
            throw new JyBizException(request.getBarCode()+"未绑定循环集包袋，请先绑定！");
        }
    }

    private void collectLoadingBaseCheck(CollectPackageReq request) {
        if (!ObjectHelper.isNotNull(request.getBizId())) {
            throw new JyBizException("参数错误：缺失任务bizId！");
        }
        if (!ObjectHelper.isNotNull(request.getBoxCode())) {
            throw new JyBizException("参数错误：缺失箱号！");
        }
        if (!BusinessUtil.isLLBoxcode(request.getBoxCode())) {
            throw new JyBizException("参数错误：不支持该类型箱号,请扫描笼车/围板箱对应的箱号！");
        }
        if (!ObjectHelper.isNotNull(request.getBarCode())) {
            throw new JyBizException("参数错误：缺失包裹号！");
        }
        if (!(BusinessUtil.isBoxcode(request.getBarCode()) && !BusinessUtil.isLLBoxcode(request.getBarCode()))) {
            throw new JyBizException("参数错误：扫描单号类型错误，请扫描XX！");//TODO 待产品确认
        }
    }
}
