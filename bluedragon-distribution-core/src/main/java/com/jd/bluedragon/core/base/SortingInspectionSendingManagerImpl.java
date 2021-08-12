package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.ql.zw.monitor.core.Pager;
import com.jd.ql.zw.monitor.service.SortingInspectionSendingService;
import com.jd.ql.zw.monitor.service.domain.OperateSiteQueryVO;
import com.jd.ql.zw.monitor.service.domain.UnsentPackageDetail;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 已验未发服务包装接口实现
 *
 * @author hujiping
 * @date 2021/6/3 9:53 下午
 */
@Service("sortingInspectionSendingManager")
public class SortingInspectionSendingManagerImpl implements SortingInspectionSendingManager {

    @Autowired
    private SortingInspectionSendingService sortingInspectionSendingService;

    @JProfiler(jKey = "DMS.BASE.SortingInspectionSendingManagerImpl.searchUnsentPackageByPage", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public List<UnsentPackageDetail> searchUnsentPackageByPage(OperateSiteQueryVO queryVO) {
        // 设置验货类型
        queryVO.setSearchType(OperateSiteQueryVO.INSPECTION_TYPE);
        Pager<UnsentPackageDetail> detailPager = sortingInspectionSendingService.searchUnsentPackageByPage(queryVO);
        if(detailPager != null && CollectionUtils.isNotEmpty(detailPager.getData())){
            return detailPager.getData();
        }
        return null;
    }

    @JProfiler(jKey = "DMS.BASE.SortingInspectionSendingManagerImpl.getUnsentPackageTotal", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public Long getUnsentPackageTotal(OperateSiteQueryVO queryVO) {
        // 设置验货类型
        queryVO.setSearchType(OperateSiteQueryVO.INSPECTION_TYPE);
        return sortingInspectionSendingService.getUnsentPackageTotal(queryVO);
    }
}
