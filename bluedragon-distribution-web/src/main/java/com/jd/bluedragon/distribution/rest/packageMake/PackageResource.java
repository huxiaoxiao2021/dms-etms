package com.jd.bluedragon.distribution.rest.packageMake;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.RePrintRecordMq;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.request.RePrintCallBackRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * Created by hujiping on 2018/4/4.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class PackageResource {

    private final Log logger= LogFactory.getLog(PackageResource.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private BaseMajorManager baseMajorManager;
    
    @Autowired
    @Qualifier("packageRePrintProducer")
    private DefaultJMQProducer packageRePrintProducer;

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
    /**
     * 包裹补打回调方法
     * @return
     */
    @POST
    @Path("/package/rePrintCallBack")
    public JdResult<Boolean> rePrintCallBack(RePrintCallBackRequest rePrintCallBackRequest){
    	JdResult<Boolean> result = new JdResult<Boolean>();
    	result.toSuccess();
    	if(rePrintCallBackRequest != null){
    		RePrintRecordMq rePrintRecordMq = new RePrintRecordMq();
    		rePrintRecordMq.setOperateType(WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT_TYPE);
    		rePrintRecordMq.setWaybillCode(rePrintCallBackRequest.getWaybillCode());
    		rePrintRecordMq.setPackageCode(rePrintCallBackRequest.getPackageCode());
    		rePrintRecordMq.setTemplateGroupCode(rePrintCallBackRequest.getTemplateGroupCode());
    		rePrintRecordMq.setTemplateName(rePrintCallBackRequest.getTemplateName());
    		rePrintRecordMq.setTemplateVersion(rePrintCallBackRequest.getTemplateVersion());
    		rePrintRecordMq.setUserCode(rePrintCallBackRequest.getUserCode());
    		rePrintRecordMq.setUserName(rePrintCallBackRequest.getUserName());
    		rePrintRecordMq.setUserErp(rePrintCallBackRequest.getUserErp());
    		rePrintRecordMq.setSiteCode(rePrintCallBackRequest.getSiteCode());
    		rePrintRecordMq.setSiteName(rePrintCallBackRequest.getSiteName());
    		rePrintRecordMq.setOperateTime(new Date());
    		
    		packageRePrintProducer.sendOnFailPersistent(rePrintRecordMq.getWaybillCode(), JsonHelper.toJson(rePrintRecordMq));
    		
    		String barCode = rePrintRecordMq.getWaybillCode();
    		if(StringHelper.isNotEmpty(rePrintRecordMq.getPackageCode())){
    			barCode = rePrintRecordMq.getPackageCode();
    		}
    		//调用旧逻辑发送全程跟踪等
    		this.packageRePrint(
    				barCode,
    				rePrintCallBackRequest.getWaybillSign(),
    				rePrintCallBackRequest.getSiteCode(), 
    				rePrintCallBackRequest.getUserErp());
    	}
    	return result;
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