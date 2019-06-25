package com.jd.bluedragon.distribution.seal.service;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.jmq.domain.SealCarMqDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.cancelSealRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicleEnum;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.SystemLogContants;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.etms.vos.dto.*;
import com.jd.etms.vos.ws.VosBusinessWS;
import com.jd.etms.vos.ws.VosQueryWS;
import com.jd.etms.vts.dto.VtsTransportResourceDto;
import com.jd.etms.vts.ws.VtsQueryWS;
import com.jd.fastjson.JSON;
import com.jd.tms.tfc.dto.TransBookBillQueryDto;
import com.jd.tms.tfc.dto.TransWorkItemDto;
import com.jd.tms.tfc.dto.TransWorkItemWsDto;
import com.jd.tms.tfc.ws.TfcQueryWS;
import com.jd.tms.tfc.ws.TfcSelectWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.*;

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
	private TfcSelectWS tfcSelectWS;

	@Autowired
	private SendMDao sendMDao;

    @Autowired
    private GoddessService goddessService;

    @Autowired
    private SealVehiclesService sealVehiclesService;

    @Autowired
    @Qualifier("sealCarProducer")
    private DefaultJMQProducer sealCarProducer;

    @Autowired
    @Qualifier("unsealCarProducer")
    private DefaultJMQProducer unsealCarProducer;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private VosManager vosManager;

    private static final Integer UNSEAL_CAR_IN_RECIVE_AREA = 2;    //带解封的车辆在围栏里(1-是否在始发网点 2-是否在目的网点)

    private static final Integer IN_AREA_FLAG = 2;    //标识车辆不在围栏内(1：在围栏内 2：不在围栏内 3：坐标数据不存在 4：围栏数据不存在 5：其他)

    private final Log logger = LogFactory.getLog(this.getClass());

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.seal",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public CommonDto<String> seal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars) throws Exception{
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
                addRedisCache(paramList);
                saveSealData(paramList);
            }else{
                msg = "["+sealCarInfo.getCode()+":"+sealCarInfo.getMessage()+"]";
            }
        }catch (Exception e){
            this.logger.error("封车-error", e);
            msg = "封车异常：["+ e.getMessage() +"]";
            throw e;
        }finally {
            addSystemLog(paramList, msg);
        }
        return sealCarInfo;
	}


    /**
     * VOS封车业务同时生成车次任务
     * @param sealCars
     * @return
     */
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.doSealCarWithVehicleJob",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse doSealCarWithVehicleJob(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars) {
        List<SealCarDto> paramList = convertList(sealCars);
        logger.info("VOS封车业务同时生成车次任务参数："+ JsonHelper.toJson(paramList));

        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        String errorMsg = "封车成功";
        List<SealCarDto> successSealCarList = new ArrayList<>(); //封车成功的

        //循环调用运输封车同时生成车次任务的接口
        for (SealCarDto param : paramList) {
            String singleErrorMsg = "";
            String transportCode = param.getTransportCode();

            CommonDto<String> sealCarInfo = vosManager.doSealCarWithVehicleJob(param);
            if (sealCarInfo == null) {
                singleErrorMsg = "运力编码封车失败：" + transportCode + ".";
                logger.warn("VOS封车业务同时生成车次任务接口返回为空.参数:" + JSON.toJSONString(param));
            } else if (Constants.RESULT_SUCCESS == sealCarInfo.getCode()) {
                successSealCarList.add(param);
            } else {
                singleErrorMsg = "运力编码封车失败：" + transportCode + "." + sealCarInfo.getCode() + "-" + sealCarInfo.getMessage() + ".";
                logger.warn("VOS封车业务同时生成车次任务失败.参数:" + JSON.toJSONString(param) + ",返回值:" + singleErrorMsg);

            }
            errorMsg += singleErrorMsg;
        }

        //封车成功的，写数据库，发送封车mq消息
        if (successSealCarList.size() > 0) {
            sealCarMQ(successSealCarList);
            addRedisCache(successSealCarList);
            saveSealData(successSealCarList);
        }

        addSystemLog(paramList, errorMsg);

        if(successSealCarList.size() == paramList.size()){
            sealVehicleResponse.setCode(JdResponse.CODE_OK);
            sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_SEAL_SUCCESS);
        }else{
            sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
            sealVehicleResponse.setMessage(errorMsg);
        }

        return sealVehicleResponse;
    }

    /**
     * 离线封车
     * @param sealCars
     * @return
     */
    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.offlineSeal",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
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
                addRedisCache(paramList);
                saveNXSealData(paramList);
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

  /**
   * 取消封车
   *
   * @param request
   * @return
   */
  @Override
  @JProfiler(
    jKey = UmpConstants.UMP_KEY_JSF_CLIENT + "vos.vosBusinessWS.doCancelSealCarWithBatchCode",
    jAppName = Constants.UMP_APP_NAME_DMSWEB,
    mState = {JProEnum.TP, JProEnum.FunctionError}
  )
  public NewSealVehicleResponse cancelSeal(cancelSealRequest request) {
    NewSealVehicleResponse sealVehicleResponse =
        new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);

    CancelSealCarDto TMS_param = new CancelSealCarDto();
    TMS_param.setBatchCode(request.getBatchCode());
    TMS_param.setOperateType(request.getOperateType());
    TMS_param.setOperateUserCode(request.getOperateUserCode());
    TMS_param.setOperateTime(DateHelper.parseAllFormatDateTime(request.getOperateTime()));

    logger.info("取消封车参数：" + JsonHelper.toJson(TMS_param));

    CommonDto<List<String>> cancelSealInfo = vosBusinessWS.doCancelSealCarWithBatchCode(TMS_param);
    if (cancelSealInfo != null
        && cancelSealInfo.getCode() == CommonDto.CODE_SUCCESS
        && cancelSealInfo.getData().isEmpty() == false) {
      List<String> batchList = cancelSealInfo.getData();

      removeRedisCache(batchList);
      cancelSealData(batchList, TMS_param.getOperateUserCode());

      sealVehicleResponse.setCode(JdResponse.CODE_OK);
      sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_CANCEL_SEAL_SUCCESS);
    } else {
      logger.warn("取消封车JSF接口返回为空.参数：" + JsonHelper.toJson(TMS_param));
      sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
      sealVehicleResponse.setMessage(cancelSealInfo != null ? cancelSealInfo.getMessage():"运输取消封车返回空");
    }
    return sealVehicleResponse;
    }

    /**
     * 查询带解任务
     * @param request
     * @param pageDto
     * @return
     */
    @Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.findSealInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public CommonDto<PageDto<SealCarDto>> findSealInfo(SealCarDto request,PageDto<SealCarDto> pageDto) {
		CommonDto<PageDto<SealCarDto>> sealCarInfo = vosQueryWS.querySealCarPage(request,pageDto);
		return sealCarInfo;
	}

    /**
     * 查询相关车辆是否在分拣中心的电子围栏内
     * @param sealCars 待解的封车任务
     * @return
     * @throws Exception
     */
    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.isSealCarInArea",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<String> isSealCarInArea(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars) throws Exception{
        List<String> sealCarCodes = new ArrayList<String>(sealCars.size());
        for (com.jd.bluedragon.distribution.wss.dto.SealCarDto sourceSealDto : sealCars) {
            sealCarCodes.add(sourceSealDto.getSealCarCode());
        }
        if(logger.isInfoEnabled()){
            logger.info("查询待解任务的车辆是否在围栏参数：" + JsonHelper.toJson(sealCarCodes));
        }
        List<String> unsealCarOutArea = new ArrayList<String>(sealCarCodes.size());//这里已经控制最大20个
        try {
            CommonDto<List<SealCarInAreaDto>> sealCarInfo = vosQueryWS.isSealCarInArea(sealCarCodes, UNSEAL_CAR_IN_RECIVE_AREA);
            logger.info("查询待解任务的车辆是否在围栏结果:" + JsonHelper.toJson(sealCarInfo));

            //由于一车可有多个封车编码，所以针对车牌号去重
            if(Constants.RESULT_SUCCESS == sealCarInfo.getCode() && sealCarInfo.getData() != null){//接口调用成功且有数据时
                for(SealCarInAreaDto dto : sealCarInfo.getData()){
                    //车辆不在围栏且前面没有标识过该车辆，将车牌号加入结果unsealCarOutArea中
                    if(IN_AREA_FLAG.equals(dto.getInAreaFlag()) && !unsealCarOutArea.contains(dto.getVehicleNumber())){
                        unsealCarOutArea.add(dto.getVehicleNumber());
                    }
                }
            }else{
                logger.warn("查询待解任务的车辆是否在围栏失败，参数：" + JsonHelper.toJson(sealCarCodes) +";结果:" + JsonHelper.toJson(sealCarInfo));
            }
        }catch (Exception e){
            logger.error("查询待解任务的车辆是否在围栏异常：" + JsonHelper.toJson(sealCarCodes) , e);
            throw e;
        }
        return unsealCarOutArea;
    }

    @Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.unseal",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public CommonDto<String> unseal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars) throws Exception{
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
                saveDeSealData(paramList);
            }else{
                msg = "["+sealCarInfo.getCode()+":"+sealCarInfo.getMessage()+"]";
            }
        }catch (Exception e){
            this.logger.error("封车-error", e);
            msg = "封车异常：["+ e.getMessage() +"]";
            throw e;
        }finally {
            addSystemLog(paramList, msg);
        }
        return sealCarInfo;
	}

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.isBatchCodeHasSealed",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public CommonDto<Boolean> isBatchCodeHasSealed(String batchCode) {
		CommonDto<Boolean> isSealed = vosQueryWS.isBatchCodeHasSealed(batchCode);
		return isSealed;
	}

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vts.getTransportResourceByTransCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public com.jd.etms.vts.dto.CommonDto<VtsTransportResourceDto> getTransportResourceByTransCode(String batchCode) {
		com.jd.etms.vts.dto.CommonDto<VtsTransportResourceDto> dto = vtsQueryWS.getTransportResourceByTransCode(batchCode);
		return dto;
	}

    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vts.queryTransWorkItemBySimpleCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public com.jd.tms.tfc.dto.CommonDto<TransWorkItemDto> queryTransWorkItemBySimpleCode(String simpleCode) throws Exception {
        return tfcQueryWS.queryTransWorkItemBySimpleCode(simpleCode);
    }

    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vts.queryVehicleNumberOrItemCodeByParam", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public com.jd.tms.tfc.dto.CommonDto<TransWorkItemWsDto> getVehicleNumberOrItemCodeByParam(TransWorkItemWsDto transWorkItemWsDto) throws Exception {
        return tfcSelectWS.getVehicleNumberOrItemCodeByParam(transWorkItemWsDto);
    }

    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vts.checkTransportCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public com.jd.tms.tfc.dto.CommonDto<String> checkTransportCode(String simpleCode, String transportCode) throws Exception {
        return tfcSelectWS.checkTransportCode(simpleCode, transportCode);
    }

    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vts.getTransBookBill", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public com.jd.tms.tfc.dto.CommonDto<com.jd.tms.tfc.dto.PageDto<com.jd.tms.tfc.dto.TransBookBillResultDto>> getTransBookBill(com.jd.tms.tfc.dto.TransBookBillQueryDto transBookBillQueryDto, com.jd.tms.tfc.dto.PageDto<TransBookBillQueryDto> pageDto) throws Exception {
        return tfcSelectWS.getTransBookBill(transBookBillQueryDto, pageDto);
    }

    /**
     * 校验车牌号能否生成车次任务
     * @param transportCode 运力编码
     * @param vehicleNumber 车牌号
     * @return
     */
    public CommonDto<String> verifyVehicleJobByVehicleNumber(String transportCode,String vehicleNumber){
        VerifyVehicleJobDto dto = new VerifyVehicleJobDto();
        dto.setTransportCode(transportCode);
        dto.setVehicleNumber(vehicleNumber);
        return vosManager.verifyVehicleJobByVehicleNumber(dto);
    }

    @Override
    public boolean checkSendIsExist(String sendCode) {
		SendM sendM = sendMDao.selectOneBySiteAndSendCode(null, sendCode);
		return sendM != null;
	}

    @Override
    public boolean checkSendCodeIsSealed(String sendCode) {
        if (this.getSealCarTimeBySendCode(sendCode) != null) {
            return true;
        }
        return false;
    }

    @Override
    public Long getSealCarTimeBySendCode(String sendCode) {
        try {
            String sealCarTime = redisManager.getCache(Constants.CACHE_KEY_PRE_SEAL_SENDCODE + sendCode);
            logger.info("Redis取封车批次号" + sendCode + "的封车时间，结果：" + sealCarTime);
            if (StringUtils.isNotBlank(sealCarTime) && Long.valueOf(sealCarTime) > 0) {
                return Long.valueOf(sealCarTime);
            }
        } catch (Exception e) {
            logger.warn("redis取封车批次号失败：" + e.getMessage());
        }
        return null;
    }

    /**
     * 将封车的批次号缓存到Redis里
     *
     * @param paramList
     */
    private void addRedisCache(List<SealCarDto> paramList) {
        if (paramList == null || paramList.size() == 0) {
            return;
        }
        for (SealCarDto dto : paramList) {
            for (String sendCode : dto.getBatchCodes()) {
                try {
                    redisManager.setex(Constants.CACHE_KEY_PRE_SEAL_SENDCODE + sendCode, Constants.TIME_SECONDS_ONE_WEEK, String.valueOf(dto.getSealCarTime().getTime()));
                    logger.info("已封车批次号存入缓存成功:" + sendCode);
                } catch (Throwable e) {
                    logger.warn("已封车批次号存入缓存失败:" + sendCode + ";异常：" + e.getMessage());
                }
            }
        }
    }

  /**
   * 将封车的批次号从Redis里删除
   *
   * @param paramList
   */
  private void removeRedisCache(List<String> paramList) {
    if (paramList == null || paramList.size() == 0) {
      return;
    }
    for (String sendCode : paramList) {
      try {
        redisManager.del(Constants.CACHE_KEY_PRE_SEAL_SENDCODE + sendCode);
        logger.info("已封车批次号刪除缓存成功:" + sendCode);
      } catch (Throwable e) {
        logger.warn("已封车批次号刪除缓存失败:" + sendCode + ";异常：" + e.getMessage());
      }
    }
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
        /** 写入日志时间处理：cassandra主键包含时间字段，为避免写入太快前面的数据被覆盖，做此处理 */
        long date = System.currentTimeMillis();
        long correctValue = 0L;
        for(SealCarDto dto : sealCars){
            Goddess goddess = new Goddess();
            goddess.setHead(dto.getSealSiteId() + "-" + dto.getDesealSiteId());
            dto.setRemark(remark);
            goddess.setBody(JsonHelper.toJson(dto));
            goddess.setDateTime(new Date(date + correctValue));
            if(StringUtils.isNotBlank(dto.getSealCarCode())){//解封车日志
                goddess.setKey(dto.getSealCarCode());
            }else if(StringUtils.isNotBlank(dto.getTransportCode())){//按运力编码封车日志
                goddess.setKey(dto.getTransportCode());
            }else if(StringUtils.isNotBlank(dto.getItemSimpleCode())){//按任务封车日志（key为任务简码）
                goddess.setKey(dto.getItemSimpleCode());
            }
            goddessService.save(goddess);
            correctValue++;
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
                sealCarMqDto.setItemSimpleCode(sealCarDto.getItemSimpleCode());
                sealCarMqDto.setSealCarType(sealCarDto.getSealCarType());
                sealCarMqDto.setWeight(sealCarDto.getWeight());
                sealCarMqDto.setVolume(sealCarDto.getVolume());
                String key = sealCarDto.getTransportCode();
                if(StringUtils.isEmpty(key)){//运力编码为空时，取任务简码
                    key = sealCarDto.getItemSimpleCode();
                }
                try {
                    sealCarProducer.send(key, JsonHelper.toJsonUseGson(sealCarMqDto));
                }catch (Exception e){
                    SystemLogUtil.log(key, sealCarDto.getSealUserCode(), sealCarProducer.getTopic(),
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
        if(sealCarDto.getSealCarType() == null && StringUtils.isNotEmpty(sealCarDto.getTransportCode())
                && StringUtils.isEmpty(sourceSealDto.getItemSimpleCode())){
            sealCarDto.setSealCarType(Constants.SEAL_TYPE_TRANSPORT);  //此时默认是按运力封车
        }
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
        sealCarDto.setRouteLineCode(sourceSealDto.getRouteLineCode());

        return sealCarDto;
    }

    /**
     * 保存封车业务数据
     * @param sealist
     */
    private void saveSealData(List<SealCarDto> sealist){
        logger.info("保存封车业务数据：" + JsonHelper.toJson(sealist));
        if(sealist == null || sealist.isEmpty()){
            return;
        }
        try {
            sealVehiclesService.batchAdd(convert2SealVehicles(sealist));
        }catch (Exception e){
            logger.error("保存封车业务数据异常，封车数据：" + JsonHelper.toJson(sealist), e);
        }
    }

    /**
     * 保存不存在的封车业务数据
     * @param sealist
     */
    private void saveNXSealData(List<SealCarDto> sealist){
        logger.info("保存不存在的封车业务数据：" + JsonHelper.toJson(sealist));
        if(sealist == null || sealist.isEmpty()){
            return;
        }
        try {
            List<SealVehicles> sealVehiclesList = convert2SealVehicles(sealist);
            Set<String> sendCodes = new HashSet<>(sealVehiclesList.size());
            for(SealVehicles vo : sealVehiclesList){
                sendCodes.add(vo.getSealDataCode());
            }
            List<String> sealed = sealVehiclesService.findBySealDataCodes(sendCodes);
            if(sealed == null || sealed.isEmpty()){
                sealVehiclesService.batchAdd(sealVehiclesList);
            }else if(sealed.size() < sealVehiclesList.size()){
                List<SealVehicles> reallyData = new ArrayList<>(sealVehiclesList.size() - sealed.size());
                for(SealVehicles vo : sealVehiclesList){
                    if(!sealed.contains(vo.getSealDataCode())){
                        reallyData.add(vo);
                    }
                }
                sealVehiclesService.batchAdd(reallyData);
            }
        }catch (Exception e){
            logger.error("保存不存在的封车业务数据，封车数据：" + JsonHelper.toJson(sealist), e);
        }
    }

    /**
     * 保存解封车业务数据
     * @param sealist
     */
    private void saveDeSealData(List<SealCarDto> sealist){
        logger.info("保存解封车业务数据：" + JsonHelper.toJson(sealist));
        if(sealist == null || sealist.isEmpty()){
            return;
        }
        try {
            sealVehiclesService.updateDeSealBySealDataCode(convert2SealVehicles(sealist));
        }catch (Exception e){
            logger.error("保存解封车业务数据异常，解封车数据：" + JsonHelper.toJson(sealist), e);
        }
    }

    /**
     * 取消封车更改业务数据状态
     * @param sendCodeList
     */
  private void cancelSealData(List<String> sendCodeList, String operateUserErp) {
    logger.info("取消封车业务数据：" + JsonHelper.toJson(sendCodeList));
    if (sendCodeList == null || sendCodeList.isEmpty()) {
      return;
    }
    try {
        List<SealVehicles> sealVehiclesList = new ArrayList<>();
        for (String sendCode :sendCodeList){
            SealVehicles temp = new SealVehicles();
            temp.setSealDataCode(sendCode);
            temp.setStatus(SealVehicleEnum.CANCEL_SEAL.getCode());
            temp.setUpdateUserErp(operateUserErp);
            temp.setUpdateTime(new Date());

            sealVehiclesList.add(temp);
        }
      sealVehiclesService.updateBySealDataCode(sealVehiclesList);
    } catch (Exception e) {
      logger.error("取消封车业务数据异常，取消封车数据：" + JsonHelper.toJson(sendCodeList), e);
    }
    }

    private List<SealVehicles> convert2SealVehicles(List<SealCarDto> sealist){
        List<SealVehicles> sealVehiclesList = new ArrayList<>();
        for (SealCarDto dto : sealist){
            for (String sendCode : dto.getBatchCodes()){
                SealVehicles temp = new SealVehicles();
                temp.setSealDataCode(sendCode);

                //seal
                temp.setCreateSiteCode(dto.getSealSiteId());
                temp.setCreateSiteName(dto.getSealSiteName());
                temp.setCreateUserErp(dto.getSealUserCode());
                temp.setCreateUserName(dto.getSealUserName());
                temp.setSource(Constants.SEND_DETAIL_SOUCRE_NORMAL);
                temp.setVehicleNumber(dto.getVehicleNumber());
                temp.setVolume(dto.getVolume());
                temp.setWeight(dto.getWeight());
                if(dto.getSealCodes() != null){
                    temp.setSealCodes(dto.getSealCodes().toString());
                }
                temp.setSealCarType(dto.getSealCarType());
                temp.setOperateTime(dto.getSealCarTime());
                temp.setTransWorkItemCode(dto.getItemSimpleCode());
                temp.setTransportCode(dto.getTransportCode());
                temp.setStatus(SealVehicleEnum.SEAL.getCode());

                //deseal
                temp.setUpdateUserErp(dto.getDesealUserCode());
                temp.setUpdateUserName(dto.getDesealUserName());
                temp.setUpdateTime(dto.getDesealCarTime());
                if(dto.getDesealCodes() != null){
                    temp.setDsealCodes(dto.getDesealCodes().toString());
                }
                temp.setReceiveSiteCode(dto.getDesealSiteId());
                temp.setReceiveSiteName(dto.getDesealSiteName());
                temp.setSealCarCode(dto.getSealCarCode());

                sealVehiclesList.add(temp);
            }
        }

        return sealVehiclesList;
    }
}
