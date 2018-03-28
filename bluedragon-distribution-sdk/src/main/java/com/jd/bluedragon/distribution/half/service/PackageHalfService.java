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


    boolean save(PackageHalf packageHalf , List<PackageHalfDetail> packageHalfDetails , Integer waybillOpeType, Integer OperatorId, String OperatorName, Date operateTime);

}
