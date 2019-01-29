package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.ham.cb.domain.external.CycleBoxDTO;
import com.jd.ham.cb.domain.external.CycleBoxQueryDTO;
import com.jd.ham.cb.domain.external.CycleBoxResultDTO;
import com.jd.ham.cb.service.CycleBoxExternalService;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("cycleBoxExternalManager")
public class CycleBoxExternalManagerImpl implements CycleBoxExternalManager {
    @Autowired
    private CycleBoxExternalService cycleBoxExternalService;
    private final Log logger = LogFactory.getLog(this.getClass());

    /**
     * 根据运单号获取青流箱号
     * @param waybillCode
     * @return
     */
    public List<String> getCbUniqueNoByWaybillCode(String waybillCode){
        CallerInfo callerInfo = null;
        try {
            callerInfo = ProfilerHelper.registerInfo("DMS.BASE.CycleBoxExternalManagerImpl.getCbUniqueNoByWaybillCode",Constants.UMP_APP_NAME_DMSWEB);
            CycleBoxQueryDTO dto = new CycleBoxQueryDTO();
            dto.setWaybillNo(waybillCode);
            CycleBoxResultDTO resultDto = cycleBoxExternalService.getCycleBoxInfo(dto);

            if(resultDto == null){
                logger.error("根据运单号获取青流箱明细失败.waybillCode:" + waybillCode);
                return null;
            }
            if(resultDto.getResultCode() != 1){
                logger.error("根据运单号获取青流箱明细失败.waybillCode:" + waybillCode +
                        ".返回值code:" + resultDto.getResultCode() +
                        ",message" + resultDto.getResultMessage());
                return null;
            }
            List<String> cbUniqueNoList = new ArrayList<String>();
            List<CycleBoxDTO> cycleBoxDTOList = (List<CycleBoxDTO>)resultDto.getResultData();
            if(cbUniqueNoList != null){
                for(CycleBoxDTO cycleBoxDTO : cycleBoxDTOList){
                    if(StringUtils.isNotBlank(cycleBoxDTO.getCbUniqueNo())){
                        cbUniqueNoList.add(cycleBoxDTO.getCbUniqueNo());
                    }
                }
            }
            return cbUniqueNoList;
        }catch (Exception e){
            Profiler.functionError(callerInfo);
            logger.error("根据运单号获取青流箱明细异常.",e);
            return null;
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }
}
