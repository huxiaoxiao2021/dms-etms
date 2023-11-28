package com.jd.bluedragon.distribution.consumer.jy.collectpackage;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.BatchCancelCollectPackageMqDto;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.CancelCollectPackageDto;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyBizTaskCollectPackageService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("batchCancelCollectPackageConsumer")
@Slf4j
public class BatchCancelCollectPackageConsumer extends MessageBaseConsumer {

    @Autowired
    private JyBizTaskCollectPackageService jyBizTaskCollectPackageService;

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
            for (String packageCode:mqBody.getPackageCodeList()){
                CancelCollectPackageDto cancelCollectPackageDto =assembleCancelCollectPackageDto(packageCode,mqBody);
                jyBizTaskCollectPackageService.cancelJyCollectPackage(cancelCollectPackageDto);
            }
        }
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
        return cancelCollectPackageDto;
    }
}
