package com.jd.bluedragon.distribution.rest.collect;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collect.domain.*;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsAreaService;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsCommonService;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsDetailService;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsPlaceService;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * 集货 rest接口
 *
 */

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class CollectGoodsResource {

    private Logger log = LoggerFactory.getLogger(CollectGoodsResource.class);

    @Autowired
    private CollectGoodsDetailService collectGoodsDetailService;

    @Autowired
    private CollectGoodsAreaService collectGoodsAreaService;

    @Autowired
    private CollectGoodsPlaceService collectGoodsPlaceService;

    @Autowired
    private CollectGoodsCommonService collectGoodsCommonService;

    @Autowired
    private OperationLogService operationLogService;


    /**
     * 获取某个分拣中心下的所有集货区
     * @param siteCode 分拣中心编号
     * @return
     */
    @GET
    @Path("/collect/areas/{siteCode}")
    public InvokeResult<List<CollectGoodsDTO>> findAreas(@PathParam("siteCode") Integer siteCode) {
        InvokeResult<List<CollectGoodsDTO>> result = new InvokeResult<>();

        try{
            List<CollectGoodsDTO> collectGoodsDTOS = new ArrayList<>();
            CollectGoodsArea queryParam = new CollectGoodsArea();
            queryParam.setCreateSiteCode(siteCode);
            List<CollectGoodsArea> collectGoodsAreas = collectGoodsAreaService.findBySiteCode(queryParam);
            if(collectGoodsAreas!=null){
                for( CollectGoodsArea collectGoodsArea :collectGoodsAreas){
                    CollectGoodsDTO collectGoodsDTO = new CollectGoodsDTO();
                    collectGoodsDTO.setCollectGoodsAreaCode(collectGoodsArea.getCollectGoodsAreaCode());
                    collectGoodsDTOS.add(collectGoodsDTO);
                }
            }
            result.setData(collectGoodsDTOS);
        }catch (Exception e){
            log.error("获取某个分拣中心下的所有集货区异常:{}", siteCode,e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;
    }


    /**
     * 集货  上架存放接口
     * 自动计算暂存位 并存放记录
     * 未验货时自动触发验货
     * @param req
     * @return
     */
    @POST
    @Path("/collect/put")
    public InvokeResult<CollectGoodsDTO> put(CollectGoodsDTO req) {
        InvokeResult<CollectGoodsDTO> result = new InvokeResult<>();
        try{

            result = collectGoodsCommonService.put(req);

        }catch (CollectException e){
            log.error("/collect/clean:{}", JsonHelper.toJson(req),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(e.getMessage());
        }catch (Exception e){
            log.error("/collect/put:{}", JsonHelper.toJson(req),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }


    /**
     * 获取数据
     * 输入包裹号 返回包裹号对应的运单所在集货位的所有数据
     * 输入货位号 返回货位全部数据
     * @param req
     * @return
     */
    @POST
    @Path("/collect/find")
    public InvokeResult<CollectGoodsDTO> find(CollectGoodsDTO req) {
        InvokeResult<CollectGoodsDTO> result = new InvokeResult<>();
        if(StringUtils.isBlank(req.getPackageCode()) && StringUtils.isBlank(req.getCollectGoodsPlaceCode())&& StringUtils.isBlank(req.getCollectGoodsAreaCode())){
            //包裹号 和 货位号 不能同时为空
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
        }
        try{
            StringBuilder message = new StringBuilder();
            CollectGoodsDetail param = new CollectGoodsDetail();
            param.setCollectGoodsPlaceCode(req.getCollectGoodsPlaceCode());
            param.setCollectGoodsAreaCode(req.getCollectGoodsAreaCode());
            if(StringUtils.isNotBlank(req.getPackageCode())){
                param.setWaybillCode(WaybillUtil.getWaybillCode(req.getPackageCode()));
            }
            param.setCreateSiteCode(req.getOperateSiteCode());
            List<CollectGoodsDetailCondition> list = collectGoodsDetailService.findScanWaybill(param);
            if(list != null && !list.isEmpty()){
                for(CollectGoodsDetailCondition collectGoodsDetailCondition : list){
                    message.append("运单"+collectGoodsDetailCondition.getWaybillCode()+
                            "已操作集货包裹数:"+collectGoodsDetailCondition.getScanPackageCount()+",");
                }
            }else{
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                if(StringUtils.isNotBlank(req.getPackageCode())){
                    message.append(WaybillUtil.getWaybillCode(req.getPackageCode())+"未操作过集货");
                }else if(StringUtils.isNotBlank(req.getCollectGoodsPlaceCode())){
                    message.append(WaybillUtil.getWaybillCode(req.getCollectGoodsPlaceCode())+"集货位无包裹数据");
                }if(StringUtils.isNotBlank(req.getCollectGoodsAreaCode())){
                    message.append(WaybillUtil.getWaybillCode(req.getCollectGoodsAreaCode())+"集货区无包裹数据");
                }

            }
            result.setMessage(message.toString());
        }catch (Exception e){
            log.error("/collect/find:{}", JsonHelper.toJson(req),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;
    }

    /**
     * 集货  释放集货位接口
     * 释放集货位 或 集货区所有数据
     * @param req
     * @return
     */
    @POST
    @Path("/collect/clean")
    public InvokeResult<Boolean> clean(CollectGoodsDTO req) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        try{

            result = collectGoodsCommonService.clean(req);

        }catch (CollectException e){
            log.error("/collect/clean:{}", JsonHelper.toJson(req),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(e.getMessage());
        }catch (Exception e){
            log.error("/collect/clean:{}", JsonHelper.toJson(req),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;

    }

    /**
     * 集货  异常转移接口
     * 转移集货位 或 包裹 至异常集货位
     * @param req
     * @return
     */
    @POST
    @Path("/collect/transfer")
    public InvokeResult<Boolean> transfer(CollectGoodsDTO req) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        try{

            result = collectGoodsCommonService.transfer(req);

        }catch (CollectException e){
            log.error("/collect/clean:{}", JsonHelper.toJson(req),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(e.getMessage());
        }catch (Exception e){
            log.error("/collect/transfer:{}", JsonHelper.toJson(req),e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;
    }



}
