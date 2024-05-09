package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.print.dto.data.IdentityDto;
import com.jdl.print.dto.data.PrintDataResult;
import com.jdl.print.dto.data.QueryPrintDataDto;
import com.jdl.print.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @author: 刘铎（liuduo8）
 * @date: 2024/4/17
 * @description: 云打印服务接口封装实现
 */
@Slf4j
@Service("cloudPrintDataServiceManager")
public class CloudPrintDataServiceManagerImpl implements CloudPrintDataServiceManager{

    /**
     *  普通打印
     */
    public static final Integer SCENE_PRINT = 101;

    /**
     * 换单打印
     */
    public static final Integer SCENE_EXCHANGE_PRINT = 103;


    /**
     * CARRIER_CODE 默认值
     */
    private static final String CARRIER_CODE = "JD";
    /**
     * PACKAGE_CODE 默认值
     */
    private static final String PACKAGE_CODE = "packageCode";
    /**
     * WAYBILL_CODE 默认值
     */
    private static final String WAYBILL_CODE = "waybillCode";

    /**
     * 云打印JSF服务
     */
    @Autowired
    @Qualifier("dataServiceJsfService")
    private DataService dataService;

    /**
     * 获取打印数据
     *
     * @param queryPrintDataDto
     * @return 打印数据
     */
    @Override
    public PrintDataResult innerPlainText(QueryPrintDataDto queryPrintDataDto) {
        PrintDataResult  result = null;
        CallerInfo info = Profiler.registerInfo("com.jd.bluedragon.core.base.CloudPrintDataServiceManagerImpl.getPrintData", Constants.UMP_APP_NAME_DMSWEB, false, true);
        try{
            //初始默认值
            queryPrintDataDto = defaultInit(queryPrintDataDto);
            if(log.isInfoEnabled()){
                log.info("com.jdl.print.service.DataService.innerPlainText req:{}",JSON.toJSONString(queryPrintDataDto));
            }
            result = dataService.innerPlainText(queryPrintDataDto);
        }catch (Exception e){
            Profiler.functionError(info);
            log.error("getPrintData error!{}", JsonHelper.toJsonMs(queryPrintDataDto),e);
            result = new PrintDataResult();
            result.setMessage(e.getMessage());
            return result;
        }finally {
            if(log.isInfoEnabled()){
                log.info("com.jdl.print.service.DataService.innerPlainText req:{} ,resp:{} ",
                        JSON.toJSONString(queryPrintDataDto),JSON.toJSONString(result));
            }
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
    * 为传入的 queryPrintDataDto 进行默认初始化操作
    * @param queryPrintDataDto 要初始化的 QueryPrintDataDto 对象
    * @return 初始化后的 QueryPrintDataDto 对象
    */
    private QueryPrintDataDto defaultInit(QueryPrintDataDto queryPrintDataDto){
        //基础信息
        queryPrintDataDto.setCarrierCode(CARRIER_CODE);
        if(WaybillUtil.isPackageCode(queryPrintDataDto.getBillCodeValue())){
            queryPrintDataDto.setBillCodeType(PACKAGE_CODE);
        }else{
            queryPrintDataDto.setBillCodeType(WAYBILL_CODE);
        }
        //身份信息
        IdentityDto identityDto = new IdentityDto();
        queryPrintDataDto.setIdentityDto(identityDto);
        identityDto.setCallSystem(Constants.UMP_APP_NAME_DMSWEB);

        return queryPrintDataDto;
    }
}
