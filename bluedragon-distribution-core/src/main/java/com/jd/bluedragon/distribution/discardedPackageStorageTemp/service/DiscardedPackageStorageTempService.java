package com.jd.bluedragon.distribution.discardedPackageStorageTemp.service;


import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedPackageStorageTempQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;
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
     * sampleMethod
     * @param discardedPackageStorageTemp DiscardedPackageStorageTemp
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    Response<DiscardedPackageStorageTemp> sampleMethod(DiscardedPackageStorageTemp discardedPackageStorageTemp);

    /**
     * 获取总数
     * @param paramObject 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    Response<Long> selectCount(DiscardedPackageStorageTempQo paramObject);

    /**
     * 获取列表
     * @param paramObject 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    Response<List<DiscardedPackageStorageTempVo>> selectList(DiscardedPackageStorageTempQo paramObject);

    /**
     * 获取分页列表
     * @param paramObject 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    Response<PageDto<DiscardedPackageStorageTempVo>> selectPageList(DiscardedPackageStorageTempQo paramObject);
}
