package com.jd.bluedragon.distribution.discardedPackageStorageTemp.jsf.impl;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedPackageStorageTempQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.jsf.DiscardedPackageStorageTempJsfService;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.service.DiscardedPackageStorageTempService;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.vo.DiscardedPackageStorageTempVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 快递弃件暂存
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021年03月31日15:03:33 周三
 */
@Service("discardedPackageStorageTempJsfService")
public class DiscardedPackageStorageTempJsfServiceImpl implements DiscardedPackageStorageTempJsfService {

    @Autowired
    private DiscardedPackageStorageTempService discardedPackageStorageTempService;

    /**
     * 获取总数
     * @param query 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    @Override
    public Response<Long> selectCount(DiscardedPackageStorageTempQo query) {
        return discardedPackageStorageTempService.selectCount(query);
    }

    /**
     * 获取列表
     * @param query 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    @Override
    public Response<List<DiscardedPackageStorageTempVo>> selectList(DiscardedPackageStorageTempQo query) {
        return discardedPackageStorageTempService.selectList(query);
    }

    /**
     * 获取分页列表
     * @param query 请求参数
     * @return Result
     * @author fanggang7
     * @time 2021-03-31 11:32:59 周三
     */
    @Override
    public Response<PageDto<DiscardedPackageStorageTempVo>> selectPageList(DiscardedPackageStorageTempQo query) {
        return discardedPackageStorageTempService.selectPageList(query);
    }
}
