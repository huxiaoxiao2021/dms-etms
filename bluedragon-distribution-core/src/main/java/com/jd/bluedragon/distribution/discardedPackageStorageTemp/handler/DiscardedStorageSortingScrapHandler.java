package com.jd.bluedragon.distribution.discardedPackageStorageTemp.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.OperateUser;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.ScanDiscardedPackagePo;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedPackageStorageTempQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedStorageContext;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.DiscardedPackageSiteDepartTypeEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedWaybillStorageTemp;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 分拣弃件废弃处理
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021年12月06日11:28:19 周一
 */
@Service("discardedStorageSortingScrapHandler")
public class DiscardedStorageSortingScrapHandler extends DiscardedStorageAbstractHandler {

    @Qualifier("bdBlockerCompleteMQ")
    @Autowired
    private DefaultJMQProducer bdBlockerCompleteMQ;

    @Autowired
    private TaskService taskService;

    /**
     * 暂存弃件处理
     * @param context 上下文
     * @return 处理结果
     * @author fanggang7
     * @time 2021-12-06 11:17:30 周一
     */
    @Override
    public Result<Boolean> doHandle(DiscardedStorageContext context) {
        log.info("DiscardedStorageSortingScrapHandler.doHandle param: {}", com.jd.bluedragon.utils.JsonHelper.toJson(context));

        final ScanDiscardedPackagePo scanDiscardedPackagePo = context.getScanDiscardedPackagePo();
        Result<Boolean> result = Result.success();
        DiscardedPackageStorageTempQo discardedPackageStorageTempQo = new DiscardedPackageStorageTempQo();
        discardedPackageStorageTempQo.setWaybillCode(context.getBigWaybillDto().getWaybill().getWaybillCode());
        discardedPackageStorageTempQo.setYn(Constants.YN_YES);
        // 得到现有已扫描数
        long scanPackageTotal = discardedPackageStorageTempDao.selectCount(discardedPackageStorageTempQo);

        // 允许重复扫描运单，第一次扫描插入，第二次更新
        List<DiscardedPackageStorageTemp> discardedPackageStorageTempList = buildInsertPackageList(context.getBigWaybillDto(), context.getCurrentSiteInfo(), scanDiscardedPackagePo);
        DiscardedWaybillStorageTemp discardedWaybillStorageTemp = this.buildInsertWaybill(context.getBigWaybillDto(), context.getCurrentSiteInfo(), scanDiscardedPackagePo);
        int dbRes = -1;
        if(scanPackageTotal == 0){
            discardedWaybillStorageTemp.setPackageScanTotal((int)scanPackageTotal + 1);
            dbRes = this.insertDiscardedWaybillAndPackageRecord(discardedWaybillStorageTemp, discardedPackageStorageTempList);
        } else {
            discardedPackageStorageTempQo.setPackageCode(scanDiscardedPackagePo.getBarCode());
            final DiscardedPackageStorageTemp discardedPackageStorageTempExist = discardedPackageStorageTempDao.selectOne(discardedPackageStorageTempQo);
            // 判断包裹是否重复扫描
            boolean isUpdate = discardedPackageStorageTempExist != null;
            if(!isUpdate){
                discardedWaybillStorageTemp.setPackageScanTotal((int)scanPackageTotal + 1);
                dbRes = this.updateDiscardedWaybillAndInsertPackageRecord(discardedWaybillStorageTemp, discardedPackageStorageTempList);
            } else {
                dbRes = this.updateDiscardedWaybillAndPackageRecord(discardedWaybillStorageTemp, discardedPackageStorageTempList);
            }
        }
        return result;
    }

    /**
     * 组装待插入包裹数据
     * @param bigWaybillDto 运单数据
     * @param siteDto 场地信息
     * @return 组装结果
     */
    private DiscardedWaybillStorageTemp buildInsertWaybill(BigWaybillDto bigWaybillDto, BaseStaffSiteOrgDto siteDto, ScanDiscardedPackagePo paramObj) {
        final List<DeliveryPackageD> packageList = bigWaybillDto.getPackageList();
        DiscardedPackageStorageTemp discardedPackageStorageTemp = buildDiscardedPackageStorageTemp(bigWaybillDto, siteDto, paramObj, packageList.get(0).getPackageBarcode());
        final DiscardedWaybillStorageTemp discardedWaybillStorageTemp = new DiscardedWaybillStorageTemp();
        BeanUtils.copyProperties(discardedPackageStorageTemp, discardedWaybillStorageTemp);
        // 设置额外信息
        discardedWaybillStorageTemp.setPackageSysTotal(bigWaybillDto.getWaybill().getGoodNumber());
        discardedWaybillStorageTemp.setSiteDepartType(DiscardedPackageSiteDepartTypeEnum.TRANSFER.getCode());
        discardedWaybillStorageTemp.setSubmitStatus(Constants.YN_NO);
        return discardedWaybillStorageTemp;
    }

