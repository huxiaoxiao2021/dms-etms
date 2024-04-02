package com.jd.bluedragon.common.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedWaybillScanResultItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.ScanDiscardedPackagePo;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao.DiscardedWaybillStorageTempDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedStorageContext;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.UnSubmitDiscardedListQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.WasteOperateTypeEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.handler.DiscardedStorageHandlerStrategy;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import static com.jd.bluedragon.dms.utils.BusinessUtil.isScrapWaybill;

/**
 * @author pengchong28
 * @description 弃件暂存任务
 * @date 2024/3/29
 */
public class DiscardedPackageStorageTask implements Callable<Result<Boolean>> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ScanDiscardedPackagePo paramObj;

    private WaybillTraceManager waybillTraceManager;

    private WaybillQueryManager waybillQueryManager;

    private DiscardedStorageHandlerStrategy discardedStorageHandlerStrategy;

    private BaseStaffSiteOrgDto siteDto;


    private final int maxScanSize = 100;

    public DiscardedPackageStorageTask(ScanDiscardedPackagePo paramObj, WaybillTraceManager waybillTraceManager,
        WaybillQueryManager waybillQueryManager, DiscardedStorageHandlerStrategy discardedStorageHandlerStrategy,
        BaseStaffSiteOrgDto siteDto) {
        this.paramObj = paramObj;
        this.waybillTraceManager = waybillTraceManager;
        this.waybillQueryManager = waybillQueryManager;
        this.discardedStorageHandlerStrategy = discardedStorageHandlerStrategy;
        this.siteDto = siteDto;
    }

    @Override
    public Result<Boolean> call() throws Exception {
        Result<Boolean> result = Result.success();

        try {
            String barCode = paramObj.getBarCode();
            String waybillCode = WaybillUtil.getWaybillCode(barCode);

            // 暂存判断
            if (!waybillTraceManager.isOpCodeWaste(barCode)) {
                log.warn("scanDiscardedPackage，不是弃件，请勿操作弃件暂存 param: {}", JsonHelper.toJson(paramObj));
                return result.toFail("不是弃件，请勿操作弃件暂存");
            }

            WChoice choice = new WChoice();
            choice.setQueryWaybillC(true);
            choice.setQueryPackList(true);
            choice.setQueryGoodList(true);
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, choice);
            log.info("查询到运单信息。运单号：{}。返回结果：{}", waybillCode,
                com.jd.bluedragon.utils.JsonHelper.toJson(baseEntity));
            if (baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode()) {
                return result.toFail("查询到运单信息失败:" + baseEntity.getMessage());
            }
            final BigWaybillDto bigWaybillDto = baseEntity.getData();
            if (bigWaybillDto == null || bigWaybillDto.getWaybill() == null) {
                return result.toFail("没有查询到运单信息");
            }
            if (CollectionUtils.isEmpty(bigWaybillDto.getPackageList())) {
                return result.toFail("没有查询到运单包裹信息");
            }

            // 3. 执行业务操作逻辑
            final DiscardedStorageContext context = new DiscardedStorageContext();
            context.setScanDiscardedPackagePo(paramObj);
            context.setCurrentSiteInfo(siteDto);
            context.setBigWaybillDto(bigWaybillDto);
            context.setWaybillCode(waybillCode);
            final Result<Boolean> handleResult = discardedStorageHandlerStrategy.handle(context);
            if (!handleResult.isSuccess()) {
                return result.toFail(handleResult.getMessage(), handleResult.getCode());
            }
        } catch (RuntimeException e) {
            result.toFail("接口异常");
            log.error("DiscardedPackageStorageTempServiceImpl.scanDiscardedPackage exception param {} exception {}",
                JsonHelper.toJson(paramObj), e.getMessage(), e);
        }
        return result;
    }
}
