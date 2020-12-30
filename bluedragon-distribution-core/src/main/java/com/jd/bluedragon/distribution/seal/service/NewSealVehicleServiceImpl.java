package com.jd.bluedragon.distribution.seal.service;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarPreRequest;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.CarrierQueryWSManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.jmq.domain.SealCarMqDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.cancelSealRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.material.service.SortingMaterialSendService;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicleExecute;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.newseal.domain.SealCarResultDto;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.LogEngine;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicleEnum;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.SystemLogContants;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.etms.vos.dto.*;
import com.jd.etms.vos.ws.VosBusinessWS;
import com.jd.etms.vos.ws.VosQueryWS;
import com.jd.fastjson.JSONObject;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.tfc.dto.TransBookBillQueryDto;
import com.jd.tms.tfc.dto.TransWorkItemDto;
import com.jd.tms.tfc.dto.TransWorkItemWsDto;
import com.jd.tms.tfc.ws.TfcQueryWS;
import com.jd.tms.tfc.ws.TfcSelectWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
	private TfcQueryWS tfcQueryWS;

	@Autowired
	private TfcSelectWS tfcSelectWS;

    @Autowired
    private GoddessService goddessService;

    @Autowired
    private SealVehiclesService sealVehiclesService;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private VosManager vosManager;

    @Autowired
    private LogEngine logEngine;

    @Autowired
    private SendMService sendMService;

    @Autowired
    private SortingMaterialSendService sortingMaterialSendService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private CarrierQueryWSManager carrierQueryWSManager;


    private static final Integer UNSEAL_CAR_IN_RECIVE_AREA = 2;    //带解封的车辆在围栏里(1-是否在始发网点 2-是否在目的网点)

    private static final Integer IN_AREA_FLAG = 2;    //标识车辆不在围栏内(1：在围栏内 2：不在围栏内 3：坐标数据不存在 4：围栏数据不存在 5：其他)

    private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.seal",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public CommonDto<String> seal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars,Map<String, String> emptyBatchCode) throws Exception{
	    long startTime=System.currentTimeMillis();
	    List<SealCarDto> paramList = convertList(sealCars);

        if(log.isDebugEnabled()){
            log.debug("封车参数：{}", JsonHelper.toJson(paramList));
        }

        //去除空批次，并记录去除数据
        SealCarResultDto sealCarResultDto = getEmptyBatchAndSealData(paramList);
        emptyBatchCode.putAll(sealCarResultDto.getDisableSendCode());
        //需要操作封车的数据
        List<SealCarDto> doSealCarDtos =sealCarResultDto.getSealCarDtos();
        //被剔除空批次的封车数据
        List<SealCarDto> removeSealCarDtos =sealCarResultDto.getRemoveCarDtos();

        CommonDto<String> sealCarInfo = null;
        if (CollectionUtils.isEmpty(doSealCarDtos)){
            sealCarInfo = new CommonDto<String>();
            sealCarInfo.setCode(0);
            sealCarInfo.setMessage("封车失败。无有效的封车信息，请重新录入");
            log.warn("封车失败。无有效的封车信息sealCars[{}]sealCarResultDto[{}]", JsonHelper.toJson(sealCars),JsonHelper.toJson(sealCarResultDto));
            return sealCarInfo;
        }

        List<SealVehicles> saveSealDataList=new ArrayList<>();
        String msg = "";
        try {
            sealCarInfo = vosBusinessWS.doSealCar(doSealCarDtos);
            if(sealCarInfo == null) {
                msg = "封车JSF接口返回为空";
                saveSealDataList.addAll(convert2SealVehicles(doSealCarDtos,SealVehicleExecute.FAIL,msg));
            }else if(Constants.RESULT_SUCCESS == sealCarInfo.getCode()){
                msg = MESSAGE_SEAL_SUCCESS;
                //封车成功，发送封车mq消息
                addRedisCache(doSealCarDtos);
                saveSealDataList.addAll(convert2SealVehicles(doSealCarDtos,SealVehicleExecute.SUCCESS,SealVehicleExecute.SUCCESS.getName()));
            }else{
                msg = "["+sealCarInfo.getCode()+":"+sealCarInfo.getMessage()+"]";
                saveSealDataList.addAll(convert2SealVehicles(doSealCarDtos,SealVehicleExecute.FAIL,msg));
            }

            saveSealDataList.addAll(convert2SealVehicles(removeSealCarDtos,SealVehicleExecute.REMOVE_EMPTY_BATCH,SealVehicleExecute.REMOVE_EMPTY_BATCH.getName()));

            saveSealData(saveSealDataList);
        }catch (Exception e){
            this.log.error("封车-error，参数：{}", JsonHelper.toJson(paramList), e);
            msg = "封车异常：["+ e.getMessage() +"]";
            throw e;
        }finally {
//            addSystemLog(paramList, msg);
            long endTime = System.currentTimeMillis();

            for (int i = 0; i < paramList.size(); i++) {
                SealCarDto param = paramList.get(i);

                JSONObject operateRequest=new JSONObject();
                operateRequest.put("sealSiteId",param.getSealSiteId());
                operateRequest.put("sealSiteName",param.getSealSiteName());

                JSONObject response=new JSONObject();
                response.put("param", param);
                response.put("msg", msg);
                response.put("errorSendCode", JsonHelper.toJson(sealCarResultDto.getDisableSendCode()));

                BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                        .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SEAL_SEAL)
                        .operateRequest(operateRequest)
                        .operateResponse(response)
                        .processTime(endTime,startTime)
                        .methodName("NewSealVehicleServiceImpl#seal")
                        .build();

                logEngine.addLog(businessLogProfiler);
                addSystemLog(paramList, msg);
            }


        }
        return sealCarInfo;
	}

  /**
   * 拆解封车入参对象列表，把列表分为空批次封车信息、要操作封车的信息
   *
   * @param sourceSealDtos
   * @param
   */
  private SealCarResultDto getEmptyBatchAndSealData(List<SealCarDto> sourceSealDtos) {
      Map<String, String> emptyBatchCode = Maps.newHashMap();
      List<SealCarDto> keepsourceSealDtos=new ArrayList<>();
      List<SealCarDto> removesourceSealDtos=new ArrayList<>();
      for (SealCarDto sourceSealDto : sourceSealDtos) {
          if (sourceSealDto == null) {
              continue;
          }

          if(CollectionUtils.isNotEmpty(sourceSealDto.getBatchCodes())){
              // 循环验证封车批次号是否有发货记录，如果没有则删除批次
              List<String> keepBatchCodes=new ArrayList<>();
              List<String> removeBatchCodes=new ArrayList<>();
              for (String item : sourceSealDto.getBatchCodes()) {
                  if (checkBatchCodeIsNewSealVehicle(item)) {
                      keepBatchCodes.add(item);
                  }else {
                      log.warn("批次内发货信息。车牌号[{}]批次[{}]",sourceSealDto.getVehicleNumber(),item);
                      emptyBatchCode.put(item, sourceSealDto.getVehicleNumber());
                      removeBatchCodes.add(item);
                  }
              }
              if(CollectionUtils.isNotEmpty(keepBatchCodes)){
                  sourceSealDto.setBatchCodes(keepBatchCodes);
                  keepsourceSealDtos.add(sourceSealDto);
              }
              if(CollectionUtils.isNotEmpty(removeBatchCodes)){
                  SealCarDto removeDto=new SealCarDto();
                  BeanUtils.copyProperties(sourceSealDto,removeDto);
                  removeDto.setBatchCodes(removeBatchCodes);
                  removesourceSealDtos.add(removeDto);
              }

          }else {
              keepsourceSealDtos.add(sourceSealDto);
          }
      }
      return new SealCarResultDto(keepsourceSealDtos,removesourceSealDtos,emptyBatchCode);
  }


    @Override
    @JProfiler(jKey = "com.jd.bluedragon.distribution.seal.service.NewSealVehicleServiceImpl.checkBatchCodeIsSendPreSealVehicle",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean checkBatchCodeIsSendPreSealVehicle(String batchCode){
        String removeEmptyBatchCode=uccPropertyConfiguration.getPreSealVehicleRemoveEmptyBatchCode();
        if(!Constants.STRING_FLG_TRUE.equals(removeEmptyBatchCode)){
            return true;
        }

        return checkBatchCodeIsSend(batchCode);
    }

    @Override
    @JProfiler(jKey = "com.jd.bluedragon.distribution.seal.service.NewSealVehicleServiceImpl.checkBatchCodeIsNewSealVehicle",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean checkBatchCodeIsNewSealVehicle(String batchCode){
        String removeEmptyBatchCode=uccPropertyConfiguration.getRemoveEmptyBatchCode();
        if(!Constants.STRING_FLG_TRUE.equals(removeEmptyBatchCode)){
            return true;
        }
        return checkBatchCodeIsSend(batchCode);
    }
    /**
     * 校验批次号是否存在发货记录
     * @param batchCode
     * @return true 存在发货数据或者物质数据 false 不存在
     */
	private boolean checkBatchCodeIsSend(String batchCode){
        boolean res=true;

	    //批次号不存在sendm记录
	    if(!checkSendIsExist(batchCode)) {

            JdResult<Integer> materialSendRet = sortingMaterialSendService.countMaterialSendRecordByBatchCode(batchCode, null);
            if (materialSendRet.isSucceed() && materialSendRet.getData() == 0) {
                res = false;
            }
        }

        return res;
    }




    /**
     * VOS封车业务同时生成车次任务
     * @param sealCars
     * @return
     */
    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.doSealCarWithVehicleJob",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse doSealCarWithVehicleJob(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars,Map<String, String> emptyBatchCode) {
        List<SealCarDto> paramList = convertList(sealCars);
        if(log.isDebugEnabled()){
            log.debug("VOS封车业务同时生成车次任务参数：{}", JsonHelper.toJson(paramList));
        }

        return doSealCarWithVehicleJobCore(paramList,emptyBatchCode);
    }

    /**
     * 分拣工作台一键封车
     */
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.doSealCarFromDmsWorkBench",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse doSealCarFromDmsWorkBench(List<SealCarDto> sealCarDtoList) {
        log.info("执行分拣工作台一键封车任务参数：{}", JsonHelper.toJson(sealCarDtoList));

        Map<String, String> emptyBatchCode=Maps.newHashMap();
        return doSealCarWithVehicleJobCore(sealCarDtoList,emptyBatchCode);
    }

    /*
    * 执行传摆封车主体
    * */
    private NewSealVehicleResponse doSealCarWithVehicleJobCore(List<SealCarDto> paramList,Map<String, String> emptyBatchCode) {
        long startTime=System.currentTimeMillis();
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        String errorMsg = "";

        //封车成功的数据
        List<SealCarDto> successSealCarList = new ArrayList<>();
        //需要保存的封车记录
        List<SealVehicles> saveSealCarList = new ArrayList<>();

        //记录封车失败次数
        int failCount=0;
        //循环调用运输封车同时生成车次任务的接口
        for (SealCarDto param : paramList) {
            String singleErrorMsg = "";
            String transportCode = param.getTransportCode();

            if(CollectionUtils.isNotEmpty(param.getBatchCodes())){
                // 循环验证封车批次号是否有发货记录，如果没有则删除批次
                List<String> keepBatchCodes=new ArrayList<>();
                List<String> removeBatchCodes=new ArrayList<>();
                for (String item : param.getBatchCodes()) {
                    if (checkBatchCodeIsSendPreSealVehicle(item)) {
                        keepBatchCodes.add(item);
                    }else {
                        log.warn("批次内发货信息。车牌号[{}]批次[{}]",param.getVehicleNumber(),item);
                        emptyBatchCode.put(item, param.getVehicleNumber());
                        removeBatchCodes.add(item);
                    }
                }
                if(CollectionUtils.isNotEmpty(removeBatchCodes)){
                    //把剔除空批次的封车记录加入到待保存
                    SealCarDto temp=new SealCarDto();
                    BeanUtils.copyProperties(param,temp);
                    temp.setBatchCodes(removeBatchCodes);
                    saveSealCarList.addAll(convert2SealVehicles(Arrays.asList(temp),SealVehicleExecute.REMOVE_EMPTY_BATCH,SealVehicleExecute.REMOVE_EMPTY_BATCH.getName()));
                }
                if(CollectionUtils.isNotEmpty(keepBatchCodes)){
                    param.setBatchCodes(keepBatchCodes);
                }else {
                    log.warn("封车批次全部为空批次，不进行封车操作[{}]。",JsonHelper.toJson(param));
                    singleErrorMsg="运力编码封车批次全部没有发货数据：" + transportCode;
                    errorMsg += singleErrorMsg;
                    continue;
                }
            }

            CommonDto<String> sealCarInfo = vosManager.doSealCarWithVehicleJob(param);
            if (sealCarInfo == null) {
                singleErrorMsg = "运力编码封车失败：" + transportCode + ".";
                log.warn("VOS封车业务同时生成车次任务接口返回为空.参数:{}", JSON.toJSONString(param));
                removeRedisCache(param.getBatchCodes());
                failCount++;
                saveSealCarList.addAll(convert2SealVehicles(Arrays.asList(param),SealVehicleExecute.FAIL,singleErrorMsg));
            } else if (Constants.RESULT_SUCCESS == sealCarInfo.getCode()) {
                successSealCarList.add(param);
                saveSealCarList.addAll(convert2SealVehicles(Arrays.asList(param),SealVehicleExecute.SUCCESS,SealVehicleExecute.SUCCESS.getName()));
            } else {
                singleErrorMsg = "运力编码封车失败：" + transportCode + "." + sealCarInfo.getCode() + "-" + sealCarInfo.getMessage() + ".";
                log.warn("VOS封车业务同时生成车次任务失败.参数:{},返回值:{}" , JSON.toJSONString(param) , singleErrorMsg);
                removeRedisCache(param.getBatchCodes());
                failCount++;
                saveSealCarList.addAll(convert2SealVehicles(Arrays.asList(param),SealVehicleExecute.FAIL,singleErrorMsg));
            }
            errorMsg += singleErrorMsg;
        }

        //封车成功的，写数据库，发送封车mq消息
        if (successSealCarList.size() > 0) {
            log.debug("doSealCarWithVehicleJob传摆封车成功！，批次数量：{}" , successSealCarList.size());
            addRedisCache(successSealCarList);
        }

        //记录封车操作数据
        saveSealData(saveSealCarList);

//        addSystemLog(paramList, errorMsg);

        for (int i = 0; i < paramList.size(); i++) {
            SealCarDto sealCarDto = paramList.get(i);

            long endTime = System.currentTimeMillis();

            JSONObject request=new JSONObject();
            request.put("siteCode",sealCarDto.getSealSiteId());
            request.put("siteName",sealCarDto.getSealSiteName());

            JSONObject response=new JSONObject();
            response.put("param",sealCarDto);
            response.put("errmsg",errorMsg);

            BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SEAL_FERRY_SEAL)
                    .methodName("NewSealVehicleServiceImpl#doSealCarWithVehicleJob")
                    .operateRequest(request)
                    .operateResponse(response)
                    .processTime(endTime,startTime)
                    .build();
            logEngine.addLog(businessLogProfiler);
            addSystemLog(paramList, errorMsg);

        }


        if(failCount<=0){
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
        long startTime=new Date().getTime();
        List<SealCarDto> paramList = offlineConvertList(sealCars);
        if(log.isDebugEnabled()){
            log.debug("离线封车参数：{}", JsonHelper.toJson(paramList));
        }

        //去除空批次，并记录去除数据
        SealCarResultDto sealCarResultDto = getEmptyBatchAndSealData(paramList);
        //需要操作封车的数据
        List<SealCarDto> doSealCarDtos =sealCarResultDto.getSealCarDtos();
        //被剔除空批次的封车数据
        List<SealCarDto> removeSealCarDtos =sealCarResultDto.getRemoveCarDtos();

        CommonDto<String> sealCarInfo = null;
        if (CollectionUtils.isEmpty(doSealCarDtos)){
            sealCarInfo = new CommonDto<String>();
            sealCarInfo.setCode(0);
            sealCarInfo.setMessage("离线封车失败。无有效的封车信息，请重新录入");
            log.error("离线封车失败。无有效的封车信息sealCars[{}]sealCarResultDto[{}]", JsonHelper.toJson(sealCars),JsonHelper.toJson(sealCarResultDto));
            return sealCarInfo;
        }

        List<SealVehicles> saveSealDataList=new ArrayList<>();
        String msg = "离线封车失败：";
        try {
            sealCarInfo = vosBusinessWS.doSealCar(doSealCarDtos);
            if(sealCarInfo == null) {
                msg += "封车JSF接口返回为空";
                log.error("封车JSF接口返回为空sealCarInfo[{}]",JsonHelper.toJson(paramList));
                removeBatchCodeRedisCache(paramList);
                saveSealDataList.addAll(convert2SealVehicles(doSealCarDtos,SealVehicleExecute.FAIL,msg));
            }else if(Constants.RESULT_SUCCESS == sealCarInfo.getCode()){
                msg = MESSAGE_OFFLINE_SEAL_SUCCESS;
                //封车成功，发送封车mq消息
                addRedisCache(doSealCarDtos);
                saveNXSealData(doSealCarDtos);
                saveSealDataList.addAll(convert2SealVehicles(doSealCarDtos,SealVehicleExecute.SUCCESS,SealVehicleExecute.SUCCESS.getName()));
            }else{
                msg += "["+sealCarInfo.getCode()+":"+sealCarInfo.getMessage()+"]";
                log.error("调用运输接口失败sealCarInfo[{}]msg[{}]",JsonHelper.toJson(paramList),msg);
                removeBatchCodeRedisCache(paramList);
                saveSealDataList.addAll(convert2SealVehicles(doSealCarDtos,SealVehicleExecute.FAIL,msg));
            }

            saveSealDataList.addAll(convert2SealVehicles(removeSealCarDtos,SealVehicleExecute.REMOVE_EMPTY_BATCH,SealVehicleExecute.REMOVE_EMPTY_BATCH.getName()));

            saveSealData(saveSealDataList);
        }catch (Exception e){
            this.log.error("离线封车-error：{}", JsonHelper.toJson(paramList), e);
            removeBatchCodeRedisCache(paramList);
            msg += "["+ e.getMessage() +"]";
        }finally {

            for (SealCarDto sealCarDto : paramList) {

                long endTime = new Date().getTime();

                JSONObject request=new JSONObject();
                request.put("siteCode",sealCarDto.getSealSiteId());

                JSONObject response=new JSONObject();
                response.put("param", sealCarDto);
                response.put("errmsg", msg);


                BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                        .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SEAL_OFFLINE_SEAL)
                        .methodName("NewSealVehicleServiceImpl#offlineSeal")
                        .operateRequest(request)
                        .operateResponse(response)
                        .processTime(endTime,startTime)
                        .build();

                logEngine.addLog(businessLogProfiler);
            }

            addSystemLog(paramList, msg);
        }
        return sealCarInfo;
    }

    /**
     * 一键传摆封车
     * @param sealCars
     * @return
     */
    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.oneClickFerrySeal",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public NewSealVehicleResponse oneClickFerrySeal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars) {
        if(log.isDebugEnabled()){
            log.debug("一键传摆封车参数：{}", JsonHelper.toJson(sealCars));
        }
        NewSealVehicleResponse newSealVehicleResponse = null;
        String msg = "一键传摆封车失败：";
        try {
            Map<String, String> emptyBatchCode=Maps.newHashMap();
            List<SealCarDto> paramList = offlineConvertList(sealCars);
            newSealVehicleResponse = this.doSealCarWithVehicleJobCore(paramList,emptyBatchCode);

            if(newSealVehicleResponse == null) {
                msg += "传摆封车JSF接口返回为空";
            } else if (NewSealVehicleResponse.CODE_OK.equals(newSealVehicleResponse.getCode())){
                log.debug("一键传摆封车成功！");
                msg = MESSAGE_ONE_CLICK_FERRY_SEAL_SUCCESS;
            } else {
                msg += "["+newSealVehicleResponse.getCode()+":"+newSealVehicleResponse.getMessage()+"]";
            }
        } catch (Exception e){
            this.log.error("一键传摆封车-error参数：{}", JsonHelper.toJson(sealCars), e);
            msg += "["+ e.getMessage() +"]";
        } finally {
            addFerrySealSystemLog(sealCars, msg);
        }
        return newSealVehicleResponse;
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
    if(log.isDebugEnabled()){
        log.debug("取消封车参数：{}" , JsonHelper.toJson(TMS_param));
    }

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
      log.warn("取消封车JSF接口返回为空.参数：{}" , JsonHelper.toJson(TMS_param));
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
        if(log.isDebugEnabled()){
            log.debug("查询待解任务的车辆是否在围栏参数：{}" , JsonHelper.toJson(sealCarCodes));
        }
        List<String> unsealCarOutArea = new ArrayList<String>(sealCarCodes.size());//这里已经控制最大20个
        try {
            CommonDto<List<SealCarInAreaDto>> sealCarInfo = vosQueryWS.isSealCarInArea(sealCarCodes, UNSEAL_CAR_IN_RECIVE_AREA);
            if(log.isDebugEnabled()){
                log.debug("查询待解任务的车辆是否在围栏结果:{}" , JsonHelper.toJson(sealCarInfo));
            }

            //由于一车可有多个封车编码，所以针对车牌号去重
            if(Constants.RESULT_SUCCESS == sealCarInfo.getCode() && sealCarInfo.getData() != null){//接口调用成功且有数据时
                for(SealCarInAreaDto dto : sealCarInfo.getData()){
                    //车辆不在围栏且前面没有标识过该车辆，将车牌号加入结果unsealCarOutArea中
                    if(IN_AREA_FLAG.equals(dto.getInAreaFlag()) && !unsealCarOutArea.contains(dto.getVehicleNumber())){
                        unsealCarOutArea.add(dto.getVehicleNumber());
                    }
                }
            }else{
                log.warn("查询待解任务的车辆是否在围栏失败，参数：{};结果:{}", JsonHelper.toJson(sealCarCodes), JsonHelper.toJson(sealCarInfo));
            }
        }catch (Exception e){
            log.error("查询待解任务的车辆是否在围栏异常：{}", JsonHelper.toJson(sealCarCodes) , e);
            throw e;
        }
        return unsealCarOutArea;
    }

    @Override
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.vos.unseal",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public CommonDto<String> unseal(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars) throws Exception{
        long startTime=new Date().getTime();
        List<SealCarDto> paramList = convertList(sealCars);
        if(log.isDebugEnabled()){
            log.debug("解封车参数：{}",JsonHelper.toJson(paramList));
        }
        CommonDto<String> sealCarInfo = null;
        String msg = "";
        try {
            sealCarInfo = vosBusinessWS.doDesealCar(paramList);
            if(sealCarInfo == null) {
                msg = "解封车JSF接口返回为空";
            }else if(Constants.RESULT_SUCCESS == sealCarInfo.getCode()){
                msg = MESSAGE_UNSEAL_SUCCESS;
                saveDeSealData(paramList);
            }else{
                msg = "["+sealCarInfo.getCode()+":"+sealCarInfo.getMessage()+"]";
            }
        }catch (Exception e){
            this.log.error("封车-error参数：{}",JsonHelper.toJson(paramList), e);
            msg = "封车异常：["+ e.getMessage() +"]";
            throw e;
        }finally {
            long endTime = new Date().getTime();
            for (SealCarDto sealCarDto : paramList) {
                JSONObject operateRequest=new JSONObject();
                operateRequest.put("siteCode",sealCarDto.getSealSiteId());
                operateRequest.put("siteName",sealCarDto.getSealSiteName());

                JSONObject response=new JSONObject();
                response.put("msg",msg);

                BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                        .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.DE_SEAL_DE_SEAL)
                        .operateRequest(operateRequest)
                        .operateResponse(response)
                        .processTime(endTime,startTime)
                        .methodName("NewSealVehicleServiceImpl#unseal")
                        .build();

                logEngine.addLog(businessLogProfiler);
            }

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
	@JProfiler(jKey = "Bluedragon_dms_center.web.method.carrierQuery.getTransportResourceByTransCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public com.jd.tms.basic.dto.CommonDto<TransportResourceDto> getTransportResourceByTransCode(String batchCode) {
		com.jd.tms.basic.dto.CommonDto<TransportResourceDto> dto = carrierQueryWSManager.getTransportResourceByTransCode(batchCode);
		return dto;
	}

    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.tfcQueryWS.queryTransWorkItemBySimpleCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public com.jd.tms.tfc.dto.CommonDto<TransWorkItemDto> queryTransWorkItemBySimpleCode(String simpleCode) throws Exception {
        return tfcQueryWS.queryTransWorkItemBySimpleCode(simpleCode);
    }

    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.tfcSelectWS.queryVehicleNumberOrItemCodeByParam", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public com.jd.tms.tfc.dto.CommonDto<TransWorkItemWsDto> getVehicleNumberOrItemCodeByParam(TransWorkItemWsDto transWorkItemWsDto) throws Exception {
        return tfcSelectWS.getVehicleNumberOrItemCodeByParam(transWorkItemWsDto);
    }

    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.tfcSelectWS.checkTransportCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public com.jd.tms.tfc.dto.CommonDto<String> checkTransportCode(String simpleCode, String transportCode) throws Exception {
        return tfcSelectWS.checkTransportCode(simpleCode, transportCode);
    }

    @Override
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.tfcSelectWS.getTransBookBill", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public com.jd.tms.tfc.dto.CommonDto<com.jd.tms.tfc.dto.PageDto<com.jd.tms.tfc.dto.TransBookBillResultDto>> getTransBookBill(com.jd.tms.tfc.dto.TransBookBillQueryDto transBookBillQueryDto, com.jd.tms.tfc.dto.PageDto<TransBookBillQueryDto> pageDto) throws Exception {
        return tfcSelectWS.getTransBookBill(transBookBillQueryDto, pageDto);
    }

    /**
     * 校验批次的体积是否超标
     * @see 'https://cf.jd.com/pages/viewpage.action?pageId=246546459'
     * @param sealCarDto
     * @return
     */
    @JProfiler(jKey = "Bluedragon_dms_center.web.method.vosBusinessWS.verifySealVehicleVolume", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public CommonDto<String> verifySealVehicleVolume(SealCarDto sealCarDto){
        CommonDto<String> commonDto = vosBusinessWS.checkSealCarData(sealCarDto);
        return commonDto;
    }

    /**
     * 校验车牌号能否生成车次任务
     * @param transportCode 运力编码
     * @param vehicleNumber 车牌号
     * @return
     */
    @Deprecated
    public CommonDto<String> verifyVehicleJobByVehicleNumber(String transportCode,String vehicleNumber){
        VerifyVehicleJobDto dto = new VerifyVehicleJobDto();
        dto.setTransportCode(transportCode);
        dto.setVehicleNumber(vehicleNumber);
        return vosManager.verifyVehicleJobByVehicleNumber(dto);
    }

    /**
     * 校验车牌号能否生成车次任务（新）
     * @param sealCarPreRequest
     * @return
     */
    public CommonDto<String> newVerifyVehicleJobByVehicleNumber(SealCarPreRequest sealCarPreRequest){
        VerifyVehicleJobDto dto = new VerifyVehicleJobDto();
        dto.setTransportCode(sealCarPreRequest.getTransportCode());
        dto.setVehicleNumber(sealCarPreRequest.getVehicleNumber());
        dto.setSealUserCode(sealCarPreRequest.getOperateErp());
        return vosManager.verifyVehicleJobByVehicleNumber(dto);
    }

    public boolean checkSendIsExist(String sendCode) {
        return sendDetailService.checkSendIsExist(sendCode);
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
            log.debug("Redis取封车批次号:{}的封车时间，结果：{}" ,sendCode, sealCarTime);
            if (StringUtils.isNotBlank(sealCarTime) && Long.valueOf(sealCarTime) > 0) {
                return Long.valueOf(sealCarTime);
            }
        } catch (Exception e) {
            log.error("redis取封车批次号失败：{}",sendCode, e);
        }
        return null;
    }


    /**
     * 查询全部的未封车批次号
     */
    @Override
    public List<String> getUnSealSendCodeList(Integer createSiteCode, Integer receiveSiteCode, Integer hourRange) {

        Date date = DateHelper.newTimeRangeHoursAgo(new Date(), hourRange);
        Set<String> sendCodeSet = new HashSet<>();
        //sendM获取所有批次信息
        List<SendM> sendMList = sendMService.findAllSendCodesWithStartTime(createSiteCode, receiveSiteCode, date);
        if (sendMList != null && ! sendMList.isEmpty()) {
            for(SendM sendM : sendMList) {
                if(!checkBatchCodeIsSendPreSealVehicle(sendM.getSendCode())){
                    continue;
                }

                //封车批次缓存中找出未封车的批次数据
                if(!this.checkSendCodeIsSealed(sendM.getSendCode())) {
                    sendCodeSet.add(sendM.getSendCode());
                }
            }
        } else {
            return null;
        }

        List<String> result = new ArrayList<>(sendCodeSet);
        //利用封车记录表再次筛选sendCode
        List<String> sealedSendCodeList = sealVehiclesService.findBySealDataCodes(sendCodeSet);
        if (sealedSendCodeList != null && ! sealedSendCodeList.isEmpty()) {
            result.removeAll(sealedSendCodeList);
        }
        return result;
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
                    redisManager.setex(Constants.CACHE_KEY_PRE_SEAL_SENDCODE + sendCode, Constants.TIME_SECONDS_FIFTEEN_DAY, String.valueOf(dto.getSealCarTime().getTime()));
                    log.debug("已封车批次号存入缓存成功:{}" , sendCode);
                } catch (Throwable e) {
                    log.error("已封车批次号存入缓存失败:{}",sendCode,e);
                }
            }
        }
    }

    public void removeBatchCodeRedisCache(List<SealCarDto> paramList){
        if(CollectionUtils.isEmpty(paramList)){
            return;
        }
        for (SealCarDto dto:paramList){
            removeRedisCache(dto.getBatchCodes());
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
        log.debug("已封车批次号刪除缓存成功:{}", sendCode);
      } catch (Throwable e) {
        log.error("已封车批次号刪除缓存失败:{}",sendCode,e);
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
     * 记录传摆封车操作日志
     * @param sealCars
     * @param remark
     */
    private void addFerrySealSystemLog(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCars, String remark) {
        long startTime=new Date().getTime();
        if(sealCars == null || sealCars.isEmpty()){
            return;
        }
        /** 写入日志时间处理：cassandra主键包含时间字段，为避免写入太快前面的数据被覆盖，做此处理 */
        long date = System.currentTimeMillis();
        long correctValue = 0L;
        for(com.jd.bluedragon.distribution.wss.dto.SealCarDto dto : sealCars){
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
            long endTime=new Date().getTime();

            JSONObject request=new JSONObject();
            request.put("siteCode",dto.getSealSiteId());
            request.put("siteName",dto.getSealSiteName());

            JSONObject response=new JSONObject();
            response.put("param",dto);
            response.put("errmsg",remark);

            BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.ONE_CLICK_SEAL_FERRY_SEAL)
                    .methodName("NewSealVehicleServiceImpl#oneClickFerrySeal")
                    .operateRequest(request)
                    .operateResponse(response)
                    .processTime(endTime,startTime)
                    .build();
            logEngine.addLog(businessLogProfiler);

            goddessService.save(goddess);
            correctValue++;
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
        sealCarDto.setPalletCount(sourceSealDto.getPalletCount());
        return sealCarDto;
    }


    /**
     * 保存封车操作数据，增加操作结果
     * @param sealist
     */
    private void saveSealData(List<SealVehicles> sealist){
        if(log.isDebugEnabled()){
            log.debug("保存封车业务数据：{}" , JsonHelper.toJson(sealist));
        }
        if(sealist == null || sealist.isEmpty()){
            return;
        }
        try {
            sealVehiclesService.batchAdd(sealist);
        }catch (Exception e){
            log.error("保存封车业务数据异常，封车数据：{}" , JsonHelper.toJson(sealist), e);
        }
    }

    /**
     * 保存不存在的封车业务数据
     * @param sealist
     */
    private void saveNXSealData(List<SealCarDto> sealist){
        if(log.isDebugEnabled()){
            log.debug("保存不存在的封车业务数据：{}" , JsonHelper.toJson(sealist));
        }
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
            log.error("保存不存在的封车业务数据，封车数据：{}" , JsonHelper.toJson(sealist), e);
        }
    }

    /**
     * 保存解封车业务数据
     * @param sealist
     */
    private void saveDeSealData(List<SealCarDto> sealist){
        if(log.isDebugEnabled()){
            log.debug("保存解封车业务数据：{}", JsonHelper.toJson(sealist));
        }
        if(sealist == null || sealist.isEmpty()){
            return;
        }
        try {
            sealVehiclesService.updateDeSealBySealDataCode(convert2SealVehicles(sealist));
        }catch (Exception e){
            log.error("保存解封车业务数据异常，解封车数据：{}" , JsonHelper.toJson(sealist), e);
        }
    }

    /**
     * 取消封车更改业务数据状态
     * @param sendCodeList
     */
  private void cancelSealData(List<String> sendCodeList, String operateUserErp) {
      if(log.isDebugEnabled()){
          log.debug("取消封车业务数据：{}" , JsonHelper.toJson(sendCodeList));
      }
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
      log.error("取消封车业务数据异常，取消封车数据：{}" , JsonHelper.toJson(sendCodeList), e);
    }
    }

    private List<SealVehicles> convert2SealVehicles(List<SealCarDto> sealist){
        List<SealVehicles> sealVehiclesList = new ArrayList<>();
        for (SealCarDto dto : sealist){
            if(dto.getBatchCodes() == null){
                continue;
            }
            for (String sendCode : dto.getBatchCodes()){
                SealVehicles sealVehicles = sealCarDtoToSealVehicles(dto);
                sealVehicles.setSealDataCode(sendCode);

                sealVehiclesList.add(sealVehicles);
            }
        }

        return sealVehiclesList;
    }

    private List<SealVehicles> convert2SealVehicles(List<SealCarDto> dts,SealVehicleExecute sealVehicleExecute,String msg){
        List<SealVehicles> res = new ArrayList<>();
        for (SealCarDto dt : dts) {
            for (String sendCode : dt.getBatchCodes()){
                SealVehicles sealVehicles = sealCarDtoToSealVehicles(dt);
                sealVehicles.setSealDataCode(sendCode);
                sealVehicles.setExecuteType(sealVehicleExecute.getCode());
                sealVehicles.setExecuteMessage(msg);

                res.add(sealVehicles);
            }
        }

        return res;
    }

    private SealVehicles sealCarDtoToSealVehicles(SealCarDto dto) {
        SealVehicles temp = new SealVehicles();
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

        return temp;
    }
}
