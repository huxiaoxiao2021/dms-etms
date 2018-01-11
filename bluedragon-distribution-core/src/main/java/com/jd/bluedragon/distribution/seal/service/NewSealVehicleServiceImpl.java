package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.domain.SealCarMqDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.SystemLogContants;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.etms.vos.ws.VosBusinessWS;
import com.jd.etms.vos.ws.VosQueryWS;
import com.jd.etms.vts.dto.VtsTransportResourceDto;
import com.jd.etms.vts.ws.VtsQueryWS;
import com.jd.tms.tfc.dto.TransWorkItemDto;
import com.jd.tms.tfc.ws.TfcQueryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("newSealVehicleService")
public class NewSealVehicleServiceImpl implements NewSealVehicleService {
	@Autowired
	private VosQueryWS vosQueryWS;

	@Autowired
	private VosBusinessWS vosBusinessWS;

	@Autowired
	private VtsQueryWS vtsQueryWS;

	@Autowired
	private TfcQueryWS tfcQueryWS;

	@Autowired
	private SendMDao sendMDao;

    @Autowired
    private GoddessService goddessService;

    @Autowired
    @Qualifier("sealCarProducer")
    private DefaultJMQProducer sealCarProducer;

    @Autowired
    @Qualifier("unsealCarProducer")
    private DefaultJMQProducer unsealCarProducer;

