package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ham.cb.domain.external.CycleBoxDTO;
import com.jd.ham.cb.domain.external.CycleBoxQueryDTO;
import com.jd.ham.cb.domain.external.CycleBoxResultDTO;
import com.jd.ham.cb.service.CycleBoxExternalService;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("cycleBoxExternalManager")
public class CycleBoxExternalManagerImpl implements CycleBoxExternalManager {
    @Autowired
    private CycleBoxExternalService cycleBoxExternalService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 调用运单接口获取青流箱明细
     * @param dto
     * @return
     * @throws Exception
     */
    public List<CycleBoxDTO> getCycleBoxInfo(CycleBoxQueryDTO dto) throws Exception{
        CallerInfo callerInfo = null;
        try {
            callerInfo = ProfilerHelper.registerInfo("DMS.BASE.CycleBoxExternalManagerImpl.getCycleBoxInfo",Constants.UMP_APP_NAME_DMSWEB);
            CycleBoxResultDTO resultDto = cycleBoxExternalService.getCycleBoxInfo(dto);

            if(resultDto == null){
                log.warn("调运单接口获取青流箱明细失败.dto:{}" , JSON.toJSONString(dto));
                return null;
            }
            if(resultDto.getResultCode() != 1){
                log.warn("调运单接口获取青流箱明细失败.dto:{}.返回值code:{},message:{}", JSON.toJSONString(dto),resultDto.getResultCode(), resultDto.getResultMessage());
                return null;
            }
            return (List<CycleBoxDTO>)resultDto.getResultData();
        }catch (Exception e){
            Profiler.functionError(callerInfo);
            log.error("根据运单号获取青流箱明细异常:{}", JsonHelper.toJson(dto),e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

    /**
     * 根据运单号获取青流箱号
     * @param waybillCode
     * @return
     */
    public List<String> getCbUniqueNoByWaybillCode(String waybillCode){
        CycleBoxQueryDTO dto = new CycleBoxQueryDTO();
        dto.setWaybillNo(waybillCode);

        List<String> cbUniqueNoList = new ArrayList<String>();

        try {
            List<CycleBoxDTO> cycleBoxDTOList = getCycleBoxInfo(dto);
            if (cbUniqueNoList != null) {
                for (CycleBoxDTO cycleBoxDTO : cycleBoxDTOList) {
                    if (StringUtils.isNotBlank(cycleBoxDTO.getCbUniqueNo())) {
                        cbUniqueNoList.add(cycleBoxDTO.getCbUniqueNo());
                    }
                }
            }
        }catch (Exception e){
            log.error("根据运单号调用运单接口获取青流箱明细异常:{}",waybillCode,e);
        }
        return cbUniqueNoList;
    }
}
