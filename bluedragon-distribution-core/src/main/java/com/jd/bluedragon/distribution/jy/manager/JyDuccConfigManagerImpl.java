package com.jd.bluedragon.distribution.jy.manager;

import com.alibaba.fastjson.JSON;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jdl.jy.realtime.api.ducc.SendOrUnloadDataReadDuccConfigJsfService;
import com.jdl.jy.realtime.base.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/12/21 18:37
 * @Description:
 */
@Service("jyDuccConfigManager")
public class JyDuccConfigManagerImpl implements JySendOrUnloadDataReadDuccConfigManager {

    private static final Logger log = LoggerFactory.getLogger(JyDuccConfigManagerImpl.class);

    @Autowired
    private SendOrUnloadDataReadDuccConfigJsfService jySendOrUnloadDataReadDuccConfigJsfService;

    @Override
//    @Cache(key = "JyDuccConfigManagerImpl.getJySendAggsDataReadSwitchInfo", memoryEnable = true, memoryExpiredTime = 30 * 1000,
//            redisEnable = true, redisExpiredTime = 30 * 1000)
    public Boolean getJySendAggsDataReadSwitchInfo() {
        try{
            ServiceResult<Boolean> result = jySendOrUnloadDataReadDuccConfigJsfService.getJySendAggsDataReadSwitchInfo();
            log.info("获取拣运发货岗主备开关切换值-{}", JSON.toJSONString(result));
            if(result != null && ServiceResult.SUCCESS_CODE == result.getCode() && result.getData() != null){
                return result.getData();
            }
        }catch (Exception e){
            log.error("获取拣运发货岗主备开关切换值异常!-{}",e.getMessage(),e);
        }
        return false;
    }

    @Override
//    @Cache(key = "JyDuccConfigManagerImpl.getJyUnloadAggsDataReadSwitchInfo", memoryEnable = true, memoryExpiredTime = 30 * 1000,
//            redisEnable = true, redisExpiredTime = 30 * 1000)
    public Boolean getJyUnloadAggsDataReadSwitchInfo() {
        try{
            ServiceResult<Boolean> result = jySendOrUnloadDataReadDuccConfigJsfService.getJyUnloadAggsDataReadSwitchInfo();
            log.info("获取拣运卸车岗主备开关切换值-{}", JSON.toJSONString(result));
            if(result != null && ServiceResult.SUCCESS_CODE == result.getCode() && result.getData() != null ){
                return result.getData();
            }
        }catch (Exception e){
            log.error("获取拣运卸车岗主备开关切换值异常!-{}",e.getMessage(),e);
        }
        return false;
    }


    @Override
//    @Cache(key = "JyDuccConfigManagerImpl.getJySendAggOldOrNewDataReadSwitch", memoryEnable = true, memoryExpiredTime = 30 * 1000,
//            redisEnable = true, redisExpiredTime = 30 * 1000)
    public Boolean getJySendAggOldOrNewDataReadSwitch() {
        try{
            ServiceResult<Boolean> result = jySendOrUnloadDataReadDuccConfigJsfService.getJySendAggOldOrNewDataReadSwitch();
            log.info("获取拣运发货岗老库or新库数据读取开关-{}", JSON.toJSONString(result));
            if(result != null && ServiceResult.SUCCESS_CODE == result.getCode() && result.getData() != null){
                return result.getData();
            }
        }catch (Exception e){
            log.error("获取拣运发货岗老库or新库数据读取开关异常!-{}",e.getMessage(),e);
        }
        return false;
    }

    @Override
//    @Cache(key = "JyDuccConfigManagerImpl.getJyUnloadAggsOldOrNewDataReadSwitch", memoryEnable = true, memoryExpiredTime = 30 * 1000,
//            redisEnable = true, redisExpiredTime = 30 * 1000)
    public Boolean getJyUnloadAggsOldOrNewDataReadSwitch() {
        try{
            ServiceResult<Boolean> result = jySendOrUnloadDataReadDuccConfigJsfService.getJyUnloadAggsOldOrNewDataReadSwitch();
            log.info("获取拣运卸车岗老库or新库数据读取开关-{}", JSON.toJSONString(result));
            if(result != null && ServiceResult.SUCCESS_CODE == result.getCode() && result.getData() != null){
                return result.getData();
            }
        }catch (Exception e){
            log.error("获取拣运卸车岗老库or新库数据读取开关异常!-{}",e.getMessage(),e);
        }
        return false;
    }

}
