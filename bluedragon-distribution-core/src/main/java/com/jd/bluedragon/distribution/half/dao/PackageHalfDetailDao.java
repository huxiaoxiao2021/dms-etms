package com.jd.bluedragon.distribution.half.dao;

import com.jd.bluedragon.distribution.half.domain.PackageHalfDetail;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 *
 * @ClassName: PackageHalfDetailDao
 * @Description: 包裹半收操作明细表--Dao接口
 * @author wuyoude
 * @date 2018年03月20日 17:33:21
 *
 */
public interface PackageHalfDetailDao extends Dao<PackageHalfDetail> {


    List<PackageHalfDetail> getPackageHalfDetailByWaybillCode(String waybillCode);

    void deleteOfSaveFail(String waybillCode);
}
