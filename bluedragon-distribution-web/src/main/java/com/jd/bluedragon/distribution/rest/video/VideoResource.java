package com.jd.bluedragon.distribution.rest.video;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.video.domain.OperateInfo;
import com.jd.bluedragon.distribution.video.domain.VideoRequest;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by wuzhi138 2018-10-11
 *
 *
 * 主要功能点
 * 1、查询包裹在不同操作节点下的操作人及操作时间
 */

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class VideoResource {
    private final Log logger = LogFactory.getLog(VideoResource.class);

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    WaybillPackageApi waybillPackageApi;

    /**验货类型*/
    private static final Integer inspectionType = 1130;
    /**分拣类型*/
    private static final Integer sortingType = 1200;
    /**称重量方类型*/
    private static final Integer weightType = 1160;
    /**发货类型*/
    private static final Integer sendType = 1300;

    @POST
    @Path("/video/getUserAndOperateTime")
    public JdResponse getUserAndOperateTime(VideoRequest videoRequest){
        JdResponse response = new JdResponse(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        //校验请求参数
        if(!checkParam(videoRequest,response)){
            return response;
        }
        //获取业务类型
        Integer operateType = videoRequest.getOperateType();
        //获取传入的包裹号
        String packageCode = videoRequest.getPackageCode();
        //返回验货记录
        List<Inspection> inspectionList = null;
        //返回分拣记录
        List<Sorting> sortingList = null;
        //返回发货记录
        List<SendDetail> sendDetailList = null;
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("createSiteCode",videoRequest.getSiteNo());
        params.put("packageCode",packageCode);
        params.put("startIndex",0);
        params.put("pageSize",1);
        try {
            if(operateType.equals(inspectionType)){
                logger.info("开始查询包裹号为"+ packageCode +"的验货业务记录！");
                inspectionList = inspectionService.findPageInspection(params);
                if(inspectionList != null && !inspectionList.isEmpty()){
                    Inspection inspection = inspectionList.get(0);
                    OperateInfo operateInfo = new OperateInfo(inspection.getCreateUserCode(),inspection.getCreateUser(),inspection.getOperateTime());
                    response.setData(JsonHelper.toJson(operateInfo));
                    logger.info("查询包裹号为："+ packageCode +"的验货记录成功！");
                }else{
                    response.toFail("包裹号"+ packageCode +"没有对应的验货记录！");
                    logger.error("根据包裹号为："+ packageCode +"查询验货记录失败！");
                }
                logger.info("结束查询包裹号为"+ packageCode +"的验货业务记录");
            }
            if(operateType.equals(sortingType)){
                logger.info("开始查询包裹号为"+ packageCode +"的分拣业务记录!");
                sortingList = sortingService.findPageSorting(params);
                if(sortingList!=null && !sortingList.isEmpty()){
                    Sorting sorting = sortingList.get(0);
                    OperateInfo operateInfo = new OperateInfo(sorting.getCreateUserCode(),sorting.getCreateUser(),sorting.getOperateTime());
                    response.setData(JsonHelper.toJson(operateInfo));
                    logger.info("查询包裹号为："+ packageCode +"的分拣记录成功！");
                }else{
                    response.toFail("包裹号"+ packageCode +"没有对应的分拣记录！");
                    logger.error("根据包裹号为："+ packageCode +"查询分拣记录失败！");
                }
                logger.info("结束查询包裹号为"+ packageCode +"的分拣业务记录");
            }
            if(operateType.equals(sendType)){
                logger.info("开始查询包裹号为"+ packageCode +"的发货业务记录!");
                sendDetailList = sendDetailService.findPageSendDetail(params);
                if(sendDetailList!=null && !sendDetailList.isEmpty()){
                    SendDetail sendDetail = sendDetailList.get(0);
                    OperateInfo operateInfo = new OperateInfo(sendDetail.getCreateUserCode(),sendDetail.getCreateUser(),sendDetail.getOperateTime());
                    response.setData(JsonHelper.toJson(operateInfo));
                    logger.info("查询包裹号为："+ packageCode +"的发货记录成功！");
                }else{
                    response.toFail("包裹号"+ packageCode +"没有对应的发货记录！");
                    logger.error("根据包裹号为："+ packageCode +"查询发货记录失败！");
                }
                logger.info("结束查询包裹号为"+ packageCode +"的发货业务记录");
            }
            if(operateType.equals(weightType)){
                logger.info("开始查询包裹号为"+ packageCode +"的称重业务记录!");
                String waybillCode = WaybillUtil.getWaybillCode(packageCode);
                //根据运单号查找运单的称重量方流水
                BaseEntity<List<PackOpeFlowDto>> packageOpe = waybillPackageApi.getPackOpeByWaybillCode(waybillCode);
                if(packageOpe != null && packageOpe.getData().size() > 0){
                    logger.info("查询运单接口称重量方成功，运单号为："+ waybillCode);
                    for(PackOpeFlowDto packOpeFlowDto : packageOpe.getData()){
                        //获取包裹号
                        String packageBar = packOpeFlowDto.getPackageCode();
                        if(packageBar != null && packageBar.equals(packageCode)){
                            Integer weightUserId = Integer.parseInt(packOpeFlowDto.getWeighUserId());
                            OperateInfo operateInfo = new OperateInfo(weightUserId,packOpeFlowDto.getWeighUserName(),packOpeFlowDto.getWeighTime());
                            response.setData(JsonHelper.toJson(operateInfo));
                            logger.info("查询包裹号为："+ packageCode +"的称重量方记录成功！");
                        }
                    }
                }else{
                    response.toFail("包裹号"+ packageCode + "没有对应的称重量方记录！");
                    logger.error("根据包裹号为："+ packageCode + "查询称重量方记录失败！");
                }
                logger.info("结束查询包裹号为"+ packageCode +"的称重业务记录!");
            }
        } catch (Exception e) {
            logger.error("获取类型为["+operateType+"]的业务节点失败！",e);
        }
        return response;
    }

    public boolean checkParam(VideoRequest videoRequest, JdResponse response){
        if(videoRequest.getSiteNo() == null){
            response.toFail("分拣中心id为空！");
            logger.warn("分拣中心id为空！参数为："+JsonHelper.toJson(videoRequest));
            return Boolean.FALSE;
        }
        Integer operateType = videoRequest.getOperateType();
        if(operateType == null || (!operateType.equals(inspectionType) && !operateType.equals(sortingType) && !operateType.equals(weightType)
                && !operateType.equals(sendType))){
            response.toFail("输入操作类型不符合定义规则！");
            logger.warn("操作类型输入有误！参数为："+JsonHelper.toJson(videoRequest));
            return Boolean.FALSE;
        }
        String packageCode = videoRequest.getPackageCode();
        if("".equals(packageCode) || !WaybillUtil.isPackageCode(packageCode)){
            response.toFail("输入的包裹号为空或不符合包裹号规则！");
            logger.warn("包裹号输入有误！参数为："+JsonHelper.toJson(videoRequest));
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
