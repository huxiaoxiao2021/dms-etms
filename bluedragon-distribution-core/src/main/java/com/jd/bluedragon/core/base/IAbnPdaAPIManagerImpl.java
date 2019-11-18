package com.jd.bluedragon.core.base;

import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.AbnormalReasonDto;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.PdaResult;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.WpAbnormalRecordPda;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.service.IAbnPdaAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service("iAbnPdaAPIManager")
public class IAbnPdaAPIManagerImpl implements IAbnPdaAPIManager {

    private static final String PROCESS_NODE = "营业部";

    @Autowired
    private IAbnPdaAPI iAbnPdaAPI;

    @Cache(key = "IAbnPdaAPIManager.selectAbnReasonByErp@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @Override
    public Map<String, AbnormalReasonDto> selectAbnReasonByErp(String userErp) {

        List<AbnormalReasonDto> abnormalReasonDtoList = iAbnPdaAPI.selectAbnReasonByErp(userErp, PROCESS_NODE);

        if (abnormalReasonDtoList == null || abnormalReasonDtoList.size() == 0) {
            return null;
        }

        Map<String, AbnormalReasonDto> abnormalReasonDtoMap = new HashMap<>(abnormalReasonDtoList.size());

        for (AbnormalReasonDto abnormalReasonDto : abnormalReasonDtoList) {
            Long id = abnormalReasonDto.getId();
            String level = abnormalReasonDto.getAbnormalLevel();
            //层级+编号作为key
            String key = level + "-" + id.toString();
            abnormalReasonDtoMap.put(key, abnormalReasonDto);
        }
        return abnormalReasonDtoMap;
    }

    @Override
    public PdaResult report(WpAbnormalRecordPda wpAbnormalRecordPda) {
        return iAbnPdaAPI.report(wpAbnormalRecordPda);
    }
}
