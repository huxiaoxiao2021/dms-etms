package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.api.request.OrderBindMessageRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 驻厂打印-外单靑流箱绑定，发送MQ
 */
@Service("cycleBoxBindToWaiDanHandler")
public class CycleBoxBindToWaiDanHandler implements InterceptHandler<WaybillPrintContext, String> {
    private static final Logger log = LoggerFactory.getLogger(CycleBoxBindToWaiDanHandler.class);

    @Autowired
    private CycleBoxService cycleBoxService;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();
        log.info("该单不是靑流箱绑定动作，不需要处理。扫描的条码[{}]", JsonHelper.toJson(context));
        if(StringUtils.isBlank(context.getRequest().getBarCode()) || StringUtils.isBlank(context.getRequest().getReBoxCode())){
            log.info("该单不是靑流箱绑定动作，不需要处理。扫描的条码{}", context.getRequest().getBarCode());
            return result;
        }

        if (context.getRequest().getSiteCode()==null || StringUtils.isBlank(context.getRequest().getUserERP())) {
            result.toFail(MessageFormat.format("靑流箱绑定失败，没有场地和操作人信息。靑流箱号：{0},包裹号：{1}", context.getRequest().getReBoxCode(),context.getRequest().getBarCode()));
            log.info(result.getMessage());
            return result;
        }

        String waybillCode = WaybillUtil.getWaybillCode(context.getRequest().getBarCode());
        OrderBindMessageRequest request=new OrderBindMessageRequest();

        List<String> sealNos =new ArrayList<>();
        sealNos.add(context.getRequest().getReBoxCode());
        request.setSealNos(sealNos);
        request.setWaybillNo(waybillCode);
        request.setSiteCode((Long) context.getRequest().getSiteCode().longValue());
        request.setSiteName(context.getRequest().getSiteName());
        request.setOperatorErp(context.getRequest().getUserERP());
        request.setOperatorName(context.getRequest().getUserName());
        request.setOperateTime(context.getRequest().getOperateTime());
        request.setCbStatus(39);

        InvokeResult res = new InvokeResult();
        try{
            res=cycleBoxService.cycleBoxBindToWD(request);
        } catch (Exception e) {
            result.toFail(MessageFormat.format(res.getMessage() + " 靑流箱号：{0},包裹号：{1}", context.getRequest().getReBoxCode(),context.getRequest().getBarCode()));
            log.info(result.getMessage());
        }

        return result;
    }
}
