package com.jd.bluedragon.distribution.discardedPackageStorageTemp.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.ScanDiscardedPackagePo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedPackageStorageTempQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedStorageContext;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.DiscardedPackageSiteDepartTypeEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedWaybillStorageTemp;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 转运弃件暂存处理
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021年12月06日11:28:19 周一
 */
@Service("discardedStorageTransferTempStorageHandler")
public class DiscardedStorageTransferTempStorageHandler extends DiscardedStorageAbstractHandler {

    /**
     * 暂存弃件处理
     * @param context 上下文
     * @return 处理结果
     * @author fanggang7
     * @time 2021-12-06 11:17:30 周一
     */
    @Override
    public Result<Boolean> doHandle(DiscardedStorageContext context) {
        log.info("DiscardedStorageSortingTempStorageHandler.doHandle param: {}", JsonHelper.toJson(context));

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

        if (dbRes < 0) {
            return result.toFail("弃件暂存失败，存储数据出现错误");
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
        discardedPackageStorageTemp.setCreateTime(new Date(paramObj.getOperateTime()));
        discardedPackageStorageTempList.add(discardedPackageStorageTemp);

        return discardedPackageStorageTempList;
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
        try {
            final String waybillCode = context.getBigWaybillDto().getWaybill().getWaybillCode();
            log.info("发送弃件废弃全程跟踪，运单号：{}", waybillCode);
            taskService.add(genTempStorageTraceTask(context.getScanDiscardedPackagePo(), waybillCode));
        } catch (Exception e) {
            log.error("DiscardedStorageSortingTempStorageHandler.doHandleAfter exception param {} exception {}", com.jd.bluedragon.distribution.api.utils.JsonHelper.toJson(context.getScanDiscardedPackagePo()), e.getMessage(), e);
            return Result.fail("分拣废弃后置处理失败，请稍后重试");
        }
        return Result.success();
    }

    /**
     * 转运暂存操作转成全程跟踪任务对象
     * @return Task
     */
    private Task genTempStorageTraceTask(ScanDiscardedPackagePo scanDiscardedPackagePo, String waybillCode) {
        WaybillStatus waybillStatus = this.genTempStorageWaybillStatus(scanDiscardedPackagePo, waybillCode);
        waybillStatus.setPackageCode(scanDiscardedPackagePo.getBarCode());

        return this.genTempStorageTask(waybillStatus);
    }
}
