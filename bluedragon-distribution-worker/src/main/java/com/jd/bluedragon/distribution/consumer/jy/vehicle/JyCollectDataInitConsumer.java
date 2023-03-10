package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.CargoDetailServiceManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectDto;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectInitNodeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 封车消息消费环节生产该消息
 */
@Service("jyCollectDataInitConsumer")
public class JyCollectDataInitConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(JyCollectDataInitConsumer.class);

    @Autowired
    private CargoDetailServiceManager cargoDetailServiceManager;
    @Autowired
    private JyCollectService jyCollectService;
    @Autowired
    private VosManager vosManager;
    @Autowired
    private BaseMajorManager baseMajorManager;


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyCollectDataInitConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JyCollectDataInitConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            logger.warn("JyCollectDataInitConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        InitCollectDto mqBody = JsonHelper.fromJson(message.getText(),InitCollectDto.class);
        if(mqBody == null){
            logger.error("JyCollectDataInitConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(logger.isInfoEnabled()){
            logger.info("消费处理 jy_collect_data_init 开始，内容{}",message.getText());
        }
        if(!deal(mqBody)){
            //处理失败 重试
            logger.error("消费处理 jy_collect_data_init 失败，内容{}",message.getText());
            throw new JyBizException("消费处理tms_seal_car_status失败");
        }else{
            if(logger.isInfoEnabled()) {
                logger.info("消费处理 jy_collect_data_init 成功，内容{}", message.getText());
            }
        }
    }

    /**
     * 处理逻辑
     * @param initCollectDto
     * @return
     */
    private boolean deal(InitCollectDto initCollectDto){

        if(initCollectDto.getOperateNode() == CollectInitNodeEnum.SEAL_INIT.getCode()) {
            return this.dealSealCollectInit(initCollectDto);
        }

        if(initCollectDto.getOperateNode() == CollectInitNodeEnum.CANCEL_SEAL_INIT.getCode()) {
            return this.dealCancelSealCollectInit(initCollectDto);
        }

        if(initCollectDto.getOperateNode() == CollectInitNodeEnum.NULL_TASK_INIT.getCode()) {
            return this.NullTaskInitCollectInit(initCollectDto);
        }
        return true;

    }
    private boolean dealCancelSealCollectInit(InitCollectDto initCollectDto) {
        //todo zcf
        return true;
    }

    private boolean NullTaskInitCollectInit(InitCollectDto initCollectDto) {
        //todo zcf
        return true;
    }

    private boolean dealSealCollectInit(InitCollectDto initCollectDto) {

        //只处理 到拣运中心的数据, 封车,解封车进围栏 状态数据
        SealCarDto sealCarInfoBySealCarCodeOfTms = vosManager.findSealCarInfoBySealCarCodeOfTms(initCollectDto.getBizId());
        if(sealCarInfoBySealCarCodeOfTms == null){
            logger.error("从运输未获取到封车信息,{}", JsonHelper.toJson(initCollectDto));
            return false;
        }
        Integer endSiteId = sealCarInfoBySealCarCodeOfTms.getEndSiteId();
        //检查目的地是否是拣运中心
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(endSiteId);
        if(siteInfo == null || !BusinessUtil.isSorting(siteInfo.getSiteType())){
            //丢弃数据
            logger.info("不需要关心的数据丢弃,目的站点:{},目的站点类型:{}，消息:{}",endSiteId,siteInfo==null?null:siteInfo.getSiteType(),
                    JsonHelper.toJson(initCollectDto));
            return true;
        }

        List<String> batchCodes = sealCarInfoBySealCarCodeOfTms.getBatchCodes();
        if (batchCodes == null) {
            logger.warn("封车编码【{}】没有获取到对应的批次信息!,message={}", initCollectDto.getBizId(), JsonHelper.toJson(initCollectDto));
            return true;
        }
        List<String> allPackageList = new ArrayList<>();
        for (String batchCode : batchCodes){
            allPackageList.addAll(cargoDetailServiceManager.querySealCarPackageList(sealCarInfoBySealCarCodeOfTms.getSealSiteId(), batchCode));
        }

        collectInitHandler(sealCarInfoBySealCarCodeOfTms, allPackageList);
        return true;
    }


    //调用初始化服务
    private void collectInitHandler(SealCarDto sealCarInfoBySealCarCodeOfTms, List<String> allPackageList) {
        CollectDto collectDto = new CollectDto();
        //todo zcf
        jyCollectService.initCollect(collectDto);
    }

}
