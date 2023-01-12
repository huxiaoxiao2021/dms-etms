package com.jd.bluedragon.distribution.sorting.service;

import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

                /*
                    循环处理sortingVO对象的waybillDtoBaseEntity属性包含了全部的包裹对象，会占用额外的内存，此处进行优化
                    1. 每个sortingVo对象不会包含全部的packageDList
                    2. 在每个循环中，会包含获取packInfo的重复行为，直接使用DeliveryPackageD对象的属性进行赋值
                 */
                if (sortingTarget.getWaybillDto() != null) {
                    sortingTarget.getWaybillDto().setPackageList(Collections.singletonList(packageD));
                }

                super.doSorting(sortingTarget);
            }
        }else{
            log.warn("运单转换拆分分拣业务包裹列表为空:{}", sorting.getWaybillCode());
        }
        return true;
    }

}
