package com.jd.bluedragon.distribution.half.service;

import com.jd.bluedragon.distribution.half.domain.PackageHalfApprove;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: PackageHalfApproveService
 * @Description: 包裹半收协商再投终端提交业务表--Service接口
 * @author wuyoude
 * @date 2018年04月11日 18:45:19
 *
 */
public interface PackageHalfApproveService extends Service<PackageHalfApprove> {

    /**
     * 根据条件查询终端提交的协商再投
     * @param wayBillCode
     * @param siteCode
     * @return
     */
    PackageHalfApprove queryOneByWaybillCode(String wayBillCode, Integer siteCode);

    /**
     * 根据条件查询终端提交的协商再投列表
     * @param wayBillCodes
     * @return
     */
    List<PackageHalfApprove> queryListByWaybillCode(List<String> wayBillCodes);
}