    /**
     * 组装待插入包裹数据
     * @param bigWaybillDto 运单数据
     * @param siteDto 场地信息
     * @return 组装结果
     */
    private List<DiscardedPackageStorageTemp> buildInsertPackageList(BigWaybillDto bigWaybillDto, BaseStaffSiteOrgDto siteDto, ScanDiscardedPackagePo paramObj) {
        List<DiscardedPackageStorageTemp> discardedPackageStorageTempList = new ArrayList<>();

        DiscardedPackageStorageTemp discardedPackageStorageTemp = buildDiscardedPackageStorageTemp(bigWaybillDto, siteDto, paramObj, paramObj.getBarCode());
        discardedPackageStorageTemp.setCreateTime(new Date());
        discardedPackageStorageTempList.add(discardedPackageStorageTemp);

        return discardedPackageStorageTempList;
    }

    /**
     * 废弃操作转成全程跟踪任务对象
     * @param request
     * @return0
     */
    private Task toWasteScrapTraceTask(ScanDiscardedPackagePo request, String waybillCode) {
        WaybillStatus waybillStatus = new WaybillStatus();
        //设置站点相关属性
        waybillStatus.setPackageCode(request.getBarCode());
        waybillStatus.setWaybillCode(waybillCode);
        final OperateUser operateUser = request.getOperateUser();
        waybillStatus.setCreateSiteCode(operateUser.getSiteCode());
        waybillStatus.setCreateSiteName(operateUser.getSiteName());
        waybillStatus.setOperatorId(operateUser.getUserId() != null ? operateUser.getUserId().intValue() : 0);
        waybillStatus.setOperator(operateUser.getUserName());
        waybillStatus.setOperateTime(new Date());
        waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_WASTE_SCRAP);
        waybillStatus.setRemark(WaybillStatus.WAYBILL_TRACK_WASTE_SCRAP_MSG);

        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_WAYBILL);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(waybillStatus.getPackageCode());
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setCreateSiteCode(waybillStatus.getCreateSiteCode());
        task.setBody(com.jd.bluedragon.utils.JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        task.setFingerprint(Md5Helper.encode(waybillStatus.getCreateSiteCode() + "_"
                + waybillStatus.getPackageCode() + "-" + waybillStatus.getOperateType() + "-" + waybillStatus.getOperateTime().getTime()));
        return task;
    }

    /**
     * 各子处理器后置处理钩子
     * @param context 上下文
     * @return 处理结果
     * @author fanggang7
     * @time 2021-12-06 15:06:37 周一
     */
    @Override
    protected Result<Boolean> doHandleAfter(DiscardedStorageContext context) {
        // 发送全程跟踪消息
        try {
            String waybillCode = WaybillUtil.getWaybillCode(context.getScanDiscardedPackagePo().getBarCode());
            taskService.add(genScrapTraceTask(context.getScanDiscardedPackagePo(), waybillCode));
            // 发送bd_blocker_complete的MQ
            String sendPay = context.getBigWaybillDto().getWaybill().getSendPay();
            if (BusinessUtil.isSx(sendPay)) {
                String mqData = BusinessUtil.bdBlockerCompleteMQ(waybillCode, DmsConstants.ORDER_TYPE_REVERSE, DmsConstants.MESSAGE_TYPE_BAOFEI, DateHelper.formatDateTimeMs(new Date()));
                this.bdBlockerCompleteMQ.send(waybillCode, mqData);
            }
        } catch (Exception e) {
            log.error("DiscardedStorageSortingScrapHandler.doHandleAfter exception param {} exception {}", JsonHelper.toJson(context.getScanDiscardedPackagePo()), e.getMessage(), e);
            return Result.fail("分拣废弃后置处理失败，请稍后重试");
        }
        return Result.success();
    }

    /**
     * 分拣废弃操作转成全程跟踪任务对象
     * @return Task
     */
    private Task genScrapTraceTask(ScanDiscardedPackagePo scanDiscardedPackagePo, String waybillCode) {
        WaybillStatus waybillStatus = this.genScrapWaybillStatus(scanDiscardedPackagePo, waybillCode);

        return this.genTraceTaskCommon(waybillStatus);
    }
}
