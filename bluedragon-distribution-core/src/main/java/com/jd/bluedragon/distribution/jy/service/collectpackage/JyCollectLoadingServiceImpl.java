package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.dto.collectpackage.request.BindCollectBagReq;
import com.jd.bluedragon.common.dto.collectpackage.request.CollectPackageReq;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.jsf.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;

@Service("jyCollectLoadingService")
@Slf4j
public class JyCollectLoadingServiceImpl extends JyCollectPackageServiceImpl{

    @Resource
    private FuncSwitchConfigService funcSwitchConfigService;
    @Resource
    private CycleBoxService cycleBoxService;

    @Override
    public InvokeResult<CollectPackageResp> collectPackage(CollectPackageReq request) {
        if (WaybillUtil.isPackageCode(request.getBarCode())){
            return super.collectPackage(request);
        }
        return collectLoading(request);
    }


    //集装能力
    private InvokeResult<CollectPackageResp> collectLoading(CollectPackageReq request) {
        //基础校验
        collectLoadingBaseCheck(request);
        //集包业务校验
        collectLoadingBizCheck(request);
        //执行集装：绑定LL和BC的关系  +    中转LL和最里层包裹的关系，匹send_d
        CollectPackageResp response = new CollectPackageResp();
        execCollectLoading(request, response);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, response);

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
