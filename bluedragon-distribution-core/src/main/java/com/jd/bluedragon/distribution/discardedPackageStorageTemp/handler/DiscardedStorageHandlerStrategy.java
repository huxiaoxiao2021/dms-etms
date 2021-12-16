package com.jd.bluedragon.distribution.discardedPackageStorageTemp.handler;

import com.jd.bluedragon.common.dto.wastepackagestorage.request.ScanDiscardedPackagePo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedStorageContext;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.DiscardedPackageSiteDepartTypeEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.WasteOperateTypeEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.workbench.utils.sdk.base.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 弃件暂存废弃处理策略类
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-06 11:18:39 周一
 */
@Service
public class DiscardedStorageHandlerStrategy {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DiscardedStorageSortingTempStorageHandler discardedStorageSortingTempStorageHandler;

    @Autowired
    private DiscardedStorageSortingScrapHandler discardedStorageSortingScrapHandler;

    @Autowired
    private DiscardedStorageTransferTempStorageHandler discardedStorageTransferTempStorageHandler;

    public Result<Boolean> handle(DiscardedStorageContext context) throws RuntimeException {

        Result<Boolean> result = Result.success();

        final ScanDiscardedPackagePo scanDiscardedPackagePo = context.getScanDiscardedPackagePo();

        if (Objects.equals(DiscardedPackageSiteDepartTypeEnum.SORTING.getCode(), scanDiscardedPackagePo.getSiteDepartType())) {
            /* 1. 分拣暂存处理 */
            if(Objects.equals(WasteOperateTypeEnum.STORAGE.getCode(), scanDiscardedPackagePo.getOperateType())){
                return discardedStorageSortingTempStorageHandler.handle(context);
            }
            /* 2. 分拣废弃处理 */
            if(Objects.equals(WasteOperateTypeEnum.SCRAP.getCode(), scanDiscardedPackagePo.getOperateType())){
                return discardedStorageSortingScrapHandler.handle(context);
            }
        }
        if (Objects.equals(DiscardedPackageSiteDepartTypeEnum.TRANSFER.getCode(), (scanDiscardedPackagePo.getSiteDepartType()))) {
            /* 3. 转运暂存处理 */
            if(Objects.equals(WasteOperateTypeEnum.STORAGE.getCode(), scanDiscardedPackagePo.getOperateType())){
                return discardedStorageTransferTempStorageHandler.handle(context);
            }
        }

        log.warn("没有对应业务类型处理策略：{}", JsonHelper.toJson(context));
        return result;
    }
}
