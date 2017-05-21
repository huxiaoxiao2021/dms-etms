package com.jd.bluedragon.distribution.rest.seal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
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
                this.logger.error("SealVehicleResource seal --> 传入参数非法");
            }
            CommonDto<String> returnCommonDto = newsealVehicleService.seal(convertList(request.getData()));
            if(returnCommonDto != null){
                if(returnCommonDto.getCode() == 1){
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                    sealVehicleResponse.setData(returnCommonDto.getData());

                }else{
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_EXCUTE_ERROR+",错误码为："+returnCommonDto.getCode()+",返回信息为："+returnCommonDto.getMessage());
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }
            }
        } catch (Exception e) {
            this.logger.error("SealVehicleResource.seal-error", e);
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
                this.logger.error("SealVehicleResource findSealInfo --> 传入参数非法");
            }

            SealCarDto sealCarDto = new SealCarDto();
            sealCarDto.setStatus(request.getStatus());
            sealCarDto.setSealCode(request.getSealCode());
            sealCarDto.setTransportCode(request.getTransportCode());
            sealCarDto.setStartSiteId(Integer.parseInt(request.getStartSiteId()));
            sealCarDto.setEndSiteId(Integer.parseInt(request.getEndSiteId()));

            PageDto<SealCarDto> pageDto = new PageDto<SealCarDto>();
            pageDto.setPageSize(request.getPageNums());

            CommonDto<PageDto<SealCarDto>> returnCommonDto = newsealVehicleService.findSealInfo(sealCarDto,pageDto);

            if(returnCommonDto != null){
                if(returnCommonDto.getCode() == 1){
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                    sealVehicleResponse.setData(returnCommonDto.getData().getResult());
                }else{
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_QUERY_ERROR+",错误码为："+returnCommonDto.getCode()+",返回信息为："+returnCommonDto.getMessage());
                    sealVehicleResponse.setData(null);
                }
            }
        } catch (Exception e) {
            this.logger.error("SealVehicleResource.findSealInfo-error", e);
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
                this.logger.error("SealVehicleResource unseal --> 传入参数非法");
            }
            CommonDto<String> returnCommonDto = newsealVehicleService.unseal(convertList(request.getData()));
            if(returnCommonDto != null){
                if(returnCommonDto.getCode() == 1){
                    sealVehicleResponse.setCode(JdResponse.CODE_OK);
                    sealVehicleResponse.setMessage(JdResponse.MESSAGE_OK);
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }else{
                    sealVehicleResponse.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    sealVehicleResponse.setMessage(NewSealVehicleResponse.MESSAGE_EXCUTE_ERROR+",错误码为："+returnCommonDto.getCode()+",返回信息为："+returnCommonDto.getMessage());
                    sealVehicleResponse.setData(returnCommonDto.getData());
                }
            }
        } catch (Exception e) {
            this.logger.error("SealVehicleResource.unseal-error", e);
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
            sealCarDto.setSealCarTime(simpleDateFormat.parse(sourceSealDto.getSealCarTime()));
            sealCarDto.setDesealCarTime(simpleDateFormat.parse(sourceSealDto.getDesealCarTime()));
            sealCarDto.setCreateTime(simpleDateFormat.parse(sourceSealDto.getCreateTime()));
            sealCarDto.setUpdateTime(simpleDateFormat.parse(sourceSealDto.getUpdateTime()));
        }catch(Exception e){
            this.logger.error("日期转换异常", e);
        }

        return sealCarDto;
    }
}
