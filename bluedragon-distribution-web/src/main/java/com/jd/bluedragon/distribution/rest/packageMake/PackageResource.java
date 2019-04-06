package com.jd.bluedragon.distribution.rest.packageMake;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ModifyOrderInfo;
import com.jd.bluedragon.distribution.client.domain.ClientOperateRequest;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.rest.waybill.WaybillResource;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * Created by hujiping on 2018/4/4.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class PackageResource {

    private final Logger logger= LoggerFactory.getLogger(PackageResource.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    @Qualifier("dmsModifyOrderInfoMQ")
    private DefaultJMQProducer dmsModifyOrderInfoMQ;

    @Autowired
    private WaybillResource waybillResource;

    public static String RE_PRINT_PREFIX = "RE_PRINT_CODE_";
    @GET
    @Path("/packageMake/packageRePrint/{barCode}/{waybillSign}/{siteId}/{operateName}")
    public JdResponse packageRePrint(@PathParam("barCode") String barCode,
                                     @PathParam("waybillSign") String waybillSign,
                                     @PathParam("siteId") Integer siteId,
                                     @PathParam("operateName") String operateName){
        JdResponse jdResponse = new JdResponse();
        if(StringHelper.isEmpty(barCode)){
            logger.error("包裹号"+barCode+"为空，不能触发包裹补打的全程跟踪!");
            jdResponse.setCode(400);
            jdResponse.setMessage("包裹号"+barCode+"为空，不能触发包裹补打的全程跟踪!");
            return jdResponse;
        }
        BaseStaffSiteOrgDto staffSiteOrgDto = null;
        Integer operatorId = null;
        if(StringHelper.isEmpty(operateName)){
            operateName="-1";
        }else{
            staffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(operateName);
            if(null!=staffSiteOrgDto){//实际是erp账号
                operateName = staffSiteOrgDto.getStaffName();
                operatorId = staffSiteOrgDto.getStaffNo();
            }else{
                operatorId=-1;
            }
        }

        //1.修改客户地址补打,发送全程跟踪,用于在商城前台显示
        BaseStaffSiteOrgDto bDto = null;
        try{
            Integer siteType = 0;
            if(siteId != null){
                bDto = baseMajorManager.getBaseSiteBySiteId(siteId);
            }
            if(bDto != null){
                siteType = bDto.getSiteType();
            }
            if(siteType != 0 && StringHelper.isNotEmpty(waybillSign)){
                //操作人所在机构是配送站并且waybillSign第八位是1或2或3的触发全程跟踪
                if(siteType == 4 && (BusinessUtil.isSignChar(waybillSign,8,'1' ) || // 1 仅修改地址
                        BusinessUtil.isSignChar(waybillSign,8,'2') ||             // 2 修改地址和其他
                        BusinessUtil.isSignChar(waybillSign,8,'3')                // 3 未修改地址仅修改其他
                )){
                    if(barCode != null && operateName != null){
                        taskService.add(this.toAddressModTask(barCode, operateName));
                    }
                }
            }
        }catch (Exception e){
            this.logger.warn("修改客户地址包裹补打触发全程跟踪失败",e);
        }

        //2.所有补打的包裹,发送全程跟踪,用于在青龙全程跟踪显示
        if(bDto != null){
            redisManager.setex(RE_PRINT_PREFIX+barCode, 3600, barCode);//1小时
            taskService.add(this.toPackReprintTask(barCode, bDto.getSiteCode(), bDto.getSiteName(), operatorId, operateName));
            jdResponse.setCode(JdResponse.CODE_OK);
            logger.info("触发包裹补打的全程跟踪成功,"+"包裹号"+barCode+",操作人"+operateName);
        }else{
            jdResponse.setCode(400);
            jdResponse.setMessage("参数错误，不能触发包裹补打的全程跟踪!不存在的siteId："+siteId);
        }

        return jdResponse;
    }

    @GET
    @Path("/package/checkRePrint/{barCode}")
    public JdResponse checkRePrint(@PathParam("barCode") String barCode){
        //1. 从redis中获得补打操作的条码缓存
        JdResponse jdResponse = new JdResponse();
        jdResponse.setCode(JdResponse.CODE_OK);
        if(StringHelper.isNotEmpty(barCode)) {
            String barCodeCached = redisManager.getCache(RE_PRINT_PREFIX+barCode);
            if(StringHelper.isNotEmpty(barCodeCached)){
                jdResponse.setCode(JdResponse.CODE_RE_PRINT_IN_ONE_HOUR);
                jdResponse.setMessage(JdResponse.MESSAGE_RE_PRINT_IN_ONE_HOUR);
            }
        }

        return jdResponse;
    }

    /**
     * 1. /services/OperationLogResource/add 记录操作日志
     * 2. /services/packageMake/packageRePrint 记录全程跟踪
     * 2. /services/waybill/sendModifyWaybillMQ 客户改址拦截成功
     * @param request
     * @return
     */
    @POST
    @Path("/package/ReprintAfter")
    public JdResponse packReprintAfter(ClientOperateRequest request) {
        JdResponse response = new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        String barCode = request.getBarCode();
        String waybillCode = WaybillUtil.getWaybillCode(barCode);
        if (StringHelper.isEmpty(barCode) || StringHelper.isEmpty(waybillCode)) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PACKAGE_ERROR);
            return response;
        }

        /* 1.记录打印操作日志 */
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setWaybillCode(waybillCode);
            operationLog.setPackageCode(barCode);
            operationLog.setCreateSiteCode(request.getCreateSiteCode());
            operationLog.setCreateSiteName(request.getCreateSiteName());
            operationLog.setCreateUserCode(request.getOperateUserCode());
            operationLog.setCreateUser(request.getOperateUserName());
            operationLog.setCreateTime(DateHelper.parseDateTime(request.getOperateTime()));
            operationLogService.add(operationLog);
        } catch (Exception e) {
            logger.error("PackageResource.packReprintAfter-->记录包裹打印日志异常,请求参数为{}", JsonHelper.toJson(request),e);
        }

        try {
            /* 2.发送全程跟踪 */
            this.packageRePrint(barCode,request.getWaybillSign(),request.getCreateSiteCode()
                    ,String.valueOf(request.getOperateUserCode()));

            /* 3.客户改址拦截MQ */
            String waybillSign = request.getWaybillSign();
            if (StringHelper.isEmpty(request.getWaybillSign()) && waybillSign.length() > 8 &&
                    (BusinessUtil.isSignChar(waybillSign,8,'1' ) || BusinessUtil.isSignChar(waybillSign,8,'2' )
                            || BusinessUtil.isSignChar(waybillSign,8,'3' ))) {
                char sign = waybillSign.charAt(7);

                ModifyOrderInfo modifyOrderInfo = new ModifyOrderInfo();
                modifyOrderInfo.setOrderId(waybillCode);
                modifyOrderInfo.setDmsId(request.getCreateSiteCode());
                modifyOrderInfo.setDmsName(request.getCreateSiteName());
                Integer resultCode = null;
                if (sign == '1') {
                    resultCode = 5;
                } else if (sign == '2'){
                    resultCode = 6;
                } else if (sign == '3') {
                    resultCode = 7;
                }
                modifyOrderInfo.setResultCode(resultCode);
                modifyOrderInfo.setOperateTime(request.getOperateTime());
                String json = JsonHelper.toJson(modifyOrderInfo);
                dmsModifyOrderInfoMQ.send(modifyOrderInfo.getOrderId(),json);
                logger.debug("PackageResource.packReprintAfter-->客户改址MQ发送成功{}",json);
            }
        } catch (Exception e) {
            logger.error("PackageResource.packReprintAfter-->包裹补打全程跟踪、客户改址拦截MQ发送失败：{}", JsonHelper.toJson(request),e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    private Task toAddressModTask(String barCode, String operateName){
        WaybillStatus waybillStatus = new WaybillStatus();

        if(WaybillUtil.isPackageCode(barCode)){
            waybillStatus.setWaybillCode(WaybillUtil.getWaybillCode(barCode));
            waybillStatus.setPackageCode(barCode);
        }else
            waybillStatus.setWaybillCode(barCode);

        waybillStatus.setOperateTime(new Date());
        waybillStatus.setOperator(operateName);
        waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_MSGTYPE_UPDATE);

        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setBody(JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        return task;
    }

    private Task toPackReprintTask(String barCode, Integer createSiteCode, String createSiteName, Integer operatorId, String operateName){
        WaybillStatus waybillStatus = new WaybillStatus();
        waybillStatus.setCreateSiteCode(createSiteCode);
        waybillStatus.setCreateSiteName(createSiteName);

        if(WaybillUtil.isPackageCode(barCode)){
            waybillStatus.setWaybillCode(WaybillUtil.getWaybillCode(barCode));
            waybillStatus.setPackageCode(barCode);
        }else
            waybillStatus.setWaybillCode(barCode);

        waybillStatus.setOperateTime(new Date());
        waybillStatus.setOperatorId(operatorId);
        waybillStatus.setOperator(operateName);
        waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_MSGTYPE_PACK_REPRINT);

        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setBody(JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        return task;
    }
}
