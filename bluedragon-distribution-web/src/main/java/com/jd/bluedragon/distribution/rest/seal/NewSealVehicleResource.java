package com.jd.bluedragon.distribution.rest.seal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.seal.service.CarLicenseChangeUtil;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.etms.vts.dto.VtsTransportResourceDto;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * create by zhanglei 2017-05-10
 * <p>
 * 新版封车解封车
 * <p>
 * 主要功能点
 * 1、封车：回传TMS发车信息（通过jsf接口）
 * 2、解封车：回传TMS解封车信息（通过jsf接口）
 * 3、获取待解封列表(通过jsf接口)
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class NewSealVehicleResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private NewSealVehicleService newsealVehicleService;

    @Autowired
    private CarLicenseChangeUtil carLicenseChangeUtil;

    @Autowired
    private GoddessService goddessService;

    private static final int ROLL_BACK_DAY = -7; //查询几天内的带解任务（负数）

    private static final int SEAL_SOURCE = 1;  //封车解封车操作源（代表我们DMS系统）

    /**
     * 检查运力编码和批次号目的地是否一致
     */
    @GET
    @Path("/new/vehicle/seal/check/{transportCode}/{batchCode}")
    public NewSealVehicleResponse checkTranCodeAndBatchCode(@PathParam("transportCode")String transportCode,@PathParam("batchCode") String batchCode) {
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            //1.检查批次号
            checkBatchCode(sealVehicleResponse, batchCode);
            if(JdResponse.CODE_OK.equals(sealVehicleResponse.getCode())){//批次号校验通过
                //2.获取运力信息并检查目的站点
                com.jd.etms.vts.dto.CommonDto<VtsTransportResourceDto> vtsDto  = newsealVehicleService.getTransportResourceByTransCode(transportCode);
                if(vtsDto == null){    //JSF接口返回空
                    sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                    sealVehicleResponse.setMessage("查询运力信息结果为空:" + transportCode);
                    return sealVehicleResponse;
                }
                if(Constants.RESULT_SUCCESS == vtsDto.getCode() ){ //JSF接口调用成功
                    if(SerialRuleUtil.getReceiveSiteCodeFromSendCode(batchCode).equals(vtsDto.getData().getEndNodeId())){  // 目标站点一致
                        sealVehicleResponse.setCode(JdResponse.CODE_OK);
                        sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                    }else{// 目标站点不一致
                        sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                        sealVehicleResponse.setMessage(NewSealVehicleResponse.TIPS_RECEIVESITE_DIFF_ERROR);
                    }
                }else if( Constants.RESULT_WARN == vtsDto.getCode()){    //查询运力信息接口返回警告，给出前台提示
                    sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                    sealVehicleResponse.setMessage(vtsDto.getMessage());
                }else { //服务出错或者出异常，打日志
                    sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
                    sealVehicleResponse.setMessage("查询运力信息出错！");
                    logger.error("查询运力信息出错,出错原因:" + vtsDto.getMessage());
                    logger.error("查询运力信息出错,运力编码:" + transportCode);
                }
            }
        } catch (Exception e) {
            sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            sealVehicleResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            this.logger.error("NewSealVehicleResource.seal-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * 封车功能
     */
    @POST
    @Path("/new/vehicle/seal")
    public NewSealVehicleResponse seal(NewSealVehicleRequest request) {
        NewSealVehicleResponse sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            if (request == null) {
                this.logger.error("NewSealVehicleResource seal --> 传入参数非法");
            }
            List<SealCarDto> paramList = convertList(request.getData());

            logger.info("封车参数："+JsonHelper.toJson(paramList));

            CommonDto<String> returnCommonDto = newsealVehicleService.seal(paramList);
            if(returnCommonDto != null){
                if(Constants.RESULT_SUCCESS == returnCommonDto.getCode()){
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_SEAL_SUCCESS);
                    sealVehicleResponse.setData(returnCommonDto.getData());

                }else{
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("["+returnCommonDto.getCode()+":"+returnCommonDto.getMessage()+"]");
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }
            }
            addSystemLog(request);
        } catch (Exception e) {
            this.logger.error("NewSealVehicleResource.seal-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * 获取待解封信息
     */
    @POST
    @Path("/new/vehicle/findSealInfo")
    public NewSealVehicleResponse findSealInfo(NewSealVehicleRequest request) {

        NewSealVehicleResponse<List<SealCarDto>> sealVehicleResponse = new NewSealVehicleResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {

            if (request == null) {
                this.logger.error("NewSealVehicleResource findSealInfo --> 传入参数非法");
            }

            SealCarDto sealCarDto = new SealCarDto();
            sealCarDto.setStatus(request.getStatus());
            sealCarDto.setSealCode(request.getSealCode());
            sealCarDto.setTransportCode(request.getTransportCode());
            //查询7天内的带解任务
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, ROLL_BACK_DAY);
            sealCarDto.setSealCarTimeBegin(c.getTime());

            if(StringHelper.isNotEmpty(request.getVehicleNumber())){
                String ChineseVehicleNumber = carLicenseChangeUtil.formateLicense2Chinese(request.getVehicleNumber());

                //增加车牌号的条件
                sealCarDto.setVehicleNumber(ChineseVehicleNumber);
            }


            Integer intStartSiteId = NumberHelper.isNumber(request.getStartSiteId()) ? Integer.parseInt(request.getStartSiteId()) : null;
            Integer intEndSiteId = NumberHelper.isNumber(request.getEndSiteId()) ? Integer.parseInt(request.getEndSiteId()) : null;


            sealCarDto.setStartSiteId(intStartSiteId);
            sealCarDto.setEndSiteId(intEndSiteId);

            PageDto<SealCarDto> pageDto = new PageDto<SealCarDto>();
            pageDto.setPageSize(request.getPageNums());

            logger.info("解封车查询参数，sealCarDto："+JsonHelper.toJson(sealCarDto));
            logger.info("解封车查询参数，pageDto："+JsonHelper.toJson(pageDto));

            CommonDto<PageDto<SealCarDto>> returnCommonDto = newsealVehicleService.findSealInfo(sealCarDto,pageDto);

            if(returnCommonDto != null){
                if(Constants.RESULT_SUCCESS == returnCommonDto.getCode()){
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                    List<SealCarDto> sealCarDtos =  returnCommonDto.getData().getResult();
                    if(sealCarDtos != null && sealCarDtos.size() > 0){    //合并批次号并按创建时间倒序排序
                        sealCarDtos = mergeBatchCode(sealCarDtos);
                        sortSealCarDtos(sealCarDtos);
                        sealVehicleResponse.setData(sealCarDtos);
                    }else{
                        sealVehicleResponse.setCode(JdResponse.CODE_OK_NULL);
                        sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK_NULL);
                        sealVehicleResponse.setData(null);
                    }
                }else{
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("["+returnCommonDto.getCode()+":"+returnCommonDto.getMessage()+"]");
                    sealVehicleResponse.setData(null);
                }
            }
        } catch (Exception e) {
            this.logger.error("NewSealVehicleResource.findSealInfo-error", e);
        }
        return sealVehicleResponse;
    }


    /**
     * 解封车功能
     */
    @POST
    @Path("/new/vehicle/unseal")
    public NewSealVehicleResponse unseal(NewSealVehicleRequest request) {
        NewSealVehicleResponse<String> sealVehicleResponse = new NewSealVehicleResponse<String>(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        try {
            if (request == null) {
                this.logger.error("NewSealVehicleResource unseal --> 传入参数非法");
            }
            List<SealCarDto> paramList = convertList(request.getData());

            logger.info("解封车参数："+JsonHelper.toJson(paramList));

            CommonDto<String> returnCommonDto = newsealVehicleService.unseal(paramList);
            if(returnCommonDto != null){
                if(Constants.RESULT_SUCCESS == returnCommonDto.getCode()){
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_UNSEAL_SUCCESS);
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }else{
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("["+returnCommonDto.getCode()+":"+returnCommonDto.getMessage()+"]");
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }
            }
            addSystemLog(request);
        } catch (Exception e) {
            this.logger.error("NewSealVehicleResource.unseal-error", e);
        }
        return sealVehicleResponse;
    }

    /**
     * 增加封车解封车系统日志
     * @param request
     */
    private void addSystemLog(NewSealVehicleRequest request){
        if (request.getData() == null && request.getData().size() == 0){
            return;
        }
        List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> list = request.getData();
        for(com.jd.bluedragon.distribution.wss.dto.SealCarDto dto : list){
            Goddess goddess = new Goddess();
            goddess.setHead(dto.getSealSiteId() + "-" + dto.getDesealSiteId());
            goddess.setBody(JsonHelper.toJson(dto));
            goddess.setDateTime(new Date());
            if(StringUtils.isNotBlank(dto.getTransportCode())){//封车日志
                goddess.setKey(dto.getTransportCode());
            }else if(StringUtils.isNotBlank(dto.getSealCarCode())){//解封车日志
                goddess.setKey(dto.getSealCarCode());
            }
            goddessService.save(goddess);
        }
    }

    private List<SealCarDto> convertList(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sourceSealDtos) {
        List<SealCarDto> sealCarDtos = new ArrayList<SealCarDto>();
        Date nowTime = new Date();//封车取当前服务器当前时间
        for (com.jd.bluedragon.distribution.wss.dto.SealCarDto sourceSealDto : sourceSealDtos) {
            sealCarDtos.add(convert(sourceSealDto, nowTime));
        }
        return sealCarDtos;
    }

    private SealCarDto convert(com.jd.bluedragon.distribution.wss.dto.SealCarDto sourceSealDto, Date nowTime) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        SealCarDto sealCarDto = new SealCarDto();
        sealCarDto.setId(sourceSealDto.getId());
        sealCarDto.setSealCarCode(sourceSealDto.getSealCarCode());
        sealCarDto.setStatus(sourceSealDto.getStatus());
        sealCarDto.setSource(SEAL_SOURCE);
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
        if(sourceSealDto.getDesealCarTime() != null && sourceSealDto.getDesealCarTime().length() > 0) {    //解封车
            sealCarDto.setDesealCarTime(nowTime);  //解封车时间取服务器当前时间
        }else{//封车
            sealCarDto.setSealCarTime(nowTime);    //封车时间取服务器当前时间
        }

        return sealCarDto;
    }

    /**
     * 1.检查批次号是否有且符合编码规则
     * 否则提示“请输入正确的批次号!”
     * 2.检查批次号是否已经封车
     * 已封车则提示“该发货批次号已操作封车，无法重复操作！”
     * @param batchCode
     * @return
     */
    private void checkBatchCode(NewSealVehicleResponse sealVehicleResponse, String batchCode){
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(batchCode);//获取批次号目的地
        //1.批次号是否符合编码规范，不合规范直接返回参数错误
        if(receiveSiteCode == null){
            sealVehicleResponse.setCode(JdResponse.CODE_PARAM_ERROR);
            sealVehicleResponse.setMessage(NewSealVehicleResponse.TIPS_BATCHCODE_PARAM_ERROR);
            return ;
        }
        //2.是否已经封车
        CommonDto<Boolean> isSealed =  newsealVehicleService.isBatchCodeHasSealed(batchCode);
        if(isSealed == null){
            sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            sealVehicleResponse.setMessage("服务异常，运输系统查询批次号状态结果为空！");
            logger.error("服务异常，运输系统查询批次号状态结果为空, 批次号:" + batchCode);
            return ;
        }

        if(Constants.RESULT_SUCCESS == isSealed.getCode()){//服务正常
            if(Boolean.TRUE.equals(isSealed.getData())){//已被封车
                sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                sealVehicleResponse.setMessage(NewSealVehicleResponse.TIPS_BATCHCODE_SEALED_ERROR);
            }else {//未被封车
                sealVehicleResponse.setCode(JdResponse.CODE_OK);
                sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
            }
        }else if(Constants.RESULT_WARN == isSealed.getCode()){ //接口返回警告信息，给前台提示
            sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            sealVehicleResponse.setMessage(isSealed.getMessage());
        }else {//服务出错或者出异常，打日志
            sealVehicleResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            sealVehicleResponse.setMessage("服务异常，运输系统查询批次号状态失败！");
            logger.error("服务异常，运输系统查询批次号状态失败, 批次号:" + batchCode);
            logger.error("服务异常，运输系统查询批次号状态失败，失败原因:" + isSealed.getMessage());
        }
        //3.批次号是否存在（最后查询批次号是否存在，不存在时给前台提示）
        if(JdResponse.CODE_OK.equals(sealVehicleResponse.getCode()) && !newsealVehicleService.checkSendIsExist(batchCode)){//批次号不存在
            sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
            sealVehicleResponse.setMessage(NewSealVehicleResponse.TIPS_BATCHCODE_PARAM_NOTEXSITE_ERROR);
        }
    }

    /**
     * 合并同一运力编码、同意操作时间的批次号
     * @param SealCarDtos
     * @return
     */
    private List<SealCarDto> mergeBatchCode(List<SealCarDto> SealCarDtos){
        Map<String, SealCarDto> mergeMap = new HashedMap();
        for (SealCarDto dto : SealCarDtos){
            String key = dto.getTransportCode()+dto.getSealCarTime().getTime();
            if(dto.getBatchCodes() == null){
                dto.setBatchCodes(new ArrayList<String>());
            }
            if(mergeMap.containsKey(key)){
                mergeMap.get(key).getBatchCodes().addAll(dto.getBatchCodes());
            }else{
                mergeMap.put(key, dto);
            }
        }
        List<SealCarDto> mergeResult = new ArrayList<SealCarDto>(mergeMap.size());
        for(SealCarDto dto : mergeMap.values()){
            String batchCodes = dto.getBatchCodes().toString();
            dto.getBatchCodes().clear();
            dto.getBatchCodes().add(StringUtils.strip(batchCodes, "[]"));    //去掉中括号
            mergeResult.add(dto);
        }

        return mergeResult;
    }

    /**
     * 按创建时间倒序排序
     * @param SealCarDtos
     */
    private void sortSealCarDtos(List<SealCarDto> SealCarDtos){
        Collections.sort(SealCarDtos, new Comparator<SealCarDto>() {
            @Override
            public int compare(SealCarDto dto1, SealCarDto dto2) {
                return dto2.getCreateTime().compareTo(dto1.getCreateTime());
            }
        });
    }

}
