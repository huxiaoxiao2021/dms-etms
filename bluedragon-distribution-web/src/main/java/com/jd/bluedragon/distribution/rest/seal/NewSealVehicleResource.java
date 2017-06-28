package com.jd.bluedragon.distribution.rest.seal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
                if(returnCommonDto.getCode() == 1){
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_SEAL_SUCCESS);
                    sealVehicleResponse.setData(returnCommonDto.getData());

                }else{
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("["+returnCommonDto.getCode()+":"+returnCommonDto.getMessage()+"]");
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }
            }
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

            //增加车牌号的条件
            sealCarDto.setVehicleNumber(request.getVehicleNumber());


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
                if(returnCommonDto.getCode() == 1){
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                    sealVehicleResponse.setData(returnCommonDto.getData().getResult());
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
                if(returnCommonDto.getCode() == 1){
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_UNSEAL_SUCCESS);
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }else{
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage("["+returnCommonDto.getCode()+":"+returnCommonDto.getMessage()+"]");
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }
            }
        } catch (Exception e) {
            this.logger.error("NewSealVehicleResource.unseal-error", e);
        }
        return sealVehicleResponse;
    }

    private List<SealCarDto> convertList(List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sourceSealDtos) {
        List<SealCarDto> sealCarDtos = new ArrayList<SealCarDto>();
        for (com.jd.bluedragon.distribution.wss.dto.SealCarDto sourceSealDto : sourceSealDtos) {
            sealCarDtos.add(convert(sourceSealDto));
        }
        return sealCarDtos;
    }

    private SealCarDto convert(com.jd.bluedragon.distribution.wss.dto.SealCarDto sourceSealDto) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        SealCarDto sealCarDto = new SealCarDto();
        sealCarDto.setId(sourceSealDto.getId());
        sealCarDto.setSealCarCode(sourceSealDto.getSealCarCode());
        sealCarDto.setStatus(sourceSealDto.getStatus());
        sealCarDto.setSource(sourceSealDto.getSource());
        sealCarDto.setVehicleNumber(sourceSealDto.getVehicleNumber());
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
        sealCarDto.setSealCode(sourceSealDto.getSealCode());
        sealCarDto.setBatchCodes(sourceSealDto.getBatchCodes());
        sealCarDto.setDesealCodes(sourceSealDto.getDesealCodes());
        sealCarDto.setSealCodes(sourceSealDto.getSealCodes());

        try {
            if(sourceSealDto.getSealCarTime() != null && sourceSealDto.getSealCarTime().length() > 0){
                sealCarDto.setSealCarTime(simpleDateFormat.parse(sourceSealDto.getSealCarTime()));
            }
        }catch(Exception e){
            this.logger.error("封车日期[SealCarTime]转换异常", e);
        }

        try {
            if(sourceSealDto.getDesealCarTime() != null && sourceSealDto.getDesealCarTime().length() > 0) {
                sealCarDto.setDesealCarTime(simpleDateFormat.parse(sourceSealDto.getDesealCarTime()));
            }
        }catch(Exception e){
            this.logger.error("解封车日期[DesealCarTime]转换异常", e);
        }
        try {
            if(sourceSealDto.getCreateTime() != null && sourceSealDto.getCreateTime().length() > 0) {
                sealCarDto.setCreateTime(simpleDateFormat.parse(sourceSealDto.getCreateTime()));
            }
        }catch(Exception e){
            this.logger.error("创建日期[CreateTime]转换异常", e);
        }

        try {
            if(sourceSealDto.getUpdateTime() != null && sourceSealDto.getUpdateTime().length() > 0) {
                sealCarDto.setUpdateTime(simpleDateFormat.parse(sourceSealDto.getUpdateTime()));
            }
        }catch(Exception e){
            this.logger.error("创建日期[UpdateTime]转换异常", e);
        }

        return sealCarDto;
    }
}
