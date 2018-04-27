package com.jd.bluedragon.distribution.half.dao;

import com.jd.bluedragon.distribution.half.domain.PackageHalfApprove;
import com.jd.bluedragon.distribution.half.domain.PackageHalfApproveCondition;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: PackageHalfApproveDao
 * @Description: 包裹半收协商再投终端提交业务表--Dao接口
 * @author wuyoude
 * @date 2018年04月11日 18:45:19
 *
 */
public interface PackageHalfApproveDao extends Dao<PackageHalfApprove> {

    /**
     * 根据条件查询终端提交的协商再投
     * @param condition
     * @return
     */
    PackageHalfApprove queryOneByWaybillCode(PackageHalfApproveCondition condition);

    /**
     * 根据条件查询终端提交的协商再投列表
     * @param wayBillCodes
     * @return
     */
    List<PackageHalfApprove> queryListByWaybillCode(List<String> wayBillCodes);
}
