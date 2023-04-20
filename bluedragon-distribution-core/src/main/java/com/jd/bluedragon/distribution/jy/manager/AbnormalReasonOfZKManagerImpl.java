package com.jd.bluedragon.distribution.jy.manager;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.wl.cs.abnormal.portal.api.dto.reason.AbnormalReasonDto;
import com.jd.wl.cs.abnormal.portal.api.result.Result;
import com.jd.wl.cs.abnormal.portal.api.service.reason.AbnormalReasonApi;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/4/20 15:48
 * @Description:
 */
@Service("abnormalReasonManagerOfZK")
public class AbnormalReasonOfZKManagerImpl implements AbnormalReasonOfZKManager {

    private static final Logger log = LoggerFactory.getLogger(AbnormalReasonOfZKManagerImpl.class);

    //系统编码
    private static  final Integer DMS_ETMS_CODE = 9;

    @Autowired
    @Qualifier("abnormalReasonApiofZK")
    private AbnormalReasonApi abnormalReasonApiofZK;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.AbnormalReasonManagerImpl.queryAbnormalReasonListBySystemCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<AbnormalReasonDto> queryAbnormalReasonListBySystemCode() {
        try{
            Result<List<AbnormalReasonDto>> result = abnormalReasonApiofZK.queryAbnormalReasonListBySystemCode(DMS_ETMS_CODE);
            if(log.isInfoEnabled()){
                log.info("queryAbnormalReasonListBySystemCode 获取异常原因Result -{}", JSON.toJSONString(result));
            }
            if(result.getCode() == 0 && CollectionUtils.isNotEmpty(result.getData())){
                return result.getData();
            }
        }catch (Exception e){
            log.error("queryAbnormalReasonListBySystemCode 获取异常原因异常-{}",e.getMessage(),e);
        }
        return null;
    }
}
