package com.jd.bluedragon.distribution.jy.service.picking.bridge;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.CargoDetailServiceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.constants.PickingGoodTaskDetailInitServiceEnum;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectSplitDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.constant.CollectServiceConstant;
import com.jd.bluedragon.distribution.jy.service.picking.factory.PickingGoodDetailInitServiceFactory;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.tms.data.dto.CargoDetailDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 待提明细初始化-取运输封车数据
 * @Author zhengchengfa
 * @Date 2023/12/7 21:40
 * @Description
 */
@Service("pickingGoodDetailInitByTmsSealInfoServiceImpl")
public class PickingGoodDetailInitByTmsSealInfoServiceImpl implements PickingGoodDetailInitService , InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AviationPickingGoodTaskInit.class);
    @Autowired
    private CargoDetailServiceManager cargoDetailServiceManager;
    @Autowired
    @Qualifier(value = "jyPickingGoodDetailInitSplitProducer")
    private DefaultJMQProducer jyPickingGoodDetailInitSplitProducer;


    @Override
    public void afterPropertiesSet() throws Exception {
        PickingGoodDetailInitServiceFactory.registerPickingGoodDetailInitService(PickingGoodTaskDetailInitServiceEnum.OTHER_SEND_DMS_PICKING.getTargetCode(), this);
    }


    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }

    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.PickingGoodDetailInitByTmsSealInfoServiceImpl.pickingGoodDetailInitSplit",mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean pickingGoodDetailInitSplit(PickingGoodTaskDetailInitDto initDto) {
        CargoDetailDto cargoDetailDto = new CargoDetailDto();
        cargoDetailDto.setBatchCode(initDto.getBatchCode());
        cargoDetailDto.setYn(1);
        int limitSize = 1000;
        int currentSize  = limitSize ;
        int offset = 0;

        List<String> packageCodeList = new ArrayList<>();
        Long startTime = System.currentTimeMillis();
        while(currentSize >= limitSize){
            com.jd.tms.data.dto.CommonDto<List<CargoDetailDto>> cargoDetailReturn = cargoDetailServiceManager.getCargoDetailInfoByBatchCode(cargoDetailDto,offset,limitSize);
            if(cargoDetailReturn == null || cargoDetailReturn.getCode() != com.jd.etms.vos.dto.CommonDto.CODE_SUCCESS ) {
                log.error("获取批次{}下包裹数据异常,条件：offset={},limitSize={}, result={}",initDto.getBatchCode(), offset, limitSize, JsonHelper.toJson(cargoDetailReturn));
                throw new JyBizException("运输接口根据批次获取包裹信息异常");
            }
            if(cargoDetailReturn.getData().isEmpty()) {
                break;
            }
            cargoDetailReturn.getData().forEach(tmsDetailDto -> {
                packageCodeList.add(tmsDetailDto.getPackageCode());
            });
            currentSize = cargoDetailReturn.getData().size();
            offset =  offset + limitSize;
        }
        logInfo("根据批次号{}查询运输封车明细数量为{}, 耗时={}ms", initDto.getBatchCode(), packageCodeList.size(), System.currentTimeMillis() - startTime);

        List<Message> messageList = new ArrayList<>();
        packageCodeList.forEach(packageCode -> {
            PickingGoodTaskDetailInitDto detailSplitInitDto = new PickingGoodTaskDetailInitDto();
            BeanUtils.copyProperties(initDto, detailSplitInitDto);
            detailSplitInitDto.setPackageCode(packageCode);
            String msgText = JsonUtils.toJSONString(detailSplitInitDto);
            logInfo("otherToDms待提明细初始化拆分最小包裹维度，businessId={},msg={}", packageCode, msgText);
            messageList.add(new Message(jyPickingGoodDetailInitSplitProducer.getTopic(), msgText, packageCode));
        });
        jyPickingGoodDetailInitSplitProducer.batchSendOnFailPersistent(messageList);
        return true;
    }

    @Override
    public boolean pickingGoodDetailInit(Collection<PickingGoodTaskDetailInitDto> values) {
        logInfo("提货任务按sealCarCode走运输封车数据执行待提明细初始化，param={}", JsonHelper.toJson(values));

        return false;
    }
}
