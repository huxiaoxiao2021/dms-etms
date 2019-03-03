package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.fastjson.JSON;
import com.jd.tms.boss.dto.CommonDto;
import com.jd.tms.boss.dto.RecyclingBoxDto;
import com.jd.tms.boss.ws.BossQueryWS;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tmsBossQueryManager")
public class TMSBossQueryManagerImpl implements TMSBossQueryManager {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private BossQueryWS bossQueryWS;

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.TMSBossQueryManagerImpl.getRecyclingBoxFaceInfo",mState = {})
    public RecyclingBoxDto getRecyclingBoxFaceInfo(RecyclingBoxDto dto) throws Exception{
        CommonDto<RecyclingBoxDto> commonDto = bossQueryWS.getRecyclingBoxFaceInfo(dto);
        if(commonDto == null){
            logger.error("调用运输系统获取青流箱信息失败.RecyclingBoxDto:" + JSON.toJSONString(dto));
            return null;
        }
        if(commonDto.getCode() != 1){
            logger.error("调用运输系统获取青流箱信息失败.RecyclingBoxDto:" + JSON.toJSONString(dto) +
                    ".返回值code:" + commonDto.getCode() +
                    ",message" + commonDto.getMessage());
            return null;
        }
        return commonDto.getData();
    }

    /**
     * 根据流水号获取青流箱箱号列表
     * @param batchCode
     * @return
     * @throws Exception
     */
    public List<String> getRecyclingBoxFaceInfoByBatchCode(String batchCode){
        RecyclingBoxDto dto = new RecyclingBoxDto();
        dto.setBatchCode(batchCode);

        try {
            RecyclingBoxDto recyclingBoxDto = getRecyclingBoxFaceInfo(dto);
            if (recyclingBoxDto != null && recyclingBoxDto.getBoxCodeList() != null) {
                return recyclingBoxDto.getBoxCodeList();
            }
            logger.warn("根据流水号获取青流箱箱号列表为空.batchCode:" + batchCode);
        }catch (Exception e){
            logger.error("调运输系统获取青流箱箱号列表异常.",e);
        }
        return null;
    }
}
