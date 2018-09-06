package com.jd.bluedragon.distribution.siteRetake.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.SiteRetakeManager;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.siteRetake.domain.SiteRetakeCondition;
import com.jd.bluedragon.distribution.siteRetake.domain.SiteRetakeOperation;
import com.jd.bluedragon.distribution.siteRetake.service.SiteRetakeService;
import com.jd.common.orm.page.Page;
import com.jd.etms.erp.service.domain.VendorOrder;
import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;
import com.jd.ql.dms.common.domain.JdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 驻厂批量再取
 * @date 2018年08月02日 14时:47分
 */
@Service("siteRetakeService")
public class SiteRetakeServiceImpl implements SiteRetakeService {
    @Autowired
    private SiteRetakeManager siteRetakeManager;

    /**
     * 查商家
     *
     * @param key
     * @return
     */
    @Override
    public List<BasicTraderQueryDTO> queryBasicTraderInfoByKey(String key) {
        return siteRetakeManager.queryBasicTraderInfoByKey(key);
    }

    public Page<VendorOrder> queryVendorOrderList(SiteRetakeCondition siteRetakeCondition, Page page) {
        VendorOrder vendorOrder = new VendorOrder();
        if (siteRetakeCondition.getSiteCode()!=null){
            vendorOrder.setSiteCode(siteRetakeCondition.getSiteCode());
        }
        if (siteRetakeCondition.getVendorId()!=null){
            vendorOrder.setSellerId(siteRetakeCondition.getVendorId());
        }
        if (siteRetakeCondition.getAssignTime()!=null){
            vendorOrder.setAssignTime(siteRetakeCondition.getAssignTime());
        }
        if (siteRetakeCondition.getWaybillCreateTime()!=null){
            vendorOrder.setWaybillCreateTime(siteRetakeCondition.getWaybillCreateTime());
        }
        if (siteRetakeCondition.getWaybillCode()!=null){
            vendorOrder.setWaybillCode(siteRetakeCondition.getWaybillCode());
        }
        Page<VendorOrder> pageResult = siteRetakeManager.selectVendorOrderList(vendorOrder, page);
        pageResult.setCurrentPage(page.getCurrentPage());
        pageResult.setPageSize(page.getPageSize());
        return pageResult;


    }

    public JdResponse<String> updateCommonOrderStatus(SiteRetakeOperation siteRetakeOperation) {
        VendorOrder vendorOrder = new VendorOrder();
        vendorOrder.setStatus(siteRetakeOperation.getStatus());
        vendorOrder.setOperatorId(siteRetakeOperation.getOperatorId());
        vendorOrder.setOperatorName(siteRetakeOperation.getOperatorName());
        vendorOrder.setSiteCode(siteRetakeOperation.getSiteCode());
        vendorOrder.setSiteName(siteRetakeOperation.getSiteName());
        vendorOrder.setEndReason(siteRetakeOperation.getEndReason());
        vendorOrder.setRequiredStartTime(siteRetakeOperation.getRequiredStartTime());
        vendorOrder.setRequiredEndTime(siteRetakeOperation.getRequiredEndTime());
        vendorOrder.setOperatorSource(3);//系统标识
        vendorOrder.setRemark(siteRetakeOperation.getRemark());
        vendorOrder.setUpdateTime(siteRetakeOperation.getOperatorTime());
        JdResponse<String> result = new JdResponse<String>();
        result.setCode(200);
        result.setMessage("");
        String[] waybillcodes = siteRetakeOperation.getWaybillCode().split(Constants.SEPARATOR_COMMA);
        if (waybillcodes != null && waybillcodes.length > 0) {

            for (String waybillCode : waybillcodes) {
                vendorOrder.setWaybillCode(waybillCode);
                InvokeResult<String> resultOne = siteRetakeManager.updateCommonOrderStatus(vendorOrder);
                if (resultOne.getCode() != 200) {
                    result.setCode(101);
                    result.setMessage(result.getMessage() + "运单[" + waybillCode + "]保存失败：" + resultOne.getMessage() + "\n");
                }
            }
        } else {
            result.setCode(100);
            result.setMessage("无可操作运单");
        }
        return result;
    }
}
