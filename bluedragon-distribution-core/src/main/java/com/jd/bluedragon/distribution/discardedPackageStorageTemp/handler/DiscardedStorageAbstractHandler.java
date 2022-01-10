package com.jd.bluedragon.distribution.discardedPackageStorageTemp.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.OperateUser;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.ScanDiscardedPackagePo;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao.DiscardedPackageStorageTempDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao.DiscardedWaybillStorageTempDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedPackageStorageMq;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedStorageContext;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.WasteOperateTypeEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedWaybillStorageTemp;
import com.jd.bluedragon.distribution.record.entity.DmsHasnoPresiteWaybillMq;
import com.jd.bluedragon.distribution.record.enums.DmsHasnoPresiteWaybillMqOperateEnum;
import com.jd.bluedragon.distribution.record.service.WaybillHasnoPresiteRecordService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 弃件抽象处理类
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-06 11:15:38 周一
 */
@Service
public abstract class DiscardedStorageAbstractHandler implements DiscardedStorageHandler {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected DiscardedPackageStorageTempDao discardedPackageStorageTempDao;

    @Autowired
    protected DiscardedWaybillStorageTempDao discardedWaybillStorageTempDao;

    @Autowired
    protected WaybillTraceManager waybillTraceManager;

    @Autowired
    protected WaybillQueryManager waybillQueryManager;

    @Autowired
    protected BaseMajorManager baseMajorManager;

    @Autowired
    protected TaskService taskService;

    @Autowired
    private DefaultJMQProducer discardedPackageStorageProducer;
    @Autowired
    private WaybillHasnoPresiteRecordService waybillHasnoPresiteRecordService;
    /**
     * 暂存弃件处理
     * @param context 上下文
     * @return 处理结果
     * @author fanggang7
     * @time 2021-12-06 11:17:30 周一
     */
    @Override
    public Result<Boolean> handle(DiscardedStorageContext context) {
        Result<Boolean> result = Result.success();

        final Result<Boolean> handleResult = doHandle(context);
        if(!handleResult.isSuccess()){
            return handleResult;
        }
        handleAfter(context);
        return result;
    }

    protected abstract Result<Boolean> doHandle(DiscardedStorageContext context);

    /**
     * 后置处理器
     * @param context 上下文
     */
    protected void handleAfter(DiscardedStorageContext context){
        if(Objects.equals(WasteOperateTypeEnum.STORAGE.getCode(), context.getScanDiscardedPackagePo().getOperateType())){
            // 暂存动作发送暂存mq
            this.sendDiscardedPackageStorageMq(context);
        }
        doHandleAfter(context);
        //弃件操作,发送运单变更mq
        sendDmsHasnoPresiteWaybillMq(context);
    }
    /**
     * 弃件操作,发送运单变更mq
     * @param request
     */
    private void sendDmsHasnoPresiteWaybillMq(DiscardedStorageContext context) {
    	ScanDiscardedPackagePo request = context.getScanDiscardedPackagePo();
    	DmsHasnoPresiteWaybillMq dmsHasnoPresiteWaybillMq = new DmsHasnoPresiteWaybillMq();
    	dmsHasnoPresiteWaybillMq.setWaybillCode(context.getWaybillCode());
    	dmsHasnoPresiteWaybillMq.setOperateCode(DmsHasnoPresiteWaybillMqOperateEnum.WASTE.getCode());
    	if(request.getOperateUser() != null) {
        	dmsHasnoPresiteWaybillMq.setOperateUserErp(request.getOperateUser().getUserCode());
        	dmsHasnoPresiteWaybillMq.setOperateUserName(request.getOperateUser().getUserName());
        	dmsHasnoPresiteWaybillMq.setOperateSiteCode(request.getOperateUser().getSiteCode());
    	}
    	dmsHasnoPresiteWaybillMq.setOperateTime(new Date());
    	waybillHasnoPresiteRecordService.sendDataChangeMq(dmsHasnoPresiteWaybillMq);
    }
    /**
     * 各子处理器后置处理钩子
     * @param context 上下文
     * @return 处理结果
     * @author fanggang7
     * @time 2021-12-06 15:06:37 周一
     */
    protected abstract Result<Boolean> doHandleAfter(DiscardedStorageContext context);

