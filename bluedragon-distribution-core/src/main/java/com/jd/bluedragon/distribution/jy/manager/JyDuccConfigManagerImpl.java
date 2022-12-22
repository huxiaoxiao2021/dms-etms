package com.jd.bluedragon.distribution.jy.manager;

import com.alibaba.fastjson.JSON;
import com.jdl.jy.realtime.api.ducc.DuccConfigJsfService;
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
public class JyDuccConfigManagerImpl implements JyDuccConfigManager{

    private static final Logger log = LoggerFactory.getLogger(JyDuccConfigManagerImpl.class);

    @Autowired
    private DuccConfigJsfService duccConfigJsfService;

    @Override
    public Boolean getJySendAggsDataReadSwitchInfo() {
        try{
            ServiceResult<Boolean> result = duccConfigJsfService.getJySendAggsDataReadSwitchInfo();
            log.info("获取拣运发货岗主备开关切换值-{}", JSON.toJSONString(result));
            if(result != null && ServiceResult.SUCCESS_CODE == result.getCode()){
                return result.getData();
            }
        }catch (Exception e){
            log.error("获取拣运发货岗主备开关切换值异常!");
        }
        return false;
    }

    @Override
    public Boolean getJyUnloadAggsDataReadSwitchInfo() {
        try{
            ServiceResult<Boolean> result = duccConfigJsfService.getJySendAggsDataReadSwitchInfo();
            log.info("获取拣运卸车岗主备开关切换值-{}", JSON.toJSONString(result));
            if(result != null && ServiceResult.SUCCESS_CODE == result.getCode()){
                return result.getData();
            }
        }catch (Exception e){
            log.error("获取拣运卸车岗主备开关切换值异常!");
        }
        return false;
    }
}
