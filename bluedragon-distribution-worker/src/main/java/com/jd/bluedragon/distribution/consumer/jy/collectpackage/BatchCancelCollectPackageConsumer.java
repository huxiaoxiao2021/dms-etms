package com.jd.bluedragon.distribution.consumer.jy.collectpackage;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.collectpackage.request.CancelCollectPackageReq;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.BatchCancelCollectPackageMqDto;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.CancelCollectPackageDto;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyBizTaskCollectPackageService;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyCollectPackageService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("batchCancelCollectPackageConsumer")
@Slf4j
public class BatchCancelCollectPackageConsumer extends MessageBaseConsumer {

    @Autowired
    private JyBizTaskCollectPackageService jyBizTaskCollectPackageService;

    @Autowired
    @Qualifier("jyCollectPackageService")
    JyCollectPackageService jyCollectPackageService;

    @Override
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("BatchCancelCollectPackageConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("BatchCancelCollectPackageConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        BatchCancelCollectPackageMqDto mqBody = JsonHelper.fromJson(message.getText(), BatchCancelCollectPackageMqDto.class);
        if(mqBody == null){
            log.error("BatchCancelCollectPackageConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(log.isInfoEnabled()){
            log.info("消费BatchCancelCollectPackageConsumer，内容{}",message.getText());
        }

        if (CollectionUtils.isNotEmpty(mqBody.getPackageCodeList())){
            for (String barCode:mqBody.getPackageCodeList()){
                if (WaybillUtil.isPackageCode(barCode)){
                    CancelCollectPackageDto cancelCollectPackageDto =assembleCancelCollectPackageDto(barCode,mqBody);
                    jyBizTaskCollectPackageService.cancelJyCollectPackage(cancelCollectPackageDto);
                }else if (BusinessUtil.isBoxcode(barCode)){
                    CancelCollectPackageReq cancelCollectBox =assembleCancelCollectBoxDto(mqBody,barCode);
                    jyCollectPackageService.execCancelCollectBox(cancelCollectBox, null);
                }else {
                    log.info("BatchCancelCollectPackageConsumer data error unsupport box type:{}",message.getText());
                }
            }
        }
    }

    private CancelCollectPackageReq assembleCancelCollectBoxDto(BatchCancelCollectPackageMqDto mqBody, String barCode) {
        CancelCollectPackageReq req =new CancelCollectPackageReq();
        req.setBarCode(barCode);
        req.setBoxCode(mqBody.getBoxCode());
        req.setBizId(mqBody.getBizId());

        CurrentOperate currentOperate =new CurrentOperate();
        currentOperate.setSiteCode(mqBody.getSiteCode());
        currentOperate.setSiteName(mqBody.getSiteName());
        req.setCurrentOperate(currentOperate);

        User user =new User();
        user.setUserErp(mqBody.getUpdateUserErp());
        user.setUserName(mqBody.getUpdateUserName());
        user.setUserCode(mqBody.getUpdateUserCode());
        req.setUser(user);

        return req;
    }

    private CancelCollectPackageDto assembleCancelCollectPackageDto(String packageCode, BatchCancelCollectPackageMqDto request) {
        CancelCollectPackageDto cancelCollectPackageDto = new CancelCollectPackageDto();
        cancelCollectPackageDto.setBizId(request.getBizId());
        cancelCollectPackageDto.setPackageCode(packageCode);
        cancelCollectPackageDto.setBoxCode(request.getBoxCode());
        cancelCollectPackageDto.setSiteCode(request.getSiteCode());
        cancelCollectPackageDto.setSiteName(request.getSiteName());
        cancelCollectPackageDto.setUpdateUserErp(request.getUpdateUserErp());
        cancelCollectPackageDto.setUpdateUserName(request.getUpdateUserName());
        cancelCollectPackageDto.setUpdateUserCode(request.getUpdateUserCode());
        cancelCollectPackageDto.setOperatorData(request.getOperatorData());
        return cancelCollectPackageDto;
    }
}
