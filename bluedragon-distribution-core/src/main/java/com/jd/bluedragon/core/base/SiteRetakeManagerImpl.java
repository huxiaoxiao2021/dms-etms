package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.orm.page.Page;
import com.jd.etms.erp.service.domain.VendorOrder;
import com.jd.etms.erp.service.dto.CommonDto;
import com.jd.ldop.middle.api.BaseResult;
import com.jd.ldop.middle.api.basic.BasicTraderInfoQueryApi;
import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;
import com.jd.ql.erp.dto.ResponseDTO;
import com.jd.ql.erp.dto.vendor.ReTakeRequestDTO;
import com.jd.ql.erp.jsf.VendorOrderJsfService;
import com.jd.ql.erp.jsf.vendor.CommonCollectApi;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 查询商家
 * @date 2018年08月02日 14时:24分
 */
@Service("siteRetakeManager")
public class SiteRetakeManagerImpl implements SiteRetakeManager {
    private static final Logger log = LoggerFactory.getLogger(SiteRetakeManagerImpl.class);
    @Autowired
    private BasicTraderInfoQueryApi basicTraderInfoQueryApi;
    @Autowired
    private VendorOrderJsfService vendorOrderJsfService;

    @Autowired
    private CommonCollectApi erpCommonCollectApi;

    @Override
    public List<BasicTraderQueryDTO> queryBasicTraderInfoByKey(String key) {
        if (StringHelper.isEmpty(key)) {
            return new ArrayList();
        }
        CallerInfo info = Profiler.registerInfo("com.jd.bluedragon.core.base.SiteRetakeManagerImpl.queryBasicTraderInfoByKey", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            BaseResult<List<BasicTraderQueryDTO>> baseResult = basicTraderInfoQueryApi.queryBasicTraderInfoByKey(key.trim());
            if (baseResult != null && baseResult.getStatusCode() == 0 && baseResult.getData() != null && baseResult.getData().size() > 0) {
                return baseResult.getData();
            } else if (baseResult != null && baseResult.getStatusCode() == 1) {
                log.warn("SiteRetakeManagerImpl-queryBasicTraderInfoByKey 查询失败，key:{},返回结果：{}" ,key, baseResult.getStatusMessage());
                return new ArrayList();
            } else {
                log.warn("SiteRetakeManagerImpl-queryBasicTraderInfoByKey 查询失败，key:{}" , key);
                return new ArrayList();
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("SiteRetakeManagerImpl-queryBasicTraderInfoByKey 查询失败，key:{}" , key, e);
            return new ArrayList();
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    @Override
    @JProfiler(jKey = "com.jd.bluedragon.core.base.SiteRetakeManagerImpl.selectVendorOrderList", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public Page<VendorOrder> selectVendorOrderList(VendorOrder vendorOrder, Page page) {

        CommonDto<Page<VendorOrder>> result = vendorOrderJsfService.selectVendorOrderList(vendorOrder, page);
//        CommonDto<Page<VendorOrder>> result = testQueryOrder();
        if (result == null) {
            log.warn("SiteRetakeManagerImpl.selectVendorOrderList 查询异常，vendorOrder:{},page：{}" ,JsonHelper.toJson(vendorOrder), JsonHelper.toJson(page));
            page.setTotalRow(0);
            return page;
        }
        if (result.getCode() != 1) {
            log.warn("SiteRetakeManagerImpl.selectVendorOrderList 查询失败，vendorOrder:{},page：{},code:{},msg:{}"
                    , JsonHelper.toJson(vendorOrder),JsonHelper.toJson(page),result.getCode(),result.getMessage());
            page.setTotalRow(0);
            return page;
        }
        return result.getData();
    }


    /**
     * 批量处理驻厂再取
     * 返回处理失败数据
     * @param reTakeRequestDTOS
     * @return
     */
    @Override
    @JProfiler(jKey = "com.jd.bluedragon.core.base.SiteRetakeManagerImpl.batchUpdateReTakeTime", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public List<String> batchUpdateReTakeTime(List<ReTakeRequestDTO> reTakeRequestDTOS){
        List<String> failList = new ArrayList<>();
        if(reTakeRequestDTOS==null || reTakeRequestDTOS.isEmpty() ){
            return failList;
        }
        int batchSize = 100;
        int pageSize = Double.valueOf(Math.ceil(Double.valueOf(reTakeRequestDTOS.size())/batchSize)).intValue();

        for(int i = 0 ; i < pageSize ;i++){
            List<ReTakeRequestDTO> batchDealList;
            if((i+1) * batchSize < reTakeRequestDTOS.size()){
                batchDealList = reTakeRequestDTOS.subList(i*batchSize,(i+1)*batchSize);
            }else{
                batchDealList = reTakeRequestDTOS.subList(i*batchSize,reTakeRequestDTOS.size());
            }

            ResponseDTO<List<String>> batchDealResult = erpCommonCollectApi.batchUpdateReTakeTime(batchDealList);
            if(batchDealResult!=null && batchDealResult.getResultCode()!=null
                    && batchDealResult.getData()!=null && !batchDealResult.getData().isEmpty()){
                failList.addAll(batchDealResult.getData());
            }


        }
        return failList;
    }

    public static void main(String[] args) {
        List<ReTakeRequestDTO> reTakeRequestDTOS = new ArrayList<>();
        for(int i = 0; i< 200; i++){
            reTakeRequestDTOS.add(new ReTakeRequestDTO());
        }

        int batchSize = 100;
        int pageSize = Double.valueOf(Math.ceil(Double.valueOf(reTakeRequestDTOS.size())/batchSize)).intValue();

        for(int i = 0 ; i < pageSize ;i++){
            List<ReTakeRequestDTO> batchDealList;
            if((i+1) * batchSize < reTakeRequestDTOS.size()){
                batchDealList = reTakeRequestDTOS.subList(i*batchSize,(i+1)*batchSize);
            }else{
                batchDealList = reTakeRequestDTOS.subList(i*batchSize,reTakeRequestDTOS.size());
            }
            System.out.println("---"+batchDealList.size());
        }

    }

}
