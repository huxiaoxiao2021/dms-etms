package com.jd.bluedragon.distribution.capability.send.handler.verify;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.send.domain.SendResult;
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
@Service("sendCycleBoxBindHandler")
public class SendCycleBoxBindVerifyHandler extends SendDimensionStrategyHandler {


    @Autowired
    private CycleBoxService cycleBoxService;

    /**
     * 循环集包袋绑定逻辑，只有扫描箱号时处理
     * 并且只有传入集包袋逻辑时处理
     * @param context
     * @return
     */
    @Override
    public boolean doBoxHandler(SendOfCAContext context) {
        //获取循环集包袋编码
        String cycleBoxCode = context.getRequest().getCycleBoxCode();
        if(StringUtils.isNotBlank(cycleBoxCode)){
            BoxMaterialRelationRequest req = getBoxMaterialRelationRequest(context);
            InvokeResult bindMaterialRet = cycleBoxService.boxMaterialRelationAlterOfCheck(req);
            if (!bindMaterialRet.codeSuccess()) {
                if(HintCodeConstants.CYCLE_BOX_NOT_BELONG_ERROR.equals(String.valueOf(bindMaterialRet.getCode()))){
                    //此场景需要做弱提示
                    context.getResponse().getData().init(SendResult.CODE_CONFIRM, bindMaterialRet.getMessage());
                }else{
                    context.getResponse().getData().init(SendResult.CODE_SENDED, bindMaterialRet.getMessage());
                }
                return false;
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
