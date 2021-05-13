package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.tms.data.dto.CargoDetailDto;
import com.jd.tms.data.dto.CommonDto;
import com.jd.tms.data.ws.DataCargoDetailWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("cargoDetailServiceManager")
public class CargoDetailServiceManagerImpl implements CargoDetailServiceManager{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataCargoDetailWS dataCargoDetailWS;
    /**
     * 运输接口：根据批次号获取批次下包裹以及运单维度信息。
     * @param CargoDetailDto cargoDetailDto
     * @param int offset
     * @param int rowlimit
     * @return CommonDto<List<CargoDetailDto>>
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.BASE.CargoDetailServiceManagerImpl.getCargoDetailInfoByBatchCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public CommonDto<List<CargoDetailDto>> getCargoDetailInfoByBatchCode(CargoDetailDto cargoDetailDto, int offset, int rowlimit){
        CommonDto<List<CargoDetailDto>> cargoDetailList = null;
        try{
            cargoDetailList = dataCargoDetailWS.queryListByCondition(cargoDetailDto,offset,rowlimit);
        }catch (Exception e){
            logger.error("{}根据批次号从运输获取批次下包裹信息时出现异常，异常信息为"+ e.getMessage(),cargoDetailDto.getBatchCode(),e);
            Profiler.businessAlarm("DMS.BASE.CargoDetailServiceManagerImpl.getCargoDetailInfoByBatchCode", cargoDetailDto.getBatchCode());
        }
        return cargoDetailList;
    }
}
