package com.jd.bluedragon.core.jsf.itBasic.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.dms.java.utils.sdk.base.PageData;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.itBasic.ItBasicRegion;
import com.jdl.basic.api.dto.itBasic.qo.ItBasicRegionQo;
import com.jdl.basic.api.dto.itBasic.vo.ItBasicRegionVo;
import com.jdl.basic.api.service.itBasic.ItBasicRegionApi;
import com.jd.bluedragon.core.jsf.itBasic.ItBasicRegionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * IT管理区域数据
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-05 00:53:05 周一
 */
@Slf4j
@Component
public class ItBasicRegionManagerImpl implements ItBasicRegionManager {

    @Resource
    private ItBasicRegionApi itBasicRegionApi;

    /**
     * 统计个数
     *
     * @param itBasicRegionQo 查询参数
     * @return 分页数据
     * @author fanggang7
     * @time 2022-12-04 10:40:36 周日
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "ItBasicRegionManagerImpl.queryCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Long> queryCount(ItBasicRegionQo itBasicRegionQo) {
        log.info("ItBasicRegionManagerImpl.queryCount param {}", JSON.toJSONString(itBasicRegionQo));
        Result<Long> result = Result.success();
        try {
            final Result<Long> remoteResult = itBasicRegionApi.queryCount(itBasicRegionQo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("查询失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("ItBasicRegionManagerImpl.queryCount exception {}", JSON.toJSONString(itBasicRegionQo), e);
        }
        return result;
    }

    /**
     * 查询区域分页列表
     *
     * @param itBasicRegionQo 查询参数
     * @return 分页数据
     * @author fanggang7
     * @time 2022-12-04 10:40:36 周日
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "ItBasicRegionManagerImpl.queryPageList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<PageData<ItBasicRegionVo>> queryPageList(ItBasicRegionQo itBasicRegionQo) {
        log.info("ItBasicRegionManagerImpl.queryPageList param {}", JSON.toJSONString(itBasicRegionQo));
        Result<PageData<ItBasicRegionVo>> result = Result.success();
        try {
            final Result<PageData<ItBasicRegionVo>> remoteResult = itBasicRegionApi.queryPageList(itBasicRegionQo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("查询失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("ItBasicRegionManagerImpl.add exception {}", JSON.toJSONString(itBasicRegionQo), e);
        }
        return result;
    }

    /**
     * 添加一条区域记录
     *
     * @param itBasicRegion 设备记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "ItBasicRegionManagerImpl.add", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Long> add(ItBasicRegion itBasicRegion) {
        log.info("ItBasicRegionManagerImpl.add param {}", JSON.toJSONString(itBasicRegion));
        Result<Long> result = Result.success();
        try {
            final Result<Long> remoteResult = itBasicRegionApi.add(itBasicRegion);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("添加失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("ItBasicRegionManagerImpl.add exception {}", JSON.toJSONString(itBasicRegion), e);
        }
        return result;
    }

    /**
     * 根据ID更新
     *
     * @param itBasicRegion 区域记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "ItBasicRegionManagerImpl.updateById", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> updateById(ItBasicRegion itBasicRegion) {
        log.info("ItBasicRegionManagerImpl.updateById param {}", JSON.toJSONString(itBasicRegion));
        Result<Boolean> result = Result.success();
        try {
            final Result<Boolean> remoteResult = itBasicRegionApi.updateById(itBasicRegion);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("更新失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("ItBasicRegionManagerImpl.updateById exception {}", JSON.toJSONString(itBasicRegion), e);
        }
        return result;
    }

    /**
     * 根据ID逻辑删除
     *
     * @param itBasicRegion 区域记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "ItBasicRegionManagerImpl.logicDeleteById", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> logicDeleteById(ItBasicRegion itBasicRegion) {
        log.info("ItBasicRegionManagerImpl.logicDeleteById param {}", JSON.toJSONString(itBasicRegion));
        Result<Boolean> result = Result.success();
        try {
            final Result<Boolean> remoteResult = itBasicRegionApi.logicDeleteById(itBasicRegion);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("删除失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("ItBasicRegionManagerImpl.logicDeleteById exception {}", JSON.toJSONString(itBasicRegion), e);
        }
        return result;
    }
}
