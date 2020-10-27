package com.jd.bluedragon.distribution.popPrint.service.impl;

import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.PopPrintResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.popPrint.dao.PopPrintDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrintSmsMsg;
import com.jd.bluedragon.distribution.popPrint.domain.ResidentTypeEnum;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.JsonUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-14 下午07:50:33
 * <p>
 * 类说明
 */
@Service("popPrintService")
public class PopPrintServiceImpl implements PopPrintService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static boolean isRedisModeAllowed = false;

    /**
     * 数据来源: 驻场打印：3
     * */
    private static int PRINT_SOURCE = 3;

    @Autowired
    private PopPrintDao popPrintDao;

    @Autowired
    private InspectionDao inspectionDao;

    @Autowired
    private SiteService siteService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("popPrintToSmsProducer")
    private DefaultJMQProducer popPrintToSmsProducer;

    @Autowired
    @Qualifier("zhuchangPrintToTerminalProducer")
    private DefaultJMQProducer zhuchangPrintToTerminalProducer;

    @Autowired
    private RedisManager redisManager;

    @Override
    public PopPrint findByWaybillCode(String waybillCode) {
        if (StringUtils.isBlank(waybillCode)) {
            log.info("传入运单号 waybillCode 为空");
        }
        return popPrintDao.findByWaybillCode(waybillCode);
    }

    @Override
    public List<PopPrint> findSitePrintDetail(Map<String, Object> map) {
        return popPrintDao.findSitePrintDetail(map);
    }

    @Override
    public Integer findSitePrintDetailCount(Map<String, Object> map) {
        return popPrintDao.findSitePrintDetailCount(map);
    }


    @Override
    public List<PopPrint> findAllByWaybillCode(String waybillCode) {
        if (StringUtils.isBlank(waybillCode)) {
            log.info("传入运单号 waybillCode 为空");
        }
        return this.popPrintDao.findAllByWaybillCode(waybillCode);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int add(PopPrint popPrint) {
        if (popPrint == null) {
            log.info("传入popPrint 为空");
            return 0;
        }
       return popPrintDao.add(popPrint);
    }

    /**
     * 发补验货任务
     */
    public void pushInspection(PopPrint popPrint) {
        BaseStaffSiteOrgDto create = siteService.getSite(popPrint.getCreateSiteCode());
        String createSiteName = null != create ? create.getSiteName() : null;

        TaskRequest request = new TaskRequest();
        request.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        request.setKeyword1(popPrint.getCreateUserCode().toString());
        if (popPrint.getPackageBarcode() != null) {
            request.setKeyword2(popPrint.getPackageBarcode());
        } else {
            request.setKeyword2(popPrint.getWaybillCode());
        }
        request.setType(Task.TASK_TYPE_POP_PRINT_INSPECTION);
        request.setOperateTime(DateHelper.formatDateTime(popPrint.getPrintPackTime()));
        request.setSiteCode(popPrint.getCreateSiteCode());
        request.setSiteName(createSiteName);
        request.setUserCode(popPrint.getCreateUserCode());
        request.setUserName(popPrint.getCreateUser());
        //request.setBody();
        String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
                + JsonHelper.toJson(popPrint)
                + Constants.PUNCTUATION_CLOSE_BRACKET;
        Task task = this.taskService.toTask(request, eachJson);

        int result = this.taskService.add(task, true);
        if (log.isDebugEnabled()) {
            log.debug("平台打印补验货任务插入条数:{}条,请求参数:{}" ,result, JsonHelper.toJson(task));
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.PopPrintServiceImpl.dealPopPrintLogic",jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public PopPrintResponse dealPopPrintLogic(PopPrintRequest popPrintRequest, Integer residentType) {
        boolean isPrintPack = false;
        try {
            PopPrint popPrint = requestToPopPrint(popPrintRequest);
            // 客户端驻厂
            if(ResidentTypeEnum.RESIDENT_DESK_CLIENT.getType() == residentType){
                // 验证运单号
                Waybill waybill = waybillCommonService.findByWaybillCode(popPrintRequest.getWaybillCode());
                if (waybill == null) {
                    this.log.warn("保存POP打印信息savePopPrint --> 运单【{}】不存在",popPrintRequest.getWaybillCode());
                    return new PopPrintResponse(JdResponse.CODE_NO_POP_WAYBILL,
                            JdResponse.MESSAGE_NO_POP_WAYBILL);
                }
                /**
                 * 判断是否打印包裹
                 */
                isPrintPack = PopPrintRequest.PRINT_PACK_TYPE.equals(popPrintRequest.getOperateType());
                int uptCount = 0;
                try {
                    uptCount = updateByWaybillOrPack(popPrint);
                } catch (Exception e) {
                    this.log.error("updateByWaybillOrPack失败", e);
                }
                boolean savePopPrintToRedis = false;
                if (uptCount <= 0) {
                    if (isPrintPack) {
                        popPrint.setPrintCount(1);
                    }
                    if (isRedisModeAllowed) { // isRedisModeAllowed 开关控制是否存Redis
                        savePopPrintToRedis = true;
                    } else {
                        try {
                            add(popPrint);
                        } catch (Exception e) {
                            savePopPrintToRedis = true;// 数据库异常的时候存Redis
                        }
                    }
                    if (savePopPrintToRedis) {
                        savePopPrintToRedis(popPrint);
                    }
                    //推补验货任务
                    if (PopPrintRequest.PRINT_PACK_TYPE.equals(popPrint.getOperateType())) {
                        pushInspection(popPrint);
                    }
                    this.log.info("插入POP打印信息savePopPrint成功，运单号【{}】",popPrint.getWaybillCode());
                }
            }

            /**
             *  发送mq信息给终端-sms
             *      1、打印后:站点平台打印 && 配送员接货
             *      2、驻厂打印 && 接货类型-5
             *      3、龙门架驻厂
             */
            boolean isNeedSendMQ = false;
            if(
                    isPrintPack
                    &&PopPrintRequest.POP_RECEIVE_TYPE_4.equals(popPrintRequest.getPopReceiveType())
                    &&PopPrintRequest.BUS_TYPE_SITE_PLATFORM_PRINT.equals(popPrintRequest.getBusinessType())){
                isNeedSendMQ = true;
            }else if(
                    ResidentTypeEnum.RESIDENT_GANTRY.getType() == residentType
                            || (isPrintPack
                    &&PopPrintRequest.POP_RECEIVE_TYPE_5.equals(popPrintRequest.getPopReceiveType())
                    &&PopPrintRequest.BUS_TYPE_IN_FACTORY_PRINT.equals(popPrintRequest.getBusinessType()))
            ){
                //驻厂打印 时
                if(StringUtils.isNotBlank(popPrintRequest.getBoxCode())){
                    // 箱号不为空时 发送MQ
                    isNeedSendMQ = true;
                }
                //操作站点类型 是站点的情况下 发送全程跟踪
                BaseStaffSiteOrgDto bDto = null;
                BaseStaffSiteOrgDto userDto=null;
                try {
                    bDto = this.baseMajorManager.getBaseSiteBySiteId(popPrintRequest.getOperateSiteCode());
                } catch (Exception e) {
                    log.error("驻厂打印时获取站点失败 站点编号：{}", popPrintRequest.getOperateSiteCode(), e);
                }
                try {
                    userDto=baseMajorManager.getBaseStaffByStaffId(popPrintRequest.getOperatorCode());
                } catch (Exception e) {
                    log.error("获取操作人资料失败：{}", popPrintRequest.getOperatorCode(), e);
                }

                if(bDto==null){
                    log.warn("驻厂打印时获取站点为空 站点编号：{}", popPrintRequest.getOperateSiteCode());
                }else if (userDto==null){
                    log.warn("获取操作人资料为空 id：{}", popPrintRequest.getOperatorCode());
                }else{
                    if(BusinessHelper.isSiteType(bDto.getSiteType())){
                        Date operatorTime=new Date(System.currentTimeMillis()-30000L);
                        //操作站点类型符合 是站点
                        toTask(popPrintRequest, WaybillStatus.WAYBILL_TRACK_UP_DELIVERY,"订单/包裹已接货",operatorTime);
                        toTask(popPrintRequest,WaybillStatus.WAYBILL_TRACK_COMPLETE_DELIVERY,"配送员"+popPrintRequest.getOperatorName()+"揽收完成",operatorTime);
                        //驻厂打印成功，发送mq给终端，他们去同步终端运单，避免挂单
                        Map<String,Object> msgBody= Maps.newHashMap();
                        msgBody.put("waybillCode",popPrintRequest.getWaybillCode());
                        msgBody.put("packageCode",popPrintRequest.getPackageBarcode());
                        msgBody.put("goods",popPrintRequest.getCategoryName());
                        msgBody.put("operatorErp",userDto.getErp());
                        msgBody.put("operatorTime",operatorTime.getTime());
                        msgBody.put("operatorSource",PRINT_SOURCE);
                        zhuchangPrintToTerminalProducer.sendOnFailPersistent(popPrintRequest.getWaybillCode(),JsonHelper.toJson(msgBody));
                    }
                }
            }

            if(isNeedSendMQ){
                PopPrintSmsMsg popPrintSmsMsg = new PopPrintSmsMsg();
                BeanUtils.copyProperties(popPrint, popPrintSmsMsg);
                popPrintToSmsProducer.sendOnFailPersistent(popPrintSmsMsg.getPackageBarcode(), JsonHelper.toJson(popPrintSmsMsg));
            }
            PopPrintResponse popPrintResponse = new PopPrintResponse(
                    JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
            BeanUtils.copyProperties(popPrint, popPrintResponse);
            return popPrintResponse;
        } catch (Exception e) {
            this.log.error("保存POP打印信息savePopPrint --> 调用服务异常：", e);
            return new PopPrintResponse(JdResponse.CODE_SERVICE_ERROR, e.getMessage());
        }
    }

    public PopPrint requestToPopPrint(PopPrintRequest request) {
        PopPrint popPrint = new PopPrint();
        popPrint.setWaybillCode(request.getWaybillCode());
        if (StringUtils.isBlank(request.getPackageBarcode())) {
            popPrint.setPackageBarcode(request.getWaybillCode());
        } else {
            popPrint.setPackageBarcode(request.getPackageBarcode());
        }
        popPrint.setCreateSiteCode(request.getOperateSiteCode());
        popPrint.setCreateSiteName(request.getOperateSiteName());
        popPrint.setCreateUserCode(request.getOperatorCode());
        popPrint.setCreateUser(request.getOperatorName());
        popPrint.setPopSupId(request.getPopSupId());
        popPrint.setPopSupName(request.getPopSupName());
        popPrint.setQuantity(request.getQuantity());
        popPrint.setWaybillType(request.getWaybillType());
        popPrint.setCrossCode(request.getCrossCode());
        popPrint.setPopReceiveType(request.getPopReceiveType());
        popPrint.setThirdWaybillCode(request.getThirdWaybillCode());
        popPrint.setQueueNo(request.getQueueNo());
        popPrint.setOperateType(request.getOperateType());
        popPrint.setBoxCode(request.getBoxCode());
        popPrint.setDriverCode(request.getDriverCode());
        popPrint.setDriverName(request.getDriverName());

        popPrint.setBusiId(request.getBusiId());
        popPrint.setBusiName(request.getBusiName());
        popPrint.setInterfaceType(request.getInterfaceType());

        popPrint.setCategoryName(request.getCategoryName());

        if (PopPrintRequest.PRINT_PACK_TYPE.equals(request.getOperateType())
                || PopPrintRequest.NOT_PRINT_PACK_TYPE.equals(request.getOperateType())) {
            popPrint.setPrintPackCode(request.getOperatorCode());
            popPrint.setPrintPackTime(DateHelper.getSeverTime(request.getOperateTime()));
            popPrint.setPrintPackUser(request.getOperatorName());
        } else if (PopPrintRequest.PRINT_INVOICE_TYPE.equals(request.getOperateType())) {
            popPrint.setPrintInvoiceCode(request.getOperatorCode());
            popPrint.setPrintInvoiceTime(DateHelper.getSeverTime(request.getOperateTime()));
            popPrint.setPrintInvoiceUser(request.getOperatorName());
        } else {
            throw new RuntimeException("保存POP打印信息 --> 传入操作类型有误：" + request.getOperateType() + ", 操作人：" + request.getOperatorCode() + ", 操作时间：" + request.getOperateTime());
        }
        return popPrint;
    }

    private void savePopPrintToRedis(PopPrint popPrint) {
        Long result = redisManager.rpush(
                CacheKeyConstants.POP_PRINT_BACKUP_KEY, JsonUtil.getInstance()
                        .object2Json(popPrint));
        if (result < 0) {
            log.warn("savePopPrintToRedis failed:{}", JsonHelper.toJson(popPrint));
        }
    }

    private void toTask(PopPrintRequest req,Integer operateType,String remark,Date date){
        WaybillStatus waybillStatus = new WaybillStatus();
        waybillStatus.setPackageCode(req.getPackageBarcode());
        waybillStatus.setWaybillCode(req.getWaybillCode());
        waybillStatus.setCreateSiteCode(req.getOperateSiteCode());
        waybillStatus.setCreateSiteName(req.getOperateSiteName());
        waybillStatus.setOperatorId(req.getOperatorCode());
        waybillStatus.setOperator(req.getOperatorName());
        waybillStatus.setOperateType(operateType);
        waybillStatus.setRemark(remark);
        waybillStatus.setOperateTime(date);
        String body = JsonHelper.toJson(waybillStatus);
        Task task = new Task();
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(remark);
        task.setKeyword2(operateType.toString());
        task.setCreateSiteCode(req.getOperateSiteCode());
        task.setBody(body);
        task.setOwnSign(BusinessHelper.getOwnSign());
        taskService.add(task,true);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int updateByWaybillCode(PopPrint popPrint) {
        return popPrintDao.updateByWaybillCode(popPrint);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int updateByWaybillOrPack(PopPrint popPrint) {
        return this.popPrintDao.updateByWaybillOrPack(popPrint);
    }

    @Override
    public List<PopPrint> findLimitListNoReceive(List<PopPrint> popList, Map<String, Object> paramMap) {
        List<PopPrint> target = new LinkedList<PopPrint>();
        if (popList != null && !popList.isEmpty()) {
            for (PopPrint popPrint : popList) {
                // 优化拆分表和非拆分表查询语句加入的代码
                String ownSign = (String) paramMap.get("ownSign");
                Inspection inspection = new Inspection();
                inspection.setCreateSiteCode(popPrint.getCreateSiteCode());
                inspection.setWaybillCode(popPrint.getWaybillCode());
                inspection.setPackageBarcode(popPrint.getPackageBarcode());
                if ("PRE".equals(ownSign)) {
                    inspection.setInspectionType(60);
                } else {
                    inspection.setInspectionType(40);
                }
                Integer inspectionList = inspectionDao.queryCountByCondition(inspection);
                if (null != inspectionList && inspectionList > 0) {
                    //popList.remove(popPrint);
                    continue;
                }


                //原来的代码
                inspection = new Inspection();
                inspection.setCreateSiteCode(popPrint.getCreateSiteCode());
                inspection.setWaybillCode(popPrint.getWaybillCode());
                inspection.setPackageBarcode(popPrint.getPackageBarcode());
                inspection.setInspectionType(popPrint.getPopReceiveType());
                if (inspectionDao.havePOPInspection(inspection)) {
                    //popList.remove(popPrint);///DEBUG TO DO
                    continue;
                }

                target.add(popPrint);
            }
        }
        return target;
    }

}
