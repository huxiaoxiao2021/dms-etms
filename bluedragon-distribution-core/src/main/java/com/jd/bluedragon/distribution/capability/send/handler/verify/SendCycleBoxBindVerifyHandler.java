package com.jd.bluedragon.distribution.capability.send.handler.verify;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanResponse;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* 天官赐福 ◎ 百无禁忌
*
* @Auther: 刘铎（liuduo8）
* @Date: 2023/8/24
* @Description:
*      循环集包袋绑定逻辑，只有扫描箱号时处理
*/
@Service("sendCycleBoxBindVerifyHandler")
public class SendCycleBoxBindVerifyHandler extends SendDimensionStrategyHandler {


    @Autowired
    private CycleBoxService cycleBoxService;

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    /**
     * 循环集包袋绑定逻辑，只有扫描箱号时处理
     * 并且只有传入集包袋逻辑时处理
     *
     * @param context
     * @return
     */
    @Override
    public boolean doBoxHandler(SendOfCAContext context) {
        //获取循环集包袋编码
        String cycleBoxCode = context.getRequest().getCycleBoxCode();
        //需要先校验是否需要进行集包袋的绑定，并且已经绑定的不需要校验
        if (!needBindMaterialBagCheck(context)) {
            return false;
        }
        //再进行绑定前动作的校验
        if (StringUtils.isNotBlank(cycleBoxCode)) {
            BoxMaterialRelationRequest req = getBoxMaterialRelationRequest(context);
            InvokeResult bindMaterialRet = cycleBoxService.boxMaterialRelationAlterOfCheck(req);
            if (!bindMaterialRet.codeSuccess()) {
                if (HintCodeConstants.CYCLE_BOX_NOT_BELONG_ERROR.equals(String.valueOf(bindMaterialRet.getCode()))) {
                    //此场景需要做弱提示
                    context.getResponse().getData().init(SendResult.CODE_CONFIRM, bindMaterialRet.getMessage());
                } else {
                    context.getResponse().getData().init(SendResult.CODE_SENDED, bindMaterialRet.getMessage());
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 是否需要绑定集包袋前有咩有扫描传入集包袋
     *
     * @param context
     * @return
     */
    private boolean needBindMaterialBagCheck(SendOfCAContext context) {
        Box box = context.getBox();
        Integer opeSiteCode = context.getRequest().getSiteCode();
        String materialCode = context.getRequest().getCycleBoxCode();
        if (BusinessHelper.isBCBoxType(box.getType())) {
            boolean needBindMaterialBag = funcSwitchConfigService.getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(), opeSiteCode);
            if (needBindMaterialBag) {
                // 箱号未绑定集包袋
                if (StringUtils.isBlank(cycleBoxService.getBoxMaterialRelation(box.getCode()))) {
                    if (!BusinessUtil.isCollectionBag(materialCode) || BusinessUtil.isTrolleyCollectionBag(materialCode)) {
                        context.getResponse().setCode(SendScanResponse.CODE_CONFIRM_MATERIAL);
                        context.getResponse().addInterceptBox(0, "请扫描或输入正确的集包袋！");
                        return false;
                    }
                }
            }
        }

        // 如果是LL类型箱号，绑定集包袋号校验
        if (BusinessHelper.isLLBoxType(box.getType())) {
            // 箱号未绑定集包袋
            if (StringUtils.isBlank(cycleBoxService.getBoxMaterialRelation(box.getCode()))) {
                if (!BusinessUtil.isLLBoxBindingCollectionBag(materialCode)) {
                    context.getResponse().setCode(SendScanResponse.CODE_CONFIRM_MATERIAL);
                    context.getResponse().addInterceptBox(0, HintService.getHint(HintCodeConstants.LL_BOX_BINDING_MATERIAL_TYPE_ERROR, Boolean.TRUE));
                    return false;
                }
            }
        }

        return true;
    }





    private BoxMaterialRelationRequest getBoxMaterialRelationRequest(SendOfCAContext context) {
        BoxMaterialRelationRequest req = new BoxMaterialRelationRequest();
        req.setUserCode(context.getRequest().getUserCode());
        req.setUserName(context.getRequest().getUserName());
        req.setOperatorERP(context.getRequest().getOpeUserErp());
        req.setSiteCode(context.getRequest().getSiteCode());
        req.setSiteName(context.getRequest().getSiteName());
        req.setBoxCode(context.getBarCode());
        req.setMaterialCode(context.getRequest().getCycleBoxCode());
        req.setForceFlag(context.getRequest().getIsForceSend());
        req.setBindFlag(Constants.CONSTANT_NUMBER_ONE); // 绑定
        return req;
    }
}
