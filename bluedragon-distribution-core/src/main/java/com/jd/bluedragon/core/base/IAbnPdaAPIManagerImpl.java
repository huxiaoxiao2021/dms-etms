package com.jd.bluedragon.core.base;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.BaseResult;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.ExceptionReason;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.PdaResult;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.WpAbnormalRecordPda;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.service.ExceptionReasonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("iAbnPdaAPIManager")
public class IAbnPdaAPIManagerImpl implements IAbnPdaAPIManager {

    private static final Integer SUCCESS_CODE= 1;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExceptionReasonService exceptionReasonService;

    @JProfiler(jKey = "DMSWEB.IAbnPdaAPIManagerImpl.selectAbnReasonByErp", mState = {JProEnum.TP})
    @Cache(key = "IAbnPdaAPIManager.selectAbnReasonByErp@args0", memoryEnable = true, memoryExpiredTime = 3 * 60 * 1000, redisEnable = true, redisExpiredTime = 5 * 60 * 1000)
    @Override
    public Map<String, ExceptionReason> selectAbnReasonByErp(String userErp) {
        List<ExceptionReason> abnormalReasonDtoList = null;
        try {
            BaseResult<List<ExceptionReason>> res = exceptionReasonService.getExceptionReasons();
            if(res!=null && res.getResultCode().equals(SUCCESS_CODE)){
                abnormalReasonDtoList=res.getData();
            }
        } catch (Exception e) {
            logger.error("getExceptionReasons JSF接口异常！ERP：{}", userErp, e);
        }

        if (abnormalReasonDtoList == null || abnormalReasonDtoList.size() == 0) {
            logger.warn("getExceptionReasons JSF接口返回原因列表为null！ERP：{}", userErp);
            return null;
        }

        logger.info("调用质控系统JSF接口获取质控侧异常原因列表：{}", JsonHelper.toJson(abnormalReasonDtoList));

        Map<String, ExceptionReason> abnormalReasonDtoMap = new HashMap<>(abnormalReasonDtoList.size());

        for (ExceptionReason abnormalReasonDto : abnormalReasonDtoList) {
            Long id = abnormalReasonDto.getId();
            String level = abnormalReasonDto.getAbnormalLevel();
            //层级+编号作为key
            String key = level + "-" + id.toString();
            abnormalReasonDtoMap.put(key, abnormalReasonDto);
        }
        return abnormalReasonDtoMap;
    }

    @JProfiler(jKey = "DMSWEB.IAbnPdaAPIManagerImpl.report", mState = {JProEnum.TP})
    @Override
    public PdaResult report(WpAbnormalRecordPda wpAbnormalRecordPda) {
        PdaResult pdaResult = null;
        try {
            pdaResult = exceptionReasonService.report(wpAbnormalRecordPda);
        } catch (Exception e) {
            logger.error("调用质控系统report JSF接口异常！", e);
        }
        return pdaResult;
    }
}
