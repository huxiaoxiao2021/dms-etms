package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.PageDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.basic.ws.BasicSelectWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/11/19 10:11
 */
@Service
public class BasicSelectWsManagerImpl implements BasicSelectWsManager {
    private static final Logger logger = LoggerFactory.getLogger(BasicSelectWsManagerImpl.class);

    @Autowired
    private BasicSelectWS basicSelectWs;

    @Override
    @JProfiler(jKey = "DMS.BASE.basicSelectWsManagerImpl.queryPageTransportResource",jAppName = Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError} )
    public List<TransportResourceDto> queryPageTransportResource(TransportResourceDto transportResourceDto) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.basicSelectWsManagerImpl.queryPageTransportResource", false, true);
        //返回的结果
        List<TransportResourceDto> result = new ArrayList<>();

        //请求参数
        PageDto<TransportResourceDto> page = new PageDto<>();
        page.setCurrentPage(1);
        page.setPageSize(1000);
        try {
            if(logger.isInfoEnabled()){
                logger.info("调用运输运力数据分页接口入参page:{},transportResourceDto:{}",JsonHelper.toJsonMs(page),JsonHelper.toJsonMs(transportResourceDto));
            }
            CommonDto<PageDto<TransportResourceDto>>  commonDto = basicSelectWs.queryPageTransportResource(page,transportResourceDto);
            if(commonDto ==null  || commonDto.getCode() != Constants.RESULT_SUCCESS || commonDto.getData()==null){
                logger.warn("BasicSelectWS.queryPageTransportResource return error!");
            }else {
                page = commonDto.getData();
                if(page.getResult()!=null && page.getResult().size()>0){
                    result.addAll(page.getResult());
                }
                if (page.getTotalPage() > 1) {
                    for(int i = 2; i <= page.getTotalPage(); ++i) {
                        PageDto<TransportResourceDto> temp = new PageDto();
                        temp.setCurrentPage(i);
                        temp.setPageSize(1000);
                        CommonDto<PageDto<TransportResourceDto>> commonTempDto  = basicSelectWs.queryPageTransportResource(temp,transportResourceDto);
                        if (commonTempDto == null || commonTempDto.getCode()!= Constants.RESULT_SUCCESS || commonTempDto.getData()==null) {
                            logger.warn("BasicSelectWS.queryPageTransportResource return error!");
                        } else if ( commonTempDto.getData().getResult()!=null && commonTempDto.getData().getResult().size() > 0) {
                            result.addAll(commonTempDto.getData().getResult());
                        }
                    }
                }
            }
        }catch (Exception e){
            logger.error("运力编码分页接口请求异常,transportResourceDto:{}",JsonHelper.toJsonMs(transportResourceDto),e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }
}
    
