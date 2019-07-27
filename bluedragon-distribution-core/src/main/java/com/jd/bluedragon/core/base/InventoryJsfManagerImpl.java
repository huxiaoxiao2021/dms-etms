package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.inventory.InventoryJsfService;
import com.jd.ql.dms.report.inventory.domain.InventoryDirection;
import com.jd.ql.dms.report.inventory.domain.InventoryPackage;
import com.jd.ql.dms.report.inventory.domain.InventoryWaybillSummary;
import com.jd.ql.dms.report.inventory.request.InventoryQueryRequest;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("inventoryJsfManager")
public class InventoryJsfManagerImpl implements  InventoryJsfManager {
    @Autowired
    private InventoryJsfService inventoryJsfService;

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.InventoryJsfManagerImpl.queryInventoryDirectionList",mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<List<InventoryDirection>> queryInventoryDirectionList(Integer createSiteCode){
        return inventoryJsfService.queryInventoryDirectionList(createSiteCode);
    }

    /**
     * 查询待盘信息列表
     * @param request
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.InventoryJsfManagerImpl.queryNeedInventoryPackageList",mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<Pager<InventoryPackage>> queryNeedInventoryPackageList(Pager<InventoryQueryRequest> request){
        return inventoryJsfService.queryNeedInventoryPackageList(request);
    }

    /**
     * 查询待盘信息--运单号维度
     * @param request
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.InventoryJsfManagerImpl.queryNeedInventoryWaybillSummaryList",mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<Pager<InventoryWaybillSummary>> queryNeedInventoryWaybillSummaryList(Pager<InventoryQueryRequest> request){
        return inventoryJsfService.queryNeedInventoryWaybillSummaryList(request);
    }

    /**
     * 获取包裹状态信息
     * @param request
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.InventoryJsfManagerImpl.queryPackageStatusList",mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<List<InventoryPackage>> queryPackageStatusList(InventoryQueryRequest request){
        return inventoryJsfService.queryPackageStatusList(request);
    }
}
