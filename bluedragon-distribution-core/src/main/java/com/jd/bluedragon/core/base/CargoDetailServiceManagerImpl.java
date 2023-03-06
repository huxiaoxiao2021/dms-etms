package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.utils.JsonHelper;
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

import java.util.ArrayList;
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


    /**
     * 获取已发货批次下包裹号
     * @param createSiteCode
     * @param batchCode
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.BASE.CargoDetailServiceManagerImpl.getCargoDetailInfoByBatchCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<String> querySealCarPackageList(Integer createSiteCode, String batchCode) {
        List<String> packageCodeList = new ArrayList<>();
        CargoDetailDto cargoDetailDto = new CargoDetailDto();
        //批次号
        cargoDetailDto.setBatchCode(batchCode);
        //有效数据
        cargoDetailDto.setYn(1);
        //每次去运输获取数据的量;
        int limitSize = 1000;
        int currentSize  = limitSize ;
        int offset = 0;
        while(currentSize == limitSize){
            CommonDto<List<CargoDetailDto>> cargoDetailReturn = this.getCargoDetailInfoByBatchCode(cargoDetailDto,offset,limitSize);
            if(cargoDetailReturn == null || cargoDetailReturn.getCode() != com.jd.etms.vos.dto.CommonDto.CODE_SUCCESS ) {
                logger.error("获取批次{}下包裹数据异常,条件：offset={},limitSize={}, result={}",batchCode,offset,limitSize, JsonHelper.toJson(cargoDetailReturn));
                throw new JyBizException("运输接口根据批次获取包裹信息异常");
            }
            if(cargoDetailReturn.getData().isEmpty()) {
                break;
            }
            List<CargoDetailDto> cargoDetailDtoList =  cargoDetailReturn.getData();
            logger.info("从运输系统获取批次【{}】下包裹信息，封车网点为{},获取包裹数量为{},offset={},limitSize={}",batchCode,createSiteCode,cargoDetailDtoList.size(), offset,limitSize);
            for(CargoDetailDto cargoDetailDtoTemp:cargoDetailDtoList){
                packageCodeList.add(cargoDetailDtoTemp.getPackageCode());
            }
            currentSize = cargoDetailDtoList.size();
            offset =  offset + limitSize;
        }
        return packageCodeList;
    }
}
