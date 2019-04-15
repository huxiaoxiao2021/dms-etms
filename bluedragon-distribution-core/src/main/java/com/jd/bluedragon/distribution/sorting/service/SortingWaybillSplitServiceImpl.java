package com.jd.bluedragon.distribution.sorting.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 运单转换拆分分拣业务类
 */
@Service("sortingWaybillTrunService")
public class SortingWaybillSplitServiceImpl extends SortingPackServiceImpl{

    @Override
    @JProfiler(jKey = "DMS.BASE.SortingWaybillSplitServiceImpl.doSorting", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWORKER)
    public boolean doSorting(SortingVO sorting) {
        //获取需要处理的数据
        List<DeliveryPackageD> packageDList = sorting.getPackageList();
        if(packageDList!=null){
            for(DeliveryPackageD packageD : packageDList){
                SortingVO sortingTarget = new SortingVO();
                BeanUtils.copyProperties(sorting,sortingTarget);
                if (!BusinessHelper.isBoxcode(sorting.getBoxCode())) {
                    sortingTarget.setBoxCode(packageD.getPackageBarcode());
                }
                sortingTarget.setPackageCode(packageD.getPackageBarcode());
                super.doSorting(sortingTarget);
            }
        }else{
            logger.error("运单转换拆分分拣业务包裹列表为空"+sorting.getWaybillCode());
        }
        return true;
    }

}
