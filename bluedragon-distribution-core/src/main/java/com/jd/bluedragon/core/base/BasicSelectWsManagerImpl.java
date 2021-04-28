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

        //请求参数
        PageDto<TransportResourceDto> page = new PageDto<>();
        page.setCurrentPage(1);
        page.setPageSize(1000);
        try {
            if(logger.isInfoEnabled()){
                logger.info("调用运输运力数据分页接口入参page:{},transportResourceDto:{}",JsonHelper.toJsonMs(page),JsonHelper.toJsonMs(transportResourceDto));
            }
            long  start = System.currentTimeMillis();
            CommonDto<PageDto<TransportResourceDto>>  commonDto = basicSelectWs.queryPageTransportResourceWithNodeId(page,transportResourceDto);
            if(commonDto == null  || commonDto.getData()==null || commonDto.getCode() != Constants.RESULT_SUCCESS){
                logger.warn("BasicSelectWS.queryPageTransportResourceWithNodeId return error! 入参transportResourceDto:{},返回结果commonDto:{}",JsonHelper.toJsonMs(transportResourceDto),JsonHelper.toJsonMs(commonDto));
                return result;
            }else {
                long firstEnd = System.currentTimeMillis();
                if(logger.isInfoEnabled()) {
                    logger.info("调用运输运力数据分页接口首次耗时:" + (firstEnd - start) / 1000 + "s");
                }
                page = commonDto.getData();
                if(!CollectionUtils.isEmpty(page.getResult())){
                    result.addAll(page.getResult());
                }
                if (page.getTotalPage() > 1) {
                    for(int i = 2; i <= page.getTotalPage(); ++i) {
                        PageDto<TransportResourceDto> temp = new PageDto();
                        temp.setCurrentPage(i);
                        temp.setPageSize(1000);
                        CommonDto<PageDto<TransportResourceDto>> commonTempDto  = basicSelectWs.queryPageTransportResourceWithNodeId(temp,transportResourceDto);
                        if (commonTempDto == null  || commonTempDto.getData()==null || commonDto.getCode() != Constants.RESULT_SUCCESS) {
                            logger.warn("BasicSelectWS.queryPageTransportResourceWithNodeId return error! 入参transportResourceDto:{},返回结果commonDto:{}",JsonHelper.toJsonMs(transportResourceDto),JsonHelper.toJsonMs(commonDto));
                        } else if (!CollectionUtils.isEmpty(commonTempDto.getData().getResult())) {
                            result.addAll(commonTempDto.getData().getResult());
                        }
                    }
                }
            }
            long end = System.currentTimeMillis();
            if(logger.isInfoEnabled()) {
                logger.info("调用运输运力数据分页接口总耗时:" + (end - start) + "ms");
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
        try {
            carrierDto.setQueryCursor(-1L);
            carrierDto.setYn(Constants.YN_YES);
            PageDto<CarrierDto> returnPageDto = null;
            do {
                PageDto<CarrierDto> pageDto = new PageDto<>();
                pageDto.setCurrentPage(1);//现有调用方式，每次必须是1
                pageDto.setPageSize(1000);

                if(logger.isInfoEnabled()){
                    logger.info("调用运输承运商数据分页接口入参pageDto:{},carrierDto:{}",JsonHelper.toJsonMs(pageDto),JsonHelper.toJsonMs(carrierDto));
                }

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
    
