package com.jd.bluedragon.core.base;

import com.jd.ql.zw.monitor.service.domain.OperateSiteQueryVO;
import com.jd.ql.zw.monitor.service.domain.UnsentPackageDetail;

import java.util.List;

/**
 * 已验未发服务包装接口
 *
 * @author hujiping
 * @date 2021/6/3 9:52 下午
 */
public interface SortingInspectionSendingManager {

    /**
     * 根据条件分页查询已验未发数据
     *
     * @param queryVO
     * @return
     */
    List<UnsentPackageDetail> searchUnsentPackageByPage(OperateSiteQueryVO queryVO);

    /**
     * 根据条件查询已验未发包裹数量
     *
     * @param queryVO
     * @return
     */
    Long getUnsentPackageTotal(OperateSiteQueryVO queryVO);
}
