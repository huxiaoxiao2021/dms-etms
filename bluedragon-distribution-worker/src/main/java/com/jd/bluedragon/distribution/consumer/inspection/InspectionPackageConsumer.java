package com.jd.bluedragon.distribution.consumer.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.coldchain.dto.CCInAndOutBoundMessage;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainOperateTypeEnum;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionPackageMQ;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.distribution.sorting.domain.SortingDto;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ump.UmpMonitorHandler;
import com.jd.bluedragon.utils.ump.UmpMonitorHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.jd.bluedragon.Constants.*;
import static com.jd.bluedragon.Constants.Numbers.INTEGER_ZERO;
import static com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill.SEPARATOR_APPEND;
import static com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum.ELECTRONIC_FENCE;
import static com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum.ELECTRONIC_GATEWAY;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_TWO;

/**
 * @ClassName InspectionPackageConsumer
 * @Description
 * @Author wyh
 * @Date 2020/11/10 22:49
 **/
@Service("inspectionPackageConsumer")
public class InspectionPackageConsumer extends MessageBaseConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InspectionPackageConsumer.class);

    @Resource( name = "storeIdSet")
    private Set<Integer> storeIdSet;

    @Autowired
    @Qualifier("ccInAndOutBoundProducer")
    private DefaultJMQProducer ccInAndOutBoundProducer;

    @Autowired
    private BaseService baseService;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private CenConfirmService cenConfirmService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private BoxService boxService;

    public static final String  OPERATE_DESC_KEY = "operateDesc";

    public static final String  ELE_FENCE = "eleFence";

    public static final String  ELE_GATEWAY = "eleGateway";


    @Override
    public void consume(Message message) throws Exception {

        try {

            final InspectionPackageMQ packageBody = JsonHelper.fromJsonMs(message.getText(), InspectionPackageMQ.class);

            String umpKey = "DmsWorker.jmq.consumer.InspectionPackageConsumer.consume";

            UmpMonitorHelper.doWithUmpMonitor(umpKey, Constants.UMP_APP_NAME_DMSWORKER, new UmpMonitorHandler() {
                @Override
                public void process() {

                    doPackageInspection(packageBody);

                }
            });
        }
        catch (Exception ex) {
            LOGGER.error("消费包裹验货消息失败. bizId:{}", message.getBusinessId(), ex);
        }

    }

    /**
     *
     * @param packageMQ
     * @return
     */
    private boolean doPackageInspection(InspectionPackageMQ packageMQ) {

        if (!inspectionService.siteEnableInspectionAgg(packageMQ.getOperateSiteCode())) {

            return false;
        }

        try {

            Inspection record = this.genUniqInspection(packageMQ);

            // 记录OP LOG
            saveInspectionOpLog(record);

            // 处理三方验货
            thirdInspectionDiff(record);

            // OEM推送WMS
            doOmeToWms(record);

            // 发送验货全流程跟踪
            sendInspectionWaybillTrack(record, packageMQ);

            // 推送冷链验货消息
            pushColdChainOperateMQ(record);

        }
        catch (Exception ex) {
            LOGGER.error("处理包裹验货消息失败. body:{}", JsonHelper.toJson(packageMQ), ex);
            return false;
        }

        return true;
    }

    /**
     * 构建inspection
     * @see com.jd.bluedragon.distribution.inspection.domain.Inspection#toInspection  字段含义与此保持一致
     * @param packageMQ
     * @return
     */
    private Inspection genUniqInspection(InspectionPackageMQ packageMQ) {
        Inspection inspection = new Inspection();
        inspection.setWaybillCode(WaybillUtil.getWaybillCode(packageMQ.getPackageCode()));
        inspection.setPackageBarcode(packageMQ.getPackageCode());
        inspection.setBoxCode(packageMQ.getBoxCode());
        inspection.setCreateSiteCode(packageMQ.getOperateSiteCode());
        inspection.setReceiveSiteCode(packageMQ.getReceiveSiteCode());
        inspection.setInspectionType(packageMQ.getInspectionType());
        inspection.setOperateType(packageMQ.getOperateType());
        inspection.setExceptionType(packageMQ.getExceptionType());

        inspection.setCreateUser(packageMQ.getOperateUser());
        inspection.setCreateUserCode(packageMQ.getOperateUserId());
        inspection.setUpdateUser(packageMQ.getOperateUser());
        inspection.setUpdateUserCode(packageMQ.getOperateUserId());
        inspection.setOperateTime(packageMQ.getInspectionTime());
        inspection.setUpdateTime(packageMQ.getInspectionTime());
        inspection.setCreateTime(packageMQ.getRecordCreateTime());
        inspection.setOperatorData(packageMQ.getOperatorData());
        inspection.setOperatorId(packageMQ.getOperatorId());
        inspection.setOperatorTypeCode(packageMQ.getOperatorTypeCode());
        inspection.setOperateFlowId(packageMQ.getOperateFlowId());
        inspection.setBizSource(packageMQ.getBizSource());
        return inspection;
    }

    /**
     * 发送验货运单全流程跟踪
     *
     * @param record
     * @param packageMQ
     */
    private void sendInspectionWaybillTrack(Inspection record, InspectionPackageMQ packageMQ) {
        BaseStaffSiteOrgDto bDto = baseService.getSiteBySiteID(record.getCreateSiteCode());

        CenConfirm cenConfirm = cenConfirmService.commonGenCenConfirmFromInspection(record);

        String message = cenConfirmService.getTipsMessage(cenConfirm);

        BaseStaffSiteOrgDto targetDto = null;
        if (record.getReceiveSiteCode() != null && record.getReceiveSiteCode() > 0) {
            targetDto = baseService.getSiteBySiteID(record.getReceiveSiteCode());
        }
        BaseStaffSiteOrgDto rDto = null;
        if (Constants.BUSSINESS_TYPE_FC != cenConfirm.getType().intValue()) {
            rDto = targetDto;
        }
        if (bDto == null) {
            LOGGER.warn("[PackageBarcode={}]根据[siteCode={}]获取基础资料站点信息[getSiteBySiteID]返回null,不再插入{}",
                    cenConfirm.getPackageBarcode(), cenConfirm.getCreateSiteCode(), message);
        }
        else {
            WaybillStatus tWaybillStatus = cenConfirmService.createWaybillStatus(cenConfirm, bDto, rDto);
            setRemarkAndExtendParamMap(tWaybillStatus, cenConfirm, record, packageMQ);
            if (cenConfirmService.checkFormat(tWaybillStatus, cenConfirm.getType())) {
                // 添加到task表
                taskService.add(cenConfirmService.toTask(tWaybillStatus, cenConfirm.getOperateType()));
            }
            else {
                LOGGER.warn("[PackageCode={} WaybillCode={}][参数信息不全],不再插入{}",
                        tWaybillStatus.getPackageCode(), tWaybillStatus.getWaybillCode(), message);
            }

        }
    }

    private void setRemarkAndExtendParamMap(WaybillStatus tWaybillStatus, CenConfirm cenConfirm, Inspection record, InspectionPackageMQ packageMQ) {
        Map<String, Object> extendParamMap =  tWaybillStatus.getExtendParamMap();
        if (Objects.isNull(extendParamMap)) {
            extendParamMap = new HashMap<>();
        }
        if (ELECTRONIC_FENCE.getCode().equals(record.getBizSource())) {
            tWaybillStatus.setOperator(ELE_FENCE);
            extendParamMap.put(OPERATE_DESC_KEY, String.format(ELECTRONIC_FENCE_TRACE_INSPECTION_REMARK, StringUtils.isEmpty(packageMQ.getVehicleNumber())? "" : packageMQ.getVehicleNumber()));
        }else if (ELECTRONIC_GATEWAY.getCode().equals(record.getBizSource())) {
            String boxCodeStr = getBoxCodeStr(cenConfirm.getPackageBarcode());
            tWaybillStatus.setOperator(ELE_GATEWAY);
            extendParamMap.put(OPERATE_DESC_KEY, String.format(ELECTRONIC_GATEWAY_TRACE_INSPECTION_REMARK, boxCodeStr));
        }else {
            extendParamMap.put(OPERATE_DESC_KEY, String.format(TRACE_INSPECTION_REMARK, tWaybillStatus.getCreateSiteName()));
        }
        tWaybillStatus.setExtendParamMap(extendParamMap);
    }

    /**
     * 获取包裹关联箱号
     * @param packageCode
     * @return
     */
    private String getBoxCodeStr(String packageCode) {
        SortingDto sortingDto = sortingService.getLastSortingInfoByPackageCode(packageCode);
        if (sortingDto == null || org.apache.commons.lang.StringUtils.isEmpty(sortingDto.getBoxCode())) {
            return "";
        }
        StringBuilder boxCodeStr = new StringBuilder();
        Box boxQuery = new Box();
        boxQuery.setCode(sortingDto.getBoxCode());
        List<Box> boxes = boxService.listAllParentBox(boxQuery);
        if (CollectionUtils.isEmpty(boxes)) {
            for (int i = boxes.size() - 1; i >= 0; i--) {
                boxCodeStr.append(boxes.get(i).getCode()).append(SEPARATOR_APPEND);
            }
        }
        boxCodeStr.append(sortingDto.getBoxCode());
        return boxCodeStr.toString();
    }

    /**
     * 推送冷链验货消息
     * @param record
     */
    private void pushColdChainOperateMQ(Inspection record) {
        Waybill waybill = waybillService.getWaybillByWayCode(record.getWaybillCode());
        if (waybill == null) {
            return;
        }
        String waybillSign = waybill.getWaybillSign();
        if (!(BusinessUtil.isColdChainKBWaybill(waybillSign) || BusinessUtil.isColdChainCityDeliveryWaybill(waybillSign))) {
            return ;
        }
        BaseStaffSiteOrgDto siteOrgDto = baseService.getSiteBySiteID(record.getCreateSiteCode());
        CCInAndOutBoundMessage body = new CCInAndOutBoundMessage();
        body.setOrgId(String.valueOf(siteOrgDto.getOrgId()));
        body.setOrgName(siteOrgDto.getOrgName());
        body.setProvinceAgencyCode(siteOrgDto.getProvinceAgencyCode());
        body.setProvinceAgencyName(siteOrgDto.getProvinceAgencyName());
        body.setAreaHubCode(siteOrgDto.getAreaCode());
        body.setAreaHubName(siteOrgDto.getAreaName());
        body.setSiteId(String.valueOf(siteOrgDto.getSiteCode()));
        body.setSiteName(siteOrgDto.getSiteName());
        body.setOperateType(ColdChainOperateTypeEnum.INSPECTION.getType());
        Integer userCode = record.getCreateUserCode();
        if (userCode != null) {
            BaseStaffSiteOrgDto dto = baseService.getBaseStaffByStaffId(userCode);
            if (dto != null) {
                body.setOperateERP(dto.getErp());
            }
        }
        body.setOperateTime(DateHelper.formatDateTime(record.getOperateTime()));
        body.setPackageNo(record.getPackageBarcode());
        body.setWaybillNo(record.getWaybillCode());

        ccInAndOutBoundProducer.sendOnFailPersistent(body.getPackageNo(), JsonHelper.toJson(body));
    }

    /**
     * OEM推送WMS
     * @param inspection
     */
    private void doOmeToWms(Inspection inspection) {
        if (Constants.BUSSINESS_TYPE_OEM == inspection.getInspectionType()) {
            // OEM同步wms
            try {
                inspectionService.pushOEMToWMS(inspection); //FIXME:51号库推送，需要检查是否在用
            }
            catch (Exception e) {
                LOGGER.error(" 验货 inspectionCore调用OEM服务异常:{}", JsonHelper.toJson(inspection), e);
            }
        }
    }

    /**
     * 三方验货差异
     * @param inspection
     */
    private void thirdInspectionDiff(Inspection inspection) {
        if (inspection.getInspectionType().equals(Inspection.BUSSINESS_TYPE_THIRD_PARTY)) {
            inspectionService.thirdPartyWorker(inspection);
        }
    }

    /**
     * 记录操作日志
     * @param record
     */
    private void saveInspectionOpLog(Inspection record) {
        OperationLog operationLog = new OperationLog();
        operationLog.setCreateSiteCode(record.getCreateSiteCode());
        operationLog.setCreateUser(record.getCreateUser());
        operationLog.setCreateUserCode(record.getCreateUserCode());
        if ((Constants.BUSSINESS_TYPE_TRANSFER == record.getInspectionType()) || storeIdSet.contains(record.getInspectionType())) {
            operationLog.setLogType(OperationLog.LOG_TYPE_TRANSFER);
        }
        else {
            operationLog.setLogType(OperationLog.LOG_TYPE_INSPECTION);
        }
        operationLog.setPackageCode(record.getPackageBarcode());
        operationLog.setOperateTime(record.getOperateTime() == null ? new Date() : record.getOperateTime());
        operationLog.setWaybillCode(record.getWaybillCode());
        operationLog.setBoxCode(record.getBoxCode());
        operationLog.setReceiveSiteCode(record.getReceiveSiteCode());
        operationLog.setMethodName("InspectionServiceImpl#saveOpLog");
        operationLogService.add(operationLog);
    }
}
