package com.jd.bluedragon.distribution.rest.transport;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.distribution.external.service.DmsArReceiveService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.receive.service.ArReceiveService;
import com.jd.bluedragon.distribution.transport.domain.ArSendCode;
import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.domain.ArWaitReceive;
import com.jd.bluedragon.distribution.transport.domain.ArWaitReceiveRequest;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.domain.ListResponse;

/**
 * Created by xumei3 on 2017/12/29.
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ArReceiveResource implements DmsArReceiveService {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ArSendRegisterService arSendRegisterService;

    @Autowired
    private BaseService baseService;
    
    @Autowired
    private ArReceiveService arReceiveService;


    /**
     * 空铁项目查找24小时内的待提货信息
     * @param request
     * @return
     */
    @POST
    @Path("/arreceive/getARWaitReceive")
    @Override
    public ListResponse<ArWaitReceive> getARWaitReceive(ArWaitReceiveRequest request) {
        //参数校验：始发城市id、操作人所属站点id必须
        //航空单号、运力名称非必须
        ListResponse<ArWaitReceive> result = new ListResponse<ArWaitReceive>();
        if(request == null){
            result.setCode(com.jd.ql.dms.common.domain.JdResponse.CODE_ERROR);
            result.setMessage(com.jd.ql.dms.common.domain.JdResponse.MESSAGE_ERROR+":参数为空！");
        }else if(request.getSiteCode() == null || request.getSiteCode() == 0){
            result.setCode(com.jd.ql.dms.common.domain.JdResponse.CODE_ERROR);
            result.setMessage(com.jd.ql.dms.common.domain.JdResponse.MESSAGE_ERROR+":参数-siteCode为0！");
        }else if(request.getStartCityId() == null || request.getStartCityId()== null){
            result.setCode(com.jd.ql.dms.common.domain.JdResponse.CODE_ERROR);
            result.setMessage(com.jd.ql.dms.common.domain.JdResponse.MESSAGE_ERROR+":参数-startCityId为0！");
        }
        else {
            //根据操作人所属站点获取目的城市
            int endCityId = getCityIdBySiteCod(request.getSiteCode());

            //组装查询参数
            ArSendRegister arSendRegister = new ArSendRegister();
            //如果始发城市的id为-1,表示需要匹配所有的始发城市
            if (request.getStartCityId() != -1) {
                arSendRegister.setStartCityId(request.getStartCityId());
            }
            //如果目的城市的id为-1,表示调用基础资料接口获取的分拣中心所在城市出错,则匹配所有目的城市
            if (endCityId != -1) {
                arSendRegister.setEndCityId(endCityId);
            }
            if (StringHelper.isNotEmpty(request.getOrderNo())) {
                arSendRegister.setOrderCode(request.getOrderNo());
            }
            if (StringHelper.isNotEmpty(request.getTransportName())) {
                arSendRegister.setTransportName(request.getTransportName());
            }
            logger.info("空铁项目待提货查询参数,"+ "始发城市id:" + request.getStartCityId() +",目的城市id:" + endCityId+
                    ",航空单号:" + request.getOrderNo() + ",运力名称:" + request.getTransportName());

            try{
                //从发货登记表中查找待提货信息
                List<ArSendRegister> arSendRegisters = arSendRegisterService.queryWaitReceive(arSendRegister);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for(ArSendRegister arSendRegister1 : arSendRegisters){
                    result.add(new ArWaitReceive(arSendRegister1.getOrderCode(),arSendRegister1.getTransportName(),
                            arSendRegister1.getStartStationName(),arSendRegister1.getEndStationName(),
                            formatter.format(arSendRegister1.getPlanStartTime()).toString(),
                            formatter.format(arSendRegister1.getPlanEndTime()).toString()));
                }
            }catch(Exception e){
                logger.error("空铁项目待提货查询失败，原因："+e.getMessage());
                result.setCode(com.jd.ql.dms.common.domain.JdResponse.CODE_ERROR);
                result.setMessage(com.jd.ql.dms.common.domain.JdResponse.MESSAGE_ERROR+":请联系研发人员");
            }

        }
        return result;
    }

    /**
     * 根据分拣中心id调用基础资料接口获取所在的城市
     * 如果是直辖市（北京、上海、天津、重庆）则将其cityId赋值为provinceId
     * （这么做是为了与运输保持一致）
     * @param siteCode
     * @return
     */
    private int getCityIdBySiteCod(int siteCode){
        int cityId = -1;
        try {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseService.queryDmsBaseSiteByCode(siteCode+"");
            //判断是否是北京(1)、上海(2)、天津(3)、重庆(4)这4个直辖市
            if(baseStaffSiteOrgDto!= null &&
                    (baseStaffSiteOrgDto.getProvinceId() == 1 || baseStaffSiteOrgDto.getProvinceId() == 2
                            || baseStaffSiteOrgDto.getProvinceId() == 3 || baseStaffSiteOrgDto.getProvinceId() == 4)){
                cityId = baseStaffSiteOrgDto.getProvinceId();
            }else{
                cityId = baseStaffSiteOrgDto.getCityId();
            }
        } catch (Exception e) {
            logger.error("中心服务调用基础资料getDmsBaseSiteByCode出错 siteCode=" + siteCode, e);
        }
        return cityId;
    }
    /**
     * 空铁提货-根据扫描包裹号/箱号获取空铁登记信息
     * @param barcode
     * @return
     */
    @POST
    @GET
    @Path("/arReceive/getArSendRegisterByBarcode/{barcode}")
    @Override
    public JdResponse<ArSendRegister> getArSendRegisterByBarcode(@PathParam("barcode") String barcode) {
    	JdResponse<ArSendRegister> rest = new JdResponse<ArSendRegister>();
    	if(StringHelper.isEmpty(barcode)){
    		rest.toFail("包裹号或箱号不能为空！");
    		return rest;
    	}
    	ArSendCode arSendCode = arReceiveService.getLastArSendCodeByBarcode(barcode);
    	if(arSendCode!=null){
    		rest.setData(arSendRegisterService.findById(arSendCode.getSendRegisterId()));
    	}
    	return rest;
    }
}
