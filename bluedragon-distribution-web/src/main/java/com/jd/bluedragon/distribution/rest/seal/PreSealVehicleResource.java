package com.jd.bluedragon.distribution.rest.seal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.newseal.domain.*;
import com.jd.bluedragon.distribution.newseal.entity.PreSealBatch;
import com.jd.bluedragon.distribution.newseal.service.PreSealBatchService;
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.wss.dto.SealCarDto;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author shipeilin
 * @Description: 预封车rest服务
 * @date 2019年03月12日 18时:17分
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class PreSealVehicleResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PreSealVehicleService preSealVehicleService;

    @Autowired
    private NewSealVehicleService newsealVehicleService;
    @Autowired
    private PreSealBatchService preSealBatchService;

    private static final String SYSTEM_CODE = "DMS";

    /**
     * 普通预封车接口
     */
    @POST
    @Path("/new/preSeal")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1010)
    @JProfiler(jKey = "DMS.WEB.PreSealVehicleResource.preSeal", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse<Boolean> preSeal(NewSealVehicleRequest request) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.PreSealVehicleResource.preSeal", Constants.UMP_APP_NAME_DMSWEB, false, true);
        NewSealVehicleResponse<Boolean> preSealResponse = new NewSealVehicleResponse<>();
        preSealResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
        preSealResponse.setMessage("普通预封车已下线，重新登录PDA更新版本，新版本该界面有【传摆预封车】字样！");

//        try {
//            preSealResponse = getNewSealVehicleResponse(request, PreSealVehicleSourceEnum.COMMON_PRE_SEAL);
//        } catch (Exception e) {
//            Profiler.functionError(info);
//            preSealResponse.setCode(NewSealVehicleResponse.CODE_SERVICE_ERROR);
//            preSealResponse.setMessage(NewSealVehicleResponse.MESSAGE_SERVICE_ERROR);
//            log.error("PreSealVehicleResource.preSeal-error:{}", JsonHelper.toJson(request), e);
//        } finally {
//            Profiler.registerInfoEnd(info);
//        }
        Profiler.registerInfoEnd(info);
        return preSealResponse;
    }

    /**
     * 传摆预封车服务
     */
    @POST
    @Path("/new/preSealFerry")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1010, operateType = 10101)
    @JProfiler(jKey = "DMS.WEB.PreSealVehicleResource.preSealFerry", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse<Boolean> preSealFerry(NewSealVehicleRequest request) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.PreSealVehicleResource.preSealFerry", Constants.UMP_APP_NAME_DMSWEB, false, true);
        NewSealVehicleResponse<Boolean> preSealResponse = new NewSealVehicleResponse<>(NewSealVehicleResponse.CODE_OK, NewSealVehicleResponse.MESSAGE_OK);

        try {
            preSealResponse = getNewSealVehicleResponse(request, PreSealVehicleSourceEnum.FERRY_PRE_SEAL);
        } catch (Exception e) {
            Profiler.functionError(info);
            preSealResponse.setCode(NewSealVehicleResponse.CODE_SERVICE_ERROR);
            preSealResponse.setMessage(NewSealVehicleResponse.MESSAGE_SERVICE_ERROR);
            log.error("PreSealVehicleResource.preSealFerry-error:{}", JsonHelper.toJson(request), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return preSealResponse;
    }

    /**
     * 预封车服务
     * source : 1 普通预封车 2 传摆预封车
     * （1）同一个目的地只能操作一个运力编码的预封车，实际封车后可进行下一个波次的运力编码的预封车
     * （2）同一个运力编码，不同车牌号，可以进行多次预封车
     */
    private NewSealVehicleResponse<Boolean> getNewSealVehicleResponse(NewSealVehicleRequest request, PreSealVehicleSourceEnum preSealVehicleSourceEnum) {
        NewSealVehicleResponse<Boolean> preSealResponse = new NewSealVehicleResponse<>(NewSealVehicleResponse.CODE_OK, NewSealVehicleResponse.MESSAGE_OK);
        if (request == null || request.getData() == null || request.getData().size() != 1) {
            log.warn("PreSealVehicleResource getNewSealVehicleResponse --> 传入参数非法:{}", JsonHelper.toJson(request));
            preSealResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
            preSealResponse.setMessage(NewSealVehicleResponse.MESSAGE_PARAM_ERROR);
            return preSealResponse;
        }

        /** 预封车数据是逐条提交的 */
        SealCarDto sealCarDto = request.getData().get(0);
        if (StringUtils.isBlank(sealCarDto.getTransportCode()) || StringUtils.isBlank(sealCarDto.getVehicleNumber())) {
            log.warn("PreSealVehicleResource getNewSealVehicleResponse --> 运力编码或车牌为空：{}", JsonHelper.toJson(request));
            preSealResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
            preSealResponse.setMessage(NewSealVehicleResponse.TIPS_PRESEAL_PARAM_ERROR);
            return preSealResponse;
        }

        /** 查询运力信息 */
        TransportResourceDto vtrd = getTransport(sealCarDto.getTransportCode(), preSealResponse);
        if (vtrd == null || !NewSealVehicleResponse.CODE_OK.equals(preSealResponse.getCode())) {
            return preSealResponse;
        }

        List<PreSealVehicle> exists = preSealVehicleService.findByCreateAndReceive(sealCarDto.getSealSiteId(), vtrd.getEndNodeId());
        PreSealVehicle preSealVehicle = null;
        Long existId = null;
        String existUuid = null;
        boolean needUpdate = false;
        if (exists == null || exists.isEmpty()) {
            //该目的地没有预封车数据时，属于新增
        	preSealVehicle = convertRequst(sealCarDto, vtrd, false, preSealVehicleSourceEnum);
            preSealVehicleService.insert(preSealVehicle);
            preSealResponse.setData(true);
        } else {
            for (PreSealVehicle exist : exists) {
                if (!sealCarDto.getTransportCode().equals(exist.getTransportCode())) {
                    //该目的已有预封车数据时，但是运力编码不一致时提示是否更新
                    preSealResponse.setCode(NewSealVehicleResponse.CODE_PRESEAL_EXIST_ERROR);
                    preSealResponse.setMessage(NewSealVehicleResponse.TIPS_PRESEAL_EXIST_ERROR);
                    break;
                } else if (sealCarDto.getVehicleNumber().equals(exist.getVehicleNumber())) {
                    //该目的已有预封车数据时，但是运力编码一致，车牌也一致时，属于更新封签号
                    existId = exist.getId();
                    existUuid = exist.getPreSealUuid();
                    break;
                }

            }
            if (NewSealVehicleResponse.CODE_OK.equals(preSealResponse.getCode())) {
                if (existId != null) {
                    //该目的已有预封车数据时，但是运力编码一致，车牌也一致时，属于更新封签号,根据ID更新数据
                    preSealVehicle = convertRequst(sealCarDto, vtrd, true, preSealVehicleSourceEnum);
                    preSealVehicle.setId(existId);
                    preSealVehicle.setPreSealUuid(existUuid);
                    preSealVehicleService.updateById(preSealVehicle);
                    needUpdate = true;
                    preSealResponse.setData(true);
                    preSealResponse.setMessage("预封车封签号更新成功!");
                } else {
                	preSealVehicle = convertRequst(sealCarDto, vtrd, false, preSealVehicleSourceEnum);
                    //该目的已有预封车数据时，但是运力编码一致，车牌不一致时，属于新增
                    preSealVehicleService.insert(preSealVehicle);
                    preSealResponse.setData(true);
                }
            }
        }
        //记录选中的批次信息
        if (NewSealVehicleResponse.CODE_OK.equals(preSealResponse.getCode())) {
        	List<PreSealBatch> preSealBatchs = convertPreSealBatchs(preSealVehicle,sealCarDto);
        	if(CollectionUtils.isNotEmpty(preSealBatchs)) {
        		/**
        		 * 更新旧数据时，删除原有的批次信息
        		 */
        		if(needUpdate) {
                    preSealBatchService.batchLogicalDeleteByUuid(existUuid);
        		}
        		this.preSealBatchService.batchInsert(preSealBatchs);
        	}
        }
        return preSealResponse;
    }
    /**
     * 转换预封车选择批次列表
     * @param preSealVehicle
     * @param request
     * @return
     */
    private List<PreSealBatch> convertPreSealBatchs(PreSealVehicle preSealVehicle, SealCarDto sealCarDto) {
    	if(preSealVehicle != null && CollectionUtils.isNotEmpty(sealCarDto.getSelectedSendCodes())) {
    		List<PreSealBatch> preSealBatchs = new ArrayList<PreSealBatch>();
    		for(String sendCode : sealCarDto.getSelectedSendCodes()) {
    			PreSealBatch preSealBatch = new PreSealBatch();
    			preSealBatch.setBatchCode(sendCode);
    			preSealBatch.setCreateTime(new Date());
    			preSealBatch.setPreSealUuid(preSealVehicle.getPreSealUuid());
    			preSealBatchs.add(preSealBatch);
    		}
    		return preSealBatchs;
    	}
		return null;
	}

	/**
     * 普通预封车更新服务
     */
    @POST
    @Path("/new/updatePreSeal")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1010)
    @JProfiler(jKey = "DMS.WEB.PreSealVehicleResource.updatePreSeal", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse<Boolean> updatePreSeal(NewSealVehicleRequest request) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.PreSealVehicleResource.updatePreSeal", Constants.UMP_APP_NAME_DMSWEB,false, true);
        NewSealVehicleResponse<Boolean> preSealResponse = new NewSealVehicleResponse<>(NewSealVehicleResponse.CODE_OK, NewSealVehicleResponse.MESSAGE_OK);
        try {
            preSealResponse = getUpdatePreSealResponse(request, PreSealVehicleSourceEnum.COMMON_PRE_SEAL);
        } catch (Exception e) {
            Profiler.functionError(info);
            preSealResponse.setCode(NewSealVehicleResponse.CODE_SERVICE_ERROR);
            preSealResponse.setMessage(NewSealVehicleResponse.MESSAGE_SERVICE_ERROR);
            log.error("PreSealVehicleResource.updatePreSeal-error:{}", JsonHelper.toJson(request), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }

        return preSealResponse;
    }

    /**
     * 传摆预封车更新服务
     */
    @POST
    @Path("/new/updatePreSealFerry")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1010, operateType = 10102)
    @JProfiler(jKey = "DMS.WEB.PreSealVehicleResource.updatePreSealFerry", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewSealVehicleResponse<Boolean> updatePreSealFerry(NewSealVehicleRequest request) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.PreSealVehicleResource.updatePreSealFerry", Constants.UMP_APP_NAME_DMSWEB,false, true);
        NewSealVehicleResponse<Boolean> preSealResponse = new NewSealVehicleResponse<>(NewSealVehicleResponse.CODE_OK, NewSealVehicleResponse.MESSAGE_OK);
        try {
            preSealResponse = getUpdatePreSealResponse(request, PreSealVehicleSourceEnum.FERRY_PRE_SEAL);
        } catch (Exception e) {
            Profiler.functionError(info);
            preSealResponse.setCode(NewSealVehicleResponse.CODE_SERVICE_ERROR);
            preSealResponse.setMessage(NewSealVehicleResponse.MESSAGE_SERVICE_ERROR);
            log.error("PreSealVehicleResource.updatePreSealFerry-error:{}", JsonHelper.toJson(request), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }

        return preSealResponse;
    }

    /**
     * 预封车更新服务
     *
     */
    private NewSealVehicleResponse<Boolean> getUpdatePreSealResponse(NewSealVehicleRequest request, PreSealVehicleSourceEnum preSealVehicleSourceEnum) {
        NewSealVehicleResponse<Boolean> preSealResponse = new NewSealVehicleResponse<>(NewSealVehicleResponse.CODE_OK, NewSealVehicleResponse.MESSAGE_OK);

        if (request == null || request.getData() == null || request.getData().size() != 1  || request.getData().get(0).getSealSiteId() == null) {
            log.warn("PreSealVehicleResource getUpdatePreSealResponse --> 传入参数非法:{}", JsonHelper.toJson(request));
            preSealResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
            preSealResponse.setMessage(NewSealVehicleResponse.MESSAGE_PARAM_ERROR);
            return preSealResponse;
        }

        /** 预封车数据是逐条提交的 */
        SealCarDto sealCarDto = request.getData().get(0);
        if(StringUtils.isBlank(sealCarDto.getTransportCode()) || StringUtils.isBlank(sealCarDto.getVehicleNumber())){
            log.warn("PreSealVehicleResource getUpdatePreSealResponse --> 运力编码或车牌为空：{}", JsonHelper.toJson(request));
            preSealResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
            preSealResponse.setMessage(NewSealVehicleResponse.TIPS_PRESEAL_PARAM_ERROR);
            return preSealResponse;
        }
        /** 查询运力信息 */
        TransportResourceDto vtrd = getTransport(sealCarDto.getTransportCode(), preSealResponse);
        if(!NewSealVehicleResponse.CODE_OK.equals(preSealResponse.getCode())){
            return preSealResponse;
        }

        preSealVehicleService.cancelPreSealBeforeInsert(convertRequst(sealCarDto, vtrd, false, preSealVehicleSourceEnum));
        preSealResponse.setData(true);

        return preSealResponse;
    }

    /**
     * 根据运力编码获取车辆信息（车牌、重量体积）
     */
    @GET
    @Path("/preSeal/vehicleInfo/{transportCode}")
    @JProfiler(jKey = "DMSWEB.PreSealVehicleResource.getVehicleNumBySimpleCode", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP})
    public NewSealVehicleResponse<PreSealVehicleMeasureInfo> getVehicleNumBySimpleCode(@PathParam("transportCode") String transportCode) {
        NewSealVehicleResponse<PreSealVehicleMeasureInfo> newSealVehicleResponse = new NewSealVehicleResponse<>(NewSealVehicleResponse.CODE_OK, NewSealVehicleResponse.MESSAGE_OK);
        try {
            PreSealVehicleMeasureInfo preSealVehicleMeasureInfo = new PreSealVehicleMeasureInfo();
            PreSealVehicle preSealVehicle = preSealVehicleService.getPreSealVehicleInfo(transportCode);

            if (preSealVehicle == null) {
                newSealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                newSealVehicleResponse.setMessage("此运力编码没有预封车信息，请返回录入！");
                this.log.warn("根据运力编码获取预封车信息为空：运力编码->{}", transportCode);
                return newSealVehicleResponse;
            }
            preSealVehicleMeasureInfo.setTransportCode(transportCode);
            preSealVehicleMeasureInfo.setTransWay(preSealVehicle.getTransWay());
            preSealVehicleMeasureInfo.setTransWayName(preSealVehicle.getTransWayName());
            List<VehicleMeasureInfo> list = preSealVehicleService.getVehicleMeasureInfoList(transportCode);
            preSealVehicleMeasureInfo.setVehicleMeasureInfoList(list);
            newSealVehicleResponse.setData(preSealVehicleMeasureInfo);
        } catch (Exception e) {
            newSealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            newSealVehicleResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            this.log.error("根据运力编码获取任务信息失败：运力编码->{}", transportCode, e);
        }
        return newSealVehicleResponse;
    }

    /**
     * 更新重量体积
     */
    @POST
    @Path("/preSeal/vehicleMeasureInfo/save")
    @JProfiler(jKey = "DMSWEB.PreSealVehicleResource.updatePreSealVehicleMeasureInfo", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP})
    public NewSealVehicleResponse<Boolean> updatePreSealVehicleMeasureInfo(PreSealMeasureInfoRequest request) {
        NewSealVehicleResponse<Boolean> preSealResponse = new NewSealVehicleResponse<>(NewSealVehicleResponse.CODE_OK, NewSealVehicleResponse.MESSAGE_OK);

        if (request.getTransportCode() == null || request.getVehicleNumber() == null) {
            preSealResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
            preSealResponse.setMessage("运力编码和车牌号不能为空！");
            return preSealResponse;
        }

        if (request.getVolume() == null || ! NumberHelper.gt0(request.getVolume())) {
            preSealResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
            preSealResponse.setMessage("体积不能为空且必须大于0！");
            return preSealResponse;
        }
        try {
            PreSealVehicle preSealVehicle = new PreSealVehicle();
            preSealVehicle.setTransportCode(request.getTransportCode());
            preSealVehicle.setVehicleNumber(request.getVehicleNumber());
            preSealVehicle.setVolume(request.getVolume());
            if (request.getWeight() != null && NumberHelper.gt0(request.getWeight())) {
                preSealVehicle.setWeight(request.getWeight());
            }
            preSealVehicleService.updatePreSealVehicleMeasureInfo(preSealVehicle);

        } catch (Exception e) {
            preSealResponse.setCode(NewSealVehicleResponse.CODE_SERVICE_ERROR);
            preSealResponse.setMessage(NewSealVehicleResponse.MESSAGE_SERVICE_ERROR);
            log.error("更新预封车重量体积信息失败！，参数：{}", JsonHelper.toJson(request), e);
        }

        return preSealResponse;
    }

    /**
     * 根据运力编码获取运力信息
     * @param transportCode
     * @param response
     * @return
     */
    private TransportResourceDto getTransport(String transportCode, NewSealVehicleResponse<Boolean> response){
        TransportResourceDto vtrd = null;
        try {
            CommonDto<TransportResourceDto> transDto = newsealVehicleService.getTransportResourceByTransCode(transportCode);
            if (transDto == null) {    //JSF接口返回空
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("查询运力信息结果为空:" + transportCode);
                return vtrd;
            }
            if (Constants.RESULT_SUCCESS == transDto.getCode()) { //JSF接口调用成功
                vtrd = transDto.getData();
                if (vtrd == null) {
                    response.setCode(JdResponse.CODE_SERVICE_ERROR);
                    response.setMessage("查询运力信息结果为空:" + transportCode);
                }else if(vtrd.getEndNodeId() == null){
                    response.setCode(JdResponse.CODE_SERVICE_ERROR);
                    response.setMessage("运力目的地为空:" + transportCode);
                }
            } else if (Constants.RESULT_WARN == transDto.getCode()) {    //查询运力信息接口返回警告，给出前台提示
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage(transDto.getMessage());
            } else { //服务出错或者出异常，打日志
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("查询运力信息出错！");
                log.warn("预封车查询运力信息出错,运力编码:{},出错原因:{}",transportCode, transDto.getMessage());
            }
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            log.error("通过运力编码获取基础资料信息异常：{}", transportCode, e);
        }
        return vtrd;
    }


    /**
     * 取消预封车
     */
    @POST
    @Path("/new/preSeal/cancel")
    @JProfiler(jKey = "DMSWEB.PreSealVehicleResource.cancelPreSeal", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP})
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1010, operateType = 10103)
    public NewSealVehicleResponse cancelPreSeal(CancelPreSealVehicleRequest request) {
        NewSealVehicleResponse<Boolean> newSealVehicleResponse = new NewSealVehicleResponse<>(NewSealVehicleResponse.CODE_OK, NewSealVehicleResponse.MESSAGE_OK);
        log.info("取消预封车任务失败！，参数：{}", JsonHelper.toJson(request));
        if (StringUtils.isEmpty(request.getVehicleNumber())) {
            newSealVehicleResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
            newSealVehicleResponse.setMessage("车牌号不能为空！");
            return newSealVehicleResponse;
        }

        try {
            List<PreSealVehicle> list = preSealVehicleService.queryBySiteCodeAndVehicleNumber(request.getSiteCode(), request.getVehicleNumber());

            if (list == null || list.isEmpty()) {
                newSealVehicleResponse.setCode(NewSealVehicleResponse.CODE_SERVICE_ERROR);
                newSealVehicleResponse.setMessage("该车牌在本场地没有预封车信息，无需取消！");
                return newSealVehicleResponse;
            }

            for (PreSealVehicle preSealVehicle : list) {
                preSealVehicle.setStatus(SealVehicleEnum.CANCEL_PRE_SEAL.getCode());
                preSealVehicle.setUpdateUserErp(request.getOperateUserErp());
                preSealVehicle.setUpdateUserName(request.getOperateUserName());
                preSealVehicle.setUpdateTime(new Date());
                //更新成功并且是传摆预封车，才需要调用运输
                if (preSealVehicleService.updateById(preSealVehicle) && PreSealVehicleSourceEnum.FERRY_PRE_SEAL.getCode() == preSealVehicle.getPreSealSource()) {
                    preSealVehicleService.notifyVosPreSealJob(preSealVehicle, PreSealVehicleService.CANCEL_FLAG);
                }
            }
        } catch (Exception e) {
            newSealVehicleResponse.setCode(NewSealVehicleResponse.CODE_SERVICE_ERROR);
            newSealVehicleResponse.setMessage("取消预封车任务失败，请稍后重试！");
            log.error("取消预封车任务失败！，参数：{}", JsonHelper.toJson(request), e);
        }
        return newSealVehicleResponse;
    }
    /**
     * 预封车VO转换
     * @param sealCarDto
     * @param vtrd
     * @param isUpdate
     * @return
     */
    private PreSealVehicle convertRequst(SealCarDto sealCarDto, TransportResourceDto vtrd, boolean isUpdate, PreSealVehicleSourceEnum preSealVehicleSourceEnum){
        PreSealVehicle preSealVehicle = new PreSealVehicle();
        //存储预封车来源
        preSealVehicle.setPreSealSource(preSealVehicleSourceEnum.getCode());
        //存储运输类型
        preSealVehicle.setTransWay(sealCarDto.getTransWay());
        //存储运输类型名称
        preSealVehicle.setTransWayName(sealCarDto.getTransWayName());

        Long now = System.currentTimeMillis();

        preSealVehicle.setTransportCode(sealCarDto.getTransportCode());
        preSealVehicle.setVehicleNumber(sealCarDto.getVehicleNumber());

        List<String> sealCodes = sealCarDto.getSealCodes();
        if(sealCodes != null && !sealCodes.isEmpty()){
            StringBuilder sealCodeStr = new StringBuilder();
            for(int i = 0; i < sealCodes.size(); i++){
                sealCodeStr.append(sealCodes.get(i));
                if(i < sealCodes.size() - 1){
                    sealCodeStr.append(Constants.SEPARATOR_COMMA);
                }
            }
            preSealVehicle.setSealCodes(sealCodeStr.toString());
        }else {
            preSealVehicle.setSealCodes("");
        }
        preSealVehicle.setSendCarTime(vtrd.getSendCarTimeStr());
        preSealVehicle.setStatus(SealVehicleEnum.PRE_SEAL.getCode());
        if(isUpdate){
            preSealVehicle.setUpdateUserErp(sealCarDto.getSealUserCode());
            preSealVehicle.setUpdateUserName(sealCarDto.getSealUserName());
            preSealVehicle.setUpdateTime(new Date(now));
        }else {
            preSealVehicle.setPreSealUuid(sealCarDto.getTransportCode() + "-" + now);
            preSealVehicle.setCreateSiteCode(sealCarDto.getSealSiteId());
            preSealVehicle.setCreateSiteName(sealCarDto.getSealSiteName());
            preSealVehicle.setReceiveSiteCode(vtrd.getEndNodeId());
            preSealVehicle.setReceiveSiteName(vtrd.getEndNodeName());
            preSealVehicle.setCreateUserErp(sealCarDto.getSealUserCode());
            preSealVehicle.setCreateUserName(sealCarDto.getSealUserName());
            preSealVehicle.setCreateTime(new Date(now));
        }
        preSealVehicle.setSource(SYSTEM_CODE);
        preSealVehicle.setOperateTime(DateHelper.parseAllFormatDateTime(sealCarDto.getSealCarTime()));

        return preSealVehicle;
    }
    /**
     * 获取未封车批次信息明细
     */
    @POST
    @Path("/new/preSeal/getUnSealSendCodes")
    @JProfiler(jKey = "DMSWEB.PreSealVehicleResource.getUnSealSendCodes", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP})
    public JdResult<List<String>> getUnSealSendCodes(NewSealVehicleRequest request) {
    	return this.newsealVehicleService.getUnSealSendCodes(request);
    }
}
