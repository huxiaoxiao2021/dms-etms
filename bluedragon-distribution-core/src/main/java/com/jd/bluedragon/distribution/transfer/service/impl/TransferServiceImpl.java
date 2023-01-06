package com.jd.bluedragon.distribution.transfer.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarStatusEnum;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.constants.TransportServiceConstants;
import com.jd.bluedragon.distribution.external.enums.AppVersionEnums;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarCommonService;
import com.jd.bluedragon.distribution.transfer.service.TransferService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 转运相关服务
 */
@Slf4j
@Service("transferService")
public class TransferServiceImpl implements TransferService {


    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Resource
    private UnloadCarCommonService unloadCarCommonService;




    @Override
    @JProfiler(jKey = "DMS.BASE.TransferServiceImpl.saveOperatePdaVersion", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> saveOperatePdaVersion(String sealCarCode, String pdaVersion) {
        InvokeResult<Boolean> res = new InvokeResult<>();
        res.success();

        if(StringUtils.isBlank(sealCarCode) || StringUtils.isBlank(pdaVersion)) {
            res.error("新老版本互斥加锁参数不能为空");
            return res;
        }
        if(!AppVersionEnums.existValidation(pdaVersion)) {
            res.error("新老版本互斥加锁版本暂不支持");
            return res;
        }
        if(log.isInfoEnabled()) {
            log.info("新老版本互斥,sealCarCode={}, version={}", sealCarCode, pdaVersion);
        }
        try{
            //todo zcf 考虑加锁
            boolean resData = true;
            String key = TransportServiceConstants.CACHE_PREFIX_PDA_ACTUAL_OPERATE_VERSION + sealCarCode;
            if (redisClientOfJy.exists(key)) {
                resData = redisClientOfJy.get(key).equals(pdaVersion);
            }else {
                //兼容历史数据：
                UnloadCar uc = unloadCarCommonService.selectBySealCarCodeWithStatus(sealCarCode);
                if (uc != null && !uc.getStatus().equals(UnloadCarStatusEnum.UNLOAD_CAR_UN_DISTRIBUTE.getType())) {
                    if(log.isInfoEnabled()) {
                        log.info("新老版本互斥，兼容历史数据，给历史数据加锁修改version为1，请求sealCarCode={}, 请求version={},task={}", sealCarCode, pdaVersion, JsonHelper.toJson(uc));
                    }
                    String defaultVersion = AppVersionEnums.PDA_OLD.getVersion();
                    if(AppVersionEnums.PDA_GUIDED.getVersion().equals(uc.getVersion())) {
                        defaultVersion = AppVersionEnums.PDA_GUIDED.getVersion();
                    }
                    //老PDA已经操作领取status=1或者已经开始扫描status=2或任务完成status=3，但是无redis
                    redisClientOfJy.setEx(key, defaultVersion, TransportServiceConstants.CACHE_PREFIX_PDA_ACTUAL_OPERATE_VERSION_EXPIRE, TimeUnit.DAYS);
                    resData = defaultVersion.equals(pdaVersion);
                } else {
                    redisClientOfJy.setEx(key, pdaVersion, TransportServiceConstants.CACHE_PREFIX_PDA_ACTUAL_OPERATE_VERSION_EXPIRE, TimeUnit.DAYS);
                }
            }
            res.setData(resData);
            return res;
        }catch (Exception e) {
            log.error("TransferServiceImpl.saveOperatePdaVersion--服务异常--sealCarCode={}，pdaVersion={}, srrMsg={}", sealCarCode, pdaVersion, e.getMessage(), e);
            res.error("保存PDA操作版本服务异常" + e.getMessage());
            return res;
        }
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.TransferServiceImpl.delOperatePdaVersion", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> delOperatePdaVersion(String sealCarCode, String pdaVersion) {
        InvokeResult<Boolean> res = new InvokeResult<>();
        res.success();

        if(StringUtils.isBlank(sealCarCode) || StringUtils.isBlank(pdaVersion)) {
            res.error("参数不能为空");
            return res;
        }
        if(!AppVersionEnums.existValidation(pdaVersion)) {
            res.error("版本暂不支持");
            return res;
        }
        try{
            UnloadCar uc = unloadCarCommonService.selectBySealCarCodeWithStatus(sealCarCode);
            if (uc != null && (uc.getStatus().equals(UnloadCarStatusEnum.UNLOAD_CAR_STARTED.getType()) || uc.getStatus().equals(UnloadCarStatusEnum.UNLOAD_CAR_END.getType()))) {
                res.error("任务已开始进行或已完成，不允许删除");
                return res;
            }
            String key = TransportServiceConstants.CACHE_PREFIX_PDA_ACTUAL_OPERATE_VERSION + sealCarCode;
            if (redisClientOfJy.exists(key)) {
                if(!redisClientOfJy.get(key).equals(pdaVersion)) {
                    String msg = StringUtils.isBlank(AppVersionEnums.getDescByCode(pdaVersion)) ? "其他版本" : AppVersionEnums.getDescByCode(pdaVersion);
                    res.error(msg + "正在操作中，不允许删除");
                    return res;
                }else {
                    redisClientOfJy.del(key);
                }
            }
            res.setData(true);
            return res;
        }catch (Exception e) {
            log.error("TransferServiceImpl.delOperatePdaVersion--服务异常--sealCarCode={}，pdaVersion={}, srrMsg={}", sealCarCode, pdaVersion, e.getMessage(), e);
            res.error("删除PDA操作版本服务异常" + e.getMessage());
            return res;
        }
    }



    @Override
    @JProfiler(jKey = "DMS.BASE.TransferServiceImpl.getOperatePdaVersion", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<String> getOperatePdaVersion(String sealCarCode) {
        InvokeResult<String> res = new InvokeResult<>();
        res.success();

        if(StringUtils.isBlank(sealCarCode)) {
            res.error("参数不能为空");
            return res;
        }
        try{
            String key = TransportServiceConstants.CACHE_PREFIX_PDA_ACTUAL_OPERATE_VERSION + sealCarCode;
            res.setData(redisClientOfJy.get(key));
            return res;
        }catch (Exception e) {
            log.error("TransferServiceImpl.getOperatePdaVersion--服务异常--sealCarCode={}， srrMsg={}", sealCarCode, e.getMessage(), e);
            res.error("查询PDA操作版本服务异常" + e.getMessage());
            return res;
        }
    }




}
