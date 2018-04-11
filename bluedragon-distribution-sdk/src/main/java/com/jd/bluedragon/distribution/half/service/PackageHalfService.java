package com.jd.bluedragon.distribution.half.service;

import com.jd.bluedragon.distribution.half.domain.PackageHalf;
import com.jd.bluedragon.distribution.half.domain.PackageHalfDetail;
import com.jd.bluedragon.distribution.half.domain.PackageHalfVO;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: PackageHalfService
 * @Description: 包裹半收操作--Service接口
 * @author wuyoude
 * @date 2018年03月20日 17:33:21
 *
 */
public interface PackageHalfService extends Service<PackageHalf> {

    /**
     * 提交操作
     * @param packageHalf
     * @param packageHalfDetails
     * @param waybillOpeType 运单操作码
     * @param OperatorId 操作人ID
     * @param OperatorName 操作人
     * @param operateTime 操作时间
     * @param packageCount 拒收包裹数量
     * @return
     */
    boolean save(PackageHalf packageHalf , List<PackageHalfDetail> packageHalfDetails , Integer waybillOpeType, Integer OperatorId, String OperatorName, Date operateTime,Integer packageCount,Integer orgId,Integer createSiteCode);


    void deleteOfSaveFail(String waybillCode);
}
