package com.jd.bluedragon.distribution.express.service;

import com.jd.bluedragon.distribution.express.domain.ExpressBoxDetailsResponse;
import com.jd.bluedragon.distribution.express.domain.ExpressPackageDetailsResponse;

/**
 * @author zhangleqi
 * @comments 快运到齐查询接口
 * @date 2017/11/14
 */
public interface ExpressCollectionService {

    /**
     * 根据运单号和扫描状态获取快运到齐包裹明细信息
     *
     * @param waybillCode     运单号
     * @param statusQueryCode 查询状态
     * @return 查询明细
     */
    ExpressPackageDetailsResponse findExpressPackageDetails(ExpressPackageDetailsResponse expressPackageDetailsResponse, String waybillCode, String statusQueryCode);


    /**
     * 根据运单号和扫描状态获取快运到齐箱号明细信息
     *
     * @param waybillCode     运单号
     * @param statusQueryCode 查询状态
     * @return 查询明细
     */
    ExpressBoxDetailsResponse findExpressBoxDetails(ExpressBoxDetailsResponse expressBoxDetailsResponse, String waybillCode, String statusQueryCode);


}
