package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.common.dto.collectpackage.request.BindCollectBagReq;
import com.jd.bluedragon.common.dto.collectpackage.request.CollectPackageReq;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.constants.BoxTypeV2Enum;
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

    @Override
    public InvokeResult<CollectPackageResp> collectBoxForMachine(CollectPackageReq request) {
        //基础校验
        collectBoxMachineCheck(request);
        //执行集装
        CollectPackageResp response = new CollectPackageResp();
        execCollectBox(request, response);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, response);
    }

    private void collectBoxMachineCheck(CollectPackageReq request) {

        if (!ObjectHelper.isNotNull(request.getBoxCode())) {
            throw new JyBizException("参数错误：缺失箱号！");
        }
        if (!BusinessUtil.isBoxcode(request.getBoxCode())) {
            throw new JyBizException("参数错误：不支持该类型箱号！");
        }
        if (!ObjectHelper.isNotNull(request.getBarCode())) {
            throw new JyBizException("参数错误：缺失扫描单号！");
        }

        String outerBoxType = request.getBoxCode().substring(0,2);
        String innerBoxType =request.getBarCode().substring(0,2);

        BoxTypeV2Enum outer =BoxTypeV2Enum.getFromCode(outerBoxType);
        if (ObjectHelper.isEmpty(outer) || ObjectHelper.isEmpty(outer.getSupportEmbeddedTypes())){
            throw new JyBizException("参数错误:"+outerBoxType+"箱号不支持内嵌箱号");
        }
        if (!outer.getSupportEmbeddedTypes().contains(innerBoxType)){
            throw new JyBizException("参数错误:"+outerBoxType+"箱号不支持内嵌"+innerBoxType+"类型的箱");
        }
    }

    @Override
    public InvokeResult<CollectPackageResp> collectPackageForMachine(CollectPackageReq request) {
        //基础校验
        collectBoxMachineCheck(request);
        //执行集包
        CollectPackageResp response = new CollectPackageResp();
        execCollectPackage(request, response);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, response);
    }
}