    private final Log logger = LogFactory.getLog(this.getClass());

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.seal", mState = {JProEnum.TP})
	public CommonDto<String> seal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars) {
        List<SealCarDto> paramList = convertList(sealCars);
        logger.info("封车参数："+ JsonHelper.toJson(paramList));
        CommonDto<String> sealCarInfo = null;
        String msg = "";
        try {
            sealCarInfo = vosBusinessWS.doSealCar(paramList);
            if(sealCarInfo == null) {
                msg = "封车JSF接口返回为空";
            }else if(Constants.RESULT_SUCCESS == sealCarInfo.getCode()){
                msg = MESSAGE_SEAL_SUCCESS;
                //封车成功，发送封车mq消息
                sealCarMQ(paramList);
            }else{
                msg = "["+sealCarInfo.getCode()+":"+sealCarInfo.getMessage()+"]";
            }
        }catch (Exception e){
            this.logger.error("封车-error", e);
            msg = "封车异常：["+ e.getMessage() +"]";
        }finally {
            addSystemLog(paramList, msg);
        }
        return sealCarInfo;
	}


    /**
     * 离线封车
     * @param sealCars
     * @return
     */
    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.offlineSeal", mState = {JProEnum.TP})
    public CommonDto<String> offlineSeal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars) {
        List<SealCarDto> paramList = offlineConvertList(sealCars);
        logger.info("离线封车参数："+ JsonHelper.toJson(paramList));
        CommonDto<String> sealCarInfo = null;
        String msg = "离线封车失败：";
        try {
            sealCarInfo = vosBusinessWS.doSealCar(paramList);
            if(sealCarInfo == null) {
                msg += "封车JSF接口返回为空";
            }else if(Constants.RESULT_SUCCESS == sealCarInfo.getCode()){
                msg = MESSAGE_OFFLINE_SEAL_SUCCESS;
                //封车成功，发送封车mq消息
                sealCarMQ(paramList);
            }else{
                msg += "["+sealCarInfo.getCode()+":"+sealCarInfo.getMessage()+"]";
            }
        }catch (Exception e){
            this.logger.error("离线封车-error", e);
            msg += "["+ e.getMessage() +"]";
        }finally {
            addSystemLog(paramList, msg);
        }
        return sealCarInfo;
    }

    @Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.findSealInfo", mState = {JProEnum.TP})
	public CommonDto<PageDto<SealCarDto>> findSealInfo(SealCarDto request,PageDto<SealCarDto> pageDto) {
		CommonDto<PageDto<SealCarDto>> sealCarInfo = vosQueryWS.querySealCarPage(request,pageDto);
		return sealCarInfo;
	}

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.unseal", mState = {JProEnum.TP})
	public CommonDto<String> unseal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars) {
        List<SealCarDto> paramList = convertList(sealCars);
        logger.info("解封车参数："+JsonHelper.toJson(paramList));
        CommonDto<String> sealCarInfo = null;
        String msg = "";
        try {
            sealCarInfo = vosBusinessWS.doDesealCar(paramList);
            if(sealCarInfo == null) {
                msg = "解封车JSF接口返回为空";
            }else if(Constants.RESULT_SUCCESS == sealCarInfo.getCode()){
                msg = MESSAGE_UNSEAL_SUCCESS;
                //解封车成功，发送封车mq消息
                deSealCarMQ(paramList);
            }else{
                msg = "["+sealCarInfo.getCode()+":"+sealCarInfo.getMessage()+"]";
            }
        }catch (Exception e){
            this.logger.error("封车-error", e);
            msg = "封车异常：["+ e.getMessage() +"]";
        }finally {
            addSystemLog(paramList, msg);
        }
        return sealCarInfo;
	}

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.isBatchCodeHasSealed", mState = {JProEnum.TP})
	public CommonDto<Boolean> isBatchCodeHasSealed(String batchCode) {
		CommonDto<Boolean> isSealed = vosQueryWS.isBatchCodeHasSealed(batchCode);
		return isSealed;
	}

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vts.getTransportResourceByTransCode", mState = {JProEnum.TP})
	public com.jd.etms.vts.dto.CommonDto<VtsTransportResourceDto> getTransportResourceByTransCode(String batchCode) {
		com.jd.etms.vts.dto.CommonDto<VtsTransportResourceDto> dto = vtsQueryWS.getTransportResourceByTransCode(batchCode);
		return dto;
	}

    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vts.queryTransWorkItemBySimpleCode", mState = {JProEnum.TP})
    public com.jd.tms.tfc.dto.CommonDto<TransWorkItemDto> queryTransWorkItemBySimpleCode(String simpleCode) throws Exception {
        return tfcQueryWS.queryTransWorkItemBySimpleCode(simpleCode);
    }

    public boolean checkSendIsExist(String sendCode) {
		SendM sendM = sendMDao.selectOneBySiteAndSendCode(null, sendCode);
		return sendM != null;
	}

    /**
     * 记录封车解封车操作日志
     * @param sealCars
     * @param remark
     */
    private void addSystemLog(List<SealCarDto> sealCars, String remark) {
        if(sealCars == null || sealCars.isEmpty()){
            return;
        }
        for(SealCarDto dto : sealCars){
            Goddess goddess = new Goddess();
            goddess.setHead(dto.getSealSiteId() + "-" + dto.getDesealSiteId());
            dto.setRemark(remark);
            goddess.setBody(JsonHelper.toJson(dto));
            goddess.setDateTime(new Date());
            if(StringUtils.isNotBlank(dto.getSealCarCode())){//解封车日志
                goddess.setKey(dto.getSealCarCode());
            }else if(StringUtils.isNotBlank(dto.getTransportCode())){//封车日志
                goddess.setKey(dto.getTransportCode());
            }
            goddessService.save(goddess);
        }
    }

    /**
     * 封车成功后发送封车MQ
     * @param sealCars
     */
    private void sealCarMQ(List<SealCarDto> sealCars){
        if(sealCars!=null){
            for(SealCarDto sealCarDto:sealCars){
                SealCarMqDto sealCarMqDto = new SealCarMqDto();
                sealCarMqDto.setDmsSiteId(sealCarDto.getSealSiteId());
                sealCarMqDto.setTransportCode(sealCarDto.getTransportCode());
                sealCarMqDto.setVehicleNumber(sealCarDto.getVehicleNumber());
                sealCarMqDto.setOperUserCode(sealCarDto.getSealUserCode());
                sealCarMqDto.setOperUserName(sealCarDto.getSealUserName());
                sealCarMqDto.setOperTime(sealCarDto.getSealCarTime());
                sealCarMqDto.setSealCodes(sealCarDto.getSealCodes());
                sealCarMqDto.setSendCodeList(sealCarDto.getBatchCodes());
                try {
                    sealCarProducer.send(sealCarDto.getTransportCode(), JsonHelper.toJsonUseGson(sealCarMqDto));
                }catch (Exception e){
                    SystemLogUtil.log(sealCarDto.getTransportCode(), sealCarDto.getSealUserCode(), sealCarProducer.getTopic(),
                            sealCarDto.getSealSiteId().longValue(), JsonHelper.toJsonUseGson(sealCarMqDto), SystemLogContants.TYPE_SEAL_MQ);
                    logger.error("发送封车mq消息失败:" + e.getMessage());
                }
            }
        }
    }

    /**
     * 解封车成功后发送封车MQ
     * @param sealCars
     */
    private void deSealCarMQ(List<SealCarDto> sealCars){
        //解封车成功，发送解封车mq消息dms_unseal_car
        if(sealCars!=null){
            for(SealCarDto sealCarDto:sealCars){
                SealCarMqDto sealCarMqDto = new SealCarMqDto();
                sealCarMqDto.setDmsSiteId(sealCarDto.getDesealSiteId());
                sealCarMqDto.setTransportCode(sealCarDto.getTransportCode());
                sealCarMqDto.setVehicleNumber(sealCarDto.getVehicleNumber());
                sealCarMqDto.setOperUserCode(sealCarDto.getDesealUserCode());
                sealCarMqDto.setOperUserName(sealCarDto.getDesealUserName());
                sealCarMqDto.setOperTime(sealCarDto.getDesealCarTime());
                sealCarMqDto.setSealCodes(sealCarDto.getSealCodes());
                sealCarMqDto.setSendCodeList(sealCarDto.getBatchCodes());
                sealCarMqDto.setSealCarCode(sealCarDto.getSealCarCode());
                try {
                    unsealCarProducer.send(sealCarDto.getTransportCode(), JsonHelper.toJsonUseGson(sealCarMqDto));
                }catch (Exception e){
                    SystemLogUtil.log(sealCarDto.getTransportCode(), sealCarDto.getSealUserCode(), unsealCarProducer.getTopic(),
                            sealCarDto.getSealSiteId().longValue(), JsonHelper.toJsonUseGson(sealCarMqDto), SystemLogContants.TYPE_UNSEAL_MQ);
                    logger.error("发送解封车mq消息失败:" + e.getMessage());
                }
            }
        }
    }

    /**
     * 离线封车vos转换
     * @param sourceSealDtos
     * @return
     */
    private List<SealCarDto> offlineConvertList(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sourceSealDtos) {
        List<SealCarDto> sealCarDtos = new ArrayList<SealCarDto>();
        for (com.jd.bluedragon.distribution.wss.dto.SealCarDto sourceSealDto : sourceSealDtos) {
            sealCarDtos.add(convert(sourceSealDto, null));
        }
        return sealCarDtos;
    }

    /**
     * 实时操作vos转换
     * @param sourceSealDtos
     * @return
     */
    private List<SealCarDto> convertList(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sourceSealDtos) {
        List<SealCarDto> sealCarDtos = new ArrayList<SealCarDto>();
        Date nowTime = new Date();//封车取当前服务器当前时间
        for (com.jd.bluedragon.distribution.wss.dto.SealCarDto sourceSealDto : sourceSealDtos) {
            sealCarDtos.add(convert(sourceSealDto, nowTime));
        }
        return sealCarDtos;
    }

    /**
     * 封车vo转换
     * @param sourceSealDto
     * @return
     */
    private SealCarDto convert(com.jd.bluedragon.distribution.wss.dto.SealCarDto sourceSealDto, Date nowTime) {
        SealCarDto sealCarDto = new SealCarDto();
        sealCarDto.setId(sourceSealDto.getId());
        sealCarDto.setSealCarCode(sourceSealDto.getSealCarCode());
        sealCarDto.setStatus(sourceSealDto.getStatus());
        sealCarDto.setSource(Constants.SEAL_SOURCE);
        sealCarDto.setVehicleNumber(sourceSealDto.getVehicleNumber());//车牌号
        sealCarDto.setTransportCode(sourceSealDto.getTransportCode());
        sealCarDto.setStartOrgCode(sourceSealDto.getStartOrgCode());
        sealCarDto.setStartOrgName(sourceSealDto.getStartOrgName());
        sealCarDto.setStartSiteId(sourceSealDto.getStartSiteId());
        sealCarDto.setStartSiteCode(sourceSealDto.getStartSiteCode());
        sealCarDto.setStartSiteName(sourceSealDto.getStartSiteName());
        sealCarDto.setEndOrgCode(sourceSealDto.getEndOrgCode());
        sealCarDto.setEndOrgName(sourceSealDto.getEndOrgName());
        sealCarDto.setEndSiteId(sourceSealDto.getEndSiteId());
        sealCarDto.setEndSiteCode(sourceSealDto.getEndSiteCode());
        sealCarDto.setEndSiteName(sourceSealDto.getEndSiteName());
        sealCarDto.setSealSiteId(sourceSealDto.getSealSiteId());
        sealCarDto.setSealSiteCode(sourceSealDto.getSealSiteCode());
        sealCarDto.setSealSiteName(sourceSealDto.getSealSiteName());
        sealCarDto.setSealUserCode(sourceSealDto.getSealUserCode());
        sealCarDto.setSealUserName(sourceSealDto.getSealUserName());
        sealCarDto.setDesealSiteId(sourceSealDto.getDesealSiteId());
        sealCarDto.setDesealSiteCode(sourceSealDto.getDesealSiteCode());
        sealCarDto.setDesealSiteName(sourceSealDto.getDesealSiteName());
        sealCarDto.setDesealUserCode(sourceSealDto.getDesealUserCode());
        sealCarDto.setDesealUserName(sourceSealDto.getDesealUserName());
        sealCarDto.setRemark(sourceSealDto.getRemark());
        sealCarDto.setYn(sourceSealDto.getYn());
        sealCarDto.setSealCode(sourceSealDto.getSealCode());//封车号
        sealCarDto.setBatchCodes(sourceSealDto.getBatchCodes());
        sealCarDto.setDesealCodes(sourceSealDto.getDesealCodes());
        sealCarDto.setSealCodes(sourceSealDto.getSealCodes());
        sealCarDto.setCarrierCode(sourceSealDto.getCarrierCode());
        sealCarDto.setCarrierName(sourceSealDto.getCarrierName());
        sealCarDto.setCarrierType(sourceSealDto.getCarrierType());
        sealCarDto.setTransType(sourceSealDto.getTransType());
        sealCarDto.setTransTypeName(sourceSealDto.getTransTypeName());
        sealCarDto.setTransWay(sourceSealDto.getTransWay());
        sealCarDto.setTransWayName(sourceSealDto.getTransWayName());
//        sealCarDto.setSealCarTimeBegin();
//        sealCarDto.setSealCarTimeEnd();
        sealCarDto.setStartSiteType(sourceSealDto.getStartSiteType());
        sealCarDto.setStartSiteTypeName(sourceSealDto.getStartSiteTypeName());
        sealCarDto.setEndSiteType(sourceSealDto.getEndSiteType());
        sealCarDto.setEndSiteTypeName(sourceSealDto.getEndSiteTypeName());
        sealCarDto.setSealCarType(sourceSealDto.getSealCarType());
        sealCarDto.setTransWorkItemCode(sourceSealDto.getTransWorkItemCode());
        sealCarDto.setItemSimpleCode(sourceSealDto.getItemSimpleCode());
        sealCarDto.setWeight(sourceSealDto.getWeight());
        sealCarDto.setVolume(sourceSealDto.getVolume());
        sealCarDto.setTransBookCode(sourceSealDto.getTransBookCode());
        //设置时间
        if(nowTime == null){//离线数据
            if(StringUtils.isNotBlank(sourceSealDto.getSealCarTime())){
                sealCarDto.setSealCarTime(DateHelper.parseDate(sourceSealDto.getSealCarTime(), Constants.DATE_TIME_MS_FORMAT));
            }else if(StringUtils.isNotBlank(sourceSealDto.getDesealCarTime())){
                sealCarDto.setDesealCarTime(DateHelper.parseDate(sourceSealDto.getDesealCarTime(), Constants.DATE_TIME_MS_FORMAT));
            }
        }else{//实时操作
            if(sourceSealDto.getDesealCarTime() != null && sourceSealDto.getDesealCarTime().length() > 0) {    //解封车
                sealCarDto.setDesealCarTime(nowTime);  //解封车时间取服务器当前时间
            }else{//封车
                sealCarDto.setSealCarTime(nowTime);    //封车时间取服务器当前时间
            }
        }

        return sealCarDto;
    }
}