    /**
     * 插入运单和包裹
     */
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED)
    protected Integer insertDiscardedWaybillAndPackageRecord(DiscardedWaybillStorageTemp discardedWaybillStorageTemp, List<DiscardedPackageStorageTemp> discardedPackageStorageTempList) {
        int count = discardedWaybillStorageTempDao.insertSelective(discardedWaybillStorageTemp);
        discardedPackageStorageTempDao.batchInsert(discardedPackageStorageTempList);
        return count;
    }

    /**
     * 更新运单，插入包裹
     */
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED)
    protected Integer updateDiscardedWaybillAndInsertPackageRecord(DiscardedWaybillStorageTemp discardedWaybillStorageTemp, List<DiscardedPackageStorageTemp> discardedPackageStorageTempList) {
        int count = discardedWaybillStorageTempDao.updateByWaybillCode(discardedWaybillStorageTemp);
        discardedPackageStorageTempDao.batchInsert(discardedPackageStorageTempList);
        return count;
    }

    /**
     * 更新运单和所有包裹
     */
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED)
    protected Integer updateDiscardedWaybillAndAllPackageRecord(DiscardedWaybillStorageTemp discardedWaybillStorageTemp, List<DiscardedPackageStorageTemp> discardedPackageStorageTempList) {
        int count = discardedWaybillStorageTempDao.updateByWaybillCode(discardedWaybillStorageTemp);
        if(CollectionUtils.isNotEmpty(discardedPackageStorageTempList)){
            discardedPackageStorageTempDao.updateByWaybillCode(discardedPackageStorageTempList.get(0));
        }
        return count;
    }

    /**
     * 更新运单和包裹
     */
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED)
    protected Integer updateDiscardedWaybillAndOnePackageRecord(DiscardedWaybillStorageTemp discardedWaybillStorageTemp, List<DiscardedPackageStorageTemp> discardedPackageStorageTempList) {
        int count = discardedWaybillStorageTempDao.updateByWaybillCode(discardedWaybillStorageTemp);
        if(CollectionUtils.isNotEmpty(discardedPackageStorageTempList)){
            discardedPackageStorageTempDao.updateByPackageCode(discardedPackageStorageTempList.get(0));
        }
        return count;
    }

    /**
     * 组装数据库对象
     * @param bigWaybillDto 运单数据
     * @param siteDto 场地数据
     * @param paramObj 原始请求参数
     * @param packageCode 包裹号
     * @return 弃件包裹纬度数据
     * @author fanggang7
     * @time 2021-12-06 14:01:53 周一
     */
    protected DiscardedPackageStorageTemp buildDiscardedPackageStorageTemp(BigWaybillDto bigWaybillDto, BaseStaffSiteOrgDto siteDto, ScanDiscardedPackagePo paramObj, String packageCode) {
        DiscardedPackageStorageTemp discardedPackageStorageTemp = new DiscardedPackageStorageTemp();
        discardedPackageStorageTemp.setSiteDepartType(paramObj.getSiteDepartType());
        Waybill waybillInfo = bigWaybillDto.getWaybill();
        discardedPackageStorageTemp.setWaybillCode(waybillInfo.getWaybillCode());
        discardedPackageStorageTemp.setPackageCode(packageCode);
        discardedPackageStorageTemp.setOperateType(paramObj.getOperateType());
        discardedPackageStorageTemp.setWaybillType(paramObj.getWaybillType());
        discardedPackageStorageTemp.setStatus(paramObj.getStatus());
        discardedPackageStorageTemp.setWaybillProduct(waybillQueryManager.getTransportMode(waybillInfo));
        String consignmentName = waybillQueryManager.getConsignmentNameByWaybillDto(bigWaybillDto);
        //consignmentName 超过30位截取
        consignmentName = StringHelper.substring(consignmentName, 0, 30);
        discardedPackageStorageTemp.setConsignmentName(consignmentName);
        discardedPackageStorageTemp.setWeight(BigDecimal.valueOf(waybillInfo.getGoodWeight()));
        if (waybillInfo.getPayment() != null && (waybillInfo.getPayment() == 1 || waybillInfo.getPayment() == 3)) {
            discardedPackageStorageTemp.setCod(Constants.YN_YES);
        } else {
            discardedPackageStorageTemp.setCod(Constants.YN_NO);
        }
        String codMoney = waybillInfo.getCodMoney();
        if (codMoney != null) {
            discardedPackageStorageTemp.setCodAmount(codMoney);
        }
        discardedPackageStorageTemp.setBusinessCode(String.valueOf(waybillInfo.getBusiId()));
        discardedPackageStorageTemp.setBusinessName(waybillInfo.getBusiName());
        final OperateUser operateUser = paramObj.getOperateUser();
        discardedPackageStorageTemp.setOperatorCode(operateUser.getUserId());
        discardedPackageStorageTemp.setOperatorName(operateUser.getUserName());
        discardedPackageStorageTemp.setOperatorErp(operateUser.getUserCode());
        discardedPackageStorageTemp.setSiteCode(operateUser.getSiteCode());
        discardedPackageStorageTemp.setSiteName(operateUser.getSiteName());
        discardedPackageStorageTemp.setSiteCity(siteDto.getCityName());
        discardedPackageStorageTemp.setOrgCode(siteDto.getOrgId());
        discardedPackageStorageTemp.setOrgName(siteDto.getOrgName());
        Integer prevSiteCode = getPreSiteCode(packageCode, operateUser.getSiteCode());
        log.info("DiscardedStorageAbstractHandler.buildDiscardedPackageStorageTemp prevSiteCode {}", prevSiteCode);
        discardedPackageStorageTemp.setPrevSiteCode(prevSiteCode);
        if (prevSiteCode != null) {
            BaseStaffSiteOrgDto prevSiteDto = baseMajorManager.getBaseSiteBySiteId(prevSiteCode);
            log.info("DiscardedStorageAbstractHandler.buildDiscardedPackageStorageTemp prevSiteDto {}", JsonHelper.toJson(prevSiteDto));
            if (prevSiteDto != null) {
                discardedPackageStorageTemp.setPrevSiteName(prevSiteDto.getSiteName());
                discardedPackageStorageTemp.setPrevProvinceCompanyCode(prevSiteDto.getProvinceCompanyCode());
                discardedPackageStorageTemp.setPrevProvinceCompanyName(prevSiteDto.getProvinceCompanyName());
            }
        }
        discardedPackageStorageTemp.setUpdateTime(new Date(paramObj.getOperateTime()));
        return discardedPackageStorageTemp;
    }

    /**
     * 获取上游操作场地
     * @param packageCode 包裹号
     * @param currentSiteCode 当前操作场地
     * @return 上游场地
     * @author fanggang7
     * @time 2021-12-02 16:37:43 周四
     */
    protected Integer getPreSiteCode(String packageCode, Integer currentSiteCode) {
        Integer preSiteCode = null;

        //查全程跟踪
        BaseEntity<List<PackageState>> waybillTrackResult = waybillTraceManager.getPkStateByPCode(packageCode);
        // 解析全程跟踪数据
        if (waybillTrackResult != null && waybillTrackResult.getData() != null && waybillTrackResult.getData().size() > 0) {
            List<PackageState> packageStateList = waybillTrackResult.getData();
            // 按全程跟踪的创建时间降序排列排序
            Collections.sort(packageStateList, new Comparator<PackageState>() {
                @Override
                public int compare(PackageState v1, PackageState v2) {
                    return v2.getCreateTime().compareTo(v1.getCreateTime());
                }
            });
            // 遍历，找到当前分拣中心之前的操作数据
            boolean findCurrentSiteOpLog = false;
            for (PackageState packageState : packageStateList) {
                if (Objects.equals(packageState.getOperatorSiteId(), currentSiteCode)) {
                    //  找到当前分拣中心的操作记录，往后就是上游节点的数据
                    findCurrentSiteOpLog = true;
                }
                if (findCurrentSiteOpLog && !Objects.equals(packageState.getOperatorSiteId(), currentSiteCode)) {
                    preSiteCode = packageState.getOperatorSiteId();
                    break;
                }
            }
            // 未出现当前场地，则为最后一个场地
            if(!findCurrentSiteOpLog){
                preSiteCode = packageStateList.get(0).getOperatorSiteId();
            }
        }

        return preSiteCode;
    }

    /**
     * 构建全程跟踪对象
     * @param request 请求参数
     * @return 全程跟踪对象
     */
    protected BdTraceDto genPackagePrintBdTraceDto(ScanDiscardedPackagePo request) {
        BdTraceDto bdTraceDto = new BdTraceDto();
        bdTraceDto.setWaybillCode(request.getBarCode());
        bdTraceDto.setOperateType(WaybillStatus.WAYBILL_TRACK_WASTE_WAYBILL);
        bdTraceDto.setOperatorDesp(WaybillStatus.WAYBILL_TRACK_WASTE_WAYBILL_MSG);
        final OperateUser operateUser = request.getOperateUser();
        bdTraceDto.setOperatorSiteId(operateUser.getSiteCode());
        bdTraceDto.setOperatorSiteName(operateUser.getSiteName());
        bdTraceDto.setOperatorUserName(operateUser.getUserName());
        bdTraceDto.setOperatorUserId(operateUser.getUserId() != null ? operateUser.getUserId().intValue() : 0);
        bdTraceDto.setOperatorTime(new Date());
        return bdTraceDto;
    }

    protected WaybillStatus genWaybillStatusCommon(ScanDiscardedPackagePo scanDiscardedPackagePo, String waybillCode) {
        WaybillStatus waybillStatus = new WaybillStatus();
        waybillStatus.setWaybillCode(waybillCode);
        //设置站点相关属性
        final OperateUser operateUser = scanDiscardedPackagePo.getOperateUser();
        waybillStatus.setCreateSiteCode(operateUser.getSiteCode());
        waybillStatus.setCreateSiteName(operateUser.getSiteName());
        waybillStatus.setOperatorId(operateUser.getUserId() != null ? operateUser.getUserId().intValue() : 0);
        waybillStatus.setOperator(operateUser.getUserName());
        waybillStatus.setOperateTime(new Date(scanDiscardedPackagePo.getOperateTime()));
        return waybillStatus;
    }

    protected WaybillStatus genTempStorageWaybillStatus(ScanDiscardedPackagePo scanDiscardedPackagePo, String waybillCode) {
        WaybillStatus waybillStatus = this.genWaybillStatusCommon(scanDiscardedPackagePo, waybillCode);
        waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_WASTE_WAYBILL);
        waybillStatus.setRemark(WaybillStatus.WAYBILL_TRACK_WASTE_WAYBILL_MSG);
        return waybillStatus;
    }

    protected WaybillStatus genScrapWaybillStatus(ScanDiscardedPackagePo scanDiscardedPackagePo, String waybillCode) {
        WaybillStatus waybillStatus = this.genWaybillStatusCommon(scanDiscardedPackagePo, waybillCode);
        waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_WASTE_SCRAP);
        waybillStatus.setRemark(WaybillStatus.WAYBILL_TRACK_WASTE_SCRAP_MSG);
        return waybillStatus;
    }

    protected Task genTempStorageTask(WaybillStatus waybillStatus) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(StringUtils.isNoneBlank(waybillStatus.getPackageCode()) ? waybillStatus.getPackageCode() : waybillStatus.getWaybillCode());
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setCreateSiteCode(waybillStatus.getCreateSiteCode());
        task.setBody(com.jd.bluedragon.utils.JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        task.setFingerprint(Md5Helper.encode(waybillStatus.getCreateSiteCode() + "_"
                + waybillStatus.getWaybillCode() + "-" + waybillStatus.getOperateType() + "-" + waybillStatus.getOperateTime().getTime()));
        return task;
    }

    protected Task genScrapTask(WaybillStatus waybillStatus) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_WAYBILL);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(StringUtils.isNoneBlank(waybillStatus.getPackageCode()) ? waybillStatus.getPackageCode() : waybillStatus.getWaybillCode());
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setCreateSiteCode(waybillStatus.getCreateSiteCode());
        task.setBody(com.jd.bluedragon.utils.JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        task.setFingerprint(Md5Helper.encode(waybillStatus.getCreateSiteCode() + "_"
                + waybillStatus.getWaybillCode() + "-" + waybillStatus.getOperateType() + "-" + waybillStatus.getOperateTime().getTime()));
        return task;
    }

    /**
     * 弃件暂存动作发出mq
     * @author fanggang7
     * @time 2021-12-09 11:15:55 周四
     */
    protected boolean sendDiscardedPackageStorageMq(DiscardedStorageContext context){
        try {
            final List<DiscardedPackageStorageMq> discardedPackageStorageMqs = this.genDiscardedPackageStorageMqList(context);
            if(CollectionUtils.isEmpty(discardedPackageStorageMqs)){
                log.warn("DiscardedStorageAbstractHandler.sendTempStorageMq empty msg ");
                return false;
            }
            List<Message> messageList = new ArrayList<>();
            for (DiscardedPackageStorageMq discardedPackageStorageMq : discardedPackageStorageMqs) {
                Message message = new Message();
                message.setBusinessId(discardedPackageStorageMq.getPackageCode());
                message.setText(JsonHelper.toJson(discardedPackageStorageMq));
                log.info("sendDiscardedPackageStorageMq topic: {} , text: {}", discardedPackageStorageProducer.getTopic(), message.getText());
                message.setTopic(discardedPackageStorageProducer.getTopic());
                messageList.add(message);
            }
            discardedPackageStorageProducer.batchSend(messageList);
        } catch (JMQException e) {
            log.error("DiscardedStorageAbstractHandler.sendDiscardedPackageStorageMq exception ", e);
            return false;
        }
        return true;
    }

    /**
     * 组装弃件暂存mq报文
     * @author fanggang7
     * @time 2021-12-09 11:15:55 周四
     */
    protected List<DiscardedPackageStorageMq> genDiscardedPackageStorageMqList(DiscardedStorageContext context){
        List<DiscardedPackageStorageMq> discardedPackageStorageMqList = new ArrayList<>();
        final ScanDiscardedPackagePo scanDiscardedPackagePo = context.getScanDiscardedPackagePo();
        if(WaybillUtil.isWaybillCode(scanDiscardedPackagePo.getBarCode())){
            for (DeliveryPackageD packageD : context.getBigWaybillDto().getPackageList()) {
                final DiscardedPackageStorageMq discardedPackageStorageMq = this.genDiscardedPackageStorageMq(context.getScanDiscardedPackagePo());
                discardedPackageStorageMq.setPackageCode(packageD.getPackageBarcode());
                discardedPackageStorageMqList.add(discardedPackageStorageMq);
            }
        } else {
            DiscardedPackageStorageMq discardedPackageStorageMq = this.genDiscardedPackageStorageMq(context.getScanDiscardedPackagePo());
            discardedPackageStorageMqList.add(discardedPackageStorageMq);
        }
        return discardedPackageStorageMqList;
    }

    /**
     * 组装弃件暂存mq报文
     * @author fanggang7
     * @time 2021-12-09 11:15:55 周四
     */
    protected DiscardedPackageStorageMq genDiscardedPackageStorageMq(ScanDiscardedPackagePo scanDiscardedPackagePo){
        final DiscardedPackageStorageMq discardedPackageStorageMq = new DiscardedPackageStorageMq();
        discardedPackageStorageMq.setPackageCode(scanDiscardedPackagePo.getBarCode());
        discardedPackageStorageMq.setWaybillCode(WaybillUtil.getWaybillCode(scanDiscardedPackagePo.getBarCode()));
        discardedPackageStorageMq.setSiteDepartType(scanDiscardedPackagePo.getSiteDepartType());
        discardedPackageStorageMq.setWaybillType(scanDiscardedPackagePo.getWaybillType());
        discardedPackageStorageMq.setStorageStatus(scanDiscardedPackagePo.getStatus());
        final OperateUser operateUser = scanDiscardedPackagePo.getOperateUser();
        discardedPackageStorageMq.setOperateUserErp(operateUser.getUserCode());
        discardedPackageStorageMq.setOperateUserName(operateUser.getUserName());
        discardedPackageStorageMq.setOperateSiteCode(operateUser.getSiteCode());
        discardedPackageStorageMq.setOperateUserName(operateUser.getSiteName());
        discardedPackageStorageMq.setOperateTimeMillSeconds(scanDiscardedPackagePo.getOperateTime());
        return discardedPackageStorageMq;
    }
}
