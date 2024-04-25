package com.jd.bluedragon.distribution.consumer.box;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.BoxMaterialRelationMq;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest.BIZ_SORTING_TERMINAL;

/**
 * 集包袋绑定关系消费
 */
@Service("boxMaterialRelationConsumer")
@Slf4j
public class BoxMaterialRelationConsumer extends MessageBaseConsumer {

    @Autowired
    private CycleBoxService cycleBoxService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public void consume(Message message) throws Exception {
        if (ObjectHelper.isEmpty(message) || StringUtils.isEmpty(message.getText())) {
            log.warn("BoxMaterialRelationConsumer 消息为空！");
            return;
        }

        BoxMaterialRelationMq mq = JsonHelper.fromJson(message.getText(), BoxMaterialRelationMq.class);
        if (mq == null) {
            log.info("箱号绑定集包袋json转换失败：{}", JsonHelper.toJson(message));
            return;
        }

        if (StringUtils.isEmpty(mq.getMaterialsCode())
                || StringUtils.isEmpty(mq.getContainerCode())
                || mq.getOperateSiteId() == null
                || mq.getOperatorId() == null) {
            log.info("箱号绑定集包袋参数校验错误，缺少必须参数：{}", JsonHelper.toJson(mq));
            return;
        }

        try{
            // 保存集包关系
            InvokeResult result = cycleBoxService.boxMaterialRelationAlter(assembleBoxMaterialRelationRequest(mq));
            log.info("箱号:{}绑定集包袋关系保存结果：{}", mq.getContainerCode(), JsonHelper.toJson(result));
        }catch (Exception e) {
            log.error("箱号:{}绑定集包袋关系保存异常", mq.getContainerCode(), e);
            throw new RuntimeException("BoxMaterialRelationConsumer 处理失败!");
        }

    }

    private BoxMaterialRelationRequest assembleBoxMaterialRelationRequest(BoxMaterialRelationMq mq) {
        BoxMaterialRelationRequest req = new BoxMaterialRelationRequest();
        BaseStaffSiteOrgDto userInfo = baseMajorManager.getBaseStaffByStaffId(mq.getOperatorId());
        if (userInfo != null && !StringUtils.isEmpty(userInfo.getErp())) {
            req.setOperatorERP(userInfo.getErp());
        } else {
            req.setOperatorERP(String.valueOf(mq.getOperatorId()));
        }
        req.setSiteCode(mq.getOperateSiteId());
        req.setBoxCode(mq.getContainerCode());
        req.setMaterialCode(mq.getMaterialsCode());
        req.setBindFlag(Constants.CONSTANT_NUMBER_ONE);
        req.setForceFlag(Boolean.TRUE);
        req.setBizSource(BIZ_SORTING_TERMINAL);
        return req;
    }
}
