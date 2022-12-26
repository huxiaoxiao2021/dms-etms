package com.jd.bluedragon.core.jsf.itBasic.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.itBasic.ItBasicIpRegionManager;
import com.jd.dms.java.utils.sdk.base.PageData;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.itBasic.ItBasicStorageIpRegion;
import com.jdl.basic.api.dto.itBasic.dto.MatchIpRegionDto;
import com.jdl.basic.api.dto.itBasic.po.ItBasicIpRegionPo;
import com.jdl.basic.api.dto.itBasic.qo.ItBasicStorageIpRegionQo;
import com.jdl.basic.api.dto.itBasic.vo.ItBasicStorageIpRegionVo;
import com.jdl.basic.api.service.itBasic.ItBasicIpRegionApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * IT设备IP对应区域信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-05 00:53:05 周一
 */
@Slf4j
@Component
public class ItBasicIpRegionManagerImpl implements ItBasicIpRegionManager {

    @Resource
    private ItBasicIpRegionApi itBasicIpRegionApi;

    /**
     * 统计个数
     *
     * @return 分页数据
     * @author fanggang7
     * @time 2022-12-04 10:40:36 周日
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "ItBasicIpRegionManagerImpl.queryCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Long> queryCount(ItBasicStorageIpRegionQo itBasicStorageIpRegionQo) {
        log.info("ItBasicIpRegionManagerImpl.queryCount param {}", JSON.toJSONString(itBasicStorageIpRegionQo));
        Result<Long> result = Result.success();
        try {
            final Result<Long> remoteResult = itBasicIpRegionApi.queryCount(itBasicStorageIpRegionQo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("查询失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("ItBasicIpRegionManagerImpl.queryCount exception {}", JSON.toJSONString(itBasicStorageIpRegionQo), e);
        }
        return result;
    }

    /**
     * 查询IP地址区域信息分页列表
     *
     * @return 分页数据
     * @author fanggang7
     * @time 2022-12-04 10:40:36 周日
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "ItBasicIpRegionManagerImpl.queryPageList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<PageData<ItBasicStorageIpRegionVo>> queryPageList(ItBasicStorageIpRegionQo itBasicStorageIpRegionQo) {
        log.info("ItBasicIpRegionManagerImpl.queryPageList param {}", JSON.toJSONString(itBasicStorageIpRegionQo));
        Result<PageData<ItBasicStorageIpRegionVo>> result = Result.success();
        try {
            final Result<PageData<ItBasicStorageIpRegionVo>> remoteResult = itBasicIpRegionApi.queryPageList(itBasicStorageIpRegionQo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("查询失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("ItBasicIpRegionManagerImpl.add exception {}", JSON.toJSONString(itBasicStorageIpRegionQo), e);
        }
        return result;
    }

    /**
     * 添加一条设备记录
     *
     * @param itBasicIpRegionPo 设备记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "ItBasicIpRegionManagerImpl.add", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Long> add(ItBasicStorageIpRegion itBasicIpRegionPo) {
        log.info("ItBasicIpRegionManagerImpl.add param {}", JSON.toJSONString(itBasicIpRegionPo));
        Result<Long> result = Result.success();
        try {
            final Result<Long> remoteResult = itBasicIpRegionApi.add(itBasicIpRegionPo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("添加失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("ItBasicIpRegionManagerImpl.add exception {}", JSON.toJSONString(itBasicIpRegionPo), e);
        }
        return result;
    }

    /**
     * 根据ID更新
     *
     * @param itBasicIpRegionPo 设备记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "ItBasicIpRegionManagerImpl.updateById", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> updateById(ItBasicStorageIpRegion itBasicIpRegionPo) {
        log.info("ItBasicIpRegionManagerImpl.updateById param {}", JSON.toJSONString(itBasicIpRegionPo));
        Result<Boolean> result = Result.success();
        try {
            final Result<Boolean> remoteResult = itBasicIpRegionApi.updateById(itBasicIpRegionPo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("更新失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("ItBasicIpRegionManagerImpl.updateById exception {}", JSON.toJSONString(itBasicIpRegionPo), e);
        }
        return result;
    }

    /**
     * 根据ID逻辑删除
     *
     * @param itBasicIpRegionPo 设备记录
     * @return 处理结果
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "ItBasicIpRegionManagerImpl.logicDeleteById", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> logicDeleteById(ItBasicStorageIpRegion itBasicIpRegionPo) {
        log.info("ItBasicIpRegionManagerImpl.logicDeleteById param {}", JSON.toJSONString(itBasicIpRegionPo));
        Result<Boolean> result = Result.success();
        try {
            final Result<Boolean> remoteResult = itBasicIpRegionApi.logicDeleteById(itBasicIpRegionPo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("删除失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("ItBasicIpRegionManagerImpl.logicDeleteById exception {}", JSON.toJSONString(itBasicIpRegionPo), e);
        }
        return result;
    }

    /**
     * 根据IP查询匹配的IP区段对应园区信息
     *
     * @return 匹配的IP对应园区信息
     * @author fanggang7
     * @time 2022-12-04 10:40:36 周日
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "ItBasicIpRegionManagerImpl.selectMatchRegionByIp", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<MatchIpRegionDto> selectMatchRegionByIp(ItBasicIpRegionPo itBasicIpRegionPo) {
        log.info("ItBasicIpRegionManagerImpl.selectMatchRegionByIp param {}", JSON.toJSONString(itBasicIpRegionPo));
        Result<MatchIpRegionDto> result = Result.success();
        try {
            final Result<MatchIpRegionDto> remoteResult = itBasicIpRegionApi.selectMatchRegionByIp(itBasicIpRegionPo);
            if (remoteResult == null || remoteResult.isFail()) {
                result.toFail(String.format("查询失败, %s", remoteResult != null ? remoteResult.getMessage() : "未知异常"));
            } else {
                result.setData(remoteResult.getData());
            }
        } catch (Exception e) {
            log.error("ItBasicIpRegionManagerImpl.selectMatchRegionByIp exception {}", JSON.toJSONString(itBasicIpRegionPo), e);
        }
        return result;
    }
}
