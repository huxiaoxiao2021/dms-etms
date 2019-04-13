package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.orm.page.Page;
import com.jd.etms.erp.service.domain.VendorOrder;
import com.jd.etms.erp.service.dto.CommonDto;
import com.jd.ldop.middle.api.BaseResult;
import com.jd.ldop.middle.api.basic.BasicTraderInfoQueryApi;
import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;
import com.jd.ql.erp.jsf.VendorOrderJsfService;
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
    private static final Logger logger = LoggerFactory.getLogger(SiteRetakeManagerImpl.class);
    @Autowired
    private BasicTraderInfoQueryApi basicTraderInfoQueryApi;
    @Autowired
    private VendorOrderJsfService vendorOrderJsfService;

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
                logger.warn("SiteRetakeManagerImpl-queryBasicTraderInfoByKey 查询失败，key:" + key + ",返回结果：" + baseResult.getStatusMessage());
                return new ArrayList();
            } else {
                logger.warn("SiteRetakeManagerImpl-queryBasicTraderInfoByKey 查询失败，key:" + key);
                return new ArrayList();
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("SiteRetakeManagerImpl-queryBasicTraderInfoByKey 查询失败，key:" + key, e);
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
            logger.error("SiteRetakeManagerImpl.selectVendorOrderList 查询异常，vendorOrder:" + JsonHelper.toJson(vendorOrder) + ",page：" + JsonHelper.toJson(page));
            page.setTotalRow(0);
            return page;
        }
        if (result.getCode() != 1) {
            logger.error(result.getMessage());
            logger.error("SiteRetakeManagerImpl.selectVendorOrderList 查询失败，vendorOrder:" + JsonHelper.toJson(vendorOrder) + ",page：" + JsonHelper.toJson(page) + ",code:" + result.getCode());
            page.setTotalRow(0);
            return page;
        }
        return result.getData();
    }
    private  CommonDto<Page<VendorOrder>> testQueryOrder(){
        List list=Lists.newArrayList();
        for (int i=0;i<15;i++){
            VendorOrder vendorOrder=new VendorOrder();
            vendorOrder.setWaybillCode("12345"+i);
            vendorOrder.setWaybillCreateTime(new Date());
            vendorOrder.setAssignTime(new Date());
            list.add(vendorOrder);
        }
        Page<VendorOrder> page=new Page<VendorOrder>();
        page.setPageSize(20);
        page.setCurrentPage(1);
        page.setTotalRow(15);
        page.setResult(list);
        CommonDto<Page<VendorOrder>> aa=new CommonDto<Page<VendorOrder>>();
        aa.setCode(1);
        aa.setData(page);
        return aa;
    }
    @Override
    @JProfiler(jKey = "com.jd.bluedragon.core.base.SiteRetakeManagerImpl.updateCommonOrderStatus", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<String> updateCommonOrderStatus(VendorOrder vendorOrder) {
        InvokeResult<String> invokeResult = new InvokeResult<String>();
        CommonDto<Boolean> result = vendorOrderJsfService.updateCommonOrderStatus(vendorOrder);
        if (result == null) {
            logger.error("SiteRetakeManagerImpl.updateCommonOrderStatus异常，vendorOrder:" + JsonHelper.toJson(vendorOrder));
            invokeResult.customMessage(500, "服务器错误");
            return invokeResult;
        }
        if (result.getCode() != 1) {
            logger.error(result.getMessage());
            logger.error("SiteRetakeManagerImpl.updateCommonOrderStatus失败，vendorOrder:" + JsonHelper.toJson(vendorOrder) + ",code:" + result.getCode());
            invokeResult.customMessage(501, result.getMessage());
            return invokeResult;
        }
        if (result.getData() == Boolean.FALSE) {
            logger.error(result.getMessage());
            logger.error("SiteRetakeManagerImpl.updateCommonOrderStatus失败1，vendorOrder:" + JsonHelper.toJson(vendorOrder) + ",code:" + result.getCode());
        }
        logger.info("SiteRetakeManagerImpl.updateCommonOrderStatus成功，vendorOrder:" + JsonHelper.toJson(vendorOrder));
        return invokeResult;
    }
}
