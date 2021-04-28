package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.basic.dto.CarrierDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.PageDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.basic.ws.BasicSelectWS;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
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
    public List<TransportResourceDto> queryPageTransportResourceWithNodeId(TransportResourceDto transportResourceDto) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.basicSelectWsManagerImpl.queryPageTransportResourceWithNodeId", false, true);
        //返回的结果
        List<TransportResourceDto> result = new ArrayList<>();

        transportResourceDto.setQueryCursor(-1L);
        transportResourceDto.setYn(Constants.YN_YES);

        PageDto<TransportResourceDto> returnPageDto = null;
        try {
            long  start = System.currentTimeMillis();
            if(logger.isInfoEnabled()){
                logger.info("调用运输运力数据分页接口入参 transportResourceDto:{}",JsonHelper.toJsonMs(transportResourceDto));
            }
            do {
                //请求参数
                PageDto<TransportResourceDto> page = new PageDto<>();
                page.setCurrentPage(1);
                page.setPageSize(1000);
                CommonDto<PageDto<TransportResourceDto>>  commonDto = basicSelectWs.queryPageTransportResourceWithNodeId(page,transportResourceDto);
                if(commonDto != null && commonDto.getCode() == CommonDto.CODE_NORMAL){
                    returnPageDto = commonDto.getData();
                }else {
                    returnPageDto = null;
                }

                if(returnPageDto != null && CollectionUtils.isNotEmpty(returnPageDto.getResult())){
                    //单次查询结果处理
                    List<TransportResourceDto> returnData = returnPageDto.getResult();
                    result.addAll(returnData);

                    //设置下一次查询的MaxId; 使用MaxId方式调用接口时, 接口实现将以自增主键升序排序
                    transportResourceDto.setQueryCursor(returnData.get(returnData.size() - 1).getId());
                }

            }while (returnPageDto != null && CollectionUtils.isNotEmpty(returnPageDto.getResult()));

            long end = System.currentTimeMillis();

            if(logger.isInfoEnabled()) {
                logger.info("调用运输运力数据分页接口总耗时:" + (end - start) + "ms"+"数据--size:"+result.size());
            }
        }catch (Exception e){
            logger.error("运力编码分页接口请求异常,transportResourceDto:{}",JsonHelper.toJsonMs(transportResourceDto),e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }


    /**
     * 根据条件获取已启用承运商(配送的已从Basic获取，仓储的仍从VTS获取)
     * @param carrierDto
     */
    public List<CarrierDto> getCarrierInfoList(CarrierDto carrierDto){
        CallerInfo info = Profiler.registerInfo("DMS.BASE.basicSelectWsManagerImpl.queryPageCarrier", false, true);
        // 返回承运商列表
        List<CarrierDto>  result = new ArrayList<>();

        carrierDto.setQueryCursor(-1L);
        carrierDto.setYn(Constants.YN_YES);

        PageDto<CarrierDto> returnPageDto = null;

        if(logger.isInfoEnabled()){
            logger.info("调用运输承运商数据分页接口入参 carrierDto:{}",JsonHelper.toJsonMs(carrierDto));
        }
        try {
            do {
                PageDto<CarrierDto> pageDto = new PageDto<>();
                pageDto.setCurrentPage(1);//现有调用方式，每次必须是1
                pageDto.setPageSize(1000);
                CommonDto<PageDto<CarrierDto>> returnCommonDto = basicSelectWs.queryPageCarrier(pageDto, carrierDto);
                if(returnCommonDto != null && returnCommonDto.getCode() == CommonDto.CODE_NORMAL){
                    returnPageDto = returnCommonDto.getData();
                } else {
                    returnPageDto = null;
                }

                if(returnPageDto != null && CollectionUtils.isNotEmpty(returnPageDto.getResult())){
                    //单次查询结果处理
                    List<CarrierDto> returnData = returnPageDto.getResult();
                    result.addAll(returnData);

                    //设置下一次查询的MaxId; 使用MaxId方式调用接口时, 接口实现将以自增主键升序排序
                    carrierDto.setQueryCursor(returnData.get(returnData.size() - 1).getCarrierId());
                }
            }while (returnPageDto != null && CollectionUtils.isNotEmpty(returnPageDto.getResult()));

            if(logger.isInfoEnabled()){
                logger.info("调用运输承运商数据分页接口 返回运力数量:size{},carrierDto:{}",result.size(),JsonHelper.toJsonMs(carrierDto));
            }
        }catch (Exception e){
            logger.error("调用运输承运商数据分页接口 异常,carrierDto:{}",JsonHelper.toJsonMs(carrierDto));
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }
}
    
