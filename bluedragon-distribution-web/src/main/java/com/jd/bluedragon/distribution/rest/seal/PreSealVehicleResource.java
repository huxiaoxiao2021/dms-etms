package com.jd.bluedragon.distribution.rest.seal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicleEnum;
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.wss.dto.SealCarDto;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.etms.vts.dto.CommonDto;
import com.jd.etms.vts.dto.VtsTransportResourceDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private PreSealVehicleService preSealVehicleService;

    @Autowired
    private NewSealVehicleService newsealVehicleService;

    @Autowired
    private SealVehiclesService sealVehiclesService;

    @Autowired
    private SiteService siteService;

    private static final String SYSTEM_CODE = "DMS";

    /**
     * 预封车服务
     * （1）同一个目的地只能操作一个运力编码的预封车，实际封车后可进行下一个波次的运力编码的预封车
     * （2）同一个运力编码，不同车牌号，可以进行多次预封车
     */
    @POST
    @Path("/new/preSeal")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1013)
    public NewSealVehicleResponse<Boolean> preSeal(NewSealVehicleRequest request) {
        logger.info("预封车请求参数：" + JsonHelper.toJson(request));
        NewSealVehicleResponse<Boolean> preSealResponse = new NewSealVehicleResponse(NewSealVehicleResponse.CODE_OK, NewSealVehicleResponse.MESSAGE_OK);
        try {
            if (request == null || request.getData() == null || request.getData().size() != 1 ) {
                logger.warn("PreSealVehicleResource preSeal --> 传入参数非法" + JsonHelper.toJson(request));
                preSealResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
                preSealResponse.setMessage(NewSealVehicleResponse.MESSAGE_PARAM_ERROR);
                return preSealResponse;
            }

            /** 预封车数据是逐条提交的 */
            SealCarDto sealCarDto = request.getData().get(0);
            if(StringUtils.isBlank(sealCarDto.getTransportCode()) || StringUtils.isBlank(sealCarDto.getVehicleNumber())){
                logger.warn("PreSealVehicleResource preSeal --> 运力编码或车牌为空：" + JsonHelper.toJson(request));
                preSealResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
                preSealResponse.setMessage(NewSealVehicleResponse.TIPS_PRESEAL_PARAM_ERROR);
                return preSealResponse;
            }

            /** 查询运力信息 */
            VtsTransportResourceDto vtrd = getTransport(sealCarDto.getTransportCode(), preSealResponse);
            if(!NewSealVehicleResponse.CODE_OK.equals(preSealResponse.getCode())){
                return preSealResponse;
            }

            List<PreSealVehicle> exists = preSealVehicleService.findByCreateAndReceive(sealCarDto.getSealSiteId(), vtrd.getEndNodeId());

            if (exists == null || exists.isEmpty()) {
                //该目的地没有预封车数据时，属于新增
                preSealVehicleService.insert(convertRequst(sealCarDto, vtrd, false));
                preSealResponse.setData(true);
            }else{
                Long existId = null;
                for(PreSealVehicle exist : exists){
                    if(!sealCarDto.getTransportCode().equals(exist.getTransportCode())){
                        //该目的已有预封车数据时，但是运力编码不一致时提示是否更新
                        preSealResponse.setCode(NewSealVehicleResponse.CODE_PRESEAL_EXIST_ERROR);
                        preSealResponse.setMessage(NewSealVehicleResponse.TIPS_PRESEAL_EXIST_ERROR);
                    }else if(sealCarDto.getVehicleNumber().equals(exist.getVehicleNumber())){
                        //该目的已有预封车数据时，但是运力编码一致，车牌也一致时，属于更新封签号
                        existId = exist.getId();
                        break;
                    }

                }
                if(NewSealVehicleResponse.CODE_OK.equals(preSealResponse.getCode())){
                    if(existId != null ){
                        //该目的已有预封车数据时，但是运力编码一致，车牌也一致时，属于更新封签号,根据ID更新数据
                        PreSealVehicle preSealVehicle = convertRequst(sealCarDto, vtrd, true);
                        preSealVehicle.setId(existId);
                        preSealVehicleService.updateById(preSealVehicle);
                        preSealResponse.setData(true);
                        preSealResponse.setMessage("预封车封签号更新成功!");
                    }else{
                        //该目的已有预封车数据时，但是运力编码一致，车牌不一致时，属于新增
                        preSealVehicleService.insert(convertRequst(sealCarDto, vtrd, false));
                        preSealResponse.setData(true);
                    }
                }
            }

        } catch (Exception e) {
            preSealResponse.setCode(NewSealVehicleResponse.CODE_SERVICE_ERROR);
            preSealResponse.setMessage(NewSealVehicleResponse.MESSAGE_SERVICE_ERROR);
            logger.error("PreSealVehicleResource.preSeal-error" + JsonHelper.toJson(request), e);
        }
        return preSealResponse;
    }

    /**
     * 预封车更新服务
     */
    @POST
    @Path("/new/updatePreSeal")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1013)
    public NewSealVehicleResponse<Boolean> updatePreSeal(NewSealVehicleRequest request) {
        logger.info("更新预封车请求参数：" + JsonHelper.toJson(request));
        NewSealVehicleResponse<Boolean> preSealResponse = new NewSealVehicleResponse(NewSealVehicleResponse.CODE_OK, NewSealVehicleResponse.MESSAGE_OK);
        try {
            if (request == null || request.getData() == null || request.getData().size() != 1  || request.getData().get(0).getSealSiteId() == null) {
                logger.warn("PreSealVehicleResource updatePreSeal --> 传入参数非法" + JsonHelper.toJson(request));
                preSealResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
                preSealResponse.setMessage(NewSealVehicleResponse.MESSAGE_PARAM_ERROR);
                return preSealResponse;
            }

            /** 预封车数据是逐条提交的 */
            SealCarDto sealCarDto = request.getData().get(0);
            if(StringUtils.isBlank(sealCarDto.getTransportCode()) || StringUtils.isBlank(sealCarDto.getVehicleNumber())){
                logger.warn("PreSealVehicleResource updatePreSeal --> 运力编码或车牌为空：" + JsonHelper.toJson(request));
                preSealResponse.setCode(NewSealVehicleResponse.CODE_PARAM_ERROR);
                preSealResponse.setMessage(NewSealVehicleResponse.TIPS_PRESEAL_PARAM_ERROR);
                return preSealResponse;
            }
            /** 查询运力信息 */
            VtsTransportResourceDto vtrd = getTransport(sealCarDto.getTransportCode(), preSealResponse);
            if(!NewSealVehicleResponse.CODE_OK.equals(preSealResponse.getCode())){
                return preSealResponse;
            }

            preSealVehicleService.cancelPreSealBeforeInsert(convertRequst(sealCarDto, vtrd, false));
            preSealResponse.setData(true);
        } catch (Exception e) {
            preSealResponse.setCode(NewSealVehicleResponse.CODE_SERVICE_ERROR);
            preSealResponse.setMessage(NewSealVehicleResponse.MESSAGE_SERVICE_ERROR);
            logger.error("PreSealVehicleResource.updatePreSeal-error" + JsonHelper.toJson(request), e);
        }
        return preSealResponse;
    }

    /**
     * 根据运力编码获取运力信息
     * @param transportCode
     * @param response
     * @return
     */
    private VtsTransportResourceDto getTransport(String transportCode, NewSealVehicleResponse<Boolean> response){
        VtsTransportResourceDto vtrd = null;
        try {
            CommonDto<VtsTransportResourceDto> transDto = newsealVehicleService.getTransportResourceByTransCode(transportCode);
            logger.info("预封车查询运力信息：" + JsonHelper.toJson(transDto));
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
                logger.warn("预封车查询运力信息出错,运力编码:" + transportCode + ",出错原因:" + transDto.getMessage());
            }
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            logger.error("通过运力编码获取基础资料信息异常：" + transportCode, e);
        }
        return vtrd;
    }

    /**
     * 预封车VO转换
     * @param sealCarDto
     * @param vtrd
     * @param isUpdate
     * @return
     */
    private PreSealVehicle convertRequst(SealCarDto sealCarDto, VtsTransportResourceDto vtrd, boolean isUpdate){
        PreSealVehicle preSealVehicle = new PreSealVehicle();
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
}
