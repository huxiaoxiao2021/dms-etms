package com.jd.bluedragon.distribution.sorting.service;

import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 运单转换拆分分拣业务类
 */
@Service("sortingWaybillTrunService")
public class SortingWaybillSplitServiceImpl extends SortingPackServiceImpl{

    @Override
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
            log.warn("运单转换拆分分拣业务包裹列表为空:{}", sorting.getWaybillCode());
        }
        return true;
    }

}
