package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.tms.boss.dto.CommonDto;
import com.jd.tms.boss.dto.RecyclingBoxDto;
import com.jd.tms.boss.ws.BossQueryWS;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("bossQueryManager")
public class BossQueryManagerImpl implements BossQueryManager{
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private BossQueryWS bossQueryWS;

    /**
     * 根据流水号获取青流箱箱号列表
     * @param batchCode
     * @return
     * @throws Exception
     */
    public List<String> getRecyclingBoxFaceInfoByBatchCode(String batchCode){
        CallerInfo callerInfo = null;
        try {
            callerInfo = ProfilerHelper.registerInfo("DMS.BASE.BossQueryManagerImpl.getRecyclingBoxFaceInfoByBatchCode", Constants.UMP_APP_NAME_DMSWEB);
            RecyclingBoxDto dto = new RecyclingBoxDto();
            dto.setBatchCode(batchCode);

            CommonDto<RecyclingBoxDto> commonDto = bossQueryWS.getRecyclingBoxFaceInfo(dto);
            if(commonDto == null){
                logger.error("根据流水号获取青流箱箱号列表失败.batchCode:" + batchCode);
                return null;
            }
            if(commonDto.getCode() != 1){
                logger.error("根据流水号获取青流箱箱号列表失败.batchCode:" + batchCode +
                        ".返回值code:" + commonDto.getCode() +
                        ",message" + commonDto.getMessage());
                return null;
            }

            RecyclingBoxDto recyclingBoxDto = commonDto.getData();
            if(recyclingBoxDto != null && recyclingBoxDto.getBoxCodeList() != null){
                return recyclingBoxDto.getBoxCodeList();
            }
            logger.error("根据流水号获取青流箱箱号列表为空.batchCode:" + batchCode);
            return null;
        }catch (Exception e){
            Profiler.functionError(callerInfo);
            logger.error("根据流水号获取青流箱箱号列表异常.",e);
            return null;
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }
}
