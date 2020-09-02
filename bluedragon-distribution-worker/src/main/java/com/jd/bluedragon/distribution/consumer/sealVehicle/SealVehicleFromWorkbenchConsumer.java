package com.jd.bluedragon.distribution.consumer.sealVehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.sealVehicle.domain.SealVehicleSendCodeInfo;
import com.jd.bluedragon.distribution.sealVehicle.domain.SubmitSealVehicleDto;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.fastjson.JSONObject;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("sealVehicleFromWorkbenchConsumer")
public class SealVehicleFromWorkbenchConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(SealVehicleFromWorkbenchConsumer.class);

//    private final static
    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Autowired
    private PreSealVehicleService preSealVehicleService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    @Qualifier("wbSealVehicleCallbackProducer")
    private DefaultJMQProducer wbSealVehicleCallbackProducer;

    @Override
    public void consume(Message message) throws Exception {
        //处理消息体
        log.info("sealVehicleFromWorkbenchConsumer consume --> 消息Body为【{}】", message.getText());
        //反序列化
        SubmitSealVehicleDto submitSealVehicleDto = JsonHelper.fromJson(message.getText(), SubmitSealVehicleDto.class);
        if (submitSealVehicleDto == null || StringHelper.isEmpty(submitSealVehicleDto.getTransportCode()) || StringHelper.isEmpty(submitSealVehicleDto.getSealVehicleTaskCode())) {
            log.warn("sealVehicleFromWorkbenchConsumer consume --> 消息转换对象失败：{}", message.getText());
            return;
        }

        String transportCode = submitSealVehicleDto.getTransportCode();
        String redisKey = "sealVehicleFromWorkbenchConsumer-" + transportCode;
        String sealVehicleTaskCode = submitSealVehicleDto.getSealVehicleTaskCode();

        if (jimdbCacheService.exists(transportCode)) {
            log.warn("sealVehicleFromWorkbenchConsumer consume --> 运力编码：{}正在执行操作，任务编号：{}", transportCode, sealVehicleTaskCode);
            callBackNoticeFail(sealVehicleTaskCode, "该运力正在执行中，无法再次提交该运力的封车任务", transportCode);
        } else {
            jimdbCacheService.setEx(redisKey, submitSealVehicleDto.getSealVehicleTaskCode(), 5 * 60);
        }

        List<com.jd.etms.vos.dto.SealCarDto> sealCarDtoList = new ArrayList<>();
        try {
            //包含批次时，进行数据转换后进行封车
            if (submitSealVehicleDto.getHasBatchInfo()) {
                List<SealVehicleSendCodeInfo> sealVehicleSendCodeInfoList = submitSealVehicleDto.getSealVehicleSendCodeInfoList();
                if (sealVehicleSendCodeInfoList == null || sealVehicleSendCodeInfoList.isEmpty()) {
                    //有批次提交但是列表为空，写失败回执
                    callBackNoticeFail(sealVehicleTaskCode, "该运力需要提交批次信息，但是任务消息中批次信息为空", transportCode);
                    return;
                }
                Map<String, SealCarDto> sealCarDtoMap = new HashMap<>();
                for (SealVehicleSendCodeInfo sealVehicleSendCodeInfo : sealVehicleSendCodeInfoList) {
                    if (StringHelper.isNotEmpty(sealVehicleSendCodeInfo.getVehicleNumber())) {
                        String vehicleNumber = sealVehicleSendCodeInfo.getVehicleNumber();
                        if (sealCarDtoMap.containsKey(vehicleNumber)) {
                            SealCarDto sealCarDto = sealCarDtoMap.get(vehicleNumber);
                            sealCarDto.getBatchCodes().add(sealVehicleSendCodeInfo.getSendCode());
                        } else {
                            List<PreSealVehicle> preSealVehicleList = preSealVehicleService.getPreSealInfoByParams(submitSealVehicleDto.getTransportCode(), vehicleNumber);
                            if (preSealVehicleList == null || preSealVehicleList.isEmpty()) {
                                //运力和车牌没有预封车信息，写失败回执
                                callBackNoticeFail(sealVehicleTaskCode, "该运力中，车牌：" + vehicleNumber + "没有预封车信息，无法封车！", transportCode);
                                return;
                            }
                            SealCarDto sealCarDto = convert2SealCarDto(submitSealVehicleDto, preSealVehicleList.get(0));
                            ArrayList<String> batchCodes = new ArrayList<>();
                            batchCodes.add(sealVehicleSendCodeInfo.getSendCode());
                            sealCarDto.setBatchCodes(batchCodes);
                            sealCarDtoMap.put(vehicleNumber, sealCarDto);
                        }
                    }
                }
                sealCarDtoList.addAll(sealCarDtoMap.values());
            } else {
                //不包含批次，需要处理封车任务时，根据当前运力取车牌，取批次
                //获取预封车信息
                List<PreSealVehicle> preSealVehicleList = preSealVehicleService.getPreSealInfoByParams(submitSealVehicleDto.getTransportCode());
                //如果预封车信息为空，无法取出车牌信息，写失败回执
                if (preSealVehicleList == null || preSealVehicleList.isEmpty()) {
                    callBackNoticeFail(sealVehicleTaskCode, "该运力没有预封车信息，无法封车！", transportCode);
                    return;
                }
                //如果预封车信息大于1，有多条预封车信息，写失败回执
                if (preSealVehicleList.size() > 1) {
                    callBackNoticeFail(sealVehicleTaskCode, "该运力存在多条预封车信息并且未提供批次与车牌关系，无法封车！", transportCode);
                    return;
                }
                PreSealVehicle preSealVehicle = preSealVehicleList.get(0);
                SealCarDto sealCarDto = convert2SealCarDto(submitSealVehicleDto, preSealVehicle);
                //获取批次信息
                List<String> sendCodeList = newSealVehicleService.getUnSealSendCodeList(preSealVehicle.getCreateSiteCode(), preSealVehicle.getReceiveSiteCode(), submitSealVehicleDto.getHourRange());
                if (sendCodeList == null || sendCodeList.isEmpty()) {
                    //无需要封车的批次，写失败回执
                    callBackNoticeFail(sealVehicleTaskCode, "该运力未抓取到待封车的批次，无法封车！", transportCode);
                    return;
                }
                sealCarDto.setBatchCodes(sendCodeList);
                sealCarDtoList.add(sealCarDto);
            }
            NewSealVehicleResponse newSealVehicleResponse = newSealVehicleService.doSealCarFromDmsWorkBench(sealCarDtoList);
            if (newSealVehicleResponse != null && NewSealVehicleResponse.CODE_OK.equals(newSealVehicleResponse.getCode())) {
                //更新预封车数据
                completePreSealRecord(sealCarDtoList, submitSealVehicleDto);
                //写成功回执
                callBackNoticeSuccess(sealVehicleTaskCode, transportCode);
            } else {
                //写失败回执
                callBackNoticeFail(sealVehicleTaskCode, newSealVehicleResponse == null ? "运输系统未未响应，封车失败" : newSealVehicleResponse.getMessage(), transportCode);
            }

        } catch (Exception e) {
            log.error("处理一键封车任务失败，参数：{}", JsonHelper.toJson(submitSealVehicleDto), e);
            //写失败回执
        } finally {
            jimdbCacheService.del(redisKey);
        }

    }

    /*
     * 实体转换
     * */
    private SealCarDto convert2SealCarDto(SubmitSealVehicleDto submitSealVehicleDto, PreSealVehicle preSealVehicle) {
        SealCarDto sealCarDto = new SealCarDto();
        sealCarDto.setSealCarTime(submitSealVehicleDto.getOperateTime());
        if (StringHelper.isNotEmpty(preSealVehicle.getSealCodes())) {
            sealCarDto.setSealCodes(Arrays.asList(preSealVehicle.getSealCodes().split(Constants.SEPARATOR_COMMA)));
        }
        sealCarDto.setSealSiteId(submitSealVehicleDto.getCreateSiteCode());
        sealCarDto.setSealSiteName(submitSealVehicleDto.getCreateSiteName());
        sealCarDto.setSealUserCode(submitSealVehicleDto.getOperatorCode().toString());
        sealCarDto.setSealUserName(submitSealVehicleDto.getOperatorName());
        sealCarDto.setSource(Constants.SEAL_SOURCE);
        sealCarDto.setSealCarType(Constants.SEAL_TYPE_TRANSPORT);
        sealCarDto.setTransportCode(submitSealVehicleDto.getTransportCode());
        sealCarDto.setVehicleNumber(preSealVehicle.getVehicleNumber());
        sealCarDto.setVolume(preSealVehicle.getVolume());
        sealCarDto.setWeight(preSealVehicle.getWeight());
        return sealCarDto;
    }

    /*
     * 回调通知
     * */
    private void callback(String taskCode, String remark, Integer taskStatus, String transportCode) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("taskCode", taskCode);
        jsonObject.put("transportCode", transportCode);
        jsonObject.put("remark", remark);
        jsonObject.put("taskStatus", taskStatus);
        wbSealVehicleCallbackProducer.sendOnFailPersistent(taskCode, jsonObject.toJSONString());
    }

    /*
    * 回调通知成功
    * */
    private void callBackNoticeSuccess(String taskCode, String transportCode) {
        callback(taskCode, "ok", 20, transportCode);
    }

    /*
     * 回调通知失败
     * */
    private void callBackNoticeFail(String taskCode, String remark, String transportCode) {
        callback(taskCode, remark, 30, transportCode);
    }

    /*
    * 更新预封车记录
    * */
    private void completePreSealRecord(List<SealCarDto> sealCarDtoList, SubmitSealVehicleDto submitSealVehicleDto) {
        for (SealCarDto sealCarDto : sealCarDtoList) {
            PreSealVehicle preSealVehicle = new PreSealVehicle();
            preSealVehicle.setTransportCode(sealCarDto.getTransportCode());
            preSealVehicle.setVehicleNumber(sealCarDto.getVehicleNumber());
            preSealVehicle.setUpdateUserErp(submitSealVehicleDto.getOperatorErp());
            preSealVehicle.setUpdateTime(new Date());
            preSealVehicle.setUpdateUserName(sealCarDto.getSealUserName());
            preSealVehicleService.completePreSealVehicleRecord(preSealVehicle);
        }
    }
}
