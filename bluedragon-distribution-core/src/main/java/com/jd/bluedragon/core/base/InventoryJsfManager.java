package com.jd.bluedragon.core.base;

import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.inventory.domain.InventoryDirection;
import com.jd.ql.dms.report.inventory.domain.InventoryPackage;
import com.jd.ql.dms.report.inventory.domain.InventoryWaybillSummary;
import com.jd.ql.dms.report.inventory.request.InventoryQueryRequest;

import java.util.List;

public interface InventoryJsfManager {
    /**
     * 查询当前分拣中心的卡位列表
     * @param createSiteCode
     * @return
     */
    BaseEntity<List<InventoryDirection>> queryInventoryDirectionList(Integer createSiteCode);

    /**
     * 查询待盘信息列表
     * @param request
     * @return
     */
    BaseEntity<Pager<InventoryPackage>> queryNeedInventoryPackageList(Pager<InventoryQueryRequest> request);

    /**
     * 查询待盘信息--运单号维度
     * @param request
     * @return
     */
    BaseEntity<Pager<InventoryWaybillSummary>> queryNeedInventoryWaybillSummaryList(Pager<InventoryQueryRequest> request);

    /**
     * 获取包裹状态信息
     * @param request
     * @return
     */
    BaseEntity<List<InventoryPackage>> queryPackageStatusList(InventoryQueryRequest request);
}
