package com.jd.bluedragon.distribution.discardedPackageStorageTemp.service;

import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedPackageNotScanItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedWaybillScanResultItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.*;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedPackageStorageTempQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.vo.DiscardedPackageStorageTempVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.List;

/**
 * Description: 快递弃件暂存<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2021-03-31 11:32:59 周三
 */
public interface DiscardedPackageStorageTempService{

    /**
     * 获取总数
     * @param query 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    Response<Long> selectCount(DiscardedPackageStorageTempQo query);

    /**
     * 获取列表
     * @param query 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    Response<List<DiscardedPackageStorageTempVo>> selectList(DiscardedPackageStorageTempQo query);

    /**
     * 获取分页列表
     * @param query 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    Response<PageDto<DiscardedPackageStorageTempVo>> selectPageList(DiscardedPackageStorageTempQo query);

    /**
     * 查询未提交已扫描的弃件扫描数据
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:37:54 周四
     */
    Result<List<DiscardedWaybillScanResultItemDto>> queryUnSubmitDiscardedList(QueryUnSubmitDiscardedListPo paramObj);

    /**
     * 弃件暂存提交
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:37:43 周四
     */
    Result<List<DiscardedWaybillScanResultItemDto>> scanDiscardedPackage(ScanDiscardedPackagePo paramObj);

    /**
     * 弃件暂存提交已扫描弃件数据
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:37:54 周四
     */
    Result<Boolean> submitDiscardedPackage(SubmitDiscardedPackagePo paramObj);

    /**
     * 查询未扫描的弃件扫描数据
     * @param paramObj 请求参数
     * @return 提交结果
     * @author fanggang7
     * @time 2021-12-02 16:55:24 周四
     */
    Result<List<DiscardedPackageNotScanItemDto>> queryUnScanDiscardedPackage(QueryUnScanDiscardedPackagePo paramObj);
}
